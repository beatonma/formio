package org.beatonma.gclocks.core.graphics

import org.beatonma.gclocks.core.geometry.Angle
import org.beatonma.gclocks.core.geometry.degrees

class PathError(message: String? = null) : Exception(message)


interface Path {
    fun moveTo(x: Float, y: Float)
    fun moveTo(position: Position) {
        moveTo(position.x, position.y)
    }

    fun lineTo(x: Float, y: Float)
    fun lineTo(position: Position) {
        lineTo(position.x, position.y)
    }

    fun cubicTo(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float)
    fun cubicTo(p1: Position, p2: Position, p3: Position) {
        cubicTo(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y)
    }

    fun boundedArc(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        startAngle: Angle = (-90f).degrees,
        sweepAngle: Angle = 180f.degrees,
    )

    fun circle(
        centerX: Float,
        centerY: Float,
        radius: Float,
    )

    fun beginPath()
    fun closePath()
}


interface PathMeasure {
    val length: Float
    fun getSegment(
        startDistance: Float,
        endDistance: Float,
        outPath: Path,
        startsWithMoveTo: Boolean = true,
    ): Path

    fun setPath(path: Path, forceClosed: Boolean = true)
    fun getPosition(distance: Float): Position?
    fun getTangent(distance: Float): Position?
}
