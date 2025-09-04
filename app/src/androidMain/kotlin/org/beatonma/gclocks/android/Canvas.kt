package org.beatonma.gclocks.android

import android.graphics.Paint
import android.graphics.RectF
import androidx.core.graphics.withRotation
import androidx.core.graphics.withScale
import androidx.core.graphics.withTranslation
import org.beatonma.gclocks.core.geometry.Angle
import org.beatonma.gclocks.core.geometry.FloatPoint
import org.beatonma.gclocks.core.geometry.Position
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.DrawStyle
import org.beatonma.gclocks.core.graphics.Fill
import org.beatonma.gclocks.core.graphics.Path
import org.beatonma.gclocks.core.graphics.PathMeasure
import org.beatonma.gclocks.core.graphics.PathMeasureScope
import org.beatonma.gclocks.core.graphics.Stroke
import org.beatonma.gclocks.core.graphics.StrokeCap
import org.beatonma.gclocks.core.graphics.StrokeJoin
import android.graphics.Canvas as PlatformCanvas
import android.graphics.Path as PlatformPath
import android.graphics.PathMeasure as PlatformPathMeasure

class AndroidPath : Path {
    internal val path: PlatformPath = PlatformPath()
    private val rectF: RectF = RectF()

    override fun moveTo(x: Float, y: Float) {
        path.moveTo(x, y)
    }

    override fun lineTo(x: Float, y: Float) {
        path.lineTo(x, y)
    }

    override fun cubicTo(
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        x3: Float,
        y3: Float,
    ) {
        path.cubicTo(x1, y1, x2, y2, x3, y3)
    }

    override fun boundedArc(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        startAngle: Angle,
        sweepAngle: Angle,
    ) {
        path.arcTo(
            rectF.apply { set(left, top, right, bottom) },
            startAngle.degrees,
            sweepAngle.degrees.run {
                /*
                 * Android canvas uses (value % 360f) so if the requested sweep angle
                 * is a full loop then reduce the value slightly below that threshold.
                 * Otherwise the arc will have an effective length of zero.
                 */
                if (this == 360f) this - 0.0001f
                else this
            }
        )
    }

    override fun circle(
        centerX: Float,
        centerY: Float,
        radius: Float,
        direction: Path.Direction,
    ) {
        path.addCircle(centerX, centerY, radius, direction.toAndroid())
    }

    override fun rect(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        direction: Path.Direction,
    ) {
        path.addRect(
            rectF.apply { set(left, top, right, bottom) },
            direction.toAndroid()
        )
    }

    override fun beginPath() {
        path.reset()
    }

    override fun closePath() {
        path.close()
    }
}

class AndroidPathMeasure(
    private val pathMeasure: PlatformPathMeasure = PlatformPathMeasure(),
) : PathMeasure {
    private val coordinates = FloatArray(2)
    private fun FloatArray.toPosition() = FloatPoint(this[0], this[1])

    override fun setPath(
        path: Path,
        forceClosed: Boolean,
    ) {
        pathMeasure.setPath((path as AndroidPath).path, forceClosed)
    }

    override val length: Float get() = pathMeasure.length

    override fun getSegment(
        startDistance: Float,
        endDistance: Float,
        outPath: Path,
        startsWithMoveTo: Boolean,
    ): Path {
        pathMeasure.getSegment(
            startDistance,
            endDistance,
            (outPath as AndroidPath).path,
            startsWithMoveTo
        )
        return outPath
    }

    override fun getPosition(distance: Float): Position {
        pathMeasure.getPosTan(distance, coordinates, null)
        return coordinates.toPosition()
    }

    override fun getTangent(distance: Float): Position {
        pathMeasure.getPosTan(distance, null, coordinates)
        return coordinates.toPosition()
    }
}

private typealias CanvasAction = Canvas.() -> Unit

class AndroidCanvasHost(
    val path: AndroidPath = AndroidPath(),
    private val paint: Paint = Paint(),
) {
    private val pathMeasure: PathMeasure by lazy {
        AndroidPathMeasure().apply {
            setPath(path)
        }
    }

    inline fun withCanvas(canvas: PlatformCanvas, block: (canvas: Canvas) -> Unit) {
        block(AndroidCanvas(canvas))
    }

    inner class AndroidCanvas(private val canvas: PlatformCanvas) : Canvas, Path by path {
        override fun measurePath(block: (scope: PathMeasureScope) -> Unit) {
            pathMeasure.setPath(path)
            pathMeasure.apply(block)
        }

        override fun drawCircle(
            color: Color,
            centerX: Float,
            centerY: Float,
            radius: Float,
            style: DrawStyle,
            alpha: Float,
        ) {
            canvas.drawCircle(
                centerX, centerY, radius,
                paint.withStyle(color, alpha, style)
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
            canvas.drawLine(x1, y1, x2, y2, paint.withStyle(color, alpha, style))
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
            canvas.drawRoundRect(
                left,
                top,
                right,
                bottom,
                radius,
                radius,
                paint.withStyle(color, alpha, style)
            )
        }

        override fun drawPath(
            color: Color,
            style: DrawStyle,
            alpha: Float,
        ) {
            canvas.drawPath(path.path, paint.withStyle(color, alpha, style))
        }

        override fun drawPath(
            path: Path,
            color: Color,
            style: DrawStyle,
            alpha: Float,
        ) {
            canvas.drawPath((path as AndroidPath).path, paint.withStyle(color, alpha, style))
        }

        override fun drawPoint(
            x: Float,
            y: Float,
            radius: Float,
            color: Color,
            style: DrawStyle,
            alpha: Float,
        ) {
            drawCircle(color, x, y, radius, style, alpha)
        }

        override fun drawText(text: String) {
            canvas.drawText(text, 0f, 0f, paint.withStyle(Color.Red, 1f, Fill))
        }

        override fun save() {
            canvas.save()
        }

        override fun restore() {
            canvas.restore()
        }

        override fun withScale(
            scale: Float,
            block: CanvasAction,
        ) {
            canvas.withScale(scale, scale) { block() }
        }

        override fun withScale(
            scaleX: Float,
            scaleY: Float,
            block: CanvasAction,
        ) {
            canvas.withScale(scaleX, scaleY) { block() }
        }

        override fun withScale(
            scale: Float,
            pivotX: Float,
            pivotY: Float,
            block: CanvasAction,
        ) {
            canvas.withScale(scale, scale, pivotX, pivotY) { block() }
        }

        override fun withScale(
            scaleX: Float,
            scaleY: Float,
            pivotX: Float,
            pivotY: Float,
            block: CanvasAction,
        ) {
            canvas.withScale(scaleX, scaleY, pivotX, pivotY) { block() }
        }

        override fun withRotation(
            angle: Angle,
            pivotX: Float,
            pivotY: Float,
            block: CanvasAction,
        ) {
            canvas.withRotation(angle.degrees, pivotX, pivotY) { block() }
        }

        override fun withTranslation(
            x: Float,
            y: Float,
            block: CanvasAction,
        ) {
            canvas.withTranslation(x, y) { block() }
        }

        override fun withTranslationAndScale(
            x: Float,
            y: Float,
            scale: Float,
            block: CanvasAction,
        ) {
            canvas.withTranslation(x, y) {
                canvas.withScale(scale, scale) {
                    block()
                }
            }
        }

        override fun clear() {
            canvas.drawRGB(0, 0, 0)
        }
    }
}

private fun Path.Direction.toAndroid(): PlatformPath.Direction = when (this) {
    Path.Direction.Clockwise -> PlatformPath.Direction.CW
    Path.Direction.AntiClockwise -> PlatformPath.Direction.CCW
}

private fun Paint.withStyle(color: Color, alpha: Float, drawStyle: DrawStyle): Paint {
    when (drawStyle) {
        is Fill -> {
            style = Paint.Style.FILL
        }

        is Stroke -> {
            style = Paint.Style.STROKE
            strokeWidth = drawStyle.width
            strokeMiter = drawStyle.miter
            strokeCap = drawStyle.cap.toAndroid()
            strokeJoin = drawStyle.join.toAndroid()
        }
    }
    this.color = color.withOpacity(alpha).toArgbInt()
    return this
}

private fun StrokeCap.toAndroid(): Paint.Cap = when (this) {
    StrokeCap.Round -> Paint.Cap.ROUND
    StrokeCap.Butt -> Paint.Cap.BUTT
    StrokeCap.Square -> Paint.Cap.SQUARE
}

private fun StrokeJoin.toAndroid(): Paint.Join = when (this) {
    StrokeJoin.Round -> Paint.Join.ROUND
    StrokeJoin.Miter -> Paint.Join.MITER
    StrokeJoin.Bevel -> Paint.Join.BEVEL
}