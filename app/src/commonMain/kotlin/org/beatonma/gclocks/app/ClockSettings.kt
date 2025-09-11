package org.beatonma.gclocks.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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


fun LazyStaggeredGridScope.ClockSettingsItems(
    settings: List<Settings>,
    groupModifier: Modifier = Modifier,
    itemModifier: Modifier = Modifier,
) {
    settings.forEach { setting ->
        SettingOrGroupItems(setting, groupModifier, itemModifier)
    }
}


private fun LazyStaggeredGridScope.SettingOrGroupItems(
    item: Settings,
    groupModifier: Modifier,
    itemModifier: Modifier,
) {
    when (item) {
        is RichSettingsGroup -> GroupItems(item, groupModifier, itemModifier)
        is RichSetting<*> -> item { Setting(item, itemModifier) }
    }
}

@Composable
fun SettingOrGroup(
    item: Settings,
    groupModifier: Modifier = Modifier,
    itemModifier: Modifier = Modifier,
) {
    when (item) {
        is RichSettingsGroup -> Group(item, groupModifier, itemModifier)
        is RichSetting<*> -> Setting(item, itemModifier)
    }
}

private fun LazyStaggeredGridScope.GroupItems(
    group: RichSettingsGroup, groupModifier: Modifier, itemModifier: Modifier,
) {
    item {
        Group(group, groupModifier, itemModifier)
    }
}

@Composable
private fun Group(group: RichSettingsGroup, groupModifier: Modifier, itemModifier: Modifier) {
    Column(groupModifier) {
        group.settings.forEach { SettingOrGroup(it, groupModifier, itemModifier) }
    }
}

@Composable
private fun Setting(
    setting: RichSetting<*>,
    modifier: Modifier,
) {
    when (setting) {
        is RichSetting.Color -> ColorSetting(setting, modifier)
        is RichSetting.Colors -> MultiColorSetting(setting, modifier)
        is RichSetting.SingleSelect<*> -> SingleSelectSetting(setting, modifier)
        is RichSetting.MultiSelect<*> -> MultiSelectSetting(setting, modifier)
        is RichSetting.Bool -> BooleanSetting(setting, modifier)
        is RichSetting.Int -> IntegerSetting(setting, modifier)
        is RichSetting.Float -> FloatSetting(setting, modifier)
    }
}
