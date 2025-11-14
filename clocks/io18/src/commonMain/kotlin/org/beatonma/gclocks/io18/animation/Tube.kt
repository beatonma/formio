package org.beatonma.gclocks.io18.animation

import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.Path
import org.beatonma.gclocks.core.graphics.StrokeCap
import org.beatonma.gclocks.core.graphics.paths.PathDefinition
import org.beatonma.gclocks.core.types.ProgressFloat
import org.beatonma.gclocks.core.types.pf
import org.beatonma.gclocks.core.util.progress
import org.beatonma.gclocks.io18.Io18Paints


internal class Tube(private val path: Path) : Io18Animation {
    private val buttStyle = Io18Paints.thickStroke
    private val roundedStyle = buttStyle.copy(cap = StrokeCap.Round)
    private val radius = buttStyle.width / 2

    private inline fun draw(
        canvas: Canvas,
        lineColor: Color,
        endColor: Color,
        start: ProgressFloat,
        end: ProgressFloat,
        endProgress: ProgressFloat,
        cap: StrokeCap,
        block: Canvas.() -> Unit,
    ) {
        canvas.beginPath()
        path.beginPath()
        canvas.block()
        canvas.measurePath { pm ->
            val endDistance = end * pm.length
            if (end != start) {
                canvas.drawPath(
                    pm.getSegment(start * pm.length, endDistance, path),
                    lineColor,
                    when (cap) {
                        StrokeCap.Butt -> buttStyle
                        StrokeCap.Round -> roundedStyle
                        StrokeCap.Square -> throw IllegalArgumentException("Tube cap must be either Butt or Round")
                    }
                )
            }

            if (endProgress > 0f) {
                pm.getPosition(endDistance)?.let { endPoint ->
                    canvas.drawCircle(endColor, endPoint.x, endPoint.y, endProgress * radius)
                }
            }
        }
    }

    fun drawEnter(
        canvas: Canvas,
        progress: Float,
        lineColor: Color,
        endColor: Color,
        cap: StrokeCap = StrokeCap.Butt,
        block: Canvas.() -> Unit,
    ) {
        val eased = easeIn(progress)

        when {
            eased < 0.1f -> draw(
                canvas = canvas,
                lineColor = lineColor,
                endColor = endColor,
                start = ProgressFloat.Zero,
                end = ProgressFloat.Zero,
                endProgress = progress(eased, 0f, 0.1f).pf,
                cap = cap,
                block = block,
            )

            else ->
                draw(
                    canvas = canvas,
                    lineColor = lineColor,
                    endColor = endColor,
                    start = ProgressFloat.Zero,
                    end = progress(eased, 0.1f, 1f).pf,
                    endProgress = ProgressFloat.One,
                    cap = cap,
                    block = block,
                )
        }
    }

    fun drawExit(
        canvas: Canvas,
        progress: Float,
        lineColor: Color,
        endColor: Color,
        cap: StrokeCap = StrokeCap.Butt,
        block: Canvas.() -> Unit,
    ) {
        when {
            progress < 0.1f -> {
                val eased = easeOut(progress(progress, 0f, 0.1f))
                draw(
                    canvas = canvas,
                    lineColor = lineColor,
                    endColor = endColor,
                    start = eased.pf,
                    end = ProgressFloat.One,
                    endProgress = ProgressFloat.One,
                    cap = cap,
                    block = block,
                )
            }


            progress < 0.2f -> draw(
                canvas = canvas,
                lineColor = lineColor,
                endColor = endColor,
                start = ProgressFloat.One,
                end = ProgressFloat.One,
                endProgress = ProgressFloat.One,
                cap = cap,
                block = block,
            )


            else -> draw(
                canvas = canvas,
                lineColor = lineColor,
                endColor = endColor,
                start = ProgressFloat.One,
                end = ProgressFloat.One,
                endProgress = easeOut(1f - progress(progress, 0.4f, 1f)).pf,
                cap = cap,
                block = block,
            )
        }
    }
}

internal fun Tube.drawEnter(
    canvas: Canvas,
    progress: Float,
    lineColor: Color,
    endColor: Color,
    cap: StrokeCap = StrokeCap.Butt,
    path: PathDefinition,
) {
    drawEnter(canvas, progress, lineColor, endColor, cap) { path.plot(canvas) }
}

internal fun Tube.drawExit(
    canvas: Canvas,
    progress: Float,
    lineColor: Color,
    endColor: Color,
    cap: StrokeCap = StrokeCap.Butt,
    path: PathDefinition,
) {
    drawExit(canvas, progress, lineColor, endColor, cap) { path.plot(canvas) }
}
