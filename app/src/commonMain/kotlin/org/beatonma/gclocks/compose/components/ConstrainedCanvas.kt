package org.beatonma.gclocks.compose.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import org.beatonma.gclocks.core.geometry.ConstrainedLayout
import org.beatonma.gclocks.core.geometry.MeasureConstraints

@Composable
fun ConstrainedCanvas(
    constrainedLayout: ConstrainedLayout,
    modifier: Modifier = Modifier,
    onDraw: DrawScope.() -> Unit,
) {
    Layout(
        modifier = modifier,
        content = {
            Canvas(Modifier, onDraw)
        }
    ) { measurables, constraints ->
        val measuredSize = constrainedLayout.setConstraints(constraints.toMeasureConstraints())
        val placeable = measurables.first().measure(
            Constraints.fixed(
                measuredSize.width.toInt(),
                measuredSize.height.toInt(),
            )
        )

        layout(placeable.width, placeable.height) {
            placeable.placeRelative(0, 0)
        }
    }
}

private fun Constraints.toMeasureConstraints(): MeasureConstraints = MeasureConstraints(
    maxWidth = when {
        hasBoundedWidth -> maxWidth.toFloat()
        else -> MeasureConstraints.Infinity
    },
    maxHeight = when {
        hasBoundedHeight -> maxHeight.toFloat()
        else -> MeasureConstraints.Infinity
    },
)