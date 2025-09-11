package org.beatonma.gclocks.compose.components.settings


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import gclocks_multiplatform.app.generated.resources.Res
import gclocks_multiplatform.app.generated.resources.setting_color_contentdescription_edited
import gclocks_multiplatform.app.generated.resources.setting_color_contentdescription_selected
import gclocks_multiplatform.app.generated.resources.setting_color_label_hue_initial
import gclocks_multiplatform.app.generated.resources.setting_color_label_lightness_initial
import gclocks_multiplatform.app.generated.resources.setting_color_label_saturation_initial
import org.beatonma.gclocks.compose.AppIcon
import org.beatonma.gclocks.compose.animation.EnterScale
import org.beatonma.gclocks.compose.animation.EnterVertical
import org.beatonma.gclocks.compose.animation.ExitScale
import org.beatonma.gclocks.compose.animation.ExitVertical
import org.beatonma.gclocks.compose.components.settings.components.CollapsibleSettingLayout
import org.beatonma.gclocks.compose.components.settings.components.LabelledSlider
import org.beatonma.gclocks.compose.components.settings.components.ScrollingRow
import org.beatonma.gclocks.compose.components.settings.components.SettingName
import org.beatonma.gclocks.compose.components.settings.components.rememberMaterialColorSwatch
import org.beatonma.gclocks.compose.toCompose
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.util.fastForEach
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.graphics.Color as ComposeColor

private val EditablePatchSize = 64.dp

@Composable
fun MultiColorSetting(
    setting: RichSetting.Colors,
    modifier: Modifier = Modifier,
) {
    MultiColorSetting(
        setting.localized.resolve(),
        setting.value,
        { index, color ->
            val value = setting.value.toMutableList()
            value[index] = color
            setting.onValueChange(value)
        },
        modifier,
    )
}

@Composable
fun MultiColorSetting(
    name: String,
    colors: List<Color>,
    onValueChange: (index: Int, color: Color) -> Unit,
    modifier: Modifier = Modifier,
) {
    val swatch = rememberMaterialColorSwatch()
    var editingIndex: Int? by remember { mutableStateOf(null) }
    val editorOpenPadding by animateDpAsState(if (editingIndex != null) 48.dp else 0.dp)

    CollapsibleSettingLayout(editingIndex != null, modifier) {
        SettingName(name, Modifier.padding(bottom = 4.dp))

        ScrollingRow(
            Modifier
                .fillMaxWidth().padding(bottom = 8.dp)
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
                    Modifier.padding(
                        top = padding * 1.5f,
                        start = padding * 0.5f,
                        end = padding * 0.5f
                    ),
                    color,
                    isSelected,
                    {
                        editingIndex = when (isSelected) {
                            true -> null
                            false -> index
                        }
                    }
                )
            }
        }

        ColorEditor(
            editingIndex != null,
            swatch,
            colors[editingIndex ?: 0],
            { color -> onValueChange(editingIndex ?: 0, color) },
        )
    }
}


@Composable
fun ColorSetting(
    setting: RichSetting.Color,
    modifier: Modifier = Modifier,
) {
    ColorSetting(
        setting.localized.resolve(),
        setting.value,
        modifier,
        onValueChange = setting.onValueChange
    )
}


@Composable
fun ColorSetting(
    name: String,
    value: Color,
    modifier: Modifier = Modifier,
    colors: List<Color> = rememberMaterialColorSwatch(),
    onValueChange: (newValue: Color) -> Unit,
) {
    var isEditing by remember { mutableStateOf(false) }

    CollapsibleSettingLayout(isEditing, modifier) {
        SettingName(name, Modifier.padding(bottom = 4.dp))

        EditablePreviewPatch(
            Modifier,
            value,
            isEditing,
            onClick = { isEditing = !isEditing },
        )
        ColorEditor(isEditing, colors, value, onValueChange)
    }
}

@Composable
private fun ColorEditor(
    isVisible: Boolean,
    colors: List<Color>,
    value: Color,
    onValueChange: (Color) -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = EnterVertical,
        exit = ExitVertical,
    ) {
        ColorEditor(colors, value, onValueChange, modifier)
    }
}

@Composable
private fun ColorEditor(
    colors: List<Color>,
    value: Color,
    onValueChange: (Color) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hsl = rememberHsl(value)

    LaunchedEffect(hsl.hue, hsl.saturation, hsl.lightness) {
        onValueChange(Color.hsla(hsl.hue, hsl.saturation, hsl.lightness))
    }

    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FlowRow(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                4.dp,
                alignment = Alignment.CenterHorizontally
            ),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            maxItemsInEachRow = 5
        ) {
            colors.fastForEach { color ->
                Patch(
                    color = color.toCompose(),
                    onClick = { hsl.set(color) },
                    content = if (color == value) {
                        {
                            Icon(
                                AppIcon.Checkmark,
                                contentDescription = stringResource(Res.string.setting_color_contentdescription_selected),
                            )
                        }
                    } else null,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        Column(
            Modifier.padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ColorComponent(
                hsl.hue,
                { hsl.hue = it },
                stringResource(Res.string.setting_color_label_hue_initial),
                0f,
                360f,
            )
            ColorComponent(
                hsl.saturation,
                { hsl.saturation = it },
                stringResource(Res.string.setting_color_label_saturation_initial),
                0f,
                1f,
            )
            ColorComponent(
                hsl.lightness,
                { hsl.lightness = it },
                stringResource(Res.string.setting_color_label_lightness_initial),
                0f,
                1f,
            )
        }
    }
}

@Composable
private fun rememberHsl(color: Color): HslColor = remember(color) { color.toHslColor() }

private class HslColor(h: Float, s: Float, l: Float) {
    var hue by mutableFloatStateOf(h)
    var saturation by mutableFloatStateOf(s)
    var lightness by mutableFloatStateOf(l)

    fun set(color: Color) {
        val (h, s, l) = color.hsl()
        hue = h
        saturation = s
        lightness = l
    }
}

private fun Color.toHslColor(): HslColor {
    val (h, s, l) = hsl()
    return HslColor(h, s, l)
}

@Composable
private fun ColorComponent(
    value: Float,
    onValueChange: (Float) -> Unit,
    name: String,
    min: Float,
    max: Float,
) {
    LabelledSlider(
        value = value,
        onValueChange = onValueChange,
        min = min,
        max = max,
        startLabel = name,
    )
}


@Composable
private fun rememberContentColor(color: ComposeColor): ComposeColor {
    return remember(color) {
        mutableStateOf(
            when {
                color.luminance() > 0.5f -> ComposeColor.Black
                else -> ComposeColor.White
            }.copy(alpha = 0.72f)
        )
    }.value
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
        border = BorderStroke(1.dp, colorScheme.onBackground.copy(alpha = 0.2f))
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
    modifier: Modifier,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
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