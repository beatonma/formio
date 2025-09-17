package org.beatonma.gclocks.wallpaper

import android.app.wallpaper.WallpaperDescription
import android.graphics.Canvas
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.beatonma.gclocks.android.AndroidCanvasHost
import org.beatonma.gclocks.android.AndroidPath
import org.beatonma.gclocks.app.settings.ContextClockOptions
import org.beatonma.gclocks.app.settings.DisplayContext
import org.beatonma.gclocks.app.settings.settingsRepository
import org.beatonma.gclocks.clocks.createAnimatorFromOptions
import org.beatonma.gclocks.core.ClockAnimator
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
        private var engineScope: CoroutineScope? = null

        @OptIn(ExperimentalCoroutinesApi::class)
        private val settings: Flow<ContextClockOptions<*>> = settingsRepository
            .load()
            .mapLatest { it.getOptions(DisplayContext.LiveWallpaper) }

        private val canvasHost = AndroidCanvasHost()
        private val path: AndroidPath = AndroidPath()
        private var animator: ClockAnimator<*, *>? = null
        private var backgroundColor: Int = 0xff000000.toInt()
        private val relativeBounds: MutableRectF = MutableRectF(0f, 0f, 1f, 1f)
        private val absoluteBounds: MutableRectF = MutableRectF(0f, 0f, 0f, 0f)
        private var width = 0
        private var height = 0

        init {
            initialize()
        }

        fun initialize() {
            if (engineScope?.isActive != true) {
                engineScope = createCoroutineScope()
            }
            engineScope?.launch {
                settings.collectLatest {
                    val wallpaperOptions = it.display as DisplayContext.Options.Wallpaper

                    backgroundColor = wallpaperOptions.backgroundColor.toRgbInt()
                    relativeBounds.set(wallpaperOptions.position)

                    animator = createAnimator(it.clock)
                    invalidate()
                }
            } ?: debug("Failed to load settings: engineScope is null")
        }

        fun invalidate() {
            draw()
        }

        private fun postInvalidate(delayMillis: Int) {
            engineScope?.launch(Dispatchers.Main) {
                // TODO delay(delayMillis.toLong())
                invalidate()
            } ?: debug("postInvalidate failed: engineScope is null")
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

            when (visible) {
                true -> {
                    initialize()
                }

                false -> engineScope?.cancel()
            }
        }

        private fun createAnimator(options: Options<*>): ClockAnimator<*, *> {
            val constraints = updateConstraints()
            return createAnimatorFromOptions(options, path, onScheduleNextFrame = ::postInvalidate)
                .apply {
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
            engineScope?.cancel()
            super.onDestroy()
        }

        private fun createCoroutineScope() = CoroutineScope(Dispatchers.Main + SupervisorJob())
    }
}
