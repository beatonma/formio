package org.beatonma.formio.io18.animation

import org.beatonma.formio.core.graphics.Canvas
import org.beatonma.formio.core.graphics.Color
import org.beatonma.formio.core.graphics.Path
import org.beatonma.formio.core.graphics.Stroke
import org.beatonma.formio.core.util.fastForEachIndexed

private const val StrokeWidth = 6.5f

internal class ConcentricCircles(
    val centerX: Float,
    val centerY: Float,
    val radii: List<Float>,
) : Io18Animation {

    private fun draw(canvas: Canvas, progress: Float, colors: Array<Color>) {
        val strokeWidth = StrokeWidth * progress
        if (strokeWidth <= 0.1f) return
        val stroke = Stroke(strokeWidth)
        radii.fastForEachIndexed { index, radius ->
            canvas.drawPath(colors[index], stroke) {
                circle(centerX, centerY, radius, Path.Direction.Clockwise)
            }
        }
    }

    fun drawEnter(canvas: Canvas, progress: Float, colors: Array<Color>) {
        draw(canvas, easeIn(progress), colors)
    }

    fun drawExit(canvas: Canvas, progress: Float, colors: Array<Color>) {
        draw(canvas, easeOut(1f - progress), colors)
    }
}