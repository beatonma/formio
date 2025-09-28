package org.beatonma.gclocks.app.ui.screens

import org.beatonma.gclocks.app.data.settings.DisplayContext
import org.beatonma.gclocks.app.data.settings.clocks.SettingKey
import org.beatonma.gclocks.app.data.settings.clocks.chooseBackgroundColor
import org.beatonma.gclocks.app.data.settings.clocks.chooseClockPosition
import org.beatonma.gclocks.compose.components.settings.data.RichSettings
import org.beatonma.gclocks.compose.components.settings.data.insertBefore


actual fun addDisplaySettings(
    settings: RichSettings,
    options: DisplayContext.Options,
    update: (DisplayContext.Options) -> Unit,
): RichSettings {
    return when (options) {
        is DisplayContext.Options.Widget -> settings

        is DisplayContext.Options.Screensaver -> settings.copy(
            colors = settings.colors.insertBefore(
                SettingKey.clockColors,
                chooseBackgroundColor(
                    value = options.backgroundColor,
                    onUpdate = { update(options.copy(backgroundColor = it)) },
                ),
            ),
            layout = listOf(
                chooseClockPosition(
                    value = options.position,
                    onUpdate = { update(options.copy(position = it)) },
                ),
            ) + settings.layout,
        )

        is DisplayContext.Options.Wallpaper -> settings.copy(
            colors = settings.colors.insertBefore(
                SettingKey.clockColors,
                chooseBackgroundColor(
                    value = options.backgroundColor,
                    onUpdate = { update(options.copy(backgroundColor = it)) },
                ),
            ),
            layout = listOf(
                chooseClockPosition(
                    value = options.position,
                    onUpdate = { update(options.copy(position = it)) },
                ),
            ) + settings.layout,
        )

        else -> defaultAddDisplaySettings(settings, options, update)
    }
}
