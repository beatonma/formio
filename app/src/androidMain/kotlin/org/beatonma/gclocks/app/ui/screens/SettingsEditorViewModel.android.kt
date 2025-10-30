package org.beatonma.gclocks.app.ui.screens

import gclocks_multiplatform.app.generated.resources.Res
import gclocks_multiplatform.app.generated.resources.setting_help_lwp_launcher_pages
import gclocks_multiplatform.app.generated.resources.setting_lwp_launcher_pages
import gclocks_multiplatform.app.generated.resources.setting_placeholder_lwp_launcher_pages
import org.beatonma.gclocks.app.data.settings.DisplayContext
import org.beatonma.gclocks.app.data.settings.clocks.SettingKey
import org.beatonma.gclocks.app.data.settings.clocks.chooseClockColors
import org.beatonma.gclocks.app.data.settings.clocks.chooseClockPosition
import org.beatonma.gclocks.compose.components.settings.data.Key
import org.beatonma.gclocks.compose.components.settings.data.RichSetting
import org.beatonma.gclocks.compose.components.settings.data.RichSettings
import org.beatonma.gclocks.compose.components.settings.data.ValidationFailed
import org.beatonma.gclocks.compose.components.settings.data.replace


actual fun addDisplaySettings(
    settings: RichSettings,
    options: DisplayContext.Options,
    update: (DisplayContext.Options) -> Unit,
): RichSettings {
    return when (options) {
        is DisplayContext.Options.Widget -> settings

        is DisplayContext.Options.Screensaver -> settings.copy(
            colors = settings.colors.replace(
                SettingKey.clockColors,
                { previous ->
                    previous as RichSetting.ClockColors
                    chooseClockColors(
                        options.backgroundColor,
                        previous.value.colors,
                        { updated ->
                            updated.background?.let {
                                update(options.copy(backgroundColor = it))
                            }
                            previous.onValueChange(updated)
                        }
                    )
                },
            ),
            layout = listOf(
                chooseClockPosition(
                    value = options.position,
                    onUpdate = { update(options.copy(position = it)) },
                ),
            ) + settings.layout,
        )

        is DisplayContext.Options.Wallpaper -> settings.copy(
            colors = settings.colors.replace(
                SettingKey.clockColors,
                { previous ->
                    previous as RichSetting.ClockColors
                    chooseClockColors(
                        options.backgroundColor,
                        previous.value.colors,
                        { updated ->
                            updated.background?.let {
                                update(options.copy(backgroundColor = it))
                            }
                            previous.onValueChange(updated)
                        }
                    )
                },
            ),
            layout = listOf(
                chooseClockPosition(
                    value = options.position,
                    onUpdate = { update(options.copy(position = it)) },
                ),
                chooseLwpLauncherPages(
                    value = options.launcherPages,
                    onUpdate = { update(options.copy(launcherPages = it)) }
                )
            ) + settings.layout,
        )

        else -> defaultAddDisplaySettings(settings, options, update)
    }
}


private val LauncherPagesKey = Key.IntList("lwp_launcher_pages")
private fun chooseLwpLauncherPages(value: List<Int>, onUpdate: (List<Int>) -> Unit) = RichSetting.IntList(
    key = LauncherPagesKey,
    localized = LocalizedString(Res.string.setting_lwp_launcher_pages),
    helpText = LocalizedString(Res.string.setting_help_lwp_launcher_pages),
    placeholder = LocalizedString(Res.string.setting_placeholder_lwp_launcher_pages),
    localized = Res.string.setting_lwp_launcher_pages,
    helpText = Res.string.setting_help_lwp_launcher_pages,
    placeholder = Res.string.setting_placeholder_lwp_launcher_pages,
    value = value,
    onValueChange = onUpdate,
    validator = { page ->
        if (page < 1) {
            throw ValidationFailed("Launcher pages must be positive integers")
        }
    }
)
