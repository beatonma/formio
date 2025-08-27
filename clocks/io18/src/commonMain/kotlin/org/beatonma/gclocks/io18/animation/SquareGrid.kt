package org.beatonma.gclocks.io18.animation

import org.beatonma.gclocks.core.geometry.HorizontalAlignment
import org.beatonma.gclocks.core.geometry.VerticalAlignment
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.Path
import org.beatonma.gclocks.core.graphics.Stroke
import org.beatonma.gclocks.io18.Io18Paints
import kotlin.math.floor


private const val SpaceBetween = Io18Paints.HalfStrokeWidth
private const val StrokeWidth = Io18Paints.ThinStrokeWidth

/**
 * A grid of squares with enter/exit animations.
 */
internal class SquareGrid(
    override val left: Float,
    override val top: Float,
    override val rows: Int,
    override val columns: Int,
    /**
     * Distance from the geometric center of one line to the geometric center of the next
     * NOT edge to edge - line width is not accounted for!
     */
) : Io18GridAnimation {
    override val spaceBetween: Float = SpaceBetween
    private val style: Stroke = Stroke(StrokeWidth)
    private val cellCount: Int = rows * columns

    companion object {
        fun fill(
            left: Float, top: Float, width: Float, height: Float,
            horizontalAlignment: HorizontalAlignment, verticalAlignment: VerticalAlignment,
        ): SquareGrid {
            val halfPaintWidth: Float = StrokeWidth / 2f
            val insetWidth: Float = width - StrokeWidth
            val insetHeight: Float = height - StrokeWidth
            val columns: Int = floor(insetWidth / SpaceBetween).toInt()
            val rows: Int = floor(insetHeight / SpaceBetween).toInt()

            return SquareGrid(
                left = left + halfPaintWidth + horizontalAlignment.apply(
                    (columns * SpaceBetween) + StrokeWidth,
                    width
                ),
                top = top + halfPaintWidth + verticalAlignment.apply(
                    (rows * SpaceBetween) + StrokeWidth,
                    height
                ),
                rows = rows,
                columns = columns,
            )
        }
    }

    override fun drawEnter(canvas: Canvas, progress: Float, color: Color) =
        drawEnterLTR(canvas, progress, color)

    override fun drawExit(canvas: Canvas, progress: Float, color: Color) =
        drawExitLTR(canvas, progress, color)

    private fun draw(
        canvas: Canvas,
        progress: Float,
        color: Color,
        rows: Iterable<Int>,
        columns: Iterable<Int>,
    ) {
        val maxCells = floor(progress * cellCount)
        var count = 0

        canvas.drawPath(color, style) {
            for (col in columns) {
                for (row in rows) {
                    if (++count > maxCells) return@drawPath
                    canvas.rect(
                        col * spaceBetween + left,
                        row * spaceBetween + top,
                        (col + 1) * spaceBetween + left,
                        (row + 1) * spaceBetween + top,
                        Path.Direction.Clockwise,
                    )
                }
            }
        }
    }

    fun drawEnterLTR(canvas: Canvas, progress: Float, color: Color) {
        draw(canvas, easeIn(progress), color, 0 until rows, 0 until columns)
    }

    fun drawEnterRTL(canvas: Canvas, progress: Float, color: Color) {
        draw(canvas, easeIn(progress), color, 0 until rows, (0 until columns).reversed())
    }

    fun drawExitLTR(canvas: Canvas, progress: Float, color: Color) {
        draw(
            canvas,
            easeOut(1f - progress),
            color,
            (0 until rows).reversed(),
            (0 until columns).reversed()
        )
    }

    fun drawExitRTL(canvas: Canvas, progress: Float, color: Color) {
        draw(canvas, easeOut(1f - progress), color, 0 until rows, 0 until columns)
    }
}
