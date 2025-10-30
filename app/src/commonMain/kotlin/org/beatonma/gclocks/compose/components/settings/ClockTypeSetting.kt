package org.beatonma.gclocks.compose.components.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.beatonma.gclocks.app.data.settings.ClockType
import org.beatonma.gclocks.app.theme.colorScheme
import org.beatonma.gclocks.app.ui.resolve
import org.beatonma.gclocks.compose.components.ButtonGroup
import org.beatonma.gclocks.compose.components.settings.components.SettingLayout
import org.beatonma.gclocks.compose.components.settings.components.SettingName
import org.beatonma.gclocks.compose.components.settings.data.RichSetting

@Composable
fun ClockTypeSetting(
    setting: RichSetting.ClockType,
    modifier: Modifier = Modifier,
) {
    ClockTypeSetting(
        setting.localized.resolve(),
        setting.value,
        setting.onValueChange,
        modifier,
        helpText = setting.helpText?.resolve(),
    )
}

@Composable
fun ClockTypeSetting(
    name: String,
    selected: ClockType,
    onSelect: (ClockType) -> Unit,
    modifier: Modifier = Modifier,
    helpText: String? = null
) {
    SettingLayout(modifier, helpText) {
        SettingName(name)

        ButtonGroup(
            selected,
            { it.name.uppercase() },
            onSelect,
            ClockType.entries,
            ClockType::colorScheme
        )
    }
}
