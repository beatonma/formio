package org.beatonma.gclocks.core.graphics

import org.beatonma.gclocks.core.geometry.Angle
import org.beatonma.gclocks.core.geometry.Rect

private const val DefaultAlpha = 1f

enum class StrokeCap {
    Round,
    Butt,
    Square,
    ;

    companion object {
        val Default = Square
    }
}

enum class StrokeJoin {
    Miter,
    Round,
    Bevel,
    ;

    companion object {
        val Default = Miter
    }
}

sealed class DrawStyle
object Fill : DrawStyle()

data class Stroke(
    val width: Float = 0f,
    val miter: Float = 0f,
    val cap: StrokeCap = StrokeCap.Default,
    val join: StrokeJoin = StrokeJoin.Default,
) : DrawStyle() {
    companion object {
        val Default = Stroke()
    }
}


private typealias CanvasAction = Canvas.() -> Unit

interface Canvas : Path {
    fun measurePath(block: (scope: PathMeasureScope) -> Unit)

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
        block: CanvasAction,
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
        alpha: Float = 1f,
    )

    fun drawText(text: String)

    fun save()
    fun restore()

    fun withScale(
        scale: Float,
        block: CanvasAction,
    )

    fun withScale(
        scaleX: Float,
        scaleY: Float,
        block: CanvasAction,
    )

    fun withScale(
        scale: Float,
        pivotX: Float,
        pivotY: Float,
        block: CanvasAction,
    )

    fun withScale(
        scaleX: Float,
        scaleY: Float,
        pivotX: Float,
        pivotY: Float,
        block: CanvasAction,
    )

    fun withRotation(
        angle: Angle,
        pivotX: Float,
        pivotY: Float,
        block: CanvasAction,
    )

    fun withTranslation(
        x: Float,
        y: Float,
        block: CanvasAction,
    )

    fun withTranslationAndScale(
        x: Float,
        y: Float,
        scale: Float,
        block: CanvasAction,
    )

    fun clear()
}
