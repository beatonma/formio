package org.beatonma.gclocks.wallpaper

import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.beatonma.gclocks.android.AndroidCanvasHost
import org.beatonma.gclocks.app.data.AppSettingsRepository
import org.beatonma.gclocks.app.data.loadDisplayMetrics
import org.beatonma.gclocks.app.data.loadWallpaperSettings
import org.beatonma.gclocks.app.data.settings.DisplayContext
import org.beatonma.gclocks.app.data.settings.DisplayMetrics
import org.beatonma.gclocks.app.io
import org.beatonma.gclocks.clocks.createAnimatorFromOptions
import org.beatonma.gclocks.core.ClockAnimator
import org.beatonma.gclocks.core.geometry.MeasureConstraints
import org.beatonma.gclocks.core.geometry.RectF
import org.beatonma.gclocks.core.glyph.GlyphState
import org.beatonma.gclocks.core.glyph.GlyphVisibility
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.options.AnyOptions
import org.beatonma.gclocks.core.util.debug
import org.beatonma.gclocks.core.util.getCurrentTimeMillis
import org.jetbrains.annotations.VisibleForTesting
import kotlin.collections.ifEmpty
import kotlin.math.roundToInt
import kotlin.random.Random


interface WallpaperEngineDelegate {
    fun onSurfaceChanged(width: Int, height: Int)
    fun onVisibilityChanged(isVisible: Boolean, getIsKeyguardLocked: () -> Boolean)
    fun onOffsetsChanged(
        xOffset: Float, yOffset: Float,
        xOffsetStep: Float, yOffsetStep: Float,
        xPixelOffset: Int, yPixelOffset: Int,
    )

    fun onTouchEvent(event: MotionEvent): Boolean
    fun onDestroy()

    fun draw(canvas: Canvas)
    fun clear(canvas: Canvas)
}

@OptIn(ExperimentalCoroutinesApi::class)
fun WallpaperEngineDelegate(
    engine: WallpaperService.Engine,
    settingsRepository: AppSettingsRepository,
    onDraw: (AndroidCanvasHost) -> Unit,
    onClearCanvas: (AndroidCanvasHost) -> Unit,
): WallpaperEngineDelegate = WallpaperEngineDelegateImpl(
    engine.isPreview,
    engine.isVisible,
    settingsRepository.loadDisplayMetrics(),
    settingsRepository.loadWallpaperSettings().mapLatest {
        it.displayOptions as DisplayContext.Options.Wallpaper
    },
    settingsRepository.loadWallpaperSettings().mapLatest { it.clockOptions },
    onDraw = onDraw,
    onClearCanvas = onClearCanvas,
)

@VisibleForTesting
internal class WallpaperEngineDelegateImpl(
    isPreview: Boolean,
    isWallpaperVisible: Boolean,
    private val displayMetrics: Flow<DisplayMetrics>,
    private val wallpaperSettings: Flow<DisplayContext.Options.Wallpaper>,
    private val clockSettings: Flow<AnyOptions>,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.io,
    private val onDraw: (AndroidCanvasHost) -> Unit,
    private val onClearCanvas: (AndroidCanvasHost) -> Unit,
    private val getCurrentTimeMillis: () -> Long = ::getCurrentTimeMillis,
    private val random: Random = Random.Default,
) : WallpaperEngineDelegate {
    private val canvasHost: AndroidCanvasHost = AndroidCanvasHost()
    private var _animator: ClockAnimator<*>? = null
    private val animator: ClockAnimator<*> get() = _animator!!
    private var previousClockOptions: AnyOptions? = null
    private var backgroundColor: Color = Color(0xff000000.toInt())
    private val engineScope: CoroutineScope = CoroutineScope(mainDispatcher + SupervisorJob())

    @VisibleForTesting
    internal val visibilityManager: VisibilityManager = VisibilityManager(
        isPreview,
        engineScope,
        ioDispatcher,
        isWallpaperVisible = isWallpaperVisible,
    )
    private val layoutManager: LayoutManager = LayoutManager()
    private var frameDelayMillis: Long = (1000f / 60f).toLong()
    private val invalidateDebouncer = Debouncer(ioDispatcher)
    private var initJob: Job? = null
    private var visibilityJob: Job? = null

    init {
        initialize()
    }

    private fun initialize() {
        debugEvent("initialize isPreview=${visibilityManager.isPreview}")

        initJob?.cancel()
        initJob = engineScope.launch(ioDispatcher) {
            displayMetrics.first().let { metrics ->
                frameDelayMillis = metrics.frameDelayMillis
            }

            clockSettings.first().let { clock ->
                _animator = createAnimator(clock)
            }

            wallpaperSettings.first().let { wallpaper ->
                backgroundColor = wallpaper.backgroundColor
                animator.setConstraints(layoutManager.setBounds(wallpaper.position))
                visibilityManager.setLauncherPages(wallpaper.zeroIndexLauncherPages.ifEmpty { null })
            }

            postInvalidate()

            visibilityManager.state.collectLatest { state ->
                setAnimatorVisible(state.targetVisibility, force = false)
                postInvalidate()
            }
        }
    }

    private fun setAnimatorVisible(visibility: GlyphVisibility, force: Boolean) {
        visibilityJob?.cancel()
        visibilityJob = _animator?.setStateWithVariance(
            engineScope,
            visibility,
            force,
            varianceMillis = 600L,
            getCurrentTimeMillis = getCurrentTimeMillis,
            random = random,
        )
    }

    private suspend fun invalidate() {
        withContext(mainDispatcher) {
            onDraw(canvasHost)
        }
    }

    private fun postInvalidate(delayMillis: Long = 0L) {
        invalidateDebouncer(engineScope, delayMillis) {
            invalidate()
        }
    }

    override fun onSurfaceChanged(width: Int, height: Int) {
        if (layoutManager.width == height && layoutManager.height == width) {
            engineScope.launch(ioDispatcher) {
                visibilityManager.startRotation()
            }
        }

        val constraints = layoutManager.setSize(width, height)
        _animator?.setConstraints(constraints)
    }

    override fun onVisibilityChanged(isVisible: Boolean, getIsKeyguardLocked: () -> Boolean) {
        if (visibilityManager.isPreview) return visibilityManager.onVisibilityChanged(
            isWallpaperVisible = true,
            isKeyguardLocked = false
        )

        val isKeyguardLocked: Boolean = getIsKeyguardLocked()
        debugEvent("onVisibilityChanged($isVisible, ${isKeyguardLocked})")

        if (isVisible) {
            initialize()
        } else {
            setAnimatorVisible(GlyphVisibility.Hidden, force = true)
            cancelUpdates()
        }

        visibilityManager.onVisibilityChanged(
            isWallpaperVisible = isVisible,
            isKeyguardLocked = isKeyguardLocked
        )
        if (isVisible && isKeyguardLocked) {
            // Poll until keyguard is unlocked
            engineScope.launch(ioDispatcher) {
                while (true) {
                    delay(50L)
                    val isLocked = getIsKeyguardLocked()
                    if (!isLocked) {
                        visibilityManager.onVisibilityChanged(true, false)
                        break
                    }
                }
            }
        }
    }

    override fun onOffsetsChanged(
        xOffset: Float,
        yOffset: Float,
        xOffsetStep: Float,
        yOffsetStep: Float,
        xPixelOffset: Int,
        yPixelOffset: Int,
    ) {
        if (xOffsetStep == 0f) {
            // Only one launcher page
            return
        }
        val pageCount = 1f / xOffsetStep
        val currentPosition: Float = pageCount * xOffset
        val currentPage: Int = currentPosition.roundToInt()
        visibilityManager.onPageChanged(currentPage)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val glyph = animator.getGlyphAt(
            event.x - layoutManager.left,
            event.y - layoutManager.top
        )
        glyph?.setState(GlyphState.Active, currentTimeMillis = getCurrentTimeMillis())

        return glyph != null
    }

    override fun onDestroy() {
        engineScope.cancel()
    }

    override fun draw(canvas: Canvas) {
        canvas.fill(backgroundColor)
        animator.tick()
        canvas.withTranslation(layoutManager.left, layoutManager.top) {
            animator.render(canvas)
        }
    }

    override fun clear(canvas: Canvas) {
        canvas.fill(backgroundColor)
        onClearCanvas(canvasHost)
    }

    private fun cancelUpdates() {
        debugEvent("cancelUpdates()")
        engineScope.coroutineContext.cancelChildren()
    }

    private fun createAnimator(options: AnyOptions): ClockAnimator<*> {
        val animator = _animator?.let {
            if (options == previousClockOptions) it
            else null
        } ?: createAnimatorFromOptions(options, canvasHost.path, allowVariance = true) {
            postInvalidate(frameDelayMillis)
        }
        animator.setConstraints(layoutManager.constraints)
        previousClockOptions = options
        return animator
    }
}


@VisibleForTesting
internal data class VisibilityState(
    val isPreview: Boolean,
    val isWallpaperVisible: Boolean,
    val isKeyguardLocked: Boolean = false,
    val visibleOnPages: List<Int>? = null,
    val currentPage: Int = 0,
) {
    val isPageAllowed: Boolean
        get() = visibleOnPages?.let { currentPage in it } ?: true

    val shouldBeVisible: Boolean
        get() {
            if (isPreview) return true
            if (isKeyguardLocked) return false
            if (!isPageAllowed) return false
            return isWallpaperVisible
        }

    val targetVisibility: GlyphVisibility
        get() = if (shouldBeVisible) GlyphVisibility.Visible else GlyphVisibility.Hidden
}

@VisibleForTesting
internal class VisibilityManager(
    val isPreview: Boolean,
    private val scope: CoroutineScope,
    dispatcher: CoroutineDispatcher,
    isWallpaperVisible: Boolean,
) {
    private var isRotating: Boolean = false

    private val visibilityChangeDebouncer = Debouncer(dispatcher)
    private val pageChangedDebouncer = Debouncer(dispatcher)
    val state = MutableStateFlow(VisibilityState(isPreview = isPreview, isWallpaperVisible = isWallpaperVisible))

    suspend fun startRotation() {
        debugEvent("startRotation")
        isRotating = true
        delay(500L)
        isRotating = false
        debugEvent("endRotation")
    }

    fun onVisibilityChanged(isWallpaperVisible: Boolean, isKeyguardLocked: Boolean) {
        debugEvent("onVisibilityChanged($isWallpaperVisible, $isKeyguardLocked)")
        visibilityChangeDebouncer(scope) {
            if (isRotating) {
                delay(250L)
            }
            state.update { previous ->
                previous.copy(isWallpaperVisible = isWallpaperVisible, isKeyguardLocked = isKeyguardLocked)
            }
        }
    }

    fun onPageChanged(page: Int) {
        debugEvent("onPageChanged($page)")
        pageChangedDebouncer(scope, 60L) {
            state.update { previous ->
                previous.copy(currentPage = page)
            }
        }
    }

    fun setLauncherPages(pages: List<Int>?) {
        debugEvent("setLauncherPages($pages)")
        state.update { previous ->
            previous.copy(visibleOnPages = pages)
        }
    }
}

private class Debouncer(private val dispatcher: CoroutineDispatcher) {
    private var job: Job? = null

    operator inline fun invoke(
        scope: CoroutineScope,
        debounceMillis: Long = 0L,
        crossinline block: suspend CoroutineScope.() -> Unit,
    ) {
        job?.cancel()
        job = scope.launch(dispatcher) {
            delay(debounceMillis)
            block()
        }
    }
}

private class LayoutManager {
    var width: Int = 0
        private set
    var height: Int = 0
        private set
    private var relativeBounds: RectF = RectF.Invalid
    private var absoluteBounds: RectF = RectF(0f, 0f, 0f, 0f)
    val top get() = absoluteBounds.top
    val left get() = absoluteBounds.left

    var constraints: MeasureConstraints = MeasureConstraints(0f, 0f)
        private set

    fun setSize(width: Int, height: Int): MeasureConstraints {
        this.width = width
        this.height = height
        return updateConstraints()
    }

    fun setBounds(bounds: RectF): MeasureConstraints {
        relativeBounds = RectF(bounds)
        return updateConstraints()
    }

    private fun updateConstraints(): MeasureConstraints {
        val w = width.toFloat()
        val h = height.toFloat()

        if (relativeBounds.isValid) {
            absoluteBounds = RectF(
                relativeBounds.left * w,
                relativeBounds.top * h,
                relativeBounds.right * w,
                relativeBounds.bottom * h
            )
        }
        constraints = MeasureConstraints(
            absoluteBounds.width,
            absoluteBounds.height
        )
        return constraints
    }
}

private const val SHOW_DEBUG_EVENTS = false

@Suppress("NOTHING_TO_INLINE")
private inline fun debugEvent(msg: String) {
    debug(enabled = SHOW_DEBUG_EVENTS) {
        debug(content = msg)
    }
}