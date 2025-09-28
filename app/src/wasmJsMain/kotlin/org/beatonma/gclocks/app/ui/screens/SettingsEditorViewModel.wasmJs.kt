package org.beatonma.gclocks.app.ui.screens

import org.beatonma.gclocks.app.data.settings.DisplayContext
import org.beatonma.gclocks.compose.components.settings.data.RichSettings

actual fun addDisplaySettings(
    settings: RichSettings,
    options: DisplayContext.Options,
    update: (DisplayContext.Options) -> Unit
): RichSettings {
    return defaultAddDisplaySettings(settings, options, update)
}
