package org.beatonma.gclocks.core.graphics

import org.beatonma.gclocks.core.geometry.Angle
import org.beatonma.gclocks.core.geometry.Position
import org.beatonma.gclocks.core.geometry.getPointOnCircle

private val matrix = Matrix()

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
        startAngle: Angle = Angle.TwoSeventy,
        sweepAngle: Angle = Angle.OneEighty,
    )

    fun boundedArc(
        centerX: Float,
        centerY: Float,
        radius: Float,
        startAngle: Angle = Angle.TwoSeventy,
        sweepAngle: Angle = Angle.OneEighty,
    ) {
        boundedArc(
            left = centerX - radius,
            top = centerY - radius,
            right = centerX + radius,
            bottom = centerY + radius,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
        )
    }

    fun arcTo(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        startAngle: Angle,
        sweepAngle: Angle,
        forceMoveTo: Boolean
    )

    fun arcTo(
        centerX: Float,
        centerY: Float,
        radius: Float,
        startAngle: Angle,
        sweepAngle: Angle,
        forceMoveTo: Boolean
    ) =
        arcTo(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius,
            startAngle,
            sweepAngle,
            forceMoveTo
        )

    fun sector(
        centerX: Float,
        centerY: Float,
        radius: Float,
        startAngle: Angle = Angle.TwoSeventy,
        sweepAngle: Angle = Angle.OneEighty,
    ) {
        moveTo(centerX, centerY)
        lineTo(getPointOnCircle(centerX, centerY, radius, startAngle))
        arcTo(centerX, centerY, radius, startAngle, sweepAngle, false)
        closePath()
    }

    fun circle(
        centerX: Float,
        centerY: Float,
        radius: Float,
        direction: Direction,
    )

    fun rect(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        direction: Direction = Direction.Clockwise
    )

    fun roundRect(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        radius: Float,
    ) {
        val leftRadius = left + radius
        val topRadius = top + radius
        val rightRadius = right - radius
        val bottomRadius = bottom - radius

        // Top edge
        moveTo(leftRadius, top)
        lineTo(rightRadius, top)
        arcTo(rightRadius, topRadius, radius, Angle.TwoSeventy, Angle.Ninety, false)

        // Right edge
        lineTo(right, topRadius)
        lineTo(right, bottomRadius)
        arcTo(rightRadius, bottomRadius, radius, Angle.Zero, Angle.Ninety, false)

        // Bottom edge
        lineTo(rightRadius, bottom)
        lineTo(leftRadius, bottom)
        arcTo(leftRadius, bottomRadius, radius, Angle.Ninety, Angle.Ninety, false)

        // Left edge
        lineTo(left, bottomRadius)
        lineTo(left, topRadius)
        arcTo(leftRadius, topRadius, radius, Angle.OneEighty, Angle.Ninety, false)
        closePath()
    }

    fun triangle(
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        x3: Float,
        y3: Float,
    ) {
        moveTo(x1, y1)
        lineTo(x2, y2)
        lineTo(x3, y3)
        closePath()
    }

    /** Arbitrary 4-sided shape */
    fun tetragon(
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        x3: Float,
        y3: Float,
        x4: Float,
        y4: Float,
    ) {
        moveTo(x1, y1)
        lineTo(x2, y2)
        lineTo(x3, y3)
        lineTo(x4, y4)
        closePath()
    }

    fun transform(matrix: Matrix)

    fun transform(
        rotation: Angle = Angle.Zero,
        translateX: Float = 0f,
        translateY: Float = 0f,
        scaleX: Float = 1f,
        scaleY: Float = 1f,
        pivotX: Float = 0f,
        pivotY: Float = 0f
    ) {
        transform(composeMatrix(rotation, translateX, translateY, scaleX, scaleY, pivotX, pivotY))
    }

    fun rotate(angle: Angle) {
        transform(matrix.build { rotateZ(angle.degrees) })
    }

    fun rotate(angle: Angle, pivotX: Float, pivotY: Float) {
        transform(composeMatrix(angle, pivotX = pivotX, pivotY = pivotY))
    }

    fun scale(scale: Float) {
        transform(matrix.build { scale(scale, scale) })
    }

    fun scale(x: Float, y: Float) {
        transform(matrix.build { scale(x, y) })
    }

    fun scale(scale: Float, pivotX: Float, pivotY: Float) {
        transform(
            composeMatrix(scaleX = scale, scaleY = scale, pivotX = pivotX, pivotY = pivotY)
        )
    }

    fun scale(x: Float, y: Float, pivotX: Float, pivotY: Float) {
        transform(
            composeMatrix(scaleX = x, scaleY = y, pivotX = pivotX, pivotY = pivotY)
        )
    }

    fun translate(x: Float, y: Float) {
        transform(
            matrix.build { translate(x, y) }
        )
    }

    fun beginPath()
    fun closePath()

    enum class Direction {
        Clockwise,
        AntiClockwise,
        ;
    }
}

interface PathMeasureScope {
    val length: Float
    fun getSegment(
        startDistance: Float,
        endDistance: Float,
        outPath: Path,
        startsWithMoveTo: Boolean = true,
    ): Path

    fun getPosition(distance: Float): Position?
    fun getTangent(distance: Float): Position?
}

interface PathMeasure : PathMeasureScope {
    fun setPath(path: Path, forceClosed: Boolean = false)
}


private fun composeMatrix(
    rotation: Angle = Angle.Zero,
    translateX: Float = 0f,
    translateY: Float = 0f,
    scaleX: Float = 1f,
    scaleY: Float = 1f,
    pivotX: Float = 0f,
    pivotY: Float = 0f,
): Matrix = matrix.build {
    reset()

    translate(pivotX, pivotY)
    scale(scaleX, scaleY)
    rotateZ(rotation.degrees)
    translate(-pivotX, -pivotY)

    translate(translateX, translateY)
}
