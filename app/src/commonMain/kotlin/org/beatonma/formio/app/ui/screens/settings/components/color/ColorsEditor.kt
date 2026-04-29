package org.beatonma.formio.app.ui.screens.settings.components.color

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.unit.dp
import formio.app.generated.resources.Res
import formio.app.generated.resources.cd_discard_changes
import formio.app.generated.resources.cd_save_changes
import formio.app.generated.resources.navigation_cd_back
import formio.app.generated.resources.setting_color_palettes_manage_palettes
import org.beatonma.formio.app.data.settings.ClockColors
import org.beatonma.formio.app.data.settings.GlobalOptions
import org.beatonma.formio.app.data.settings.copyWithColors
import org.beatonma.formio.app.ui.components.AppIcon
import org.beatonma.formio.app.ui.components.AssistChip
import org.beatonma.formio.app.ui.components.BackNavigationIcon
import org.beatonma.formio.app.ui.components.Clock
import org.beatonma.formio.app.ui.components.Column
import org.beatonma.formio.app.ui.components.FloatingActionButton
import org.beatonma.formio.app.ui.components.FullScreenOverlay
import org.beatonma.formio.app.ui.components.NavigationIconContainer
import org.beatonma.formio.app.ui.components.Row
import org.beatonma.formio.app.ui.components.ScrollingRow
import org.beatonma.formio.app.ui.components.toCompose
import org.beatonma.formio.app.ui.screens.settings.LocalClockPreview
import org.beatonma.formio.app.ui.theme.rememberContentColor
import org.beatonma.formio.app.ui.theme.tokens.CardTokens
import org.beatonma.formio.app.ui.theme.tokens.ColumnTokens
import org.beatonma.formio.app.ui.theme.tokens.FloatingActionButtonTokens
import org.beatonma.formio.app.ui.theme.tokens.WindowTokens
import org.beatonma.formio.app.ui.util.isHeightAtLeastMedium
import org.beatonma.formio.app.ui.util.isHeightSmall
import org.beatonma.formio.app.ui.util.isWidthSmall
import org.beatonma.formio.core.graphics.Color
import org.jetbrains.compose.resources.stringResource

@Composable
fun ColorsEditor(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    colors: ClockColors,
    onValueChange: (ClockColors) -> Unit,
    palettes: List<ClockColors>,
    onUpdatePalettes: (List<ClockColors>) -> Unit,
) {
    FullScreenOverlay(isOpen, onDismiss) {
        val clockPreview = LocalClockPreview.current
            ?: error("ColorSetting editor requires LocalClockPreview to be provided")

        // Temporary 'working' copy of [colors].
        var editableColors by remember { mutableStateOf(colors) }

        val saveChanges: () -> Unit = {
            onValueChange(editableColors)
            onDismiss()
        }

        val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

        ColorsEditorLayout(
            isVertical = windowSizeClass.isHeightAtLeastMedium(),
            backgroundColor = editableColors.background,
            onDiscardChanges = onDismiss,
            onSaveChanges = saveChanges,
        ) { modifier ->
            if (!(windowSizeClass.isHeightSmall() && windowSizeClass.isWidthSmall())) {
                Clock(
                    clockPreview.options.copyWithColors(editableColors.colors),
                    modifier
                        .sizeIn(maxWidth = 600.dp, maxHeight = 400.dp)
                        .padding(WindowTokens.ContentPadding * 2)
                )
            }

            EditorCard(
                editableColors,
                { editableColors = it },
                palettes,
                onUpdatePalettes,
                modifier
            )
        }
    }
}

@Composable
private fun ColorsEditorLayout(
    isVertical: Boolean,
    backgroundColor: Color?,
    onDiscardChanges: () -> Unit,
    onSaveChanges: () -> Unit,
    content: @Composable (Modifier) -> Unit,
) {
    val backgroundColor by animateColorAsState(backgroundColor?.toCompose() ?: colorScheme.background)
    val contentColor = rememberContentColor(backgroundColor)

    Box(
        Modifier
            .background(backgroundColor)
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        if (isVertical) {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                content(Modifier)
            }
        } else {
            Row(
                Modifier.fillMaxSize().padding(end = FloatingActionButtonTokens.Size),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                content(Modifier.weight(1f, fill = false))
            }
        }

        NavigationIconContainer(contentColor) {
            BackNavigationIcon(
                onDiscardChanges,
                contentDescription = stringResource(Res.string.cd_discard_changes)
            )
        }

        FloatingActionButton(
            true,
            onClick = onSaveChanges
        ) {
            Icon(AppIcon.Checkmark, stringResource(Res.string.cd_save_changes))
        }
    }
}

private enum class EditMode {
    Palette,
    Color,
}

@Composable
private fun EditorCard(
    colors: ClockColors,
    setColors: (ClockColors) -> Unit,
    palettes: List<ClockColors>,
    onUpdatePalettes: (List<ClockColors>) -> Unit,
    modifier: Modifier = Modifier,
) {
    var editMode by rememberSaveable { mutableStateOf(EditMode.Color) }

    @OptIn(ExperimentalComposeUiApi::class)
    BackHandler(editMode == EditMode.Palette) {
        editMode = EditMode.Color
    }

    Card(modifier) {
        Column(
            Modifier
                .widthIn(max = ColumnTokens.PreferredMaxWidth)
                .padding(CardTokens.ContentPadding)
                .animateContentSize(),
            verticalArrangement = Column.LargeSpacingArrangement,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (editMode) {
                EditMode.Color -> {
                    EditorCardToolbar(
                        onBack = null,
                        actions = {
                            AssistChip(
                                { editMode = EditMode.Palette },
                                stringResource(Res.string.setting_color_palettes_manage_palettes),
                                icon = AppIcon.Palette,
                            )
                        }
                    )

                    EditorCardContentColor(colors, setColors)
                }

                EditMode.Palette -> {
                    EditorCardToolbar(onBack = { editMode = EditMode.Color }, actions = null)
                    EditorCardContentPalette(colors, setColors, palettes, onUpdatePalettes)
                }
            }
        }
    }
}

@Composable
private fun EditorCardToolbar(
    onBack: (() -> Unit)?,
    actions: @Composable (RowScope.() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier,
        horizontalArrangement = Row.MediumSpacingArrangement,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        onBack?.let { onBack ->
            IconButton(onBack) {
                Icon(AppIcon.Back, stringResource(Res.string.navigation_cd_back))
            }
        }

        Spacer(Modifier.weight(1f))

        Row { actions?.invoke(this) }
    }
}

@Composable
private fun EditorCardContentColor(colors: ClockColors, setColors: (ClockColors) -> Unit) {
    var editingIndex by rememberSaveable { mutableStateOf(0) }
    val colorsAsList = colors.allColors

    ActiveColorsRow(colorsAsList, editingIndex, { editingIndex = it })
    SingleColorEditor(
        colorsAsList[editingIndex],
        onColorChanged = { color ->
            val updatedColors = colorsAsList.toMutableList()
            updatedColors[editingIndex] = color
            setColors(colors.update(updatedColors.toList()))
        },
    )
}

@Composable
private fun EditorCardContentPalette(
    colors: ClockColors,
    setColors: (ClockColors) -> Unit,
    palettes: List<ClockColors>,
    onUpdatePalettes: (List<ClockColors>) -> Unit,
) {
    ColorPalettesEditor(
        colors,
        palettes,
        onSelect = { palette ->
            setColors(
                ClockColors(
                    background = palette.background ?: colors.background,
                    colors = colors.colors.mapIndexed { i, color ->
                        palette.colors.getOrNull(i) ?: color
                    }
                )
            )
        },
        onSave = { palette -> onUpdatePalettes(palettes + palette) },
        onDelete = { deleted -> onUpdatePalettes(palettes.filter { it != deleted }) },
        onRestoreDefaults = { onUpdatePalettes(GlobalOptions().colorPalettes) }
    )
}


@Composable
private fun ActiveColorsRow(
    colors: List<Color>,
    editingIndex: Int?,
    setEditingIndex: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
) {
    ScrollingRow(
        modifier.heightIn(min = ColorItemTokens.EditablePatchSize),
        state,
        horizontalArrangement = Row.MediumSpacingArrangement
    ) {
        itemsIndexed(colors) { index, color ->
            val isSelected = index == editingIndex
            val size by animateDpAsState(if (isSelected) ColorItemTokens.EditablePatchSize else ColorItemTokens.DefaultPatchSize)

            ColorPatch(
                color.toCompose(),
                { setEditingIndex(index) },
                size = size
            ) {
                if (isSelected) {
                    Icon(AppIcon.ArrowDown, null)
                }
            }
        }
    }
}
