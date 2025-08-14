package org.beatonma.gclocks.core.graphics

import org.beatonma.gclocks.core.geometry.Angle
import org.beatonma.gclocks.core.geometry.Point
import org.beatonma.gclocks.core.geometry.Rect

private const val DefaultAlpha = 1f

enum class StrokeCap {
    Round,
    Butt,
    Square,
    ;
}

enum class StrokeJoin {
    Miter,
    Round,
    Bevel,
    ;
}

sealed class DrawStyle
object Fill : DrawStyle()

class Stroke(
    val width: Float = 0f,
    val miter: Float = 0f,
    val cap: StrokeCap = StrokeCap.Square,
    val join: StrokeJoin = StrokeJoin.Miter,
) : DrawStyle() {
    companion object {
        val Default = Stroke()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Stroke) return false

        if (width != other.width) return false
        if (miter != other.miter) return false
        if (cap != other.cap) return false
        if (join != other.join) return false

        return true
    }

    override fun hashCode(): Int {
        var result = width.hashCode()
        result = 31 * result + miter.hashCode()
        result = 31 * result + cap.hashCode()
        result = 31 * result + join.hashCode()
        return result
    }

    override fun toString(): String {
        return "Stroke(width=$width, miter=$miter, cap=$cap, join=$join"
    }
}


typealias Position = Point<Float>
private typealias CanvasAction<T> = Canvas<T>.() -> Unit
typealias GenericCanvas = Canvas<*>

interface Canvas<T> : Path {
    val pathMeasure: PathMeasure

    fun measurePath()

    fun drawCircle(
        color: Color,
        centerX: Float,
        centerY: Float,
        radius: Float,
        style: DrawStyle = Fill,
        alpha: Float = DefaultAlpha,
    )

    fun drawLine(
        color: Color,
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        style: Stroke = Stroke.Default,
        alpha: Float = DefaultAlpha,
    )

    fun drawRect(
        color: Color,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        style: DrawStyle = Fill,
        alpha: Float = DefaultAlpha,
    ) {
        beginPath()
        moveTo(left, top)
        lineTo(right, top)
        lineTo(right, bottom)
        lineTo(left, bottom)
        closePath()

        drawPath(
            color = color,
            style = style,
            alpha = alpha
        )
    }

    fun drawRect(
        color: Color,
        rect: Rect<Float>,
        style: DrawStyle = Fill,
        alpha: Float = DefaultAlpha,
    ) = drawRect(
        left = rect.left,
        top = rect.top,
        right = rect.right,
        bottom = rect.bottom,
        color = color,
        style = style,
        alpha = alpha
    )

    fun drawRoundRect(
        color: Color,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        radius: Float,
        style: DrawStyle = Fill,
        alpha: Float = DefaultAlpha,
    )

    fun drawBoundedArc(
        color: Color,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        startAngle: Angle = Angle.TwoSeventy,
        sweepAngle: Angle = Angle.OneEighty,
        style: DrawStyle = Fill,
        alpha: Float = DefaultAlpha,
    ) {
        drawPath(color, style, alpha) {
            boundedArc(
                left = left,
                top = top,
                right = right,
                bottom = bottom,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
            )
        }
    }

    fun drawBoundedArc(
        color: Color,
        centerX: Float,
        centerY: Float,
        radius: Float,
        startAngle: Angle = Angle.TwoSeventy,
        sweepAngle: Angle = Angle.OneEighty,
        style: DrawStyle = Fill,
        alpha: Float = DefaultAlpha,
    ) {
        drawPath(color, style, alpha) {
            boundedArc(centerX, centerY, radius, startAngle, sweepAngle)
        }
    }

    /**
     * Render the currently plotted path to the canvas.
     */
    fun drawPath(
        color: Color,
        style: DrawStyle = Fill,
        alpha: Float = DefaultAlpha,
    )

    fun drawPath(
        path: Path,
        color: Color,
        style: DrawStyle = Fill,
        alpha: Float = DefaultAlpha,
    )

    fun drawPath(
        color: Color,
        style: DrawStyle = Fill,
        alpha: Float = DefaultAlpha,
        block: CanvasAction<T>,
    ) {
        beginPath()
        block()
        drawPath(color, style, alpha)
        closePath()
    }

    fun drawPoint(
        x: Float,
        y: Float,
        color: Color = Color.Red,
        style: DrawStyle = Fill,
    )

    fun drawText(text: String)

    fun save()
    fun restore()

    fun withScale(
        scale: Float,
        block: CanvasAction<T>,
    )

    fun withScale(
        scaleX: Float,
        scaleY: Float,
        block: CanvasAction<T>,
    )

    fun withScale(
        scale: Float,
        pivotX: Float,
        pivotY: Float,
        block: CanvasAction<T>,
    )

    fun withScale(
        scaleX: Float,
        scaleY: Float,
        pivotX: Float,
        pivotY: Float,
        block: CanvasAction<T>,
    )

    fun withRotation(
        angle: Angle,
        pivotX: Float,
        pivotY: Float,
        block: CanvasAction<T>,
    )

    fun withTranslation(
        x: Float,
        y: Float,
        block: CanvasAction<T>,
    )

    fun withTranslationAndScale(
        x: Float,
        y: Float,
        scale: Float,
        block: CanvasAction<T>,
    )

    fun clear()
}
