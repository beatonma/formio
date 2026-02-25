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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.isActive
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
import kotlin.coroutines.CoroutineContext
import kotlin.math.roundToInt


interface WallpaperEngineDelegate {
    fun onSurfaceChanged(width: Int, height: Int)
    fun onVisibilityChanged(visible: Boolean, getIsKeyguardLocked: () -> Boolean)
    fun onOffsetsChanged(
        xOffset: Float,
        yOffset: Float,
        xOffsetStep: Float,
        yOffsetStep: Float,
        xPixelOffset: Int,
        yPixelOffset: Int,
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
    private val isPreview: Boolean,
    isWallpaperVisible: Boolean,
    private val displayMetrics: Flow<DisplayMetrics>,
    private val wallpaperSettings: Flow<DisplayContext.Options.Wallpaper>,
    private val clockSettings: Flow<AnyOptions>,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.io,
    private val onDraw: (AndroidCanvasHost) -> Unit,
    private val onClearCanvas: (AndroidCanvasHost) -> Unit,
    private val getCurrentTimeMillis: () -> Long = ::getCurrentTimeMillis,
) : WallpaperEngineDelegate {
    private val canvasHost: AndroidCanvasHost = AndroidCanvasHost()
    var animator: ClockAnimator<*>? = null
        private set
    private var backgroundColor: Color = Color(0xff000000.toInt())
    private var relativeBounds: RectF = RectF(0f, 0f, 1f, 1f)
    private var absoluteBounds: RectF = RectF(0f, 0f, 0f, 0f)
    private var width: Int = 0
    private var height: Int = 0
    private var frameDelayMillis: Long = (1000f / 60f).toLong()
    private var previousClockOptions: AnyOptions? = null

    internal val visibilityManager = VisibilityManager(isWallpaperVisible)

    private var engineScope: CoroutineScope = createCoroutineScope()
    private fun requireEngineScope(): CoroutineScope {
        if (!engineScope.isActive) {
            engineScope = createCoroutineScope()
        }
        return engineScope
    }

    private val deferredVisibilityDebouncer = Debouncer(ioDispatcher)

    fun initialize() {
        val scope = requireEngineScope()

        scope.launch(ioDispatcher) {
            clockSettings.collectLatest { clock ->
                animator = createAnimator(clock).apply {
                    setState(GlyphVisibility.Hidden, true, getCurrentTimeMillis())
                }
                // Ensure visibility state is applied to animator
                visibilityManager.applyStateNow()
                invalidate()
            }
        }

        scope.launch(ioDispatcher) {
            wallpaperSettings.collectLatest { wallpaper ->
                backgroundColor = wallpaper.backgroundColor
                relativeBounds = RectF(wallpaper.position)
                updateConstraints()
                visibilityManager.update {
                    visibleOnLauncherPages = wallpaper.zeroIndexLauncherPages.ifEmpty { null }
                }
                invalidate()
            }
        }

        scope.launch(ioDispatcher) {
            displayMetrics.collectLatest { metrics ->
                frameDelayMillis = metrics.frameDelayMillis
            }
        }
    }

    suspend fun invalidate() {
        withContext(mainDispatcher) {
            onDraw(canvasHost)
        }
    }

    private fun postInvalidate(delayMillis: Long) {
        requireEngineScope().launch(mainDispatcher) {
            delay(delayMillis)
            invalidate()
        }
    }

    override fun onSurfaceChanged(width: Int, height: Int) {
        if (this.width == height && this.height == width && width != height) {
            visibilityManager.startRotation()
        }

        this.width = width
        this.height = height

        updateConstraints()
    }

    override fun onVisibilityChanged(visible: Boolean, getIsKeyguardLocked: () -> Boolean) {
        visibilityManager.update {
            isWallpaperVisible = visible
            isKeyguardLocked = getIsKeyguardLocked()
        }

        if (visible) {
            deferredVisible(getIsKeyguardLocked)
        } else {
            if (!visibilityManager.isRotating) {
                cancelScope()
            }
        }
    }

    /**
     * During device rotation, [onVisibilityChanged] can be called many times
     * and reacting to each of those events results in jarring animations being
     * cancelled a moment after they start.
     *
     * By calling [deferredVisible] we allow a grace period for this to happen,
     * and only make the animation visible once that period has passed
     * without further [onVisibilityChanged] calls.
     */
    private fun deferredVisible(getIsKeyguardLocked: () -> Boolean) {
        deferredVisibilityDebouncer.debounce(200) {
            if (!visibilityManager.state.isWallpaperVisible) return@debounce

            if (animator == null || !visibilityManager.isRotating) {
                initialize()
            } else {
                invalidate()
            }
            val isLocked = getIsKeyguardLocked()
            visibilityManager.update {
                isWallpaperVisible = true
                isKeyguardLocked = isLocked
            }

            if (isLocked) {
                while (true) {
                    delay(200)
                    if (!getIsKeyguardLocked()) {
                        visibilityManager.update { isKeyguardLocked = false }
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
        visibilityManager.update { currentLauncherPage = currentPosition.roundToInt() }
    }

    /** @return true if the event is consumed, false otherwise */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val glyph = animator?.getGlyphAt(
            event.x - absoluteBounds.left,
            event.y - absoluteBounds.top
        )
        glyph?.setState(GlyphState.Active, currentTimeMillis = getCurrentTimeMillis())

        return glyph != null
    }

    private fun createAnimator(options: AnyOptions): ClockAnimator<*> {
        val constraints = updateConstraints()

        val animator = animator?.let {
            if (options == previousClockOptions) it
            else {
                previousClockOptions = options
                null
            }
        } ?: createAnimatorFromOptions(options, canvasHost.path, allowVariance = true) {
            postInvalidate(frameDelayMillis)
        }
        animator.setConstraints(constraints)
        return animator
    }

    private fun updateConstraints(): MeasureConstraints {
        val w = width.toFloat()
        val h = height.toFloat()

        absoluteBounds = RectF(
            relativeBounds.left * w,
            relativeBounds.top * h,
            relativeBounds.right * w,
            relativeBounds.bottom * h
        )
        val constraints = MeasureConstraints(
            absoluteBounds.width,
            absoluteBounds.height
        )

        animator?.setConstraints(constraints)
        return constraints
    }

    override fun draw(canvas: Canvas) {
        canvas.fill(backgroundColor)
        animator?.let { animator ->
            animator.tick()
            canvas.withTranslation(absoluteBounds.left, absoluteBounds.top) {
                animator.render(canvas)
            }
        }
    }

    override fun clear(canvas: Canvas) {
        canvas.fill(backgroundColor)
    }

    override fun onDestroy() {
        cancelScope()
    }

    private fun createCoroutineScope() = CoroutineScope(mainDispatcher + SupervisorJob())
    private fun cancelScope() {
        visibilityManager.forceHide()
        engineScope.cancel()
    }

    internal inner class VisibilityManager(isWallpaperVisible: Boolean) {
        private val debouncer = Debouncer(ioDispatcher)

        // May need further tuning - if this is too short, state changes can
        // be incorrectly triggered during device rotation (which calls
        // onVisibilityChanged many times); if too long, changes
        // may feel sluggish. Probably device-dependent.
        private val debounceDuration = 300L

        internal var state: VisibilityState = VisibilityState(
            isWallpaperVisible = isWallpaperVisible,
            isKeyguardLocked = false,
            visibleOnLauncherPages = null,
            currentLauncherPage = 0
        )
            private set
        var isRotating: Boolean = false
            private set
        private var appliedState: VisibilityState? = null

        fun startRotation() {
            isRotating = true
            applyState()
        }

        fun update(block: MutableVisibilityState.() -> Unit) {
            val newState = state.mutable().apply(block).immutable()

            state = newState
            applyState()
        }

        private fun applyState() {
            debouncer.debounce(debounceDuration) {
                withContext(mainDispatcher) {
                    updateVisibility()
                }
            }
        }

        fun applyStateNow() {
            isRotating = false
            val state = state
            appliedState = state
            if (shouldBeVisible(state)) {
                show()
            } else {
                hide(force = !state.isWallpaperVisible)
            }
        }

        fun forceHide() {
            hide(force = true)
            appliedState = null
            onClearCanvas(canvasHost)
        }

        fun shouldBeVisible(state: VisibilityState): Boolean {
            if (isPreview) return true

            return state.shouldBeVisible
        }

        private fun updateVisibility() {
            isRotating = false
            val state = state
            if (state == appliedState) {
                return debug("updateVisibility: state has not changed")
            }
            applyStateNow()
        }

        private fun show(force: Boolean = false) {
            animator?.setState(
                GlyphVisibility.Visible,
                force,
                currentTimeMillis = getCurrentTimeMillis()
            )
        }

        private fun hide(force: Boolean = false) {
            animator?.setState(
                GlyphVisibility.Hidden,
                force,
                currentTimeMillis = getCurrentTimeMillis()
            )
        }
    }

    /**
     * Wallpaper events can be fired in quick succession so reacting to each one
     * immediately can cause glitchy animations. By using [debounce],
     * we allow a grace period for events to fire, and only apply the update
     * once that period has passed without further events. As a result, only
     * the latest requested update will actually be applied.
     */
    private inner class Debouncer(private val dispatcher: CoroutineContext) {
        private var job: Job? = null

        fun debounce(gracePeriodMillis: Long = 200L, block: suspend () -> Unit) {
            job?.cancel()
            job = requireEngineScope().launch(dispatcher) {
                delay(gracePeriodMillis)
                block()
            }
        }
    }
}


internal interface _VisibilityState {
    val isWallpaperVisible: Boolean
    val isKeyguardLocked: Boolean

    val visibleOnLauncherPages: List<Int>?
    val currentLauncherPage: Int

    val isPageAllowed: Boolean
        get() = visibleOnLauncherPages?.let { currentLauncherPage in it } ?: true

    val shouldBeVisible: Boolean
        get() {
            if (isKeyguardLocked) return false
            if (!isPageAllowed) return false
            return isWallpaperVisible
        }
}

internal data class VisibilityState(
    override val isWallpaperVisible: Boolean,
    override val isKeyguardLocked: Boolean,
    override val visibleOnLauncherPages: List<Int>?,
    override val currentLauncherPage: Int,
) : _VisibilityState {
    fun mutable() = MutableVisibilityState(
        isWallpaperVisible = isWallpaperVisible,
        isKeyguardLocked = isKeyguardLocked,
        visibleOnLauncherPages = visibleOnLauncherPages,
        currentLauncherPage = currentLauncherPage
    )
}

internal data class MutableVisibilityState(
    override var isWallpaperVisible: Boolean,
    override var isKeyguardLocked: Boolean,
    override var visibleOnLauncherPages: List<Int>?,
    override var currentLauncherPage: Int,
) : _VisibilityState {
    fun immutable() = VisibilityState(
        isWallpaperVisible = isWallpaperVisible,
        isKeyguardLocked = isKeyguardLocked,
        visibleOnLauncherPages = visibleOnLauncherPages,
        currentLauncherPage = currentLauncherPage
    )
}
