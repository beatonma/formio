package org.beatonma.gclocks.core.graphics.paths

import org.beatonma.gclocks.core.geometry.Angle
import org.beatonma.gclocks.core.geometry.FloatPoint
import org.beatonma.gclocks.core.geometry.degrees
import org.beatonma.gclocks.core.geometry.getPointOnCircle
import org.beatonma.gclocks.core.geometry.getPointOnEllipse
import org.beatonma.gclocks.core.graphics.Path
import org.beatonma.gclocks.core.util.lerp


internal sealed interface PathCommand {
    /** The position of the end of the path after this command has been applied. */
    val endPoint: FloatPoint?

    fun plot(path: Path)
    fun plotInterpolated(path: Path, other: PathCommand, progress: Float)
}

private sealed interface BoundedEllipse : PathCommand {
    val left: Float
    val top: Float
    val right: Float
    val bottom: Float
    val startAngle: Angle
    val sweepAngle: Angle

    val endAngle get() = startAngle + sweepAngle

    override val endPoint: FloatPoint? get() = getPointOnEllipse(left, top, right, bottom, endAngle)
}

internal sealed interface PathTransform : PathCommand


internal object BeginPath : PathCommand {
    override val endPoint = null

    override fun plot(path: Path) {
        path.beginPath()
    }

    override fun plotInterpolated(path: Path, other: PathCommand, progress: Float) {
        path.beginPath()
    }

    override fun toString(): String = "BeginPath"
}

internal object ClosePath : PathCommand {
    override val endPoint = null

    override fun plot(path: Path) {
        path.closePath()
    }

    override fun plotInterpolated(path: Path, other: PathCommand, progress: Float) {
        path.closePath()
    }

    override fun toString(): String = "Z"
}

internal data class MoveTo(private val x: Float, private val y: Float) : PathCommand {
    override val endPoint = FloatPoint(x, y)

    override fun plot(path: Path) {
        path.moveTo(x, y)
    }

    override fun plotInterpolated(path: Path, other: PathCommand, progress: Float) {
        other as MoveTo
        path.moveTo(
            progress.lerp(x, other.x),
            progress.lerp(y, other.y),
        )
    }

    override fun toString(): String {
        return "M $x,$y"
    }
}

internal data class LineTo(private val x: Float, private val y: Float) : PathCommand {
    override val endPoint = FloatPoint(x, y)

    override fun plot(path: Path) {
        path.lineTo(x, y)
    }

    override fun plotInterpolated(path: Path, other: PathCommand, progress: Float) {
        other as LineTo
        path.lineTo(
            progress.lerp(x, other.x),
            progress.lerp(y, other.y),
        )
    }

    override fun toString(): String {
        return "L $x,$y"
    }
}

internal data class CubicTo(
    private val x1: Float,
    private val y1: Float,
    private val x2: Float,
    private val y2: Float,
    private val x3: Float,
    private val y3: Float,
) : PathCommand {
    override val endPoint = FloatPoint(x3, y3)

    override fun plot(path: Path) {
        path.cubicTo(x1, y1, x2, y2, x3, y3)
    }

    override fun plotInterpolated(path: Path, other: PathCommand, progress: Float) {
        other as CubicTo
        path.cubicTo(
            progress.lerp(x1, other.x1),
            progress.lerp(y1, other.y1),
            progress.lerp(x2, other.x2),
            progress.lerp(y2, other.y2),
            progress.lerp(x3, other.x3),
            progress.lerp(y3, other.y3),
        )
    }

    override fun toString(): String {
        return "C $x1,$y1 $x2,$y2 $x3,$y3"
    }
}

internal data class ArcTo(
    override val left: Float,
    override val top: Float,
    override val right: Float,
    override val bottom: Float,
    override val startAngle: Angle,
    override val sweepAngle: Angle,
    private val forceMoveTo: Boolean
) : BoundedEllipse, PathCommand {
    override fun plot(path: Path) {
        path.arcTo(left, top, right, bottom, startAngle, sweepAngle, forceMoveTo)
    }

    override fun plotInterpolated(path: Path, other: PathCommand, progress: Float) {
        other as ArcTo
        path.arcTo(
            progress.lerp(left, other.left),
            progress.lerp(top, other.top),
            progress.lerp(right, other.right),
            progress.lerp(bottom, other.bottom),
            progress.lerp(startAngle.asDegrees, other.startAngle.asDegrees).degrees,
            progress.lerp(sweepAngle.asDegrees, other.sweepAngle.asDegrees).degrees,
            forceMoveTo || other.forceMoveTo
        )
    }
}

internal data class Circle(
    private val centerX: Float,
    private val centerY: Float,
    private val radius: Float,
    private val direction: Path.Direction,
) : PathCommand {
    override val endPoint = null

    override fun plot(path: Path) {
        path.circle(centerX, centerY, radius, direction)
    }

    override fun plotInterpolated(path: Path, other: PathCommand, progress: Float) {
        other as Circle
        path.circle(
            progress.lerp(centerX, other.centerX),
            progress.lerp(centerY, other.centerY),
            progress.lerp(radius, other.radius),
            direction,
        )
    }
}

internal data class Rect(
    private val left: Float,
    private val top: Float,
    private val right: Float,
    private val bottom: Float,
    private val direction: Path.Direction,
) : PathCommand {
    override val endPoint = null

    override fun plot(path: Path) {
        path.rect(left, top, right, bottom, direction)
    }

    override fun plotInterpolated(
        path: Path,
        other: PathCommand,
        progress: Float,
    ) {
        other as Rect
        path.rect(
            progress.lerp(left, other.left),
            progress.lerp(top, other.top),
            progress.lerp(right, other.right),
            progress.lerp(bottom, other.bottom),
            direction
        )
    }
}


internal data class BoundedArc(
    override val left: Float,
    override val top: Float,
    override val right: Float,
    override val bottom: Float,
    override val startAngle: Angle,
    override val sweepAngle: Angle,
    private val close: Boolean = false,
) : BoundedEllipse, PathCommand {
    override val endPoint = if (close) {
        null
    } else {
        getPointOnEllipse(left, top, right, bottom, endAngle)
    }

    override fun plot(path: Path) {
        path.boundedArc(left, top, right, bottom, startAngle, sweepAngle)
        if (close) {
            path.closePath()
        }
    }

    override fun plotInterpolated(
        path: Path,
        other: PathCommand,
        progress: Float,
    ) {
        other as BoundedArc
        path.boundedArc(
            progress.lerp(left, other.left),
            progress.lerp(top, other.top),
            progress.lerp(right, other.right),
            progress.lerp(bottom, other.bottom),
            progress.lerp(startAngle.asDegrees, other.startAngle.asDegrees).degrees,
            progress.lerp(sweepAngle.asDegrees, other.sweepAngle.asDegrees).degrees,
        )
        if (close || other.close) {
            path.closePath()
        }
    }
}

internal data class Sector(
    private val centerX: Float,
    private val centerY: Float,
    private val radius: Float,
    private val startAngle: Angle = Angle.TwoSeventy,
    private val sweepAngle: Angle = Angle.OneEighty,
) : PathCommand {
    private val endAngle = startAngle + sweepAngle
    override val endPoint = getPointOnCircle(centerX, centerY, radius, endAngle)

    override fun plot(path: Path) {
        path.sector(centerX, centerY, radius, startAngle, sweepAngle)
    }

    override fun plotInterpolated(
        path: Path,
        other: PathCommand,
        progress: Float
    ) {
        other as Sector
        path.sector(
            progress.lerp(centerX, other.centerX),
            progress.lerp(centerY, other.centerY),
            progress.lerp(radius, other.radius),
            progress.lerp(startAngle.degrees, other.startAngle.degrees).degrees,
            progress.lerp(sweepAngle.degrees, other.sweepAngle.degrees).degrees
        )
    }
}

internal data class Transform(
    val rotation: Angle = Angle.Zero,
    val translateX: Float = 0f,
    val translateY: Float = 0f,
    val scaleX: Float = 1f,
    val scaleY: Float = 1f,
    val pivotX: Float = 0f,
    val pivotY: Float = 0f,
) : PathTransform {
    override val endPoint = null
    private val isNoOp
        get() = rotation == Angle.Zero
                && translateX == 0f
                && translateY == 8f
                && scaleX == 1f
                && scaleY == 1f

    override fun plot(path: Path) {
        if (isNoOp) return
        path.transform(
            rotation,
            translateX,
            translateY,
            scaleX,
            scaleY,
            pivotX,
            pivotY,
        )
    }

    override fun plotInterpolated(
        path: Path,
        other: PathCommand,
        progress: Float
    ) {
        other as Transform

        path.transform(
            rotation = progress.lerp(rotation.degrees, other.rotation.degrees).degrees,
            translateX = progress.lerp(translateX, other.translateX),
            translateY = progress.lerp(translateY, other.translateY),
            scaleX = progress.lerp(scaleX, other.scaleX),
            scaleY = progress.lerp(scaleY, other.scaleY),
            pivotX = progress.lerp(pivotX, other.pivotX),
            pivotY = progress.lerp(pivotY, other.pivotY),
        )
    }
}
