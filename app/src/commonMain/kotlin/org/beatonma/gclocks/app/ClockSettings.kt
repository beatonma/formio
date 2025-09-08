package org.beatonma.gclocks.app

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.beatonma.gclocks.compose.components.settings.BooleanSetting
import org.beatonma.gclocks.compose.components.settings.ColorSetting
import org.beatonma.gclocks.compose.components.settings.FloatSetting
import org.beatonma.gclocks.compose.components.settings.IntegerSetting
import org.beatonma.gclocks.compose.components.settings.MultiColorSetting
import org.beatonma.gclocks.compose.components.settings.MultiSelectSetting
import org.beatonma.gclocks.compose.components.settings.RichSetting
import org.beatonma.gclocks.compose.components.settings.RichSettingsGroup
import org.beatonma.gclocks.compose.components.settings.Settings
import org.beatonma.gclocks.compose.components.settings.SingleSelectSetting


fun LazyListScope.ClockSettingsItems(settings: List<Settings>) {
    settings.map { setting ->
        SettingOrGroupItems(setting, Modifier.padding(bottom = 8.dp))
    }
}

@Composable
fun ClockSettingsItems(settings: List<Settings>) {
    settings.map { setting ->
        SettingOrGroup(setting, Modifier.padding(bottom = 8.dp))
    }
}


private fun LazyListScope.SettingOrGroupItems(
    item: Settings,
    modifier: Modifier = Modifier,
) {
    when (item) {
        is RichSettingsGroup -> GroupItems(item, modifier)
        is RichSetting<*> -> item { Setting(item, modifier) }
    }
}

@Composable
fun SettingOrGroup(item: Settings, modifier: Modifier = Modifier) {
    when (item) {
        is RichSettingsGroup -> Group(item, modifier)
        is RichSetting<*> -> Setting(item, modifier)
    }
}

private fun LazyListScope.GroupItems(
    group: RichSettingsGroup,
    modifier: Modifier = Modifier,
) {
    group.settings.forEach {
        SettingOrGroupItems(it, modifier)
    }
}

@Composable
private fun Group(group: RichSettingsGroup, modifier: Modifier = Modifier) {
    group.settings.forEach { SettingOrGroup(it, modifier) }
}

@Composable
private fun <T : Any> Setting(
    setting: RichSetting<T>,
    modifier: Modifier = Modifier,
) {
    when (setting) {
        is RichSetting.Color -> ColorSetting(setting, modifier = modifier)
        is RichSetting.Colors -> MultiColorSetting(setting, modifier = modifier)
        is RichSetting.SingleSelect<*> -> SingleSelectSetting(setting, modifier = modifier)
        is RichSetting.MultiSelect<*> -> MultiSelectSetting(setting, modifier = modifier)
        is RichSetting.Bool -> BooleanSetting(setting, modifier = modifier)
        is RichSetting.Int -> IntegerSetting(setting, modifier = modifier)
        is RichSetting.Float -> FloatSetting(setting, modifier = modifier)
    }
}
