package org.beatonma.formio.io18.animation

import org.beatonma.formio.core.geometry.RectF
import org.beatonma.formio.core.geometry.Rect as GeometryRect
import org.beatonma.formio.core.graphics.Canvas
import org.beatonma.formio.core.graphics.Color
import org.beatonma.formio.core.graphics.DrawStyle
import org.beatonma.formio.core.graphics.Fill
import org.beatonma.formio.core.types.ProgressFloat
import org.beatonma.formio.core.types.pf
import org.beatonma.formio.io18.Io18Paints

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