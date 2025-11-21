package org.beatonma.gclocks.io16

import org.beatonma.gclocks.core.LoadingSpinner
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.graphics.Path
import org.beatonma.gclocks.core.graphics.Stroke
import org.beatonma.gclocks.core.types.pf
import org.beatonma.gclocks.core.util.decelerate5
import org.beatonma.gclocks.core.util.progress

class Io16LoadingSpinner(
    path: Path,
    override val paints: Paints = Io16Paints(),
) : LoadingSpinner {
    override val size: Float = 100f
    private val pathRenderer = Io16PathRenderer(
        segmentPath = path,
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
