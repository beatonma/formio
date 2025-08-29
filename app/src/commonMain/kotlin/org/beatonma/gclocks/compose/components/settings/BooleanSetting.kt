package org.beatonma.gclocks.compose.components.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import org.beatonma.gclocks.compose.components.settings.components.CheckableSettingLayout
import org.beatonma.gclocks.compose.components.settings.components.SettingName

@Composable
fun BooleanSetting(
    setting: Setting.Bool,
    modifier: Modifier = Modifier,
) {
    BooleanSetting(
        setting.localized.resolve(),
        setting.helpText?.resolve(),
        setting.value,
        setting.onValueChange,
        modifier,
    )
}

@Composable
fun BooleanSetting(
    name: String,
    helpText: String?,
    value: Boolean,
    onValueChange: (value: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    CheckableSettingLayout(
//        name,
        helpText = helpText,
        modifier = modifier.fillMaxWidth(),
        onClick = { onValueChange(!value) },
        role = Role.Switch,
        text = { SettingName(name) },
    ) {
        Switch(checked = value, onCheckedChange = { checked -> onValueChange(checked) })
    }
}
