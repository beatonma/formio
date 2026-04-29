package org.beatonma.formio.app.ui.screens.settings.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import org.beatonma.formio.app.data.settings.ClockColors
import org.beatonma.formio.app.data.settings.RichSetting
import org.beatonma.formio.app.ui.resolve
import org.beatonma.formio.app.ui.screens.settings.components.color.ColorPreview
import org.beatonma.formio.app.ui.screens.settings.components.color.ColorsEditor


@Composable
fun ClockColorsSetting(
    setting: RichSetting.ClockColors,
    modifier: Modifier = Modifier,
) {
    ClockColorsSetting(
        setting.key,
        setting.name.resolve(),
        setting.helpText?.resolve(),
        setting.value,
        onValueChange = setting.onValueChange,
        palettes = setting.palettes,
        onUpdatePalettes = setting.onUpdatePalettes,
        modifier = modifier,
    )
}

@Composable
fun ClockColorsSetting(
    key: Any,
    name: String,
    helpText: String?,
    colors: ClockColors,
    onValueChange: (ClockColors) -> Unit,
    palettes: List<ClockColors>,
    onUpdatePalettes: (List<ClockColors>) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isEditorOpen by rememberSaveable(key) { mutableStateOf(false) }

    CheckableSettingLayout(
        onClick = { isEditorOpen = true },
        role = Role.Button,
        modifier = modifier,
        helpText = helpText,
        text = { Text(name) },
        checkable = {
            for (color in colors.allColors) {
                ColorPreview(color)
            }
        },
    )

    ColorsEditor(
        isEditorOpen,
        { isEditorOpen = false },
        colors,
        onValueChange,
        palettes,
        { onUpdatePalettes(it.distinct()) }
    )
}
