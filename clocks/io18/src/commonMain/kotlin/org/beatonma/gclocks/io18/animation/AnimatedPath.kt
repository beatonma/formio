package org.beatonma.gclocks.io18.animation

import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.PathDefinition
import org.beatonma.gclocks.core.types.ProgressFloat
import org.beatonma.gclocks.core.types.pf
import org.beatonma.gclocks.io18.Io18Paints
import org.beatonma.gclocks.core.graphics.Path as GraphicsPath


internal class AnimatedPath(
    private val path: GraphicsPath,
) : Io18Animation.InlineDraw {
    private val style = Io18Paints.thickStroke

    private inline fun draw(
        canvas: Canvas,
        start: ProgressFloat,
        end: ProgressFloat,
        color: Color,
        block: Canvas.() -> Unit,
    ) {
        canvas.beginPath()
        path.beginPath()
        if (end != start) {
            canvas.block()
            canvas.measurePath { pm ->
                canvas.drawPath(
                    pm.getSegment(start * pm.length, end * pm.length, path),
                    color,
                    style
                )
            }
        }
    }

    override fun drawEnter(
        canvas: Canvas,
        progress: Float,
        color: Color,
        block: Canvas.() -> Unit,
    ) {
        draw(canvas, ProgressFloat.Zero, easeIn(progress).pf, color, block)
    }

    override fun drawExit(
        canvas: Canvas,
        progress: Float,
        color: Color,
        block: Canvas.() -> Unit,
    ) {
        draw(canvas, easeOut(progress).pf, ProgressFloat.One, color, block)
    }
}

internal fun AnimatedPath.drawEnter(
    canvas: Canvas,
    progress: Float,
    color: Color,
    path: PathDefinition,
) {
    drawEnter(canvas, progress, color) {
        path.plot(canvas)
    }
}

internal fun AnimatedPath.drawExit(
    canvas: Canvas,
    progress: Float,
    color: Color,
    path: PathDefinition,
) {
    drawExit(canvas, progress, color) {
        path.plot(canvas)
    }
}