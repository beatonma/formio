package org.beatonma.gclocks.app.ui.screens

import org.beatonma.gclocks.app.data.settings.DisplayContext
import org.beatonma.gclocks.app.data.settings.GlobalOptions
import org.beatonma.gclocks.compose.components.settings.data.RichSettings


actual object DisplaySettingsProvider {
    actual fun addDisplaySettings(
        settings: RichSettings,
        displayContextOptions: DisplayContext.Options,
        updateDisplayContextOptions: (DisplayContext.Options) -> Unit,
        globalOptions: GlobalOptions,
        updateGlobalOptions: (GlobalOptions) -> Unit
    ): RichSettings {
        return defaultAddDisplaySettings(
            settings,
            displayContextOptions,
            updateDisplayContextOptions,
            globalOptions,
            updateGlobalOptions
        )
    }
}
