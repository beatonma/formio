package org.beatonma.gclocks.app.settings

import kotlinx.serialization.Serializable
import org.beatonma.gclocks.core.geometry.HorizontalAlignment
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.core.options.TimeFormat
import kotlin.math.floor


private val DefaultWidgetTimeFormat = TimeFormat.HH_MM_24

actual val DefaultAppSettings: AppSettings
    get() = AppSettings(
        AppState(DisplayContext.Widget),
        AppSettings.DefaultSettings.mapValues { (key, value) ->
            when (key) {
                DisplayContext.Widget -> value.copy(
                    form = value.form.copy(
                        clockOptions = value.form.clockOptions.copy(
                            layout = value.form.clockOptions.layout.copy(
                                layout = Layout.Horizontal,
                                format = DefaultWidgetTimeFormat,
                                horizontalAlignment = HorizontalAlignment.Center,
                            )
                        )
                    ),
                    io16 = value.io16.copy(
                        clockOptions = value.io16.clockOptions.copy(
                            layout = value.io16.clockOptions.layout.copy(
                                layout = Layout.Horizontal,
                                format = DefaultWidgetTimeFormat,
                                horizontalAlignment = HorizontalAlignment.Center,
                            )
                        )
                    ),
                    io18 = value.io18.copy(
                        clockOptions = value.io18.clockOptions.copy(
                            layout = value.io18.clockOptions.layout.copy(
                                layout = Layout.Horizontal,
                                format = DefaultWidgetTimeFormat,
                                horizontalAlignment = HorizontalAlignment.Center,
                            )
                        )
                    ),
                )

                else -> value
            }
        },
    )


@Serializable
data class DisplayMetrics(private val refreshRate: Float = 0f) {
    val frameDelayMillis: Long = when {
        refreshRate <= 0f -> floor(1000f / 60f).toLong()
        else -> floor(1000f / refreshRate).toLong()
    }
}
