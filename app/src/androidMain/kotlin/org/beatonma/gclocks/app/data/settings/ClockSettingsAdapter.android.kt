package org.beatonma.gclocks.app.data.settings

import org.beatonma.gclocks.compose.components.settings.data.RichSetting
import org.beatonma.gclocks.compose.components.settings.data.RichSettings
import org.beatonma.gclocks.core.options.AnyOptions
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.form.FormOptions
import org.beatonma.gclocks.io16.Io16Options
import org.beatonma.gclocks.io18.Io18Options


actual fun <O : AnyOptions> buildClockSettingsAdapter(clock: ClockType): ClockSettingsAdapter<O> {
    @Suppress("UNCHECKED_CAST")
    return when (clock) {
        ClockType.Form -> object : AndroidClockSettingsAdapter<FormOptions>,
            FormClockSettingsAdapter {}

        ClockType.Io16 -> object : AndroidClockSettingsAdapter<Io16Options>,
            Io16ClockSettingsAdapter {}

        ClockType.Io18 -> object : AndroidClockSettingsAdapter<Io18Options>,
            Io18ClockSettingsAdapter {}
    } as ClockSettingsAdapter<O>
}

private interface AndroidClockSettingsAdapter<O : AnyOptions> :
    ClockSettingsAdapter<O> {
    override fun filterRichSettings(
        richSettings: RichSettings,
        options: O,
        displayContext: DisplayContext
    ): RichSettings {
        val superFiltered = super.filterRichSettings(richSettings, options, displayContext)
        return filterSettings(displayContext, superFiltered)
    }
}

private fun filterSettings(context: DisplayContext, settings: RichSettings): RichSettings {
    return when (context) {
        DisplayContext.Widget -> settings.filter(::filterWidgetSettings)
        else -> settings
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
