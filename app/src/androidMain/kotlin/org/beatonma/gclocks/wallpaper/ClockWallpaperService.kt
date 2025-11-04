package org.beatonma.gclocks.wallpaper

import android.app.KeyguardManager
import android.app.wallpaper.WallpaperDescription
import android.graphics.Canvas
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder
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
import org.beatonma.gclocks.android.AndroidCanvasHost
import org.beatonma.gclocks.android.AndroidPath
import org.beatonma.gclocks.app.data.loadDisplayMetrics
import org.beatonma.gclocks.app.data.settings.ContextClockOptions
import org.beatonma.gclocks.app.data.settings.DisplayContext
import org.beatonma.gclocks.app.data.settings.DisplayMetrics
import org.beatonma.gclocks.app.data.settingsRepository
import org.beatonma.gclocks.clocks.createAnimatorFromOptions
import org.beatonma.gclocks.core.ClockAnimator
import org.beatonma.gclocks.core.GlyphState
import org.beatonma.gclocks.core.GlyphVisibility
import org.beatonma.gclocks.core.geometry.MeasureConstraints
import org.beatonma.gclocks.core.geometry.RectF
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.core.util.debug
import kotlin.math.roundToInt


class ClockWallpaperService : WallpaperService() {
    private var engine: ClockEngine? = null

    override fun onCreateEngine(): Engine? {
        engine = ClockEngine()
        return engine
    }

    override fun onCreateEngine(description: WallpaperDescription): Engine? {
        return this.onCreateEngine()
    }

    override fun onDestroy() {
        super.onDestroy()
        engine?.onDestroy()
        engine = null
    }

    private inner class ClockEngine : Engine() {
        @OptIn(ExperimentalCoroutinesApi::class)
        private val settings: Flow<ContextClockOptions<*>> = settingsRepository
            .loadAppSettings()
            .mapLatest { it.getContextOptions(DisplayContext.LiveWallpaper) }

        private val displayMetrics: Flow<DisplayMetrics> = settingsRepository.loadDisplayMetrics()

        private val canvasHost: AndroidCanvasHost = AndroidCanvasHost()
        private val path: AndroidPath = AndroidPath()
        private var animator: ClockAnimator<*, *>? = null
        private var backgroundColor: Color = Color(0xff000000.toInt())
        private var relativeBounds: RectF = RectF(0f, 0f, 1f, 1f)
        private var absoluteBounds: RectF = RectF(0f, 0f, 0f, 0f)
        private var width: Int = 0
        private var height: Int = 0
        private var frameDelayMillis: Long = (1000f / 60f).toLong()
        private var previousClockOptions: Options<*>? = null

        private val visibilityManager = VisibilityManager()

        private val keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager

        private var engineScope: CoroutineScope = createCoroutineScope()
        private fun requireEngineScope(): CoroutineScope {
            if (!engineScope.isActive) {
                engineScope = createCoroutineScope()
            }
            return engineScope
        }

        fun initialize(visibility: GlyphVisibility? = null) {
            val scope = requireEngineScope()
            scope.launch {
                settings.collectLatest {
                    val wallpaperOptions = it.displayOptions as DisplayContext.Options.Wallpaper

                    backgroundColor = wallpaperOptions.backgroundColor
                    relativeBounds = RectF(wallpaperOptions.position)

                    animator = createAnimator(it.clockOptions).apply {
                        visibility?.let { setState(it, true) }
                    }

                    visibilityManager.update {
                        launcherPages = wallpaperOptions.zeroIndexLauncherPages.ifEmpty { null }
                    }

                    invalidate()
                }
            }

            scope.launch {
                displayMetrics.collectLatest { metrics ->
                    frameDelayMillis = metrics.frameDelayMillis
                }
            }
        }

        fun invalidate() {
            draw()
        }

        private fun postInvalidate(delayMillis: Long) {
            requireEngineScope().launch(Dispatchers.Main) {
                delay(delayMillis)
                invalidate()
            }
        }

        override fun onSurfaceChanged(
            holder: SurfaceHolder?,
            format: Int,
            width: Int,
            height: Int,
        ) {
            super.onSurfaceChanged(holder, format, width, height)

            if (this.width == height && this.height == width && width != height) {
                visibilityManager.update { isRotating = true }
            }

            this.width = width
            this.height = height

            updateConstraints()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)

            visibilityManager.update {
                isVisible = visible
                isKeyguardLocked = keyguardManager.isKeyguardLocked
            }

            if (visible) {
                deferredVisible()
            } else {
                if (!visibilityManager.state.isRotating) {
                    engineScope.cancel()
                    clear()
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
        private fun deferredVisible(): Job {
            return requireEngineScope().launch(Dispatchers.Main) {
                delay(200)
                if (isVisible) {
                    val isLocked = keyguardManager.isKeyguardLocked
                    initialize(visibility = if (isLocked) GlyphVisibility.Hidden else null)
                    visibilityManager.update {
                        isRotating = false
                        isVisible = true
                        isKeyguardLocked = isLocked
                    }

                    if (isLocked) {
                        while (true) {
                            delay(200)
                            if (!keyguardManager.isKeyguardLocked) {
                                visibilityManager.update { isKeyguardLocked = false }
                                break
                            }
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
            super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset)
            if (xOffsetStep == 0f) {
                return
            }
            val currentPosition: Float = (1f / xOffsetStep) * xOffset
            visibilityManager.update { currentLauncherPage = currentPosition.roundToInt() }
        }

        override fun onTouchEvent(event: MotionEvent) {
            animator?.getGlyphAt(
                event.x - absoluteBounds.left,
                event.y - absoluteBounds.top
            )?.setState(GlyphState.Active)

            super.onTouchEvent(event)
        }

        private fun createAnimator(options: Options<*>): ClockAnimator<*, *> {
            val constraints = updateConstraints()

            if (options == previousClockOptions) {
                animator?.let {
                    it.setConstraints(constraints)
                    return it
                }
            }

            previousClockOptions = options
            return createAnimatorFromOptions(options, path, allowVariance = true) {
                postInvalidate(frameDelayMillis)
            }.apply {
                setConstraints(constraints)
            }
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

        private fun draw() {
            val animator = animator ?: run {
                debug("draw aborted: animator is null")
                return
            }
            withCanvas { canvas ->
                canvas.fill(backgroundColor)
                animator.tick()
                canvas.withTranslation(absoluteBounds.left, absoluteBounds.top) {
                    animator.render(canvas)
                }
            }
        }

        private fun clear() {
            withCanvas { canvas ->
                canvas.fill(backgroundColor)
            }
        }

        private inline fun withCanvas(block: (org.beatonma.gclocks.core.graphics.Canvas) -> Unit) {
            var surfaceCanvas: Canvas? = null
            try {
                surfaceCanvas = surfaceHolder.lockCanvas() ?: run {
                    debug("draw aborted: unabled to lock surface canvas")
                    return
                }

                canvasHost.withCanvas(surfaceCanvas) { canvas ->
                    block(canvas)
                }
            } finally {
                if (surfaceCanvas != null) {
                    surfaceHolder.unlockCanvasAndPost(surfaceCanvas)
                }
            }
        }

        override fun onDestroy() {
            engineScope.cancel()
            super.onDestroy()
        }

        private fun createCoroutineScope() = CoroutineScope(Dispatchers.Main + SupervisorJob())

        private inner class VisibilityManager {
            var state: VisibilityState = VisibilityState(
                isRotating = false,
                isVisible = false,
                isKeyguardLocked = false,
                launcherPages = null,
                currentLauncherPage = 0
            )
                private set

            fun update(block: MutableVisibilityState.() -> Unit) {
                val newState = state.mutable().apply(block).immutable()

                if (state != newState) {
                    state = newState
                    updateVisibility()
                }
            }

            private fun updateVisibility() {
                if (isPreview) return
                if (!isVisible && !state.isRotating) {
                    return hide(force = true)
                }

                val isPageAllowed = state.launcherPages?.let { pages ->
                    state.currentLauncherPage in pages
                } ?: true

                if (!state.isKeyguardLocked && isPageAllowed) {
                    show()
                } else {
                    if (state.isRotating) return

                    hide()
                }
            }

            private fun show(force: Boolean = false) {
                animator?.setState(GlyphVisibility.Visible, force)
            }

            private fun hide(force: Boolean = false) {
                animator?.setState(GlyphVisibility.Hidden, force)
            }
        }
    }
}

private interface _VisibilityState {
    val isRotating: Boolean
    val isVisible: Boolean
    val isKeyguardLocked: Boolean
    val launcherPages: List<Int>?
    val currentLauncherPage: Int
}

private data class VisibilityState(
    override val isRotating: Boolean,
    override val isVisible: Boolean,
    override val isKeyguardLocked: Boolean,
    override val launcherPages: List<Int>?,
    override val currentLauncherPage: Int,
) : _VisibilityState {
    fun mutable() = MutableVisibilityState(
        isRotating = isRotating,
        isVisible = isVisible,
        isKeyguardLocked = isKeyguardLocked,
        launcherPages = launcherPages,
        currentLauncherPage = currentLauncherPage
    )
}

private data class MutableVisibilityState(
    override var isRotating: Boolean,
    override var isVisible: Boolean,
    override var isKeyguardLocked: Boolean,
    override var launcherPages: List<Int>?,
    override var currentLauncherPage: Int,
) : _VisibilityState {
    fun immutable() = VisibilityState(
        isRotating = isRotating,
        isVisible = isVisible,
        isKeyguardLocked = isKeyguardLocked,
        launcherPages = launcherPages,
        currentLauncherPage = currentLauncherPage
    )
}
