package org.beatonma.gclocks.io18

import org.beatonma.gclocks.core.LoadingSpinner
import org.beatonma.gclocks.core.geometry.HorizontalAlignment
import org.beatonma.gclocks.core.geometry.VerticalAlignment
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.graphics.Stroke
import org.beatonma.gclocks.core.util.interpolate
import org.beatonma.gclocks.core.util.progress
import org.beatonma.gclocks.io18.animation.DiagonalLines
import org.beatonma.gclocks.io18.animation.DiagonalLines.Direction
import org.beatonma.gclocks.io18.animation.Io18Animation
import org.beatonma.gclocks.io18.animation.RectangularDotGroup
import org.beatonma.gclocks.io18.animation.SquareGrid


class Io18LoadingSpinner(
    override val paints: Paints = Io18Paints()
) : LoadingSpinner {
    override val size: Float = 100f
    private val lines = DiagonalLines.fill(
        0f, 0f, size, size,
        HorizontalAlignment.Center, VerticalAlignment.Center,
        Direction.Forward
    )
    private val dots = RectangularDotGroup.fill(
        0f, 0f, size, size,
        HorizontalAlignment.Center, VerticalAlignment.Center
    )
    private val grid = SquareGrid.fill(
        0f, 0f, size, size,
        HorizontalAlignment.Center, VerticalAlignment.Center
    )
    private val totalDuration = 3200
    private val animations: List<Io18Animation.ClassDraw> = listOf(lines, dots, grid).shuffled()
    private val stepSize = 1f / animations.size.toFloat()

    override fun draw(canvas: Canvas, currentTimeMillis: Long) {
        val progressMillis = currentTimeMillis % totalDuration
        val totalProgress = progress(progressMillis.toFloat(), 0f, totalDuration.toFloat())

        animations.forEachIndexed { index, animation ->
            val color = paints.colors[index % paints.colors.size]
            val stepProgress = interpolate(
                progress(totalProgress, index.toFloat() * stepSize, (index + 1f) * stepSize),
                0f, 1f
            )
            val enterProgress = animation.easeIn(progress(stepProgress, 0f, 0.6f))
            val exitProgress = animation.easeOut(progress(stepProgress, 0.6f, 1f))

            if (exitProgress > 0f) {
                animation.drawExit(canvas, exitProgress, color)
            } else {
                animation.drawEnter(canvas, enterProgress, color)
            }
        }
        canvas.drawRect(paints.colors[0], 0f, 0f, size, size, Stroke.Default)
    }
}
