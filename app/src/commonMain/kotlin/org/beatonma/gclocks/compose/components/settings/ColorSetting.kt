package org.beatonma.gclocks.compose.components.settings


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import gclocks_multiplatform.app.generated.resources.Res
import gclocks_multiplatform.app.generated.resources.setting_color_contentdescription_edited
import gclocks_multiplatform.app.generated.resources.setting_color_contentdescription_selected
import gclocks_multiplatform.app.generated.resources.setting_color_label_hsl_hue_initial
import gclocks_multiplatform.app.generated.resources.setting_color_label_hsl_lightness_initial
import gclocks_multiplatform.app.generated.resources.setting_color_label_hsl_saturation_initial
import gclocks_multiplatform.app.generated.resources.setting_color_label_rgb_blue_initial
import gclocks_multiplatform.app.generated.resources.setting_color_label_rgb_green_initial
import gclocks_multiplatform.app.generated.resources.setting_color_label_rgb_red_initial
import org.beatonma.gclocks.app.theme.rememberContentColor
import org.beatonma.gclocks.app.ui.Localization.stringResourceMap
import org.beatonma.gclocks.compose.AppIcon
import org.beatonma.gclocks.compose.animation.EnterScale
import org.beatonma.gclocks.compose.animation.EnterVertical
import org.beatonma.gclocks.compose.animation.ExitScale
import org.beatonma.gclocks.compose.animation.ExitVertical
import org.beatonma.gclocks.compose.components.ButtonGroup
import org.beatonma.gclocks.compose.components.ButtonGroupSize
import org.beatonma.gclocks.compose.components.settings.components.CollapsibleSettingLayout
import org.beatonma.gclocks.compose.components.settings.components.LabelledSlider
import org.beatonma.gclocks.compose.components.settings.components.OnFocusSetting
import org.beatonma.gclocks.compose.components.settings.components.ScrollingRow
import org.beatonma.gclocks.compose.components.settings.components.SettingName
import org.beatonma.gclocks.compose.components.settings.components.rememberMaterialColorSwatch
import org.beatonma.gclocks.compose.components.settings.data.RichSetting
import org.beatonma.gclocks.compose.toCompose
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.toColor
import org.beatonma.gclocks.core.graphics.withBlue
import org.beatonma.gclocks.core.graphics.withGreen
import org.beatonma.gclocks.core.graphics.withHue
import org.beatonma.gclocks.core.graphics.withLightness
import org.beatonma.gclocks.core.graphics.withRed
import org.beatonma.gclocks.core.graphics.withSaturation
import org.beatonma.gclocks.core.util.fastForEach
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.graphics.Color as ComposeColor

private val EditablePatchSize = 64.dp

@Composable
fun MultiColorSetting(
    setting: RichSetting.Colors,
    modifier: Modifier = Modifier,
    onFocus: OnFocusSetting?,
) {
    MultiColorSetting(
        setting.localized.resolve(),
        setting.value,
        modifier,
        onFocus = onFocus,
        onValueChange = { index, color ->
            val value = setting.value.toMutableList()
            value[index] = color
            setting.onValueChange(value)
        },
    )
}

@Composable
fun MultiColorSetting(
    name: String,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    onFocus: OnFocusSetting?,
    onValueChange: (index: Int, color: Color) -> Unit,
) {
    var editingIndex: Int? by remember(colors.size) { mutableStateOf(null) }
    val editorOpenPadding by animateDpAsState(if (editingIndex != null) 48.dp else 0.dp)

    CollapsibleSettingLayout(editingIndex != null, modifier, onFocus = onFocus) {
        SettingName(name)

        ScrollingRow(
            Modifier
                .fillMaxWidth()
                .requiredHeight(EditablePatchSize + (editorOpenPadding * 2f)),
            horizontalArrangement = Arrangement.spacedBy(
                16.dp,
                Alignment.CenterHorizontally
            )
        ) {
            itemsIndexed(colors) { index, color ->
                val isSelected = index == editingIndex
                val padding by animateDpAsState(if (isSelected) editorOpenPadding else 0.dp)

                EditablePreviewPatch(
                    color,
                    isSelected,
                    {
                        editingIndex = when (isSelected) {
                            true -> null
                            false -> index
                        }
                    },
                    Modifier.padding(
                        top = padding * 1.5f,
                        start = padding * 0.5f,
                        end = padding * 0.5f
                    ),
                )
            }
        }

        ColorEditor(
            editingIndex != null,
            colors[editingIndex ?: 0],
            { color -> onValueChange(editingIndex ?: 0, color) },
        )
    }
}


@Composable
fun ColorSetting(
    setting: RichSetting.Color,
    modifier: Modifier = Modifier,
    onFocus: OnFocusSetting?,
) {
    ColorSetting(
        setting.localized.resolve(),
        setting.value,
        modifier,
        onFocus = onFocus,
        onValueChange = setting.onValueChange
    )
}


@Composable
fun ColorSetting(
    name: String,
    value: Color,
    modifier: Modifier = Modifier,
    onFocus: OnFocusSetting?,
    onValueChange: (newValue: Color) -> Unit,
) {
    var isEditing by remember { mutableStateOf(false) }

    CollapsibleSettingLayout(isEditing, modifier, onFocus = onFocus) {
        SettingName(name)

        EditablePreviewPatch(
            color = value,
            isSelected = isEditing,
            onClick = { isEditing = !isEditing },
        )

        ColorEditor(isEditing, value, onValueChange)
    }
}

@Composable
private fun ColorEditor(
    isVisible: Boolean,
    value: Color,
    onValueChange: (Color) -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = EnterVertical,
        exit = ExitVertical,
    ) {
        ColorEditor(value, onValueChange, modifier)
    }
}


enum class ColorEditorMode {
    Samples,
    HSL,
    RGB,
    HEX,
}

@Composable
private fun ColorEditor(
    color: Color,
    onColorChange: (Color) -> Unit,
    modifier: Modifier = Modifier,
) {
    var mode by rememberSaveable { mutableStateOf(ColorEditorMode.Samples) }
    val localizedModes = remember { ColorEditorMode::class.stringResourceMap }

    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ButtonGroup(
            mode,
            { localizedModes[it]?.resolve() ?: it.name },
            { mode = it },
            ColorEditorMode.entries,
            size = ButtonGroupSize.Small
        )

        when (mode) {
            ColorEditorMode.Samples -> SampleColors(color, onColorChange, Modifier.fillMaxWidth())
            ColorEditorMode.HSL -> HslComponents(color, onColorChange, Modifier.fillMaxWidth())
            ColorEditorMode.RGB -> RgbComponents(color, onColorChange, Modifier.fillMaxWidth())
            ColorEditorMode.HEX -> HexEditor(color, onColorChange, Modifier.fillMaxWidth())
        }
    }
}


@Composable
private fun ColumnScope.HslComponents(
    color: Color,
    onColorChange: (Color) -> Unit,
    modifier: Modifier = Modifier,
) {
    val (hue, saturation, lightness) = color.hsl()

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

@Composable
private fun ColumnScope.RgbComponents(
    color: Color,
    onColorChange: (Color) -> Unit,
    modifier: Modifier = Modifier,
) {
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
private fun SampleColors(
    value: Color,
    onValueChange: (Color) -> Unit,
    modifier: Modifier,
) {
    val swatch = rememberMaterialColorSwatch()
    val spacing = 12.dp
    val horizontalArrangement =
        Arrangement.spacedBy(spacing, alignment = Alignment.CenterHorizontally)
    val isVerticallySmall =
        currentWindowAdaptiveInfo().windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)

    when (isVerticallySmall) {
        true -> {
            ScrollingRow(modifier, horizontalArrangement = horizontalArrangement) {
                items(swatch) { color ->
                    SampleColorPatch(color, onValueChange, color == value)
                }
            }
        }

        false -> {
            FlowRow(
                modifier,
                horizontalArrangement = horizontalArrangement,
                verticalArrangement = Arrangement.spacedBy(spacing),
                maxItemsInEachRow = 5
            ) {
                swatch.fastForEach { color ->
                    SampleColorPatch(color, onValueChange, color == value)
                }
            }
        }
    }
}

@Composable
private fun SampleColorPatch(
    value: Color,
    onValueChange: (Color) -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    Patch(
        color = value.toCompose(),
        onClick = { onValueChange(value) },
        content = if (isSelected) {
            {
                Icon(
                    AppIcon.Checkmark,
                    contentDescription = stringResource(Res.string.setting_color_contentdescription_selected),
                )
            }
        } else null,
        modifier = modifier
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

@Composable
private fun Patch(
    color: ComposeColor,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentColor: ComposeColor = rememberContentColor(color),
    size: Dp = 48.dp,
    content: (@Composable () -> Unit)? = null,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.size(size),
        color = color,
        contentColor = contentColor,
        shape = shapes.extraSmall,
        border = BorderStroke(1.dp, colorScheme.onBackground.copy(alpha = 0.3f))
    ) {
        content?.let {
            Box(contentAlignment = Alignment.Center) {
                content.invoke()
            }
        }
    }
}

@Composable
private fun EditablePreviewPatch(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val patchScale by animateFloatAsState(if (isSelected) 1.2f else 1f)

    Patch(
        color.toCompose(),
        onClick = onClick,
        size = EditablePatchSize * patchScale,
        modifier = modifier
    ) {
        AnimatedVisibility(
            visible = isSelected,
            enter = EnterScale,
            exit = ExitScale,
        ) {
            Icon(
                AppIcon.ArrowDown,
                stringResource(Res.string.setting_color_contentdescription_edited),
                Modifier.requiredSize(32.dp)
            )
        }
    }
}
