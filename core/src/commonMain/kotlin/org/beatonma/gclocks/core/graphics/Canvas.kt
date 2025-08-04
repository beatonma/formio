package org.beatonma.gclocks.core.graphics

import org.beatonma.gclocks.core.geometry.Angle
import org.beatonma.gclocks.core.geometry.Point
import org.beatonma.gclocks.core.geometry.Rect

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


interface Canvas<T> : CanvasPath {
    fun drawCircle(
        centerX: Float,
        centerY: Float,
        radius: Float,
        color: Color,
        style: DrawStyle,
        alpha: Float = 1f,
    )

    fun drawCircle(
        center: Position,
        radius: Float,
        color: Color,
        style: DrawStyle,
        alpha: Float = 1f,
    ) = drawCircle(
        centerX = center.x,
        centerY = center.y,
        radius = radius,
        color = color,
        style = style,
        alpha = alpha,
    )

    fun drawLine(
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        color: Color,
        style: Stroke,
        alpha: Float = 1f,
    )

    fun drawLine(
        start: Position,
        end: Position,
        color: Color,
        style: Stroke,
        alpha: Float = 1f,
    ) = drawLine(
        x1 = start.x,
        y1 = start.y,
        x2 = end.x,
        y2 = end.y,
        color = color,
        style = style,
        alpha = alpha,
    )

    fun drawRect(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        color: Color,
        style: DrawStyle,
        alpha: Float = 1f,
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
        rect: Rect<Float>,
        color: Color,
        style: DrawStyle,
        alpha: Float = 1f,
    ) = drawRect(
        left = rect.left,
        top = rect.top,
        right = rect.right,
        bottom = rect.bottom,
        color = color,
        style = style,
        alpha = alpha
    )

    /**
     * Render the currently plotted path to the canvas.
     */
    fun drawPath(
        color: Color,
        style: DrawStyle,
        alpha: Float = 1f,
    )

    fun drawPath(
        color: Color,
        style: DrawStyle,
        alpha: Float = 1f,
        block: Canvas<T>.() -> Unit,
    ) {
        beginPath()

        block()
        drawPath(color, style, alpha)
        closePath()
    }

    fun withCheckpoint(block: Canvas<T>.() -> Unit) {
        save()
        block()
        restore()
    }

    fun save()
    fun restore()

    fun withScale(
        scale: Float,
        block: Canvas<T>.() -> Unit,
    )

    fun withScale(
        scaleX: Float,
        scaleY: Float,
        block: Canvas<T>.() -> Unit,
    )

    fun withScale(
        scale: Float,
        pivotX: Float,
        pivotY: Float,
        block: Canvas<T>.() -> Unit,
    )

    fun withScale(
        scaleX: Float,
        scaleY: Float,
        pivotX: Float,
        pivotY: Float,
        block: Canvas<T>.() -> Unit,
    )

    fun scaleWithPivot(
        scaleX: Float,
        scaleY: Float,
        pivotX: Float,
        pivotY: Float,
        block: Canvas<T>.() -> Unit,
    ) {
        withTranslation(pivotX, pivotY) {
            withScale(scaleX, scaleY) {
                block()
            }
        }
    }

    fun withRotation(
        angle: Angle,
        pivotX: Float,
        pivotY: Float,
        block: Canvas<T>.() -> Unit,
    )

    fun withTranslation(
        x: Float,
        y: Float,
        block: Canvas<T>.() -> Unit,
    )

    fun withTranslationAndScale(
        x: Float,
        y: Float,
        scale: Float,
        block: Canvas<T>.() -> Unit,
    )

    fun clear()
}


interface CanvasPath {
    fun moveTo(x: Float, y: Float)
    fun moveTo(position: Position) {
        moveTo(position.x, position.y)
    }

    fun lineTo(x: Float, y: Float)
    fun lineTo(position: Position) {
        lineTo(position.x, position.y)
    }

    fun boundedArc(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        startAngle: Angle,
        sweepAngle: Angle,
    )

    fun beginPath()
    fun closePath()
}

inline fun <T> Canvas<T>.withPath(block: Canvas<T>.() -> Unit) {
    block()
    closePath()
}
