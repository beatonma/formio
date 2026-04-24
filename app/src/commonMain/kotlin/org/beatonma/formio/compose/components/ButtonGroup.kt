package org.beatonma.formio.compose.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.beatonma.formio.app.theme.getForegroundColor
import org.beatonma.formio.app.theme.tokens.ButtonGroupTokens

enum class ButtonGroupSize {
    Small,
    Default,
    ;
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

    val defaultColors = rememberDefaultButtonColors()

    val tokens = when (size) {
        ButtonGroupSize.Small -> ButtonGroupTokens.Small
        ButtonGroupSize.Default -> ButtonGroupTokens.Default
    }

    val textStyle = tokens.TextStyle
    val contentPadding = tokens.ContentPadding
    val buttonModifier = Modifier.height(tokens.Height)

    ScrollingRow(modifier, horizontalArrangement = Arrangement.spacedBy(tokens.ButtonSpacing)) {
        itemsIndexed(items) { index, item ->
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
                    isSelected || isFirst -> tokens.LargeRadius
                    else -> tokens.SmallRadius
                }
            )
            val endRadius by animateDpAsState(
                when {
                    isSelected || isLast -> tokens.LargeRadius
                    else -> tokens.SmallRadius
                }
            )

            Button(
                onClick = { onValueChange(item) },
                colors = ButtonDefaults.buttonColors(color, contentColor),
                shape = RoundedCornerShape(
                    topStart = startRadius,
                    bottomStart = startRadius,
                    topEnd = endRadius,
                    bottomEnd = endRadius
                ),
                contentPadding = contentPadding,
                modifier = buttonModifier,
            ) {
                Text(label(item), style = textStyle)
            }
        }
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
