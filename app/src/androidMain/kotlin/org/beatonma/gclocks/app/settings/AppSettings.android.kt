package org.beatonma.gclocks.app.settings

import kotlinx.serialization.Serializable
import org.beatonma.gclocks.core.geometry.HorizontalAlignment
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.core.options.TimeFormat
import kotlin.math.floor


private val DefaultWidgetTimeFormat = TimeFormat.HH_MM_24

actual val DefaultAppSettings: AppSettings
    get() = AppSettings(
        AppState(
            DisplayContext.Widget,
            AppSettings.Clock.Form,
        ),
        AppSettings.DefaultSettings.mapValues { (key, value) ->
            when (key) {
                DisplayContext.Widget -> value.copy(
                    form = value.form.copy(
                        clock = value.form.clock.copy(
                            layout = value.form.clock.layout.copy(
                                layout = Layout.Horizontal,
                                format = DefaultWidgetTimeFormat,
                                horizontalAlignment = HorizontalAlignment.Center,
                            )
                        )
                    ),
                    io16 = value.io16.copy(
                        clock = value.io16.clock.copy(
                            layout = value.io16.clock.layout.copy(
                                layout = Layout.Horizontal,
                                format = DefaultWidgetTimeFormat,
                                horizontalAlignment = HorizontalAlignment.Center,
                            )
                        )
                    ),
                    io18 = value.io18.copy(
                        clock = value.io18.clock.copy(
                            layout = value.io18.clock.layout.copy(
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
