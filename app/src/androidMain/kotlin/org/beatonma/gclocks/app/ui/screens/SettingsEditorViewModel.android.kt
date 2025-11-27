package org.beatonma.gclocks.app.ui.screens

import gclocks_multiplatform.app.generated.resources.Res
import gclocks_multiplatform.app.generated.resources.setting_help_lwp_launcher_pages
import gclocks_multiplatform.app.generated.resources.setting_lwp_launcher_pages
import gclocks_multiplatform.app.generated.resources.setting_lwp_launcher_pages_all
import gclocks_multiplatform.app.generated.resources.setting_placeholder_lwp_launcher_pages
import org.beatonma.gclocks.app.data.settings.DisplayContext
import org.beatonma.gclocks.app.data.settings.GlobalOptions
import org.beatonma.gclocks.app.data.settings.SettingKey
import org.beatonma.gclocks.app.data.settings.chooseClockColors
import org.beatonma.gclocks.app.data.settings.chooseClockPosition
import org.beatonma.gclocks.compose.components.settings.data.Key
import org.beatonma.gclocks.compose.components.settings.data.RichSetting
import org.beatonma.gclocks.compose.components.settings.data.RichSettings
import org.beatonma.gclocks.compose.components.settings.data.ValidationFailed
import org.beatonma.gclocks.compose.components.settings.data.replace


actual object DisplaySettingsProvider {
    actual fun addDisplaySettings(
        settings: RichSettings,
        displayContextOptions: DisplayContext.Options,
        updateDisplayContextOptions: (DisplayContext.Options) -> Unit,
        globalOptions: GlobalOptions,
        updateGlobalOptions: (GlobalOptions) -> Unit,
    ): RichSettings {
        return when (displayContextOptions) {
            is DisplayContext.Options.Widget -> settings

            is DisplayContext.Options.Screensaver -> settings.copy(
                colors = settings.colors.replace(
                    SettingKey.clockColors,
                    { previous ->
                        previous as RichSetting.ClockColors
                        chooseClockColors(
                            value = previous.value.copy(background = displayContextOptions.backgroundColor),
                            onValueChange = { updated ->
                                updated.background?.let {
                                    updateDisplayContextOptions(
                                        displayContextOptions.copy(
                                            backgroundColor = it
                                        )
                                    )
                                }
                                previous.onValueChange(updated)
                            },
                            palettes = globalOptions.colorPalettes,
                            onUpdatePalettes = {
                                updateGlobalOptions(
                                    globalOptions.copy(
                                        colorPalettes = it
                                    )
                                )
                            })
                    },
                ),
                layout = listOf(
                    chooseClockPosition(displayContextOptions.position) {
                        updateDisplayContextOptions(displayContextOptions.copy(position = it))
                    },
                ) + settings.layout,
            )

            is DisplayContext.Options.Wallpaper -> settings.copy(
                colors = settings.colors.replace(SettingKey.clockColors) { previous ->
                    previous as RichSetting.ClockColors
                    chooseClockColors(
                        value = previous.value.copy(background = displayContextOptions.backgroundColor),
                        onValueChange = { updated ->
                            updated.background?.let {
                                updateDisplayContextOptions(
                                    displayContextOptions.copy(
                                        backgroundColor = it
                                    )
                                )
                            }
                            previous.onValueChange(updated)
                        },
                        palettes = globalOptions.colorPalettes,
                        onUpdatePalettes = { updateGlobalOptions(globalOptions.copy(colorPalettes = it)) }
                    )
                },
                layout = listOf(
                    chooseClockPosition(displayContextOptions.position) {
                        updateDisplayContextOptions(displayContextOptions.copy(position = it))
                    },
                    chooseLwpLauncherPages(displayContextOptions.visibleOnLauncherPages) {
                        updateDisplayContextOptions(
                            displayContextOptions.copy(
                                visibleOnLauncherPages = it
                            )
                        )
                    }
                ) + settings.layout,
            )

            else -> defaultAddDisplaySettings(
                settings,
                displayContextOptions,
                updateDisplayContextOptions,
                globalOptions,
                updateGlobalOptions
            )
        }
    }
}


private val LauncherPagesKey = Key.IntList("lwp_launcher_pages")
private fun chooseLwpLauncherPages(value: List<Int>, onUpdate: (List<Int>) -> Unit) =
    RichSetting.IntList(
        key = LauncherPagesKey,
        name = Res.string.setting_lwp_launcher_pages,
        helpText = Res.string.setting_help_lwp_launcher_pages,
        placeholder = Res.string.setting_placeholder_lwp_launcher_pages,
        defaultValueDescription = Res.string.setting_lwp_launcher_pages_all,
        value = value,
        onValueChange = onUpdate,
        validator = { page ->
            if (page < 1) {
                throw ValidationFailed("Launcher pages must be positive integers")
            }
        }
    )
