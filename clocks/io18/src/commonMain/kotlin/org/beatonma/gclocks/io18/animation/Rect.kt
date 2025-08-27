package org.beatonma.gclocks.io18.animation

import org.beatonma.gclocks.core.geometry.RectF
import org.beatonma.gclocks.core.geometry.Rect as GeometryRect
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.DrawStyle
import org.beatonma.gclocks.core.graphics.Fill
import org.beatonma.gclocks.core.types.ProgressFloat
import org.beatonma.gclocks.core.types.pf
import org.beatonma.gclocks.io18.Io18Paints

internal class Rect(
    left: Float,
    top: Float,
    width: Float,
    height: Float,
    private val style: DrawStyle = Fill,
) : Io18Animation.ClassDraw {
    private val rect: GeometryRect<Float> = RectF(left, top, left + width, top + height)
    private val center = rect.center
    private val halfHeight = rect.height / 2f
    private val halfWidth = rect.width / 2f

    private fun draw(canvas: Canvas, progress: ProgressFloat, color: Color) {
        canvas.drawRect(
            color,
            center.x - (progress * halfWidth),
            center.y - (progress * halfHeight),
            center.x + (progress * halfWidth),
            center.y + (progress * halfHeight),
            style
        )
    }

    override fun drawEnter(canvas: Canvas, progress: Float, color: Color) {
        draw(canvas, easeIn(progress).pf, color)
    }

    override fun drawExit(canvas: Canvas, progress: Float, color: Color) {
        draw(canvas, easeOut(1f - progress).pf, color)
    }
}

internal fun Square(
    left: Float,
    top: Float,
    size: Float = Io18Paints.ThickStrokeWidth,
    style: DrawStyle = Fill,
) =
    Rect(left, top, size, size, style)