package org.beatonma.gclocks.compose.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
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

/**
 * Approximate implementation of Connected button group:
 *   https://m3.material.io/components/button-groups/specs
 *
 * Tried using the current alpha M3 Expressive implementation but it was a mess
 * - try it again later to see if we can use it instead of this.
 */
@Composable
fun <T> ButtonGroup(
    value: T,
    label: @Composable (T) -> String,
    onValueChange: (T) -> Unit,
    items: List<T>,
    modifier: Modifier = Modifier,
    size: ButtonGroupSize = ButtonGroupSize.Default,
) {
    val itemCount = items.size
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEachIndexed { index, item ->
            val isSelected = item == value
            val isFirst = index == 0
            val isLast = index == itemCount - 1

            val color by animateColorAsState(if (isSelected) colorScheme.primaryContainer else colorScheme.secondary)
            val contentColor by animateColorAsState(if (isSelected) colorScheme.onPrimaryContainer else colorScheme.onSecondary)
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

            val layoutDirection = LocalLayoutDirection.current

            Button(
                onClick = { onValueChange(item) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = color,
                    contentColor = contentColor
                ),
                shape = RoundedCornerShape(
                    topStart = startRadius,
                    bottomStart = startRadius,
                    topEnd = endRadius,
                    bottomEnd = endRadius
                ),
                contentPadding = size.contentPadding(layoutDirection),
                modifier = Modifier.heightIn(max = if (size == ButtonGroupSize.Small) 32.dp else Dp.Unspecified),
            ) {
                Text(label(item), style = size.textStyle)
            }
        }
    }
}
