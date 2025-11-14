package org.beatonma.gclocks.core.graphics.paths

import org.beatonma.gclocks.core.geometry.Angle
import org.beatonma.gclocks.core.geometry.Position
import org.beatonma.gclocks.core.geometry.degrees
import org.beatonma.gclocks.core.geometry.getPointOnEllipse
import org.beatonma.gclocks.core.graphics.Path
import org.beatonma.gclocks.core.util.lerp

sealed interface PathCommand {
    fun plot(path: Path)
    fun plotInterpolated(path: Path, other: PathCommand, progress: Float)
}

object BeginPath : PathCommand {
    override fun plot(path: Path) {
        path.beginPath()
    }

    override fun plotInterpolated(path: Path, other: PathCommand, progress: Float) {
        path.beginPath()
    }

    override fun toString(): String = "BeginPath"
}

object ClosePath : PathCommand {
    override fun plot(path: Path) {
        path.closePath()
    }

    override fun plotInterpolated(path: Path, other: PathCommand, progress: Float) {
        path.closePath()
    }

    override fun toString(): String = "Z"
}

data class MoveTo(private val x: Float, private val y: Float) : PathCommand {
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

data class LineTo(private val x: Float, private val y: Float) : PathCommand {
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

data class CubicTo(
    private val x1: Float,
    private val y1: Float,
    private val x2: Float,
    private val y2: Float,
    private val x3: Float,
    private val y3: Float,
) : PathCommand {
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

data class Circle(
    private val centerX: Float,
    private val centerY: Float,
    private val radius: Float,
    private val direction: Path.Direction,
) : PathCommand {
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

data class Rect(
    private val left: Float,
    private val top: Float,
    private val right: Float,
    private val bottom: Float,
    private val direction: Path.Direction,
) : PathCommand {
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


data class BoundedArc(
    private val left: Float,
    private val top: Float,
    private val right: Float,
    private val bottom: Float,
    private val startAngle: Angle,
    private val sweepAngle: Angle,
) : PathCommand {
    override fun plot(path: Path) {
        path.boundedArc(left, top, right, bottom, startAngle, sweepAngle)
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
    }

    fun endPosition(): Position {
        return getPointOnEllipse(left, top, right, bottom, startAngle + sweepAngle)
    }
}
