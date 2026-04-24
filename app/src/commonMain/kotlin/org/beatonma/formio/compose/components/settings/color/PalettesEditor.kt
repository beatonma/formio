package org.beatonma.formio.compose.components.settings.color

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import formio.app.generated.resources.Res
import formio.app.generated.resources.setting_color_palettes_current_palette_label
import formio.app.generated.resources.setting_color_palettes_delete_palette_cd
import formio.app.generated.resources.setting_color_palettes_restore_default_palettes
import formio.app.generated.resources.setting_color_palettes_save_palette_cd
import formio.app.generated.resources.setting_color_palettes_saved_palettes_label
import org.beatonma.formio.app.data.settings.ClockColors
import org.beatonma.formio.compose.AppIcon
import org.beatonma.formio.compose.components.Button
import org.beatonma.formio.compose.components.settings.components.CheckableSettingLayout
import org.beatonma.formio.core.util.fastForEach
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ColorPalettesEditor(
    currentPalette: ClockColors,
    palettes: List<ClockColors>,
    onSelect: (ClockColors) -> Unit,
    onSave: (ClockColors) -> Unit,
    onDelete: (ClockColors) -> Unit,
    onRestoreDefaults: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        val isCurrentPaletteSaved = currentPalette in palettes

        Text(stringResource(Res.string.setting_color_palettes_current_palette_label), style = typography.labelSmall)
        ColorPalette(
            currentPalette,
            onSelect = { onSelect(currentPalette) },
            onSave = if (isCurrentPaletteSaved) null else {
                { onSave(currentPalette) }
            },
            onDelete = if (isCurrentPaletteSaved) {
                { onDelete(currentPalette) }
            } else null
        )

        HorizontalDivider(Modifier.padding(top = 8.dp, bottom = 16.dp))

        Text(stringResource(Res.string.setting_color_palettes_saved_palettes_label), style = typography.labelSmall)
        palettes.fastForEach { palette ->
            if (palette == currentPalette) return@fastForEach
            ColorPalette(
                palette,
                onSelect = { onSelect(palette) },
                onSave = null,
                onDelete = { onDelete(palette) }
            )
        }

        if (palettes.isEmpty()) {
            Button(
                AppIcon.Reset,
                stringResource(Res.string.setting_color_palettes_restore_default_palettes),
                Modifier.align(Alignment.CenterHorizontally).padding(16.dp),
                onClick = onRestoreDefaults
            )
        }
    }
}

@Composable
private fun ColorPalette(
    palette: ClockColors,
    onSelect: () -> Unit,
    onSave: (() -> Unit)?,
    onDelete: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    CheckableSettingLayout(
        onSelect,
        Role.RadioButton,
        modifier,
        text = {
            palette.background?.let {
                ColorPreview(it, 32.dp)
                Spacer(Modifier.width(4.dp))
            }
            for (color in palette.colors) {
                ColorPreview(color, 32.dp)
            }
        },
        checkable = {
            onSave?.let { onSave ->
                IconButton(onSave) {
                    Icon(AppIcon.Save, stringResource(Res.string.setting_color_palettes_save_palette_cd))
                }
            }

            onDelete?.let { onDelete ->
                IconButton(onDelete) {
                    Icon(AppIcon.Delete, stringResource(Res.string.setting_color_palettes_delete_palette_cd))
                }
            }
        }
    )
}
