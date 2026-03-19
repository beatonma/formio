package org.beatonma.formio.app.ui.screens

import org.beatonma.formio.app.data.settings.DisplayContext
import org.beatonma.formio.app.data.settings.GlobalOptions
import org.beatonma.formio.compose.components.settings.data.RichSettings


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
