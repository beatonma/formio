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
import org.beatonma.gclocks.core.geometry.MutableRectF
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.core.util.debug


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
        private var engineScope: CoroutineScope = createCoroutineScope()

        @OptIn(ExperimentalCoroutinesApi::class)
        private val settings: Flow<ContextClockOptions<*>> = settingsRepository
            .loadAppSettings()
            .mapLatest { it.getContextOptions(DisplayContext.LiveWallpaper) }

        private val displayMetrics: Flow<DisplayMetrics> = settingsRepository.loadDisplayMetrics()

        private val canvasHost: AndroidCanvasHost = AndroidCanvasHost()
        private val path: AndroidPath = AndroidPath()
        private var animator: ClockAnimator<*, *>? = null
        private var backgroundColor: Int = 0xff000000.toInt()
        private val relativeBounds: MutableRectF = MutableRectF(0f, 0f, 1f, 1f)
        private val absoluteBounds: MutableRectF = MutableRectF(0f, 0f, 0f, 0f)
        private var width: Int = 0
        private var height: Int = 0
        private var frameDelayMillis: Long = (1000f / 60f).toLong()

        private val keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager

        init {
            initialize()
        }

        fun initialize(
            state: GlyphState = GlyphState.Activating,
            visibility: GlyphVisibility = GlyphVisibility.Appearing
        ) {
            if (!engineScope.isActive) {
                engineScope = createCoroutineScope()
            }

            engineScope.launch {
                settings.collectLatest {
                    val wallpaperOptions = it.displayOptions as DisplayContext.Options.Wallpaper

                    backgroundColor = wallpaperOptions.backgroundColor.toRgbInt()
                    relativeBounds.set(wallpaperOptions.position)

                    animator = createAnimator(it.clockOptions).apply {
                        setState(state, true)
                    }
                    invalidate()
                }
            }

            engineScope.launch {
                displayMetrics.collectLatest { metrics ->
                    frameDelayMillis = metrics.frameDelayMillis
                }
            }
        }

        fun invalidate() {
            draw()
        }

        private fun postInvalidate(delayMillis: Long) {
            engineScope.launch(Dispatchers.Main) {
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
            this.width = width
            this.height = height

            updateConstraints()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)

            val isLocked = keyguardManager.isKeyguardLocked

            if (isVisible) {
                initialize(visibility = if (isLocked) GlyphVisibility.Hidden else GlyphVisibility.Appearing)
                if (isLocked) {
                    engineScope.launch(Dispatchers.Main) {
                        // Poll keyguard lock state and animate visibility when unlocked
                        while (true) {
                            delay(500)
                            if (!keyguardManager.isKeyguardLocked) {
                                animator?.setState(GlyphVisibility.Appearing, false)
                                break
                            }
                        }
                    }
                }
            } else {
                engineScope.cancel()
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
            super.onOffsetsChanged(
                xOffset,
                yOffset,
                xOffsetStep,
                yOffsetStep,
                xPixelOffset,
                yPixelOffset
            )
            // TODO hide/show depending on launcher page.
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
            return createAnimatorFromOptions(options, path, allowVariance = true) {
                postInvalidate(frameDelayMillis)
            }.apply {
                setConstraints(constraints)
            }
        }

        private fun updateConstraints(): MeasureConstraints {
            val w = width.toFloat()
            val h = height.toFloat()

            absoluteBounds.set(
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
            var surfaceCanvas: Canvas? = null
            try {
                surfaceCanvas = surfaceHolder.lockCanvas() ?: run {
                    debug("draw aborted: unabled to lock surface canvas")
                    return
                }

                canvasHost.withCanvas(surfaceCanvas) { canvas ->
                    canvas.fill(Color(backgroundColor))
                    animator.tick()
                    canvas.withTranslation(absoluteBounds.left, absoluteBounds.top) {
                        animator.render(canvas)
                    }
                }
            } finally {
                if (surfaceCanvas != null) {
                    surfaceHolder.unlockCanvasAndPost(surfaceCanvas)
                }
            }
        }

        override fun onDestroy() {
            path.beginPath()
            engineScope.cancel()
            super.onDestroy()
        }

        private fun createCoroutineScope() = CoroutineScope(Dispatchers.Main + SupervisorJob())
    }
}
