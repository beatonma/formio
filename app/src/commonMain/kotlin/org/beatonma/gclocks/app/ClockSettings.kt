package org.beatonma.gclocks.app

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.beatonma.gclocks.compose.components.settings.BooleanSetting
import org.beatonma.gclocks.compose.components.settings.ClockSettings
import org.beatonma.gclocks.compose.components.settings.ColorSetting
import org.beatonma.gclocks.compose.components.settings.FloatSetting
import org.beatonma.gclocks.compose.components.settings.IntegerSetting
import org.beatonma.gclocks.compose.components.settings.MultiColorSetting
import org.beatonma.gclocks.compose.components.settings.MultiSelectSetting
import org.beatonma.gclocks.compose.components.settings.OptionsAdapter
import org.beatonma.gclocks.compose.components.settings.RichSetting
import org.beatonma.gclocks.compose.components.settings.RichSettingsGroup
import org.beatonma.gclocks.compose.components.settings.SingleSelectSetting
import org.beatonma.gclocks.core.options.Options


fun <O : Options<*>> LazyStaggeredGridScope.ClockSettings(settings: ClockSettings<O>) {
    settings.richSettings.map { setting ->
        SettingOrGroup(setting, Modifier.padding(bottom = 8.dp))
    }
}

private fun LazyStaggeredGridScope.SettingOrGroup(
    item: OptionsAdapter,
    modifier: Modifier = Modifier,
) {
    when (item) {
        is RichSettingsGroup -> Group(item, modifier)
        is RichSetting<*, *> -> Setting(item, modifier)
    }
}

private fun LazyStaggeredGridScope.Group(group: RichSettingsGroup, modifier: Modifier = Modifier) {
    group.settings.forEach {
        SettingOrGroup(it, modifier)
    }
}

private fun <T : Any> LazyStaggeredGridScope.Setting(
    setting: RichSetting<T, *>,
    modifier: Modifier = Modifier,
) {
    when (setting) {
        is RichSetting.Color -> item {
            ColorSetting(
                setting,
                modifier = modifier,
            )
        }

        is RichSetting.Colors -> item {
            MultiColorSetting(setting, modifier = modifier)
        }

        is RichSetting.SingleSelect<*> -> item {
            SingleSelectSetting(setting, modifier = modifier)
        }

        is RichSetting.MultiSelect<*> -> item {
            MultiSelectSetting(setting, modifier = modifier)
        }

        is RichSetting.Bool -> item {
            BooleanSetting(setting, modifier = modifier)
        }

        is RichSetting.Int -> item { IntegerSetting(setting, modifier = modifier) }
        is RichSetting.Float -> item { FloatSetting(setting, modifier = modifier) }
    }
}
