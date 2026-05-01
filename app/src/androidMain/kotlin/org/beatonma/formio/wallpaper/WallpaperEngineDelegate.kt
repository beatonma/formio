package org.beatonma.formio.wallpaper

import android.app.WallpaperColors
import android.os.Build
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
import org.beatonma.formio.android.AndroidCanvasHost
import org.beatonma.formio.android.toAndroidColor
import org.beatonma.formio.app.data.AppSettingsRepository
import org.beatonma.formio.app.data.loadDisplayMetrics
import org.beatonma.formio.app.data.loadWallpaperSettings
import org.beatonma.formio.app.data.settings.DisplayContext
import org.beatonma.formio.app.data.settings.DisplayMetrics
import org.beatonma.formio.app.io
import org.beatonma.formio.clocks.createAnimatorFromOptions
import org.beatonma.formio.core.ClockAnimator
import org.beatonma.formio.core.geometry.MeasureConstraints
import org.beatonma.formio.core.geometry.RectF
import org.beatonma.formio.core.glyph.GlyphState
import org.beatonma.formio.core.glyph.GlyphVisibility
import org.beatonma.formio.core.graphics.Canvas
import org.beatonma.formio.core.graphics.Color
import org.beatonma.formio.core.graphics.luminance
import org.beatonma.formio.core.options.AnyOptions
import org.beatonma.formio.core.util.getCurrentTimeMillis
import org.jetbrains.annotations.VisibleForTesting
import kotlin.math.roundToInt
import kotlin.random.Random
import org.beatonma.formio.core.util.debug as coreDebug


interface WallpaperEngineDelegate {
    fun onSurfaceChanged(width: Int, height: Int)

    fun onVisibilityChanged(isVisible: Boolean, getIsKeyguardLocked: () -> Boolean)
    fun onOffsetsChanged(
        xOffset: Float, yOffset: Float,
        xOffsetStep: Float, yOffsetStep: Float,
        xPixelOffset: Int, yPixelOffset: Int,
    )

    fun onZoomChanged(zoom: Float)

    fun onTouchEvent(event: MotionEvent): Boolean

    fun onComputeColors(): WallpaperColors?
    fun notifyColorsChanged()

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
    onNotifyColorsChanged: () -> Unit,
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
    onNotifyColorsChanged = onNotifyColorsChanged,
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
    private val onNotifyColorsChanged: () -> Unit,
    private val getCurrentTimeMillis: () -> Long = ::getCurrentTimeMillis,
    private val random: Random = Random.Default,
) : WallpaperEngineDelegate {
    private val canvasHost: AndroidCanvasHost = AndroidCanvasHost()
    private var _animator: ClockAnimator<*>? = null
    private val animator: ClockAnimator<*> get() = _animator!!
    private var previousClockOptions: AnyOptions? = null
    private var backgroundColor: Color = Color(0xff000000.toInt())
    private var colors: List<Color>? = null

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
                colors = clock.paints.colors.toList()
            }

            wallpaperSettings.first().let { wallpaper ->
                backgroundColor = wallpaper.backgroundColor
                val constraints = layoutManager.setBounds(wallpaper.position)
                updateConstraints(constraints)
                visibilityManager.setLauncherPages(wallpaper.zeroIndexLauncherPages.ifEmpty { null })
            }

            notifyColorsChanged()
            postInvalidate()
        }
    }

    private fun updateConstraints(constraints: MeasureConstraints) {
        _animator?.setConstraints(constraints)
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
        val constraints = layoutManager.setAvailableSize(width, height)
        updateConstraints(constraints)
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

    override fun onZoomChanged(zoom: Float) {
        debug("onZoomChanged: $zoom")
        layoutManager.setZoom(zoom)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val glyph = _animator?.getGlyphAt(
            event.x - layoutManager.left,
            event.y - layoutManager.top
        )
        glyph?.setState(GlyphState.Active, currentTimeMillis = getCurrentTimeMillis())

        return glyph != null
    }

    override fun onComputeColors(): WallpaperColors? {
        val colors = colors ?: return null
        if (colors.size < 3) return null
        val (primary, secondary, tertiary) = colors.map { it.toAndroidColor() }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val hints: Int = if (backgroundColor.luminance() > 0.5f) WallpaperColors.HINT_SUPPORTS_DARK_TEXT else 0

            return WallpaperColors(primary, secondary, tertiary, hints)
        }

        return WallpaperColors(primary, secondary, tertiary)
    }

    override fun notifyColorsChanged() {
        onNotifyColorsChanged()
    }

    override fun onDestroy() {
        supervisorJob.cancel()
    }

    override fun draw(canvas: Canvas) {
        canvas.fill(backgroundColor)
        animator.tick()
        layoutManager.withTransform(canvas, animator::render)
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
        updateConstraints(layoutManager.constraints)
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
    companion object {
        private val MaxZoomOut = 0.15f
    }

    private var availableWidth: Int = 0
    private var availableHeight: Int = 0
    private var relativeBounds: RectF = RectF.Invalid
    private var absoluteBounds: RectF = RectF(0f, 0f, 0f, 0f)
    val top get() = absoluteBounds.top
    val left get() = absoluteBounds.left

    private var scale: Float = 1f
    private var scalePivotX: Float = top
    private var scalePivotY: Float = left

    var constraints: MeasureConstraints = MeasureConstraints(0f, 0f)
        private set

    fun setAvailableSize(width: Int, height: Int): MeasureConstraints {
        availableWidth = width
        availableHeight = height

        scalePivotX = width / 2f
        scalePivotY = height / 2f

        return updateConstraints()
    }

    fun setBounds(bounds: RectF): MeasureConstraints {
        relativeBounds = RectF(bounds)
        return updateConstraints()
    }

    /**
     * Somewhat unintuitively, WallpaperServiceEngine reports zoom as a value between 0..1 where
     * - 0 means "fully zoomed in" (apparently the default state)
     * - 1 means "fully zoomed out"
     */
    fun setZoom(zoom: Float) {
        scale = 1f - (zoom * MaxZoomOut)
    }

    fun withTransform(canvas: Canvas, block: (Canvas) -> Unit) {
        canvas.withScale(scale, scale, scalePivotX, scalePivotY) {
            canvas.withTranslation(left, top, block)
        }
    }

    private fun updateConstraints(): MeasureConstraints {
        val w = availableWidth.toFloat()
        val h = availableHeight.toFloat()

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