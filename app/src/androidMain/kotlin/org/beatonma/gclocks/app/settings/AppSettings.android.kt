package org.beatonma.gclocks.app.settings


actual enum class SettingsContext {
    Widget,
    LiveWallpaper,
    Screensaver,
    ;
}


actual val DefaultAppSettings: AppSettings
    get() = AppSettings(
        AppState(
            SettingsContext.Widget,
            AppSettings.Clock.Form,
        ),
        AppSettings.DefaultSettings,
    )