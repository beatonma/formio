package org.beatonma.gclocks.app.data.settings

import org.beatonma.gclocks.app.data.settings.clocks.FormClockSettingsAdapter
import org.beatonma.gclocks.app.data.settings.clocks.Io16ClockSettingsAdapter
import org.beatonma.gclocks.app.data.settings.clocks.Io18ClockSettingsAdapter
import org.beatonma.gclocks.app.data.settings.clocks.SettingKey
import org.beatonma.gclocks.compose.components.settings.data.RichSettings
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.core.options.Options


interface ClockSettingsAdapter<O : Options<*>> {
    fun addClockSettings(richSettings: RichSettings, options: O, updateOptions: (O) -> Unit): RichSettings

    fun filterRichSettings(richSettings: RichSettings, options: O, displayContext: DisplayContext): RichSettings {
        return richSettings.filter { setting ->
            when (setting.key) {
                SettingKey.clockVerticalAlignment -> {
                    // Vertical alignment only affects Layout.Horizontal
                    when (options.layout.layout) {
                        Layout.Horizontal -> setting
                        else -> null
                    }
                }

                SettingKey.clockSecondsScale -> {
                    // No need to edit second scale if they are not visible
                    when (options.layout.format.showSeconds) {
                        true -> setting
                        false -> null
                    }
                }

                else -> setting
            }
        }
    }
}

expect fun buildClockSettingsAdapter(clock: ClockType): ClockSettingsAdapter<*>

internal fun defaultBuildClockSettingsAdapter(clock: ClockType): ClockSettingsAdapter<*> {
    return when (clock) {
        ClockType.Form -> object : FormClockSettingsAdapter {}
        ClockType.Io16 -> object : Io16ClockSettingsAdapter {}
        ClockType.Io18 -> object : Io18ClockSettingsAdapter {}
    }
}
