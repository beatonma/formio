package org.beatonma.gclocks.io18.animation

import org.beatonma.gclocks.core.geometry.HorizontalAlignment
import org.beatonma.gclocks.core.geometry.VerticalAlignment
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.Fill
import org.beatonma.gclocks.core.graphics.Path
import org.beatonma.gclocks.core.types.ProgressFloat
import org.beatonma.gclocks.core.types.pf
import org.beatonma.gclocks.core.util.progress
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max

/**
 * A grid-based collection of dots.
 */
internal abstract class DotGroup(
    override val left: Float,
    override val top: Float,
    override val rows: Int,
    override val columns: Int,

    /** Distance from the edge of one dot the edge of its nearest neighbours
     * (edge-to-edge, NOT center-to-center).*/
    override val spaceBetween: Float,
    val dotRadius: Float,
) : Io18GridAnimation

private val style = Fill

internal class RectangularDotGroup(
    x: Float,
    y: Float,
    rows: Int,
    columns: Int,
    spaceBetween: Float,
    dotRadius: Float,
) : DotGroup(x, y, rows, columns, spaceBetween, dotRadius) {
    val cellCount: Int = rows * columns

    companion object {
        fun fill(
            left: Float,
            top: Float,
            width: Float,
            height: Float,
            horizontalAlignment: HorizontalAlignment,
            verticalAlignment: VerticalAlignment,
        ): RectangularDotGroup {
            val spaceBetween = 23.5f
            val dotRadius = 3f
            val diameter = dotRadius * 2
            val columns = floor((width - diameter) / spaceBetween).toInt()
            val rows = floor((height - diameter) / spaceBetween).toInt()

            val objWidth = (columns * (spaceBetween + diameter)) - spaceBetween
            val objHeight = (rows * (spaceBetween + diameter)) - spaceBetween

            return RectangularDotGroup(
                x = left + horizontalAlignment.apply(objWidth, width),
                y = top + verticalAlignment.apply(objHeight, height),
                rows = rows,
                columns = columns,
                spaceBetween = spaceBetween,
                dotRadius = dotRadius,
            )
        }
    }

    override fun drawEnter(canvas: Canvas, progress: Float, color: Color) {
        drawEnterLTR(canvas, progress, color)
    }

    override fun drawExit(canvas: Canvas, progress: Float, color: Color) {
        drawExitLTR(canvas, progress, color)
    }

    private fun draw(
        canvas: Canvas,
        progress: Float,
        color: Color,
        rows: Iterable<Int>,
        columns: Iterable<Int>,
    ) {
        var count = 0
        val perDot = 1f / cellCount

        canvas.drawPath(color, style) {
            for (row in rows) {
                for (col in columns) {
                    canvas.circle(
                        left + (2 * col * dotRadius) + (spaceBetween * col),
                        top + (2 * row * dotRadius) + (spaceBetween * row),
                        dotRadius * progress(progress, count * perDot, (count + 1) * perDot),
                        Path.Direction.Clockwise
                    )
                    count++
                }
            }
        }
    }

    fun drawEnterLTR(canvas: Canvas, progress: Float, color: Color) {
        draw(canvas, easeIn(progress), color, 0 until rows, 0 until columns)
    }

    fun drawExitLTR(canvas: Canvas, progress: Float, color: Color) {
        draw(canvas, easeOut(1f - progress), color, (0 until rows).reversed(), 0 until columns)
    }
}


internal class TriangleDotGroup(
    x: Float,
    y: Float,
    sideLength: Int, // == rows == columns
    spaceBetween: Float,
    dotRadius: Float,
) : DotGroup(x, y, sideLength, sideLength, spaceBetween, dotRadius) {
    val middlish: Float = ceil(sideLength / 2f)
    val perDot: ProgressFloat

    init {
        val sideDots = max(0, sideLength - 2)
        val totalDots = sideDots * sideDots
        perDot = (1f / totalDots).pf
    }

    override fun drawEnter(canvas: Canvas, progress: Float, color: Color) {
        drawEnterRTL(canvas, progress, color)
    }

    override fun drawExit(canvas: Canvas, progress: Float, color: Color) {
        drawExitRTL(canvas, progress, color)
    }

    private fun draw(
        canvas: Canvas,
        progress: Float,
        color: Color,
        rows: Iterable<Int>,
        columns: Iterable<Int>,
    ) {
        var count = 0

        canvas.drawPath(color, style) {
            for (col in columns) {
                for (row in rows) {
                    if (row % 2 == col % 2 && row + col > middlish) {
                        canvas.circle(
                            left + (2 * col * dotRadius) + (spaceBetween * col),
                            top + (2 * row * dotRadius) + (spaceBetween * row),
                            dotRadius * progress(progress, perDot * count, perDot * (count + 1)),
                            Path.Direction.Clockwise
                        )
                        count++
                    }
                }
            }
        }
    }

    fun drawEnterRTL(canvas: Canvas, progress: Float, color: Color) {
        draw(
            canvas,
            easeIn(progress),
            color,
            (0 until rows).reversed(),
            (0 until columns).reversed()
        )
    }

    fun drawExitRTL(canvas: Canvas, progress: Float, color: Color) {
        draw(
            canvas,
            easeOut(1f - progress),
            color,
            (0 until rows).reversed(),
            (0 until columns).reversed()
        )
    }
}