package org.beatonma.gclocks.app

import org.beatonma.gclocks.app.settings.ContextClockOptions
import org.beatonma.gclocks.app.settings.DisplayContext
import org.beatonma.gclocks.app.settings.clocks.CommonKeys
import org.beatonma.gclocks.app.settings.clocks.FormSettingsViewModel
import org.beatonma.gclocks.app.settings.clocks.Io16SettingsViewModel
import org.beatonma.gclocks.app.settings.clocks.Io18SettingsViewModel
import org.beatonma.gclocks.compose.components.settings.Key
import org.beatonma.gclocks.compose.components.settings.RichSetting
import org.beatonma.gclocks.compose.components.settings.Settings
import org.beatonma.gclocks.compose.components.settings.forEachSetting
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
    return when (initial.clock) {
        is FormOptions -> {
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
    } as SettingsViewModel<O>
}

private val BackgroundColorKey = Key.ColorKey("background_color")
private fun <O : Options<*>> SettingsViewModel<O>.displaySettings(displayOptions: DisplayContext.Options): List<Settings> {
    return when (displayOptions) {
        is DisplayContext.Options.Widget -> listOf()

        is DisplayContext.Options.Screensaver -> listOf(
            RichSetting.Color(
                key = BackgroundColorKey,
                localized = LocalizedString(literal = "Background color"),
                value = (contextOptions.value.display as DisplayContext.Options.Screensaver).backgroundColor,
                onValueChange = {
                    update(
                        (contextOptions.value.display as DisplayContext.Options.Screensaver).copy(
                            backgroundColor = it
                        )
                    )
                }
            )
        )

        is DisplayContext.Options.Wallpaper -> listOf(
            RichSetting.Color(
                key = BackgroundColorKey,
                localized = LocalizedString(literal = "Background color"),
                value = (contextOptions.value.display as DisplayContext.Options.Wallpaper).backgroundColor,
                onValueChange = {
                    update(
                        (contextOptions.value.display as DisplayContext.Options.Wallpaper).copy(
                            backgroundColor = it
                        )
                    )
                }
            ))
    }
}

private fun filterWidgetSettings(settings: List<Settings>): List<Settings> {
    val out = mutableListOf<Settings>()

    settings.forEachSetting { setting ->
        when (setting.key) {
            CommonKeys.clockTimeFormat -> {
                @Suppress("UNCHECKED_CAST")
                out.add(
                    (setting as RichSetting.SingleSelect<TimeFormat>)
                        .copy(values = setting.values.filter { it.resolution == TimeResolution.Minutes }
                            .toSet())
                )
            }

            else -> out.add(setting)
        }
    }

    return out.toList()
}