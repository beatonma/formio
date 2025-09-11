package org.beatonma.gclocks.compose.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private val ButtonSmallRadius = 8.dp
private val ButtonLargeRadius = 32.dp

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
    label: (T) -> String,
    onValueChange: (T) -> Unit,
    items: List<T>,
    modifier: Modifier = Modifier,
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
                    isSelected || isFirst -> ButtonLargeRadius
                    else -> ButtonSmallRadius
                }
            )
            val endRadius by animateDpAsState(
                when {
                    isSelected || isLast -> ButtonLargeRadius
                    else -> ButtonSmallRadius
                }
            )
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
                )
            ) {
                Text(label(item))
            }
        }
    }
}