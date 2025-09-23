package org.beatonma.gclocks.compose.components

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import org.beatonma.gclocks.app.settings.DisplayContext
import org.beatonma.gclocks.compose.toCompose

/**
 * Wrapper for [Clock] which applies background and positioning from [displayOptions].
 */
@Composable
fun ClockLayout(
    displayOptions: DisplayContext.Options.WithBackground,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier.background(displayOptions.backgroundColor.toCompose()),
        content = content,
    ) { measurables, constraints ->
        val position = displayOptions.position
        val left = position.left * constraints.maxWidth
        val top = position.top * constraints.maxHeight
        val width = position.width * constraints.maxWidth
        val height = position.height * constraints.maxHeight

        val placeables = measurables.map {
            it.measure(
                Constraints(
                    maxWidth = width.toInt(),
                    maxHeight = height.toInt()
                )
            )
        }

        layout(constraints.maxWidth, constraints.maxHeight) {
            placeables.forEach { it.place(left.toInt(), top.toInt()) }
        }
    }
}
