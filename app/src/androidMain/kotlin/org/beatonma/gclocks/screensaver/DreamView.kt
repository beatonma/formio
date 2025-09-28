package org.beatonma.gclocks.screensaver

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import org.beatonma.gclocks.android.ClockView
import org.beatonma.gclocks.app.data.settings.DisplayContext
import org.beatonma.gclocks.app.data.settingsRepository
import org.beatonma.gclocks.core.geometry.MutableRectF

class DreamView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : FrameLayout(context, attributeSet, defStyleAttr, defStyleRes) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val relativeBounds = MutableRectF(0f, 0f, 1f, 1f)
    private val absoluteBounds = MutableRectF(0f, 0f, 144f, 144f)

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        coroutineScope.cancel()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        coroutineScope.launch {
            @OptIn(ExperimentalCoroutinesApi::class)
            context.settingsRepository.loadAppSettings()
                .mapLatest { it.getContextOptions(DisplayContext.Screensaver) }
                .collectLatest { settings ->
                    val screensaverOptions =
                        settings.displayOptions as DisplayContext.Options.Screensaver

                    setBackgroundColor(
                        screensaverOptions.backgroundColor.toRgbInt()
                    )
                    relativeBounds.set(screensaverOptions.position)
                    val w = width.toFloat()
                    val h = height.toFloat()

                    absoluteBounds.set(
                        relativeBounds.left * w,
                        relativeBounds.top * h,
                        relativeBounds.right * w,
                        relativeBounds.bottom * h
                    )

                    val layoutParams = LayoutParams(
                        absoluteBounds.width.toInt(),
                        absoluteBounds.height.toInt()
                    ).apply {
                        setMargins(
                            absoluteBounds.left.toInt(),
                            absoluteBounds.top.toInt(),
                            absoluteBounds.right.toInt(),
                            absoluteBounds.bottom.toInt()
                        )
                    }

                    val clock = ClockView(context)
                    clock.setOptions(settings.clockOptions)

                    addView(clock, layoutParams)
                }
        }
    }
}
