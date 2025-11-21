package org.beatonma.gclocks.form

import org.beatonma.gclocks.core.LoadingSpinner
import org.beatonma.gclocks.core.geometry.Angle
import org.beatonma.gclocks.core.geometry.degrees
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.util.decelerate5
import org.beatonma.gclocks.core.util.progress

private const val OneThird = 1f / 3f
private const val TwoThirds = 2f / 3f

class FormLoadingSpinner(
    override val paints: Paints = FormPaints()
) : LoadingSpinner {
    override val size: Float = 100f
    private val center: Float = size / 2f
    private val rotationDuration = 16_000
    private val totalDuration = 2400
    private var previousProgress = 0f
    private var rotationOffset = 0f

    // Progress where the animation changes from appearing to disappearing state.
    private val inflection = 0.7f

    private fun ease(f: Float): Float = decelerate5(f)

    override fun draw(canvas: Canvas, currentTimeMillis: Long) {
        val rotationProgress = progress(
            (currentTimeMillis % rotationDuration).toFloat(),
            0f,
            rotationDuration.toFloat()
        )
        val totalProgress = progress(
            (currentTimeMillis % totalDuration).toFloat(),
            0f,
            totalDuration.toFloat()
        )
        if (totalProgress < previousProgress) {
            rotationOffset = (rotationOffset + 120f) % 360f
        }
        previousProgress = totalProgress
        val rotation = (rotationProgress * 360f) + rotationOffset

        canvas.withRotation(rotation.degrees, center, center) {
            if (totalProgress < inflection) {
                val p = progress(totalProgress, 0f, inflection)
                val p1 = ease(progress(p, 0f, OneThird))
                val p2 = ease(progress(p, OneThird, TwoThirds))
                val p3 = ease(progress(p, TwoThirds, 1f))
                drawSector(
                    paints.colors[2],
                    (p2 * 240f).degrees,
                    (p3 * 120f).degrees,
                )
                drawSector(
                    paints.colors[1],
                    (p1 * 120f).degrees,
                    (p2 * 120f).degrees,
                )
                drawSector(
                    paints.colors[0],
                    0f.degrees,
                    (p1 * 120f).degrees
                )
            } else {
                val p = progress(totalProgress, inflection, 1f)
                val p1 = ease(progress(p, 0f, OneThird))
                val p2 = ease(progress(p, OneThird, TwoThirds))
                val p3 = ease(progress(p, TwoThirds, 1f))
                drawSector(
                    paints.colors[0],
                    (p1 * 120f).degrees,
                    ((1f - p1) * 120f).degrees
                )
                drawSector(
                    paints.colors[1],
                    (120f + (p2 * 120f)).degrees,
                    ((1f - p2) * 120f).degrees
                )
                drawSector(
                    paints.colors[2],
                    (240f + (p3 * 120f)).degrees,
                    ((1f - p3) * 120f).degrees
                )
            }
        }
    }

    private fun Canvas.drawSector(color: Color, startAngle: Angle, sweepAngle: Angle) {
        drawSector(color, center, center, center, startAngle, sweepAngle)
    }
}
