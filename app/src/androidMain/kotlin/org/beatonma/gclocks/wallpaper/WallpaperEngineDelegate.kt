package org.beatonma.gclocks.wallpaper

import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
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
import org.beatonma.gclocks.core.util.debug as coreDebug
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

    @VisibleForTesting
    internal val visibilityManager: VisibilityManager = VisibilityManager(
        isPreview,
        isWallpaperVisible = isWallpaperVisible,
    )
    private val layoutManager: LayoutManager = LayoutManager()
    private var frameDelayMillis: Long = (1000f / 60f).toLong()

    private val supervisorJob = SupervisorJob()
    private val engineScope: CoroutineScope = CoroutineScope(mainDispatcher + SupervisorJob(supervisorJob))
    private val visibilityScope: CoroutineScope = CoroutineScope(mainDispatcher + SupervisorJob(supervisorJob))

    private val observeSettingsJob = SingleJob(engineScope, ioDispatcher)
    private val observeStateJob = SingleJob(engineScope, mainDispatcher)
    private val stateDispatchDebouncer = Debouncer(engineScope, ioDispatcher)

    private val invalidateDebouncer = Debouncer(visibilityScope, ioDispatcher)
    private val visibilityAnimatorJob = SingleJob(visibilityScope, ioDispatcher)
    private val keyguardPollingJob = SingleJob(visibilityScope, ioDispatcher)

    init {
        observeSettings()
        observeState()
    }

    private fun observeState() {
        debug("observeState")

        observeStateJob {
            visibilityManager.state.collectLatest { state ->
                stateDispatchDebouncer(Timing.StateDebounce) {
                    debug(state.toString())
                    if (!state.isWallpaperVisible) {
                        onClearCanvas(canvasHost)
                        visibilityScope.coroutineContext.cancelChildren()
                        _animator?.setState(GlyphVisibility.Hidden, force = true, getCurrentTimeMillis())
                    } else {
                        visibilityAnimatorJob.launch {
                            _animator?.setStateWithVariance(
                                engineScope,
                                state.targetVisibility,
                                false,
                                varianceMillis = Timing.GlyphVariance,
                                getCurrentTimeMillis = getCurrentTimeMillis,
                                random = random,
                            )
                        }
                        postInvalidate()
                    }
                }
            }
        }
    }

    private fun observeSettings() {
        debug("observeSettings")

        observeSettingsJob {
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
        }
    }

    private fun postInvalidate(delayMillis: Long = 0L) {
        invalidateDebouncer(delayMillis) {
            withContext(mainDispatcher) {
                onDraw(canvasHost)
            }
        }
    }

    override fun onSurfaceChanged(width: Int, height: Int) {
        debug("onSurfaceChanged($width, $height)")
        val constraints = layoutManager.setSize(width, height)
        _animator?.setConstraints(constraints)
    }

    override fun onVisibilityChanged(isVisible: Boolean, getIsKeyguardLocked: () -> Boolean) {
        if (visibilityManager.isPreview) return visibilityManager.onVisibilityChanged(
            isWallpaperVisible = true,
            isKeyguardLocked = false
        )

        if (isVisible) {
            observeSettings()
        }

        val isKeyguardLocked: Boolean = getIsKeyguardLocked()
        debug("onVisibilityChanged($isVisible, ${isKeyguardLocked})")

        visibilityManager.onVisibilityChanged(
            isWallpaperVisible = isVisible,
            isKeyguardLocked = isKeyguardLocked
        )
        if (isVisible && isKeyguardLocked) {
            // Poll until keyguard is unlocked
            keyguardPollingJob {
                while (true) {
                    delay(Timing.KeyguardLockedPoll)
                    val isLocked = getIsKeyguardLocked()
                    if (!isLocked) {
                        visibilityManager.onVisibilityChanged(isWallpaperVisible = true, isKeyguardLocked = false)
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
        val glyph = _animator?.getGlyphAt(
            event.x - layoutManager.left,
            event.y - layoutManager.top
        )
        glyph?.setState(GlyphState.Active, currentTimeMillis = getCurrentTimeMillis())

        return glyph != null
    }

    override fun onDestroy() {
        supervisorJob.cancel()
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
    }

    private fun createAnimator(options: AnyOptions): ClockAnimator<*> {
        val animator = _animator?.let { existingAnimator ->
            if (options == previousClockOptions) existingAnimator
            else null
        } ?: createAnimatorFromOptions(options, allowVariance = true) {
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
    isWallpaperVisible: Boolean,
) {
    val state = MutableStateFlow(VisibilityState(isPreview = isPreview, isWallpaperVisible = isWallpaperVisible))

    fun onVisibilityChanged(isWallpaperVisible: Boolean, isKeyguardLocked: Boolean) {
        debug("onVisibilityChanged($isWallpaperVisible, $isKeyguardLocked)")
        state.update { previous ->
            previous.copy(isWallpaperVisible = isWallpaperVisible, isKeyguardLocked = isKeyguardLocked)
        }
    }

    fun onPageChanged(page: Int) {
        debug("onPageChanged($page)")
        state.update { previous ->
            previous.copy(currentPage = page)
        }
    }

    fun setLauncherPages(pages: List<Int>?) {
        debug("setLauncherPages($pages)")
        state.update { previous ->
            previous.copy(visibleOnPages = pages)
        }
    }
}


private open class SingleJob(private val scope: CoroutineScope, private val dispatcher: CoroutineDispatcher) {
    private var job: Job? = null

    operator fun invoke(
        block: suspend CoroutineScope.() -> Unit,
    ): Job {
        job?.cancel()
        return scope.launch(dispatcher, block = block).also { job = it }
    }

    fun launch(
        block: (CoroutineDispatcher) -> Job?,
    ): Job? {
        job?.cancel()
        return block(dispatcher).also { job = it }
    }
}

private class Debouncer(scope: CoroutineScope, dispatcher: CoroutineDispatcher) : SingleJob(scope, dispatcher) {
    inline operator fun invoke(
        debounceMillis: Long = 0L,
        crossinline block: suspend CoroutineScope.() -> Unit,
    ) {
        super.invoke {
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

private object Timing {
    /**
     * Grace period after a state change requested before it is applied.
     */
    const val StateDebounce = 250L

    /**
     * How often to poll the isKeyguardLocked function passed to onVisibilityChanged.
     */
    const val KeyguardLockedPoll = 50L

    /**
     * Maximum variance of delays applied to per-glyph visibility animations.
     */
    const val GlyphVariance = 800L
}


private const val DebuggingEnabled = true
private inline fun debug(msg: String) = coreDebug {
    if (DebuggingEnabled) {
        coreDebug(msg)
    }
}