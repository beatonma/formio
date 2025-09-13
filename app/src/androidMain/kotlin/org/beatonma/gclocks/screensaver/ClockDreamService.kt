package org.beatonma.gclocks.screensaver

import android.service.dreams.DreamService
import org.beatonma.R

/**
 * LifecycleOwner implementation lifted from androidx.lifecycle.LifecycleService
 * (which we are unable to use directly because we must extend DreamService)
 */
class ClockDreamService : DreamService() {
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isInteractive = true
        isScreenBright = true
        isFullscreen = true

        setContentView(R.layout.screeensaver)
    }
}