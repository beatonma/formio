package org.beatonma.gclocks.screensaver

import android.service.dreams.DreamService
import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.beatonma.R
import org.beatonma.gclocks.android.ClockView
import org.beatonma.gclocks.app.settings.ContextClockOptions
import org.beatonma.gclocks.app.settings.DisplayContext
import org.beatonma.gclocks.app.settings.settingsRepository

/**
 * LifecycleOwner implementation lifted from androidx.lifecycle.LifecycleService
 * (which we are unable to use directly because we must extend DreamService)
 */
class ClockDreamService : DreamService() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isInteractive = true
        isScreenBright = true
        isFullscreen = true

        coroutineScope.launch {
            settingsRepository.load().collectLatest { settings ->
                val options = settings.getOptions(DisplayContext.Screensaver)
                setContentView(options)
            }
        }
    }

    private fun setContentView(options: ContextClockOptions<*>) {
        setContentView(R.layout.screeensaver)

        findViewById<View>(R.id.background).setBackgroundColor(
            (options.display as DisplayContext.Options.Screensaver)
                .backgroundColor.toRgbInt()
        )
        findViewById<ClockView>(R.id.clock).options = options.clock
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        coroutineScope.cancel()
    }
}