package org.beatonma.gclocks.app

import org.beatonma.gclocks.app.settings.ContextClockOptions
import org.beatonma.gclocks.app.settings.DisplayContext
import org.beatonma.gclocks.app.settings.clocks.FormSettingsViewModel
import org.beatonma.gclocks.app.settings.clocks.Io16SettingsViewModel
import org.beatonma.gclocks.app.settings.clocks.Io18SettingsViewModel
import org.beatonma.gclocks.app.settings.clocks.SettingKey
import org.beatonma.gclocks.app.settings.clocks.chooseBackgroundColor
import org.beatonma.gclocks.app.settings.clocks.chooseClockPosition
import org.beatonma.gclocks.compose.components.settings.RichSetting
import org.beatonma.gclocks.compose.components.settings.RichSettings
import org.beatonma.gclocks.compose.components.settings.insertBefore
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.form.FormOptions
import org.beatonma.gclocks.io16.Io16Options
import org.beatonma.gclocks.io18.Io18Options


private fun filterSettings(context: DisplayContext, settings: RichSettings): RichSettings {
    return when (context) {
        DisplayContext.Widget -> settings.filter(::filterWidgetSettings)
        else -> settings
    }
}


actual fun <O : Options<*>> buildSettingsViewModel(
    initial: ContextClockOptions<O>,
    onEditClockOptions: (O) -> Unit,
    onEditDisplayOptions: (DisplayContext.Options) -> Unit,
): SettingsViewModel<O> {
    @Suppress("UNCHECKED_CAST")
    val viewmodel = when (initial.clockOptions) {
        is FormOptions -> {
            object : FormSettingsViewModel(
                initial as ContextClockOptions<FormOptions>,
                onEditClockOptions as (FormOptions) -> Unit,
                onEditDisplayOptions,
            ) {
                override fun buildDisplaySettings(
                    settings: RichSettings,
                    displayOptions: DisplayContext.Options,
                ): RichSettings {
                    return displaySettings(settings, displayOptions)
                }

                override fun filterSettings(
                    settings: RichSettings,
                    clockOptions: FormOptions,
                    displayOptions: DisplayContext.Options,
                ): RichSettings {
                    return filterSettings(
                        context,
                        super.filterSettings(settings, clockOptions, displayOptions)
                    )
                }
            }
        }

        is Io16Options -> {
            @Suppress("UNCHECKED_CAST")
            object : Io16SettingsViewModel(
                initial as ContextClockOptions<Io16Options>,
                onEditClockOptions as (Io16Options) -> Unit,
                onEditDisplayOptions,
            ) {
                override fun buildDisplaySettings(
                    settings: RichSettings,
                    displayOptions: DisplayContext.Options,
                ): RichSettings {
                    return displaySettings(settings, displayOptions)
                }

                override fun filterSettings(
                    settings: RichSettings,
                    clockOptions: Io16Options,
                    displayOptions: DisplayContext.Options,
                ): RichSettings {
                    return filterSettings(
                        context,
                        super.filterSettings(settings, clockOptions, displayOptions)
                    )
                }
            }
        }

        is Io18Options -> {
            @Suppress("UNCHECKED_CAST")
            object : Io18SettingsViewModel(
                initial as ContextClockOptions<Io18Options>,
                onEditClockOptions as (Io18Options) -> Unit,
                onEditDisplayOptions,
            ) {
                override fun buildDisplaySettings(
                    settings: RichSettings,
                    displayOptions: DisplayContext.Options,
                ): RichSettings {
                    return displaySettings(settings, displayOptions)
                }

                override fun filterSettings(
                    settings: RichSettings,
                    clockOptions: Io18Options,
                    displayOptions: DisplayContext.Options,
                ): RichSettings {
                    return filterSettings(
                        context,
                        super.filterSettings(settings, clockOptions, displayOptions)
                    )
                }
            }
        }

        else -> throw NotImplementedError("Unhandled Options class ${initial.clockOptions::class}")
    }

    @Suppress("UNCHECKED_CAST")
    return viewmodel as SettingsViewModel<O>
}


private fun <O : Options<*>> SettingsViewModel<O>.displaySettings(
    settings: RichSettings,
    displayOptions: DisplayContext.Options,
): RichSettings {
    return when (displayOptions) {
        is DisplayContext.Options.Widget -> settings

        is DisplayContext.Options.Screensaver -> {
            settings.copy(
                colors = settings.colors.insertBefore(
                    SettingKey.clockColors,
                    chooseBackgroundColor(
                        value = displayOptions.backgroundColor,
                        onUpdate = { update(displayOptions.copy(backgroundColor = it)) },
                    ),
                ),
                layout = listOf(
                    chooseClockPosition(
                        value = displayOptions.position,
                        onUpdate = { update(displayOptions.copy(position = it)) },
                    ),
                ) + settings.layout,
            )
        }

        is DisplayContext.Options.Wallpaper -> {
            settings.copy(
                colors = settings.colors.insertBefore(
                    SettingKey.clockColors,
                    chooseBackgroundColor(
                        value = displayOptions.backgroundColor,
                        onUpdate = { update(displayOptions.copy(backgroundColor = it)) },
                    ),
                ),
                layout = listOf(
                    chooseClockPosition(
                        value = displayOptions.position,
                        onUpdate = { update(displayOptions.copy(position = it)) },
                    ),
                ) + settings.layout,
            )
        }

        else -> {
            throw IllegalStateException("Unhandled displayOptions: ${displayOptions::class}")
        }
    }
}

/**
 * Remove settings/setting values which reference seconds as widget only
 * updates once a minute and never shows seconds.
 */
private fun filterWidgetSettings(setting: RichSetting<*>): RichSetting<*>? {
    return when (setting.key) {
        SettingKey.clockLayout -> {
            // Layout.Wrapped only useful for displaying seconds.
            filterSingleSelect(setting) { it != Layout.Wrapped }
        }

        SettingKey.clockTimeFormatShowSeconds,
        SettingKey.clockSecondsScale,
            -> {
            // Remove setting
            null
        }

        else -> setting
    }
}

private fun <E : Enum<E>> filterSingleSelect(
    setting: RichSetting<*>,
    filter: (E) -> Boolean,
): RichSetting.SingleSelect<E> {
    @Suppress("UNCHECKED_CAST") return (setting as RichSetting.SingleSelect<E>).copy(
        values = setting.values.filter(filter).toSet()
    )
}
