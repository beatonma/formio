package org.beatonma.gclocks.compose.components.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpRect
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.height
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.size
import androidx.compose.ui.unit.width
import gclocks_multiplatform.app.generated.resources.Res
import gclocks_multiplatform.app.generated.resources.cd_align_horizontal_center
import gclocks_multiplatform.app.generated.resources.cd_align_reset
import gclocks_multiplatform.app.generated.resources.cd_align_vertical_center
import gclocks_multiplatform.app.generated.resources.cd_save_changes
import org.beatonma.gclocks.app.theme.DesignSpec
import org.beatonma.gclocks.app.theme.DesignSpec.floatingActionButton
import org.beatonma.gclocks.app.theme.rememberContentColor
import org.beatonma.gclocks.app.ui.resolve
import org.beatonma.gclocks.app.ui.screens.LocalClockPreview
import org.beatonma.gclocks.compose.AppIcon
import org.beatonma.gclocks.compose.components.Clock
import org.beatonma.gclocks.compose.components.FullScreenOverlay
import org.beatonma.gclocks.compose.components.settings.components.CheckableSettingLayout
import org.beatonma.gclocks.compose.components.settings.components.SettingName
import org.beatonma.gclocks.compose.components.settings.data.RichSetting
import org.beatonma.gclocks.core.geometry.MutableRectF
import org.beatonma.gclocks.core.geometry.RectF
import org.jetbrains.compose.resources.stringResource


private val MinBoundarySize = DesignSpec.TouchTargetMinSize * 2f
private val DragHandleSize = DesignSpec.TouchTargetMinSize
private val DragHandleOffset = DragHandleSize / 2f


@Composable
fun ClockPositionSetting(
    setting: RichSetting.ClockPosition,
    modifier: Modifier,
) {
    ClockPositionSetting(
        name = setting.name.resolve(),
        value = setting.value,
        onValueChange = setting.onValueChange,
        modifier = modifier,
    )
}


@Composable
fun ClockPositionSetting(
    name: String,
    value: RectF,
    modifier: Modifier,
    onValueChange: (RectF) -> Unit,
) {
    var isEditMode by rememberSaveable { mutableStateOf(false) }

    CheckableSettingLayout(
        onClick = { isEditMode = true },
        role = Role.Button,
        modifier = modifier,
        text = { SettingName(name) }
    ) {
        Icon(AppIcon.PositionAndSize, null)
    }

    FullScreenOverlay(
        isOpen = isEditMode,
        onDismiss = { isEditMode = false },
    ) {
        PositionEditor(value) {
            onValueChange(it)
            isEditMode = false
        }
    }
}

@Composable
private fun PositionEditor(
    value: RectF,
    onSaveChanges: (RectF) -> Unit,
) {
    val clockPreview = LocalClockPreview.current
    val backgroundColor = clockPreview?.background ?: colorScheme.surface
    val boundaryColor = rememberContentColor(backgroundColor, alpha = 0.08f)

    @Suppress("UnusedBoxWithConstraintsScope")
    BoxWithConstraints(
        Modifier
            .background(backgroundColor)
            .fillMaxSize()
    ) {
        val containerSize = DpSize(maxWidth, maxHeight)
        var bounds by remember { mutableStateOf(relativeToDp(value, containerSize)) }
        val density = LocalDensity.current

        TransformableBoundary(
            bounds,
            { bounds = it },
            containerSize,
            density,
            boundaryColor
        ) {
            if (clockPreview != null) {
                Clock(clockPreview.options, allowVariance = true)
            }
        }

        ToolButtons(
            bounds,
            { bounds = it },
            containerSize = containerSize,
            onSave = {
                val normalizedRect = RectF(
                    left = (bounds.left / containerSize.width).coerceIn(0f, 1f),
                    top = (bounds.top / containerSize.height).coerceIn(0f, 1f),
                    right = (bounds.right / containerSize.width).coerceIn(0f, 1f),
                    bottom = (bounds.bottom / containerSize.height).coerceIn(0f, 1f)
                )
                onSaveChanges(normalizedRect)
            },
            modifier = Modifier.align(Alignment.BottomEnd).floatingActionButton()
        )
    }
}

@Composable
private fun TransformableBoundary(
    bounds: DpRect,
    onBoundsChange: (DpRect) -> Unit,
    containerSize: DpSize,
    density: Density,
    backgroundColor: Color,
    content: (@Composable BoxScope.() -> Unit)?,
) {
    val currentBounds by rememberUpdatedState(bounds)

    Box(
        Modifier
            .offset(currentBounds.left, currentBounds.top)
            .size(currentBounds.size)
            .background(backgroundColor)
            .border(Dp.Hairline, colorScheme.surface)
            .border(Dp.Hairline, colorScheme.onSurface)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, gestureZoom, _ ->
                    val newBounds = (when {
                        gestureZoom == 1f -> {
                            pan(
                                currentBounds,
                                containerSize,
                                pan.x.toDp(),
                                pan.y.toDp()
                            )
                        }

                        else -> scale(currentBounds, containerSize, gestureZoom)
                    })
                    onBoundsChange(newBounds)
                }
            }
    ) {
        content?.invoke(this)

        DragHandles { left, top, right, bottom ->
            with(density) {
                val newBounds = resize(
                    currentBounds,
                    containerSize,
                    left.toDp(), top.toDp(), right.toDp(), bottom.toDp(),
                )
                onBoundsChange(newBounds)
            }
        }
    }
}

/**
 * Generate [DragHandle]s for each edge and corner,
 */
@Composable
private fun BoxScope.DragHandles(
    onResize: (Float, Float, Float, Float) -> Unit,
) {
    val biases = listOf(-1f, 0f, 1f)

    for (x in biases) {
        for (y in biases) {
            if (x == 0f && y == 0f) continue

            DragHandle(
                Modifier
                    .align(BiasAlignment(x, y))
                    .offset(x = DragHandleOffset * x, y = DragHandleOffset * y),
                onDrag = { dx, dy ->
                    onResize(
                        if (x == -1f) dx else 0f,
                        if (y == -1f) dy else 0f,
                        if (x == 1f) dx else 0f,
                        if (y == 1f) dy else 0f,
                    )
                }
            ) {
                // Show icon on edges, not on corners.
                if (x == 0f || y == 0f) {
                    Icon(
                        AppIcon.DragHandle,
                        null,
                        Modifier.rotate(if (y == 0f) 90f else 0f)
                    )
                }
            }
        }
    }
}

@Composable
private fun DragHandle(
    modifier: Modifier = Modifier,
    onDrag: (Float, Float) -> Unit,
    content: @Composable () -> Unit = {},
) {
    Surface(
        modifier
            .size(DragHandleSize)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onDrag(dragAmount.x, dragAmount.y)
                }
            }
            .padding(16.dp),
        shape = shapes.extraSmall,
        content = content
    )
}

@Composable
private fun ToolButtons(
    bounds: DpRect,
    onChange: (DpRect) -> Unit,
    containerSize: DpSize,
    onSave: () -> Unit,
    modifier: Modifier,
) {
    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(DesignSpec.TouchTargetPadding),
        horizontalAlignment = Alignment.End,
    ) {
        AnimatedVisibility(bounds.height > containerSize.height * .8f || bounds.width > containerSize.width * .8f) {
            OutlinedIconButton({ onChange(shrink(containerSize)) }) {
                Icon(
                    AppIcon.Reset,
                    stringResource(Res.string.cd_align_reset)
                )
            }
        }

        FilledTonalIconButton({ onChange(centerHorizontal(bounds, containerSize)) }) {
            Icon(
                AppIcon.AlignCenterHorizontal,
                stringResource(Res.string.cd_align_horizontal_center)
            )
        }
        FilledTonalIconButton({ onChange(centerVertical(bounds, containerSize)) }) {
            Icon(AppIcon.AlignCenterVertical, stringResource(Res.string.cd_align_vertical_center))
        }
        FloatingActionButton(onSave) {
            Icon(AppIcon.Checkmark, stringResource(Res.string.cd_save_changes))
        }
    }
}

/**
 * Return the result of scaling [rect] by [scale] while ensuring the result
 * never overflows the [container].
 */
private fun scale(rect: DpRect, container: DpSize, scale: Float): DpRect {
    val targetSize = DpSize(
        max(rect.width * scale, MinBoundarySize),
        max(rect.height * scale, MinBoundarySize),
    )

    val halfWidth = targetSize.width / 2f
    val halfHeight = targetSize.height / 2f

    val centerX = rect.left + (rect.width / 2f)
    val centerY = rect.top + (rect.height / 2f)

    return DpRect(
        left = max(0.dp, centerX - halfWidth),
        top = max(0.dp, centerY - halfHeight),
        right = min(container.width, centerX + halfWidth),
        bottom = min(container.height, centerY + halfHeight)
    )
}

/**
 * Return the result of moving each edge of [rect] by the given Dp distance,
 * while ensuring the result never overflows the [container].
 */
private fun resize(
    rect: DpRect,
    container: DpSize,
    dxLeft: Dp,
    dyTop: Dp,
    dxRight: Dp,
    dyBottom: Dp,
): DpRect {
    val left = when (dxLeft) {
        Dp.Hairline -> rect.left
        else -> max(
            Dp.Hairline,
            min(rect.left + dxLeft, rect.right - MinBoundarySize)
        )
    }
    val top = when (dyTop) {
        Dp.Hairline -> rect.top
        else -> max(
            Dp.Hairline,
            min(rect.top + dyTop, rect.bottom - MinBoundarySize)
        )
    }

    val right = when (dxRight) {
        Dp.Hairline -> rect.right
        else -> min(
            container.width,
            max(
                left + MinBoundarySize,
                rect.right + dxRight
            )
        )
    }
    val bottom = when (dyBottom) {
        Dp.Hairline -> rect.bottom
        else -> min(
            container.height,
            max(
                top + MinBoundarySize,
                rect.bottom + dyBottom
            )
        )
    }

    return DpRect(left = left, top = top, right = right, bottom = bottom)
}

/**
 * Return the result of translating [rect] by [x], [y], while ensuring the
 * result never overflows [container].
 */
private fun pan(rect: DpRect, container: DpSize, x: Dp, y: Dp): DpRect {
    val left = max(
        0.dp,
        min(rect.left + x, container.width - rect.width)
    )
    val top = max(
        0.dp,
        min(rect.top + y, container.height - rect.height)
    )

    return DpRect(
        left = left,
        top = top,
        right = left + rect.width,
        bottom = top + rect.height,
    )
}

private fun centerHorizontal(rect: DpRect, container: DpSize): DpRect {
    val left = (container.width - rect.width) / 2f
    return rect.copy(left = left, right = left + rect.width)
}

private fun centerVertical(rect: DpRect, container: DpSize): DpRect {
    val top = (container.height - rect.height) / 2f
    return rect.copy(top = top, bottom = top + rect.height)
}

private fun relativeToDp(relative: RectF, container: DpSize): DpRect = DpRect(
    container.width * relative.left,
    container.height * relative.top,
    container.width * relative.right,
    container.height * relative.bottom
)

/**
 * It's possible to set the bounds so large that they extend behind system bars
 * in all directions on Android - so this lets us get out of that situation.
 */
private fun shrink(container: DpSize): DpRect {
    return relativeToDp(MutableRectF(0f, 0f, 1f, 1f).inset(.25f).toRect(), container)
}
