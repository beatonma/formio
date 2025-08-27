package org.beatonma.gclocks.io18.animation

import org.beatonma.gclocks.core.geometry.HorizontalAlignment
import org.beatonma.gclocks.core.geometry.VerticalAlignment
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.util.distance
import org.beatonma.gclocks.core.util.fastForEach
import org.beatonma.gclocks.io18.Io18Paints
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.max


private const val StrokeWidth = Io18Paints.ThinStrokeWidth
private const val SpaceBetween = Io18Paints.HalfStrokeWidth


internal class DiagonalLines(
    val left: Float,
    val top: Float,
    rows: Int,
    columns: Int,
    direction: Direction,
) : Io18Animation.ClassDraw {
    enum class Direction {
        Forward,
        Backward,
    }

    private val lines: Array<Line>

    init {
        val lineCount = rows + columns - 1
        lines = Array(lineCount) { index ->
            val i = index + 1
            val x1: Int
            val x2: Int
            val y1: Int
            val y2: Int

            when (direction) {
                Direction.Forward -> {
                    x1 = if (i < rows) 0 else i - rows
                    y1 = min(i, rows)
                    x2 = min(i, columns)
                    y2 = if (i < columns) 0 else i - columns
                }

                Direction.Backward -> {
                    x1 = if (i < rows) 0 else i - rows
                    y1 = max(0, rows - i)
                    x2 = min(i, columns)
                    y2 = min(rows, lineCount + 1 - i)
                }
            }

            when {
                i % 2 == 0 -> {
                    Line(
                        x1 * SpaceBetween, y1 * SpaceBetween,
                        x2 * SpaceBetween, y2 * SpaceBetween,
                    )
                }

                else -> {
                    Line(
                        x2 * SpaceBetween, y2 * SpaceBetween,
                        x1 * SpaceBetween, y1 * SpaceBetween,
                    )
                }
            }
        }
    }

    override fun drawEnter(canvas: Canvas, progress: Float, color: Color) {
        canvas.withTranslation(left, top) {
            lines.fastForEach { it.drawEnter(canvas, progress, color) }
        }
    }

    override fun drawExit(canvas: Canvas, progress: Float, color: Color) {
        canvas.withTranslation(left, top) {
            lines.fastForEach { it.drawExit(canvas, progress, color) }
        }
    }

    companion object {
        fun fill(
            left: Float,
            top: Float,
            width: Float,
            height: Float,
            horizontalAlignment: HorizontalAlignment,
            verticalAlignment: VerticalAlignment,
            direction: Direction,
        ): DiagonalLines {
            val halfPaintWidth: Float = StrokeWidth / 2f
            val insetWidth: Float = width - StrokeWidth
            val insetHeight: Float = height - StrokeWidth
            val columns: Int = floor(insetWidth / SpaceBetween).toInt()
            val rows: Int = floor(insetHeight / SpaceBetween).toInt()

            return DiagonalLines(
                left = left + halfPaintWidth + horizontalAlignment.apply(
                    (columns * SpaceBetween) + StrokeWidth,
                    width
                ),
                top = top + halfPaintWidth + verticalAlignment.apply(
                    (rows * SpaceBetween) + StrokeWidth,
                    height
                ),
                rows, columns, direction,
            )
        }
    }
}

private class Line(
    val x1: Float,
    val y1: Float,
    val x2: Float,
    val y2: Float,
) : Io18Animation.ClassDraw {
    private val style = Io18Paints.thinStroke

    private fun draw(canvas: Canvas, progress: Float, color: Color) {
        val startX = x1 + (x2 - x1) * progress
        val startY = y1 + (y2 - y1) * progress

        if (distance(startX, startY, x2, y2) < 1f) {
            /*
             * Skip drawing if line is very short.
             * Otherwise, lines appear as dots and are visible for a little too long.
             */
            return
        }
        canvas.drawLine(color, startX, startY, x2, y2, style)
    }

    override fun drawEnter(canvas: Canvas, progress: Float, color: Color) {
        draw(canvas, easeIn(1f - progress), color)
    }

    override fun drawExit(canvas: Canvas, progress: Float, color: Color) {
        draw(canvas, easeOut(progress), color)
    }
}