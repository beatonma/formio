package org.beatonma.formio.app.data.settings


actual val DefaultAppSettings: AppSettings
    get() = AppSettings(
        AppState(DisplayContext.Default),
        AppSettings.DefaultSettings,
        GlobalOptions()
    )
