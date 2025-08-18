package org.beatonma.gclocks.core.graphics

import org.beatonma.gclocks.core.geometry.Angle
import org.beatonma.gclocks.core.geometry.FloatPoint
import org.beatonma.gclocks.core.geometry.Position
import org.beatonma.gclocks.core.geometry.degrees
import org.beatonma.gclocks.core.util.fastForEach
import org.beatonma.gclocks.core.util.interpolate
import kotlin.math.cos
import kotlin.math.sin

sealed interface PathCommand {
    fun plot(canvas: Canvas)
    fun plotInterpolated(canvas: Canvas, other: PathCommand, progress: Float)
}

object BeginPath : PathCommand {
    override fun plot(canvas: Canvas) {
        canvas.beginPath()
    }

    override fun plotInterpolated(canvas: Canvas, other: PathCommand, progress: Float) {
        canvas.beginPath()
    }

    override fun toString(): String {
        return "BeginPath"
    }
}

object ClosePath : PathCommand {
    override fun plot(canvas: Canvas) {
        canvas.closePath()
    }

    override fun plotInterpolated(canvas: Canvas, other: PathCommand, progress: Float) {
        canvas.closePath()
    }

    override fun toString(): String {
        return "Z"
    }
}

class MoveTo(private val x: Float, private val y: Float) : PathCommand {
    override fun plot(canvas: Canvas) {
        canvas.moveTo(x, y)
    }

    override fun plotInterpolated(canvas: Canvas, other: PathCommand, progress: Float) {
        other as MoveTo
        canvas.moveTo(
            interpolate(progress, x, other.x),
            interpolate(progress, y, other.y),
        )
    }

    override fun toString(): String {
        return "M $x,$y"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MoveTo

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }
}

class LineTo(private val x: Float, private val y: Float) : PathCommand {
    override fun plot(canvas: Canvas) {
        canvas.lineTo(x, y)
    }

    override fun plotInterpolated(canvas: Canvas, other: PathCommand, progress: Float) {
        other as LineTo
        canvas.lineTo(
            interpolate(progress, x, other.x),
            interpolate(progress, y, other.y),
        )
    }

    override fun toString(): String {
        return "L $x,$y"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LineTo

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }
}

class CubicTo(
    private val x1: Float,
    private val y1: Float,
    private val x2: Float,
    private val y2: Float,
    private val x3: Float,
    private val y3: Float,
) : PathCommand {
    override fun plot(canvas: Canvas) {
        canvas.cubicTo(x1, y1, x2, y2, x3, y3)
    }

    override fun plotInterpolated(canvas: Canvas, other: PathCommand, progress: Float) {
        other as CubicTo
        canvas.cubicTo(
            interpolate(progress, x1, other.x1),
            interpolate(progress, y1, other.y1),
            interpolate(progress, x2, other.x2),
            interpolate(progress, y2, other.y2),
            interpolate(progress, x3, other.x3),
            interpolate(progress, y3, other.y3),
        )
    }

    override fun toString(): String {
        return "C $x1,$y1 $x2,$y2 $x3,$y3"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CubicTo

        if (x1 != other.x1) return false
        if (y1 != other.y1) return false
        if (x2 != other.x2) return false
        if (y2 != other.y2) return false
        if (x3 != other.x3) return false
        if (y3 != other.y3) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x1.hashCode()
        result = 31 * result + y1.hashCode()
        result = 31 * result + x2.hashCode()
        result = 31 * result + y2.hashCode()
        result = 31 * result + x3.hashCode()
        result = 31 * result + y3.hashCode()
        return result
    }
}

class Circle(
    private val centerX: Float,
    private val centerY: Float,
    private val radius: Float,
    private val direction: Path.Direction,
) : PathCommand {
    override fun plot(canvas: Canvas) {
        canvas.circle(centerX, centerY, radius, direction)
    }

    override fun plotInterpolated(canvas: Canvas, other: PathCommand, progress: Float) {
        other as Circle
        canvas.circle(
            interpolate(progress, centerX, other.centerX),
            interpolate(progress, centerY, other.centerY),
            interpolate(progress, radius, other.radius),
            direction,
        )
    }

    override fun toString(): String {
        return "Circle($centerX, $centerY, $radius, $direction)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Circle

        if (centerX != other.centerX) return false
        if (centerY != other.centerY) return false
        if (radius != other.radius) return false
        if (direction != other.direction) return false

        return true
    }

    override fun hashCode(): Int {
        var result = centerX.hashCode()
        result = 31 * result + centerY.hashCode()
        result = 31 * result + radius.hashCode()
        result = 31 * result + direction.hashCode()
        return result
    }
}

class BoundedArc(
    private val left: Float,
    private val top: Float,
    private val right: Float,
    private val bottom: Float,
    private val startAngle: Angle,
    private val sweepAngle: Angle,
) : PathCommand {
    override fun plot(canvas: Canvas) {
        canvas.boundedArc(left, top, right, bottom, startAngle, sweepAngle)
    }

    override fun plotInterpolated(
        canvas: Canvas,
        other: PathCommand,
        progress: Float,
    ) {
        other as BoundedArc
        canvas.boundedArc(
            interpolate(progress, left, other.left),
            interpolate(progress, top, other.top),
            interpolate(progress, right, other.right),
            interpolate(progress, bottom, other.bottom),
            interpolate(progress, startAngle.asDegrees, other.startAngle.asDegrees).degrees,
            interpolate(progress, sweepAngle.asDegrees, other.sweepAngle.asDegrees).degrees,
        )
    }

    fun endPosition(): Position {
        val centerX = left + (right - left) / 2f
        val centerY = top + (bottom - top) / 2f

        val radiusX = (right - left) / 2f
        val radiusY = (bottom - top) / 2f

        val angle = startAngle + sweepAngle
        val x = centerX + radiusX * cos(angle.asRadians)
        val y = centerY + radiusY * sin(angle.asRadians)

        return FloatPoint(x, y)
    }


    override fun toString(): String {
        return "BoundedArc($left, $top, $right, $bottom, $startAngle, $sweepAngle)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BoundedArc

        if (left != other.left) return false
        if (top != other.top) return false
        if (right != other.right) return false
        if (bottom != other.bottom) return false
        if (startAngle != other.startAngle) return false
        if (sweepAngle != other.sweepAngle) return false

        return true
    }

    override fun hashCode(): Int {
        var result = left.hashCode()
        result = 31 * result + top.hashCode()
        result = 31 * result + right.hashCode()
        result = 31 * result + bottom.hashCode()
        result = 31 * result + startAngle.hashCode()
        result = 31 * result + sweepAngle.hashCode()
        return result
    }
}


/**
 * A container for a set of Path commands which can be accessed post-definition.
 * Main selling-point: Allows relatively simple
 */
class PathDefinition(
    val width: Float,
    val height: Float,
    val commands: List<PathCommand>,
) {
    fun plot(canvas: Canvas) {
        commands.fastForEach { it.plot(canvas) }
    }

    /**
     * Plot the result of Interpolating from this definition to the other.
     *
     * Both [PathDefinition]s must have compatible lists of commands:
     * - They must both have the same number of commands
     * - Each position of the command lists must share the same type of command
     *   (i.e. must interpolate from [LineTo] to [LineTo], etc.; not [LineTo] to [CubicTo], etc.)
     */
    fun plotInterpolated(canvas: Canvas, other: PathDefinition, progress: Float) {
        commands.zip(other.commands).fastForEach { (a, b) ->
            a.plotInterpolated(canvas, b, progress)
        }
    }

    class Builder(
        private val width: Float,
        private val height: Float,
    ) : Path {
        private val _commands: MutableList<PathCommand> = mutableListOf()

        // Track current endpoint of the path so we can easily add 'filler' via [zeroCubic] and [zeroLine].
        private var x: Float? = null
        private var y: Float? = null

        override fun moveTo(x: Float, y: Float) {
            _commands.add(MoveTo(x, y))
            this.x = x
            this.y = y
        }

        override fun lineTo(x: Float, y: Float) {
            _commands.add(LineTo(x, y))
            this.x = x
            this.y = y
        }

        override fun cubicTo(
            x1: Float,
            y1: Float,
            x2: Float,
            y2: Float,
            x3: Float,
            y3: Float,
        ) {
            _commands.add(CubicTo(x1, y1, x2, y2, x3, y3))
            this.x = x3
            this.y = y3
        }

        override fun boundedArc(
            left: Float,
            top: Float,
            right: Float,
            bottom: Float,
            startAngle: Angle,
            sweepAngle: Angle,
        ) {
            _commands.add(
                BoundedArc(left, top, right, bottom, startAngle, sweepAngle).also {
                    val (x, y) = it.endPosition()
                    this.x = x
                    this.y = y
                }
            )
        }

        override fun circle(
            centerX: Float,
            centerY: Float,
            radius: Float,
            direction: Path.Direction,
        ) {
            _commands.add(Circle(centerX, centerY, radius, direction))
        }

        override fun beginPath() {
            _commands.add(BeginPath)
        }

        override fun closePath() {
            _commands.add(ClosePath)
        }

        /** Create a cubic curve with zero size - used as filler to help with interpolation between
         * [PathDefinition] instances that do not naturally share the same sequence of commands. */
        fun zeroCubic() {
            val x = x ?: return
            val y = y ?: return
            cubicTo(x, y, x, y, x, y)
        }

        fun zeroLine() {
            lineTo(x ?: return, y ?: return)
        }

        fun build(): PathDefinition = PathDefinition(width, height, _commands)
    }

    override fun toString(): String {
        return "PathDefinition(${commands.joinToString("\n")})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PathDefinition

        return commands == other.commands
    }

    override fun hashCode(): Int {
        return commands.hashCode()
    }
}

inline fun PathDefinition(width: Float, height: Float, init: PathDefinition.Builder.() -> Unit) =
    PathDefinition.Builder(width, height).apply(init).build()

///** Create a cubic curve with zero size - used as filler to help with interpolation between
// * [PathDefinition] instances that do not naturally share the same sequence of commands. */
//fun PathDefinition.Builder.zeroCubic(x: Float, y: Float) {
//    cubicTo(x, y, x, y, x, y)
//}
//
///** Create a line with zero size */
//fun PathDefinition.Builder.zeroLine(x: Float, y: Float) {
//    lineTo(x, y)
//}