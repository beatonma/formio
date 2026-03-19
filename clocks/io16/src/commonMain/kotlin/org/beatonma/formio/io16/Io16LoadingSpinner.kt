package org.beatonma.formio.io16

import org.beatonma.formio.core.LoadingSpinner
import org.beatonma.formio.core.graphics.Canvas
import org.beatonma.formio.core.graphics.Paints
import org.beatonma.formio.core.graphics.Path
import org.beatonma.formio.core.graphics.Stroke
import org.beatonma.formio.core.types.pf
import org.beatonma.formio.core.util.decelerate5
import org.beatonma.formio.core.util.progress

class Io16LoadingSpinner(
    override val paints: Paints = Io16Paints(),
) : LoadingSpinner {
    override val size: Float = 100f
    private val pathRenderer = Io16PathRenderer(
        style = Stroke(2f)
    )
    private val totalDuration = 4000L
    private val rotationDuration = totalDuration

    private val enterDuration = 600L
    private val exitDuration = 300L
    private val exitStarts = totalDuration - exitDuration

    private fun ease(f: Float): Float = decelerate5(f)

    override fun draw(canvas: Canvas, currentTimeMillis: Long) {
        val progressMillis = totalDuration % currentTimeMillis

        val enterProgress = ease(progress(progressMillis, 0, enterDuration))
        val exitProgress = ease(progress(progressMillis, exitStarts, totalDuration))

        val offset = 1f - progress(
            (progressMillis % rotationDuration).toFloat(),
            0f,
            rotationDuration.toFloat()
        )
        canvas.circle(size / 2f, size / 2f, size / 2f, Path.Direction.Clockwise)

        val invisible: Float
        val state: Io16PathRenderer.State
        if (exitProgress > 0f) {
            state = Io16PathRenderer.State.Disappearing
            invisible = exitProgress
        } else {
            state = Io16PathRenderer.State.Appearing
            invisible = 1f - enterProgress
        }

        pathRenderer.drawSegments(
            canvas,
            paints,
            offset = offset,
            invisible = invisible.pf,
            inactive = 0f.pf,
            state = state
        )
    }
}
