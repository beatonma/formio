package org.beatonma.formio.app.ui.screens.settings.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import org.beatonma.formio.app.data.settings.RichSetting
import org.beatonma.formio.app.ui.resolve

@Composable
fun BooleanSetting(
    setting: RichSetting.Bool,
    modifier: Modifier = Modifier,
) {
    BooleanSetting(
        setting.name.resolve(),
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
        helpText = helpText,
        modifier = modifier.fillMaxWidth(),
        onClick = { onValueChange(!value) },
        role = Role.Switch,
        text = { SettingName(name) },
    ) {
        Switch(checked = value, onCheckedChange = { checked -> onValueChange(checked) })
    }
}
