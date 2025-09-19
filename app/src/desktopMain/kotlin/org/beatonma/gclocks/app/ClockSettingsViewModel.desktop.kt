package org.beatonma.gclocks.app

import org.beatonma.gclocks.app.settings.ContextClockOptions
import org.beatonma.gclocks.app.settings.DisplayContext
import org.beatonma.gclocks.app.settings.clocks.FormSettingsViewModel
import org.beatonma.gclocks.app.settings.clocks.Io16SettingsViewModel
import org.beatonma.gclocks.app.settings.clocks.Io18SettingsViewModel
import org.beatonma.gclocks.compose.components.settings.RichSettings
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.form.FormOptions
import org.beatonma.gclocks.io16.Io16Options
import org.beatonma.gclocks.io18.Io18Options

actual fun <O : Options<*>> buildSettingsViewModel(
    initial: ContextClockOptions<O>,
    onEditOptions: suspend (ContextClockOptions<O>) -> Unit,
): SettingsViewModel<O> {
    @Suppress("UNCHECKED_CAST")
    return when (initial.clock) {
        is FormOptions -> {
            object : FormSettingsViewModel(
                initial as ContextClockOptions<FormOptions>,
                onEditOptions as suspend (ContextClockOptions<FormOptions>) -> Unit
            ) {
                override fun buildDisplaySettings(
                    settings: RichSettings,
                    displayOptions: DisplayContext.Options,
                ): RichSettings {
                    return settings
                }

                override fun filterSettings(settings: RichSettings): RichSettings {
                    return settings
                }
            }
        }

        is Io16Options -> {
            object : Io16SettingsViewModel(
                initial as ContextClockOptions<Io16Options>,
                onEditOptions as suspend (ContextClockOptions<Io16Options>) -> Unit
            ) {
                override fun buildDisplaySettings(
                    settings: RichSettings,
                    displayOptions: DisplayContext.Options,
                ): RichSettings {
                    return settings
                }

                override fun filterSettings(settings: RichSettings): RichSettings {
                    return settings
                }
            }
        }

        is Io18Options -> {
            object : Io18SettingsViewModel(
                initial as ContextClockOptions<Io18Options>,
                onEditOptions as suspend (ContextClockOptions<Io18Options>) -> Unit
            ) {
                override fun buildDisplaySettings(
                    settings: RichSettings,
                    displayOptions: DisplayContext.Options,
                ): RichSettings {
                    return settings
                }

                override fun filterSettings(settings: RichSettings): RichSettings {
                    return settings
                }
            }
        }

        else -> throw NotImplementedError("Unhandled Options class ${initial.clock::class}")
    } as SettingsViewModel<O>

}
