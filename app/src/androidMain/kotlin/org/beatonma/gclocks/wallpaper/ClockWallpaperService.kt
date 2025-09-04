package org.beatonma.gclocks.wallpaper

import android.app.wallpaper.WallpaperDescription
import android.graphics.Canvas
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import org.beatonma.gclocks.android.AndroidCanvasHost
import org.beatonma.gclocks.android.AndroidPath
import org.beatonma.gclocks.app.settings.SettingsContext
import org.beatonma.gclocks.app.settings.settingsRepository
import org.beatonma.gclocks.clocks.createAnimatorFromOptions
import org.beatonma.gclocks.core.ClockAnimator
import org.beatonma.gclocks.core.geometry.MeasureConstraints
import org.beatonma.gclocks.core.options.Options


class ClockWallpaperService : WallpaperService() {
    private var engine: ClockEngine? = null
    private val path: AndroidPath = AndroidPath()
    private val serviceSupervisor: Job = SupervisorJob()
    private val serviceScope: CoroutineScope =
        CoroutineScope(serviceSupervisor + Dispatchers.Default)

    override fun onCreateEngine(): Engine? {
        engine = ClockEngine()
        return engine
    }

    override fun onCreateEngine(description: WallpaperDescription): Engine? {
        return this.onCreateEngine()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceSupervisor.cancel()
        engine?.onDestroy()
        engine = null
    }

    private inner class ClockEngine : Engine() {
        private val settings: Flow<Options<*>> = settingsRepository
            .load()
            .mapLatest {
                it.getOptions(SettingsContext.LiveWallpaper)
            }
        private val canvasHost = AndroidCanvasHost()
        private var animator: ClockAnimator<*, *>? = null
        private val engineSupervisor: Job = SupervisorJob(serviceSupervisor)
        private val engineScope: CoroutineScope =
            CoroutineScope(engineSupervisor + Dispatchers.Default)

        private var constraints = MeasureConstraints(0f, 0f)

        init {
            engineScope.launch {
                settings.collect {
                    animator = createAnimator(it)
                    invalidate()
                }
            }
        }

        fun invalidate() {
            draw()
        }

        private fun createAnimator(options: Options<*>): ClockAnimator<*, *> {
            return createAnimatorFromOptions(options, path) { delayMillis ->
                serviceScope.launch {
                    delay(delayMillis.toLong())
                    invalidate()
                }
            }.apply { setConstraints(constraints) }
        }

        override fun onSurfaceChanged(
            holder: SurfaceHolder?,
            format: Int,
            width: Int,
            height: Int,
        ) {
            super.onSurfaceChanged(holder, format, width, height)
            constraints = MeasureConstraints(width.toFloat(), height.toFloat())
            animator?.setConstraints(constraints)
        }

        private fun draw() {
            val animator = animator ?: return
            val surface = surfaceHolder
            var _canvas: Canvas? = null
            try {
                _canvas = surface.lockCanvas() ?: return

                canvasHost.withCanvas(_canvas) { canvas ->
                    animator.tick()
                    animator.render(canvas)
                }
            } finally {
                surface.unlockCanvasAndPost(_canvas)
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            engineSupervisor.cancel()
        }
    }
}