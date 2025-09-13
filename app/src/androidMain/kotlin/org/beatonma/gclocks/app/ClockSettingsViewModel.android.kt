package org.beatonma.gclocks.app

import org.beatonma.gclocks.app.settings.ContextClockOptions
import org.beatonma.gclocks.app.settings.DisplayContext
import org.beatonma.gclocks.app.settings.clocks.CommonKeys
import org.beatonma.gclocks.app.settings.clocks.FormSettingsViewModel
import org.beatonma.gclocks.app.settings.clocks.Io16SettingsViewModel
import org.beatonma.gclocks.app.settings.clocks.Io18SettingsViewModel
import org.beatonma.gclocks.app.settings.clocks.chooseBackgroundColor
import org.beatonma.gclocks.app.settings.clocks.chooseClockPosition
import org.beatonma.gclocks.compose.components.settings.RichSetting
import org.beatonma.gclocks.compose.components.settings.Settings
import org.beatonma.gclocks.compose.components.settings.forEachSetting
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.core.options.TimeFormat
import org.beatonma.gclocks.core.options.TimeResolution
import org.beatonma.gclocks.form.FormOptions
import org.beatonma.gclocks.io16.Io16Options
import org.beatonma.gclocks.io18.Io18Options


private fun filterSettings(context: DisplayContext, settings: List<Settings>): List<Settings> {
    return when (context) {
        DisplayContext.Widget -> filterWidgetSettings(settings)
        else -> settings
    }
}


actual fun <O : Options<*>> buildSettingsViewModel(
    initial: ContextClockOptions<O>,
    onEditOptions: suspend (ContextClockOptions<O>) -> Unit,
): SettingsViewModel<O> {
    val viewmodel = when (initial.clock) {
        is FormOptions -> {
            @Suppress("UNCHECKED_CAST")
            object : FormSettingsViewModel(
                initial as ContextClockOptions<FormOptions>,
                onEditOptions as suspend (ContextClockOptions<FormOptions>) -> Unit
            ) {
                override fun buildDisplaySettings(displayOptions: DisplayContext.Options): List<Settings> {
                    return displaySettings(displayOptions)
                }

                override fun filterSettings(settings: List<Settings>): List<Settings> {
                    return filterSettings(context, settings)
                }
            }
        }

        is Io16Options -> {
            @Suppress("UNCHECKED_CAST")
            object : Io16SettingsViewModel(
                initial as ContextClockOptions<Io16Options>,
                onEditOptions as suspend (ContextClockOptions<Io16Options>) -> Unit
            ) {
                override fun buildDisplaySettings(displayOptions: DisplayContext.Options): List<Settings> {
                    return displaySettings(displayOptions)
                }

                override fun filterSettings(settings: List<Settings>): List<Settings> {
                    return filterSettings(context, settings)
                }
            }
        }

        is Io18Options -> {
            @Suppress("UNCHECKED_CAST")
            object : Io18SettingsViewModel(
                initial as ContextClockOptions<Io18Options>,
                onEditOptions as suspend (ContextClockOptions<Io18Options>) -> Unit
            ) {
                override fun buildDisplaySettings(displayOptions: DisplayContext.Options): List<Settings> {
                    return displaySettings(displayOptions)
                }

                override fun filterSettings(settings: List<Settings>): List<Settings> {
                    return filterSettings(context, settings)
                }
            }
        }

        else -> throw NotImplementedError("Unhandled Options class ${initial.clock::class}")
    }

    @Suppress("UNCHECKED_CAST")
    return viewmodel as SettingsViewModel<O>
}


private fun <O : Options<*>> SettingsViewModel<O>.displaySettings(displayOptions: DisplayContext.Options): List<Settings> {
    return when (displayOptions) {
        is DisplayContext.Options.Widget -> listOf()

        is DisplayContext.Options.Screensaver -> listOf(
            chooseClockPosition(
                value = displayOptions.position,
                onUpdate = { update(displayOptions.copy(position = it)) }
            ),
            chooseBackgroundColor(
                value = displayOptions.backgroundColor,
                onUpdate = { update(displayOptions.copy(backgroundColor = it)) }
            ),
        )

        is DisplayContext.Options.Wallpaper -> listOf(
            chooseClockPosition(
                value = displayOptions.position,
                onUpdate = { update(displayOptions.copy(position = it)) }
            ),
            chooseBackgroundColor(
                value = displayOptions.backgroundColor,
                onUpdate = { update(displayOptions.copy(backgroundColor = it)) }
            )
        )
    }
}

/**
 * Widget only updates each minute so never shows seconds. Here we remove
 * settings/setting values which reference seconds to avoid confusion.
 */
private fun filterWidgetSettings(settings: List<Settings>): List<Settings> {
    val out = mutableListOf<Settings>()

    settings.forEachSetting { setting ->
        when (setting.key) {
            CommonKeys.clockLayout -> {
                out.add(
                    filterSingleSelect(setting) { it != Layout.Wrapped }
                )
            }

            CommonKeys.clockTimeFormat -> {
                // Remove TimeFormat values which have second resolution.
                out.add(
                    filterSingleSelect<TimeFormat>(setting) { it.resolution == TimeResolution.Minutes }
                )
            }

            CommonKeys.clockSecondsScale -> {
                // Remove clockSecondsScale setting
            }

            else -> out.add(setting)
        }
    }

    return out.toList()
}

private fun <E : Enum<E>> filterSingleSelect(
    setting: RichSetting<*>,
    filter: (E) -> Boolean,
): RichSetting.SingleSelect<E> {
    @Suppress("UNCHECKED_CAST")
    return (setting as RichSetting.SingleSelect<E>).copy(
        values = setting.values.filter(filter).toSet()
    )
}