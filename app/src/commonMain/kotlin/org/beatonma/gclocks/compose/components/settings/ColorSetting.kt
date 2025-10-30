package org.beatonma.gclocks.compose.components.settings

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import gclocks_multiplatform.app.generated.resources.Res
import gclocks_multiplatform.app.generated.resources.cd_save_changes
import gclocks_multiplatform.app.generated.resources.setting_color_contentdescription_selected
import gclocks_multiplatform.app.generated.resources.setting_color_label_hsl_hue_initial
import gclocks_multiplatform.app.generated.resources.setting_color_label_hsl_lightness_initial
import gclocks_multiplatform.app.generated.resources.setting_color_label_hsl_saturation_initial
import gclocks_multiplatform.app.generated.resources.setting_color_label_rgb_blue_initial
import gclocks_multiplatform.app.generated.resources.setting_color_label_rgb_green_initial
import gclocks_multiplatform.app.generated.resources.setting_color_label_rgb_red_initial
import org.beatonma.gclocks.app.data.settings.copyWithColors
import org.beatonma.gclocks.app.theme.DesignSpec.FloatingActionButtonSize
import org.beatonma.gclocks.app.theme.DesignSpec.floatingActionButton
import org.beatonma.gclocks.app.theme.rememberContentColor
import org.beatonma.gclocks.app.ui.Localization.stringResourceMap
import org.beatonma.gclocks.app.ui.resolve
import org.beatonma.gclocks.app.ui.screens.LocalClockPreview
import org.beatonma.gclocks.compose.AppIcon
import org.beatonma.gclocks.compose.LoadingSpinner
import org.beatonma.gclocks.compose.components.ButtonGroup
import org.beatonma.gclocks.compose.components.ButtonGroupSize
import org.beatonma.gclocks.compose.components.Clock
import org.beatonma.gclocks.compose.components.FullScreenOverlay
import org.beatonma.gclocks.compose.components.settings.components.CheckableSettingLayout
import org.beatonma.gclocks.compose.components.settings.components.LabelledSlider
import org.beatonma.gclocks.compose.components.settings.components.ScrollingRow
import org.beatonma.gclocks.compose.components.settings.components.rememberMaterialColorSwatch
import org.beatonma.gclocks.compose.components.settings.data.RichSetting
import org.beatonma.gclocks.compose.isHeightAtLeastMedium
import org.beatonma.gclocks.compose.isHeightSmall
import org.beatonma.gclocks.compose.isWidthSmall
import org.beatonma.gclocks.compose.toCompose
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.toColor
import org.beatonma.gclocks.core.graphics.withBlue
import org.beatonma.gclocks.core.graphics.withGreen
import org.beatonma.gclocks.core.graphics.withHue
import org.beatonma.gclocks.core.graphics.withLightness
import org.beatonma.gclocks.core.graphics.withRed
import org.beatonma.gclocks.core.graphics.withSaturation
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.core.util.fastForEach
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.graphics.Color as ComposeColor

private val DefaultPatchSize = 48.dp
private val EditablePatchSize = 64.dp

@Immutable
data class ClockColors(
    val background: Color?,
    val colors: List<Color>,
)

private fun ClockColors.unpack(): List<Color> = (listOf(background) + colors.toList()).filterNotNull()
private fun List<Color>.pack(hasBackgroundColor: Boolean): ClockColors =
    ClockColors(
        background = this.background(hasBackgroundColor),
        colors = this.colors(hasBackgroundColor)
    )


private fun List<Color>.background(hasBackgroundColor: Boolean): Color? = if (hasBackgroundColor) this[0] else null
private fun List<Color>.colors(hasBackgroundColor: Boolean): List<Color> =
    if (hasBackgroundColor) this.subList(1, size) else this

@Composable
fun ClockColorsSetting(
    setting: RichSetting.ClockColors,
    modifier: Modifier = Modifier,
) {
    ClockColorsSetting(
        setting.key,
        setting.localized.resolve(),
        setting.helpText?.resolve(),
        setting.value,
        modifier,
        onValueChange = setting.onValueChange
    )
}

@Composable
fun ClockColorsSetting(
    key: Any,
    name: String,
    helpText: String?,
    colors: ClockColors,
    modifier: Modifier = Modifier,
    onValueChange: (ClockColors) -> Unit,
) {
    val hasBackgroundColor = colors.background != null
    var editingIndex: Int? by rememberSaveable(key) { mutableStateOf(null) }

    CheckableSettingLayout(
        onClick = { editingIndex = 0 },
        role = Role.Button,
        modifier = modifier,
        helpText = helpText,
        text = { Text(name) },
        checkable = {
            @Composable
            fun ColorBlock(color: Color) {
                Box(
                    Modifier
                        .size(24.dp)
                        .border(Dp.Hairline, LocalContentColor.current.copy(alpha = 0.2f), shapes.small)
                        .background(color.toCompose(), shapes.small)
                        .aspectRatio(1f)
                ) {}
            }

            colors.unpack().forEach { ColorBlock(it) }
        },
    )

    FullScreenOverlay(editingIndex != null, { editingIndex = null }) {
        val clockPreview = LocalClockPreview.current
            ?: throw IllegalStateException("ColorSetting expects LocalClockPreview to be provided")

        var editableColors: List<Color> by remember(colors) { mutableStateOf(colors.unpack()) }

        val saveChanges: () -> Unit = {
            onValueChange(editableColors.pack(hasBackgroundColor))
            editingIndex = null
        }

        val clockPreviewOptions = clockPreview.options.copyWithColors(editableColors.colors(hasBackgroundColor))

        Box(
            Modifier
                .background(editableColors.background(hasBackgroundColor)?.toCompose() ?: colorScheme.background)
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
            if (windowSizeClass.isHeightAtLeastMedium()) {
                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    EditorContent(
                        clockPreviewOptions,
                        windowSizeClass,
                        editingIndex,
                        { editingIndex = it },
                        editableColors,
                        { editableColors = it },
                    )
                }
            } else {
                Row(
                    Modifier.fillMaxSize().padding(end = FloatingActionButtonSize),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    EditorContent(
                        clockPreviewOptions,
                        windowSizeClass,
                        editingIndex,
                        { editingIndex = it },
                        editableColors,
                        { editableColors = it },
                        Modifier.weight(1f, fill = false)
                    )
                }
            }

            FloatingActionButton(
                onClick = saveChanges,
                Modifier.floatingActionButton().align(Alignment.BottomEnd)
            ) {
                Icon(AppIcon.Checkmark, stringResource(Res.string.cd_save_changes))
            }
        }
    }
}

@Composable
private fun EditorContent(
    clockOptions: Options<*>,
    windowSizeClass: WindowSizeClass,
    editingIndex: Int?,
    setEditingIndex: (Int?) -> Unit,
    editableColors: List<Color>,
    setEditableColors: (List<Color>) -> Unit,
    modifier: Modifier = Modifier
) {
    if (!(windowSizeClass.isHeightSmall() && windowSizeClass.isWidthSmall())) {
        Clock(
            clockOptions,
            modifier
                .sizeIn(maxWidth = 600.dp, maxHeight = 400.dp)
                .padding(32.dp)
        )
    }

    Card(modifier.padding(16.dp)) {
        Column(
            Modifier
                .widthIn(max = 600.dp)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            val rowState = rememberLazyListState()

            ScrollingRow(
                Modifier.heightIn(min = EditablePatchSize),
                rowState,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(editableColors) { index, color ->
                    val isSelected = index == editingIndex
                    val size by animateDpAsState(if (isSelected) EditablePatchSize else DefaultPatchSize)

                    Patch(color.toCompose(), { setEditingIndex(index) }, size = size) {
                        if (isSelected) {
                            Icon(AppIcon.ArrowDown, null)
                        }
                    }
                }
            }

            editingIndex?.let { index ->
                val editingColor = editableColors[index]
                ColorEditor(
                    editingColor,
                    {
                        val value = editableColors.toMutableList()
                        value[index] = it
                        setEditableColors(value.toList())
                    },
                )
            }
        }
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

    LazyColumn(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ButtonGroup(
                mode,
                { localizedModes[it]?.resolve() ?: it.name },
                { mode = it },
                ColorEditorMode.entries,
                size = ButtonGroupSize.Small
            )
        }

        item {
            Box(Modifier.animateContentSize()) {
                when (mode) {
                    ColorEditorMode.Samples -> SampleColors(color, onColorChange)
                    ColorEditorMode.HSL -> HslComponents(color, onColorChange)
                    ColorEditorMode.RGB -> RgbComponents(color, onColorChange)
                    ColorEditorMode.HEX -> HexEditor(color, onColorChange)
                }
            }
        }
    }
}


@Composable
private fun ColorComponents(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        content = content
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
        swatch.fastForEach { color -> SampleColorPatch(color, onValueChange, color == value) }
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
    size: Dp = DefaultPatchSize,
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
