package org.beatonma.gclocks.wallpaper

import android.app.KeyguardManager
import android.app.wallpaper.WallpaperDescription
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder
import org.beatonma.gclocks.android.AndroidCanvasHost
import org.beatonma.gclocks.app.data.settingsRepository
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.util.debug
import android.graphics.Canvas as AndroidCanvas


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
        private val keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
        private lateinit var delegate: WallpaperEngineDelegate

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            delegate = WallpaperEngineDelegate(
                this,
                settingsRepository,
                onDraw = {
                    withCanvasHost(it, delegate::draw)
                },
                onClearCanvas = {
                    withCanvasHost(it, delegate::clear)
                }
            )
        }

        private inline fun withCanvasHost(canvasHost: AndroidCanvasHost, block: (Canvas) -> Unit) {
            var surfaceCanvas: AndroidCanvas? = null
            try {
                surfaceCanvas = surfaceHolder.lockCanvas() ?: run {
                    debug("draw aborted: unabled to lock surface canvas")
                    return
                }
                canvasHost.withCanvas(surfaceCanvas, block)
            } finally {
                if (surfaceCanvas != null) {
                    surfaceHolder.unlockCanvasAndPost(surfaceCanvas)
                }
            }
        }

        override fun onSurfaceChanged(
            holder: SurfaceHolder?,
            format: Int,
            width: Int,
            height: Int
        ) {
            super.onSurfaceChanged(holder, format, width, height)
            delegate.onSurfaceChanged(width, height)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            delegate.onVisibilityChanged(visible) { keyguardManager.isKeyguardLocked }
        }

        override fun onOffsetsChanged(
            xOffset: Float,
            yOffset: Float,
            xOffsetStep: Float,
            yOffsetStep: Float,
            xPixelOffset: Int,
            yPixelOffset: Int
        ) {
            super.onOffsetsChanged(
                xOffset,
                yOffset,
                xOffsetStep,
                yOffsetStep,
                xPixelOffset,
                yPixelOffset
            )
            delegate.onOffsetsChanged(
                xOffset,
                yOffset,
                xOffsetStep,
                yOffsetStep,
                xPixelOffset,
                yPixelOffset
            )
        }

        override fun onTouchEvent(event: MotionEvent) {
            val consumed = delegate.onTouchEvent(event)
            if (!consumed) {
                super.onTouchEvent(event)
            }
        }

        override fun onDestroy() {
            delegate.onDestroy()
            super.onDestroy()
        }
    }
}
