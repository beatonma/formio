package org.beatonma.gclocks.compose.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import org.beatonma.gclocks.app.theme.getForegroundColor
import org.beatonma.gclocks.compose.components.settings.components.ScrollingRow
import org.beatonma.gclocks.compose.plus
import org.beatonma.gclocks.compose.vertical

enum class ButtonGroupSize {
    Small {
        override val smallRadius = 4.dp
        override val largeRadius = 32.dp
        override val textStyle @Composable get() = typography.labelSmall

        @Composable
        override fun contentPadding(layoutDirection: LayoutDirection): PaddingValues =
            PaddingValues(
                start = 12.dp,
                end = 12.dp,
            ) + ButtonDefaults.ContentPadding.vertical()
    },
    Default {
        override val smallRadius = 8.dp
        override val largeRadius = 32.dp
        override val textStyle @Composable get() = typography.labelLarge

        @Composable
        override fun contentPadding(layoutDirection: LayoutDirection) =
            ButtonDefaults.ContentPadding
    }
    ;

    abstract val smallRadius: Dp
    abstract val largeRadius: Dp
    abstract val textStyle: TextStyle @Composable get

    @Composable
    abstract fun contentPadding(layoutDirection: LayoutDirection = LocalLayoutDirection.current): PaddingValues
}

@Immutable
data class ButtonColors(
    val selectedContainerColor: Color,
    val selectedContentColor: Color = selectedContainerColor.getForegroundColor(),
    val unselectedContainerColor: Color,
    val unselectedContentColor: Color = unselectedContainerColor.getForegroundColor(),
)

/**
 * Approximate implementation of Connected button group:
 *   https://m3.material.io/components/button-groups/specs
 */
@Composable
fun <T> ButtonGroup(
    value: T,
    onValueChange: (T) -> Unit,
    label: @Composable (T) -> String,
    items: List<T>,
    buttonColors: ((T) -> ButtonColors)? = null,
    modifier: Modifier = Modifier,
    size: ButtonGroupSize = ButtonGroupSize.Default,
) {
    val itemCount = items.size
    val layoutDirection = LocalLayoutDirection.current
    val defaultColors = rememberDefaultButtonColors()

    ScrollingRow(modifier, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        items.forEachIndexed { index, item ->
            item {
                val colors = when (buttonColors) {
                    null -> defaultColors
                    else -> remember { buttonColors(item) }
                }

                val isSelected = item == value
                val isFirst = index == 0
                val isLast = index == itemCount - 1

                val color by animateColorAsState(if (isSelected) colors.selectedContainerColor else colors.unselectedContainerColor)
                val contentColor by animateColorAsState(if (isSelected) colors.selectedContentColor else colors.unselectedContentColor)

                val startRadius by animateDpAsState(
                    when {
                        isSelected || isFirst -> size.largeRadius
                        else -> size.smallRadius
                    }
                )
                val endRadius by animateDpAsState(
                    when {
                        isSelected || isLast -> size.largeRadius
                        else -> size.smallRadius
                    }
                )

                SelectableButton(
                    label(item),
                    ButtonDefaults.buttonColors(color, contentColor),
                    { onValueChange(item) },
                    RoundedCornerShape(
                        topStart = startRadius,
                        bottomStart = startRadius,
                        topEnd = endRadius,
                        bottomEnd = endRadius
                    ),
                    size,
                    layoutDirection,
                )
            }
        }
    }
}


@Composable
private fun SelectableButton(
    label: String,
    colors: androidx.compose.material3.ButtonColors,
    onClick: () -> Unit,
    shape: Shape,
    size: ButtonGroupSize,
    layoutDirection: LayoutDirection,
) {
    Button(
        onClick = onClick,
        colors = colors,
        shape = shape,
        contentPadding = size.contentPadding(layoutDirection),
        modifier = Modifier.heightIn(max = if (size == ButtonGroupSize.Small) 32.dp else Dp.Unspecified),
    ) {
        Text(label, style = size.textStyle)
    }
}


@Composable
private fun rememberDefaultButtonColors(): ButtonColors {
    val primaryContainer = colorScheme.primary
    val onPrimaryContainer = colorScheme.onPrimary
    val secondaryContainer = colorScheme.secondaryContainer
    val onSecondaryContainer = colorScheme.onSecondaryContainer
    return remember(
        primaryContainer,
        onPrimaryContainer,
        secondaryContainer,
        onSecondaryContainer
    ) {
        ButtonColors(
            selectedContainerColor = primaryContainer,
            selectedContentColor = onPrimaryContainer,
            unselectedContainerColor = secondaryContainer,
            unselectedContentColor = onSecondaryContainer
        )
    }
}
