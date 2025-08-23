package org.beatonma.gclocks.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect as PlatformRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathMeasure as PlatformPathMeasure
import androidx.compose.ui.graphics.Path as PlatformPath
import androidx.compose.ui.graphics.StrokeCap as PlatformStrokeCap
import androidx.compose.ui.graphics.StrokeJoin as PlatformStrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import org.beatonma.gclocks.core.geometry.Angle
import org.beatonma.gclocks.core.geometry.FloatPoint
import org.beatonma.gclocks.core.geometry.Point
import org.beatonma.gclocks.core.geometry.Position
import androidx.compose.ui.graphics.drawscope.Fill as PlatformFill
import androidx.compose.ui.graphics.drawscope.Stroke as PlatformStroke
import androidx.compose.ui.graphics.drawscope.DrawStyle as PlatformStyle
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Path
import org.beatonma.gclocks.core.graphics.StrokeCap
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.DrawStyle
import org.beatonma.gclocks.core.graphics.Fill
import org.beatonma.gclocks.core.graphics.PathMeasure
import org.beatonma.gclocks.core.graphics.PathMeasureScope
import org.beatonma.gclocks.core.graphics.Stroke
import org.beatonma.gclocks.core.graphics.StrokeJoin
import androidx.compose.ui.graphics.Color as PlatformColor

private val DefaultPivot = Offset.Zero

@Composable
fun rememberCanvas(): ComposeCanvas {
    val textMeasurer = rememberTextMeasurer()
    val canvas = remember { ComposeCanvas(textMeasurer) }
    return canvas
}

class ComposePath : Path {
    internal val composePath: PlatformPath = PlatformPath()

    override fun moveTo(x: Float, y: Float) {
        composePath.moveTo(x, y)
    }

    override fun lineTo(x: Float, y: Float) {
        composePath.lineTo(x, y)
    }

    override fun cubicTo(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float) {
        composePath.cubicTo(x1, y1, x2, y2, x3, y3)
    }

    override fun boundedArc(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        startAngle: Angle,
        sweepAngle: Angle,
    ) {
        composePath.addArc(
            PlatformRect(left, top, right, bottom),
            startAngle.asDegrees,
            sweepAngle.asDegrees,
        )
    }

    override fun circle(
        centerX: Float,
        centerY: Float,
        radius: Float,
        direction: Path.Direction,
    ) {
        composePath.addOval(
            PlatformRect(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius
            ),
            direction = direction.toCompose()
        )
    }

    override fun closePath() {
        composePath.close()
    }

    override fun beginPath() {
        composePath.reset()
    }
}

class ComposePathMeasure(
    private val pathMeasure: PlatformPathMeasure = PlatformPathMeasure(),
) : PathMeasure {
    override val length: Float get() = pathMeasure.length

    override fun setPath(path: Path, forceClosed: Boolean) {
        pathMeasure.setPath((path as ComposePath).composePath, forceClosed = forceClosed)
    }

    override fun getSegment(
        startDistance: Float,
        endDistance: Float,
        outPath: Path,
        startsWithMoveTo: Boolean,
    ): Path {
        pathMeasure.getSegment(
            startDistance,
            endDistance,
            (outPath as ComposePath).composePath,
            startsWithMoveTo
        )
        return outPath
    }

    override fun getPosition(distance: Float): Position? {
        return pathMeasure.getPosition(distance).toPosition()
    }

    override fun getTangent(distance: Float): Position? {
        return pathMeasure.getTangent(distance).toPosition()
    }
}

private typealias CanvasAction = Canvas.() -> Unit

class ComposeCanvas(
    private val textMeasurer: TextMeasurer,
    private val path: ComposePath = ComposePath(),
) : Canvas, Path by path {
    private var _drawScope: DrawScope? = null
    private val drawScope: DrawScope get() = _drawScope!!

    private val pathMeasure: PathMeasure by lazy {
        ComposePathMeasure().apply {
            setPath(path)
        }
    }

    override fun measurePath(block: PathMeasureScope.() -> Unit) {
        pathMeasure.setPath(path)
        pathMeasure.apply { block() }
    }

    fun withScope(scope: DrawScope, block: ComposeCanvas.() -> Unit) {
        _drawScope = scope
        block()
        _drawScope = null
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

    override fun drawPath(
        color: Color,
        style: DrawStyle,
        alpha: Float,
    ) {
        drawPath(path, color, style, alpha)
    }

    override fun drawPath(path: Path, color: Color, style: DrawStyle, alpha: Float) {
        drawScope.drawPath(
            (path as ComposePath).composePath,
            color = color.toCompose(),
            alpha = alpha,
            style = style.toCompose(),
        )
    }

    override fun drawPoint(
        x: Float,
        y: Float,
        radius: Float,
        color: Color,
        style: DrawStyle,
        alpha: Float,
    ) {
        drawScope.drawCircle(
            color.toCompose(),
            radius = radius,
            center = Offset(x, y),
            style = style.toCompose(),
            alpha = alpha,
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

    override fun drawText(text: String) {
        drawScope.drawText(
            textMeasurer,
            text,
            Offset.Zero,
            style = TextStyle(
                fontSize = 12.sp,
                color = PlatformColor.Green
            ),
            overflow = TextOverflow.Visible
        )
    }

    override fun withRotation(
        angle: Angle,
        pivotX: Float,
        pivotY: Float,
        block: CanvasAction,
    ) {
        drawScope.rotate(angle.asDegrees, Offset(pivotX, pivotY)) {
            block()
        }
    }

    override fun withScale(
        scale: Float,
        block: CanvasAction,
    ) {
        drawScope.scale(scale, pivot = DefaultPivot) {
            block()
        }
    }

    override fun withScale(scaleX: Float, scaleY: Float, block: Canvas.() -> Unit) {
        drawScope.scale(scaleX, scaleY, pivot = DefaultPivot) {
            block()
        }
    }

    override fun withScale(
        scale: Float,
        pivotX: Float,
        pivotY: Float,
        block: CanvasAction,
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
        block: CanvasAction,
    ) {
        drawScope.scale(scaleX, scaleY, pivot = Offset(pivotX, pivotY)) {
            block()
        }
    }

    override fun withTranslation(
        x: Float,
        y: Float,
        block: CanvasAction,
    ) {
        drawScope.translate(x, y) {
            block()
        }
    }

    override fun withTranslationAndScale(
        x: Float,
        y: Float,
        scale: Float,
        block: CanvasAction,
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


fun Color.toCompose(): PlatformColor = PlatformColor(
    red = red,
    green = green,
    blue = blue,
    alpha = alpha,
)

private fun DrawStyle.toCompose(): PlatformStyle = when (this) {
    Fill -> PlatformFill
    is Stroke -> PlatformStroke(
        width = width,
        cap = cap.toCompose(),
        join = join.toCompose()
    )
}

private fun StrokeCap.toCompose(): PlatformStrokeCap = when (this) {
    StrokeCap.Round -> PlatformStrokeCap.Round
    StrokeCap.Square -> PlatformStrokeCap.Square
    StrokeCap.Butt -> PlatformStrokeCap.Butt
}

private fun StrokeJoin.toCompose(): PlatformStrokeJoin = when (this) {
    StrokeJoin.Miter -> PlatformStrokeJoin.Miter
    StrokeJoin.Round -> PlatformStrokeJoin.Round
    StrokeJoin.Bevel -> PlatformStrokeJoin.Bevel
}

private fun Offset.toPosition(): Point<Float> = FloatPoint(x, y)

private fun Path.Direction.toCompose(): PlatformPath.Direction = when (this) {
    Path.Direction.Clockwise -> PlatformPath.Direction.Clockwise
    Path.Direction.AntiClockwise -> PlatformPath.Direction.CounterClockwise
}