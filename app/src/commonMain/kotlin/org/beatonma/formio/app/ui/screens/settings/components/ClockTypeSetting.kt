package org.beatonma.formio.app.ui.screens.settings.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.beatonma.formio.app.data.settings.ClockType
import org.beatonma.formio.app.data.settings.RichSetting
import org.beatonma.formio.app.ui.components.ButtonGroup
import org.beatonma.formio.app.ui.resolve
import org.beatonma.formio.app.ui.theme.colorScheme

@Composable
fun ClockTypeSetting(
    setting: RichSetting.ClockType,
    modifier: Modifier = Modifier,
) {
    ClockTypeSetting(
        setting.name.resolve(),
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
    helpText: String? = null,
) {
    SettingLayout(
        modifier.padding(bottom = SettingTokens.SettingHorizontalPadding),
        helpText
    ) {
        SettingName(name)

        ButtonGroup(
            selected,
            onSelect,
            { it.name.uppercase() },
            ClockType.entries,
            ClockType::colorScheme
        )
    }
}
