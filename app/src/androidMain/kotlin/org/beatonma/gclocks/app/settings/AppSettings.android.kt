package org.beatonma.gclocks.app.settings


actual val DefaultAppSettings: AppSettings
    get() = AppSettings(
        AppState(
            DisplayContext.Widget,
            AppSettings.Clock.Form,
        ),
        AppSettings.DefaultSettings,
    )