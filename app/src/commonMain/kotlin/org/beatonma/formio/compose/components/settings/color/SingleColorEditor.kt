package org.beatonma.formio.compose.components.settings.color

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import formio.app.generated.resources.Res
import formio.app.generated.resources.setting_color_cd_selected
import formio.app.generated.resources.setting_color_label_hsl_hue_initial
import formio.app.generated.resources.setting_color_label_hsl_lightness_initial
import formio.app.generated.resources.setting_color_label_hsl_saturation_initial
import formio.app.generated.resources.setting_color_label_rgb_blue_initial
import formio.app.generated.resources.setting_color_label_rgb_green_initial
import formio.app.generated.resources.setting_color_label_rgb_red_initial
import org.beatonma.formio.app.ui.Localization.stringResourceMap
import org.beatonma.formio.app.ui.resolve
import org.beatonma.formio.compose.AppIcon
import org.beatonma.formio.compose.LoadingSpinner
import org.beatonma.formio.compose.components.ButtonGroup
import org.beatonma.formio.compose.components.ButtonGroupSize
import org.beatonma.formio.compose.components.Column
import org.beatonma.formio.compose.components.settings.components.LabelledSlider
import org.beatonma.formio.compose.toCompose
import org.beatonma.formio.core.graphics.Color
import org.beatonma.formio.core.graphics.toColor
import org.beatonma.formio.core.graphics.withBlue
import org.beatonma.formio.core.graphics.withGreen
import org.beatonma.formio.core.graphics.withHue
import org.beatonma.formio.core.graphics.withLightness
import org.beatonma.formio.core.graphics.withRed
import org.beatonma.formio.core.graphics.withSaturation
import org.beatonma.formio.core.util.fastForEachIndexed
import org.jetbrains.compose.resources.stringResource


enum class ColorEditorMode {
    Samples,
    HSL,
    RGB,
    HEX,
    ;
}

@Composable
fun SingleColorEditor(
    color: Color,
    onColorChanged: (Color) -> Unit,
    modifier: Modifier = Modifier,
) {
    var mode by remember { mutableStateOf(ColorEditorMode.entries.first()) }
    val localizedModes = remember { ColorEditorMode::class.stringResourceMap }
    LazyColumn(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Column.LargeSpacingArrangement,
    ) {
        item {
            ButtonGroup(
                mode,
                { mode = it },
                { localizedModes[it]?.resolve() ?: it.name },
                ColorEditorMode.entries,
                size = ButtonGroupSize.Small
            )
        }
        item {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                when (mode) {
                    ColorEditorMode.Samples -> SampleColors(color, onColorChanged)
                    ColorEditorMode.HSL -> HslComponents(color, onColorChanged)
                    ColorEditorMode.RGB -> RgbComponents(color, onColorChanged)
                    ColorEditorMode.HEX -> HexEditor(color, onColorChanged)
                }
            }
        }
    }
}

@Composable
private fun SampleColors(
    value: Color,
    onValueChange: (Color) -> Unit,
    modifier: Modifier = Modifier,
) {
    val swatch = rememberMaterialColorSwatch() ?: return LoadingSpinner()
    val spacing = 8.dp

    FlowRow(
        modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalArrangement = Arrangement.spacedBy(spacing),
        maxItemsInEachRow = 5,
    ) {
        swatch.fastForEachIndexed { index, color ->
            ColorPatch(
                color = color.toCompose(),
                onClick = { onValueChange(color) },
                content = if (color == value) {
                    {
                        Icon(
                            AppIcon.Checkmark,
                            contentDescription = stringResource(Res.string.setting_color_cd_selected),
                        )
                    }
                } else null,
            )
        }
    }
}

@Composable
private fun HexEditor(
    color: Color,
    onColorChange: (Color) -> Unit,
    modifier: Modifier = Modifier,
) {
    var text by rememberSaveable(color) { mutableStateOf(color.toStringRgb()) }

    TextField(
        text,
        {
            if (!it.matches(Regex("[\\da-fA-F]*"))) return@TextField
            text = it
            if (it.length != 6) return@TextField

            try {
                onColorChange(it.toColor())
            } catch (e: NumberFormatException) {
                // Invalid color -> don't change anything
            }
        },
        modifier,
        placeholder = { Text("123abc", color = LocalContentColor.current.copy(alpha = 0.72f)) },
        prefix = { Text("#") },
        maxLines = 1,
        isError = text.length != 6
    )
}

@Composable
private fun HslComponents(
    color: Color,
    onColorChange: (Color) -> Unit,
    modifier: Modifier = Modifier,
) {
    val (hue, saturation, lightness) = color.hsl()

    ColorComponents(modifier) {
        ColorComponent(
            hue,
            { onColorChange(color.withHue(it)) },
            stringResource(Res.string.setting_color_label_hsl_hue_initial),
            0f,
            360f,
            modifier,
        )
        ColorComponent(
            saturation,
            { onColorChange(color.withSaturation(it)) },
            stringResource(Res.string.setting_color_label_hsl_saturation_initial),
            0f,
            1f,
            modifier,
        )
        ColorComponent(
            lightness,
            { onColorChange(color.withLightness(it)) },
            stringResource(Res.string.setting_color_label_hsl_lightness_initial),
            0f,
            1f,
            modifier,
        )
    }
}

@Composable
private fun RgbComponents(
    color: Color,
    onColorChange: (Color) -> Unit,
    modifier: Modifier = Modifier,
) {
    ColorComponents(modifier) {
        ColorComponent(
            color.red,
            { onColorChange(color.withRed(it)) },
            stringResource(Res.string.setting_color_label_rgb_red_initial),
            0,
            255,
            modifier,
        )
        ColorComponent(
            color.green,
            { onColorChange(color.withGreen(it)) },
            stringResource(Res.string.setting_color_label_rgb_green_initial),
            0,
            255,
            modifier,
        )
        ColorComponent(
            color.blue,
            { onColorChange(color.withBlue(it)) },
            stringResource(Res.string.setting_color_label_rgb_blue_initial),
            0,
            255,
            modifier,
        )
    }
}

@Composable
private fun ColorComponents(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier,
        verticalArrangement = Column.SmallSpacingArrangement,
        content = content
    )
}


@Composable
private fun ColorComponent(
    value: Float,
    onValueChange: (Float) -> Unit,
    name: String,
    min: Float,
    max: Float,
    modifier: Modifier = Modifier,
    steps: Int = 0,
) {
    LabelledSlider(
        value = value,
        onValueChange = onValueChange,
        min = min,
        max = max,
        startLabel = name,
        modifier = modifier,
        steps = steps,
    )
}

@Composable
private fun ColorComponent(
    value: Int,
    onValueChange: (Int) -> Unit,
    name: String,
    min: Int,
    max: Int,
    modifier: Modifier = Modifier,
    steps: Int = 0,
) {
    LabelledSlider(
        value = value.toFloat(),
        onValueChange = { onValueChange(it.toInt()) },
        min = min.toFloat(),
        max = max.toFloat(),
        startLabel = name,
        modifier = modifier,
        steps = steps,
    )
}

