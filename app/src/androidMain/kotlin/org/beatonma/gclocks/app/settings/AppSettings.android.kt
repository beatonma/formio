package org.beatonma.gclocks.app.settings

import kotlinx.serialization.Serializable
import kotlin.math.floor


actual val DefaultAppSettings: AppSettings
    get() = AppSettings(
        AppState(
            DisplayContext.Widget,
            AppSettings.Clock.Form,
        ),
        AppSettings.DefaultSettings,
    )


@Serializable
data class DisplayMetrics(private val refreshRate: Float = 0f) {
    val frameDelayMillis: Long = when {
        refreshRate <= 0f -> floor(1000f / 60f).toLong()
        else -> floor(1000f / refreshRate).toLong()
    }
}
