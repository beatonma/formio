package org.beatonma.gclocks.compose

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap as ComposeStrokeCap
import androidx.compose.ui.graphics.StrokeJoin as ComposeStrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import org.beatonma.gclocks.core.geometry.Angle
import androidx.compose.ui.graphics.drawscope.Fill as ComposeFill
import androidx.compose.ui.graphics.drawscope.Stroke as ComposeStroke
import androidx.compose.ui.graphics.drawscope.DrawStyle as ComposeStyle
import org.beatonma.gclocks.core.geometry.Vector2
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.StrokeCap
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.DrawStyle
import org.beatonma.gclocks.core.graphics.Fill
import org.beatonma.gclocks.core.graphics.Stroke
import org.beatonma.gclocks.core.graphics.StrokeJoin
import androidx.compose.ui.graphics.Color as ComposeColor

private val DefaultPivot = Offset.Zero // TODO not certain if pivot is correct

object ComposeCanvas : Canvas<DrawScope> {
    private val path: Path = Path()
    private var _drawScope: DrawScope? = null
    private val drawScope: DrawScope get() = _drawScope!!

    fun withScope(scope: DrawScope, block: ComposeCanvas.() -> Unit) {
        _drawScope = scope
        block()
        _drawScope = null
    }

    override fun withCheckpoint(block: Canvas<DrawScope>.() -> Unit) {
        /**Based on [DrawScope.withTransform]*/
        with(drawScope.drawContext) {
            // Transformation can include inset calls which change the drawing area
            // so cache the previous size before the transformation is done
            // and reset it afterwards
            val previousSize = size
            canvas.save()
            try {
                block()
            } finally {
                canvas.restore()
                size = previousSize
            }
        }
    }

    override fun drawCircle(
        color: Color,
        centerX: Float,
        centerY: Float,
        radius: Float,
        style: DrawStyle,
        alpha: Float,
    ) {
        drawScope.drawCircle(
            color = color.toCompose(),
            radius = radius,
            center = Offset(centerX, centerY),
            style = style.toCompose(),
        )
    }

    override fun drawLine(
        color: Color,
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        style: Stroke,
        alpha: Float,
    ) {
        drawScope.drawLine(
            color = color.toCompose(),
            start = Offset(x1, y1),
            end = Offset(x2, y2),
            strokeWidth = style.width,
            cap = style.cap.toCompose(),
        )
    }

    override fun moveTo(x: Float, y: Float) {
        path.moveTo(x, y)
    }

    override fun lineTo(x: Float, y: Float) {
        path.lineTo(x, y)
    }

    override fun boundedArc(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        startAngle: Angle,
        sweepAngle: Angle,
    ) {
        path.addArc(
            Rect(left, top, right, bottom),
            startAngleDegrees = startAngle.asDegrees,
            sweepAngleDegrees = sweepAngle.asDegrees,
        )
    }

    override fun closePath() {
        path.close()
    }

    override fun beginPath() {
        path.reset()
    }

    override fun drawPath(
        color: Color,
        style: DrawStyle,
        alpha: Float,
    ) {
        drawScope.drawPath(
            path,
            color = color.toCompose(),
            alpha = alpha,
            style = style.toCompose(),
        )
    }

    override fun drawRoundRect(
        color: Color,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        radius: Float,
        style: DrawStyle,
        alpha: Float,
    ) {
        drawScope.drawRoundRect(
            color = color.toCompose(),
            topLeft = Offset(left, top),
            size = Size(right - left, bottom - top),
            cornerRadius = CornerRadius(radius),
            style = style.toCompose(),
            alpha = alpha,
        )
    }

    override fun withRotation(
        angle: Angle,
        pivotX: Float,
        pivotY: Float,
        block: Canvas<DrawScope>.() -> Unit,
    ) {
        drawScope.rotate(angle.asDegrees, Offset(pivotX, pivotY)) {
            block()
        }
    }

    override fun withScale(
        scale: Float,
        block: Canvas<DrawScope>.() -> Unit,
    ) {
        drawScope.scale(scale, pivot = DefaultPivot) {
            block()
        }
    }

    override fun withScale(scaleX: Float, scaleY: Float, block: Canvas<DrawScope>.() -> Unit) {
        drawScope.scale(scaleX, scaleY, pivot = DefaultPivot) {
            block()
        }
    }

    override fun withScale(
        scale: Float,
        pivotX: Float,
        pivotY: Float,
        block: Canvas<DrawScope>.() -> Unit,
    ) {
        drawScope.scale(scale, pivot = Offset(pivotX, pivotY)) {
            block()
        }
    }

    override fun withScale(
        scaleX: Float,
        scaleY: Float,
        pivotX: Float,
        pivotY: Float,
        block: Canvas<DrawScope>.() -> Unit,
    ) {
        drawScope.scale(scaleX, scaleY, pivot = Offset(pivotX, pivotY)) {
            block()
        }
    }

    override fun withTranslation(
        x: Float,
        y: Float,
        block: Canvas<DrawScope>.() -> Unit,
    ) {
        drawScope.translate(x, y) {
            block()
        }
    }

    override fun withTranslationAndScale(
        x: Float,
        y: Float,
        scale: Float,
        block: Canvas<DrawScope>.() -> Unit,
    ) {
        drawScope.withTransform({
            translate(x, y)
            scale(scale, pivot = DefaultPivot)
        }) {
            block()
        }
    }

    override fun save() {
        drawScope.drawContext.canvas.save()
    }

    override fun restore() {
        drawScope.drawContext.canvas.restore()
    }

    override fun clear() {}
}

@JvmName("intVectorToOffset")
fun Vector2<Int>.toOffset() = Offset(x.toFloat(), y.toFloat())

@JvmName("floatVectorToOffset")
fun Vector2<Float>.toOffset() = Offset(x, y)

fun Color.toCompose(): ComposeColor = ComposeColor(
    red = red,
    green = green,
    blue = blue,
    alpha = alpha,
)

fun DrawStyle.toCompose(): ComposeStyle = when (this) {
    Fill -> ComposeFill
    is Stroke -> ComposeStroke(
        width = width,
        cap = cap.toCompose(),
        join = join.toCompose()
    )
}

fun StrokeCap.toCompose(): ComposeStrokeCap = when (this) {
    StrokeCap.Round -> ComposeStrokeCap.Round
    StrokeCap.Square -> ComposeStrokeCap.Square
    StrokeCap.Butt -> ComposeStrokeCap.Butt
}

fun StrokeJoin.toCompose(): ComposeStrokeJoin = when (this) {
    StrokeJoin.Miter -> ComposeStrokeJoin.Miter
    StrokeJoin.Round -> ComposeStrokeJoin.Round
    StrokeJoin.Bevel -> ComposeStrokeJoin.Bevel
}