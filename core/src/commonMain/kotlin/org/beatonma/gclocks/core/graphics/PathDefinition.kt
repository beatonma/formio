package org.beatonma.gclocks.core.graphics

import org.beatonma.gclocks.core.geometry.Angle
import org.beatonma.gclocks.core.geometry.FloatPoint
import org.beatonma.gclocks.core.geometry.MutableRectF
import org.beatonma.gclocks.core.geometry.Position
import org.beatonma.gclocks.core.geometry.degrees
import org.beatonma.gclocks.core.util.fastForEach
import org.beatonma.gclocks.core.util.interpolate
import kotlin.math.cos
import kotlin.math.sin

typealias RenderCallback = () -> Unit

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

    override fun toString(): String = "BeginPath"
}

object ClosePath : PathCommand {
    override fun plot(canvas: Canvas) {
        canvas.closePath()
    }

    override fun plotInterpolated(canvas: Canvas, other: PathCommand, progress: Float) {
        canvas.closePath()
    }

    override fun toString(): String = "Z"
}

data class MoveTo(private val x: Float, private val y: Float) : PathCommand {
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
}

data class LineTo(private val x: Float, private val y: Float) : PathCommand {
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
}

data class CubicTo(
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
}

data class Circle(
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
}

data class Rect(
    private val left: Float,
    private val top: Float,
    private val right: Float,
    private val bottom: Float,
    private val direction: Path.Direction,
) : PathCommand {
    override fun plot(canvas: Canvas) {
        canvas.rect(left, top, right, bottom, direction)
    }

    override fun plotInterpolated(
        canvas: Canvas,
        other: PathCommand,
        progress: Float,
    ) {
        other as Rect
        canvas.rect(
            interpolate(progress, left, other.left),
            interpolate(progress, top, other.top),
            interpolate(progress, right, other.right),
            interpolate(progress, bottom, other.bottom),
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
    fun plot(canvas: Canvas, render: RenderCallback? = null) {
        commands.fastForEach { command ->
            command.plot(canvas)
            maybeRender(command, render)
        }
    }

    /**
     * Plot the result of Interpolating from this definition to the other.
     *
     * Both [PathDefinition]s must have compatible lists of commands:
     * - They must both have the same number of commands
     * - Each position of the command lists must share the same type of command
     *   (i.e. must interpolate from [LineTo] to [LineTo], etc.; not [LineTo] to [CubicTo], etc.)
     */
    fun plotInterpolated(
        canvas: Canvas,
        other: PathDefinition,
        progress: Float,
        render: RenderCallback? = null,
    ) {
        commands.zip(other.commands).fastForEach { (a, b) ->
            a.plotInterpolated(canvas, b, progress)
            maybeRender(a, render)
        }
    }

    private fun maybeRender(command: PathCommand, render: RenderCallback?) {
        when (command) {
            is ClosePath, is Circle -> render?.invoke()
            else -> {}
        }
    }

    override fun toString(): String {
        return "PathDefinition(${commands.joinToString("\n")})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false

        other as PathDefinition

        return commands == other.commands
    }

    override fun hashCode(): Int {
        return commands.hashCode()
    }

    class Builder(
        private val width: Float?,
        private val height: Float?,
    ) : Path {
        private val _commands: MutableList<PathCommand> = mutableListOf()
        private val bounds = MutableRectF(0f, 0f, 0f, 0f)

        // Track current endpoint of the path so we can easily add 'filler' via [zeroCubic] and [zeroLine].
        private var x: Float? = null
        private var y: Float? = null

        override fun moveTo(x: Float, y: Float) {
            _commands.add(MoveTo(x, y))
            this.x = x
            this.y = y
            bounds.include(x, y)
        }

        override fun lineTo(x: Float, y: Float) {
            _commands.add(LineTo(x, y))
            this.x = x
            this.y = y
            bounds.include(x, y)
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
            bounds.include(x3, y3)
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
                    bounds.include(x, y)
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
            bounds.include(centerX - radius, centerY - radius)
            bounds.include(centerX + radius, centerY + radius)
        }

        override fun rect(
            left: Float,
            top: Float,
            right: Float,
            bottom: Float,
            direction: Path.Direction,
        ) {
            _commands.add(Rect(left, top, right, bottom, direction))
            bounds.include(left, top)
            bounds.include(right, bottom)
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

        fun build(): PathDefinition {
            if (_commands.first() !is BeginPath) {
                _commands.add(0, BeginPath)
            }
            return PathDefinition(
                width ?: bounds.width,
                height ?: bounds.height,
                _commands
            )
        }
    }
}

inline fun PathDefinition(width: Float, height: Float, init: PathDefinition.Builder.() -> Unit) =
    PathDefinition.Builder(width, height).apply(init).build()

inline fun PathDefinition(init: PathDefinition.Builder.() -> Unit) =
    PathDefinition.Builder(null, null).apply(init).build()
