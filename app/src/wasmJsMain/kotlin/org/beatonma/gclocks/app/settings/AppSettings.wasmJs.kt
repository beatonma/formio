package org.beatonma.gclocks.app.settings

actual val DefaultAppSettings: AppSettings
    get() = AppSettings(
        AppState(DisplayContext.Default),
        AppSettings.DefaultSettings,
    )
