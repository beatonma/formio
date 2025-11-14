package org.beatonma.gclocks.core.graphics.paths

import org.beatonma.gclocks.core.geometry.Angle
import org.beatonma.gclocks.core.geometry.MutableRectF
import org.beatonma.gclocks.core.geometry.Position
import org.beatonma.gclocks.core.geometry.degrees
import org.beatonma.gclocks.core.geometry.getPointOnEllipse
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Path
import org.beatonma.gclocks.core.util.fastForEach

typealias RenderCallback = () -> Unit


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
            is ClosePath, is Circle, is Rect -> render?.invoke()
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

        private val previousX: Float
            get() = x
                ?: throw IllegalStateException("PathDefinition.Builder tried to use a relative path command without a recorded previous position.")
        private val previousY: Float
            get() = y
                ?: throw IllegalStateException("PathDefinition.Builder tried to use a relative path command without a recorded previous position.")

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

        /**
         * Create a cubic curve with zero size - used as filler to help with interpolation between
         * [PathDefinition] instances that do not naturally share the same sequence of commands.
         */
        fun zeroCubic() {
            val x = previousX
            val y = previousY
            cubicTo(x, y, x, y, x, y)
        }

        /**
         * Create a line with zero size - used as filler to help with interpolation between
         * [PathDefinition] instances that do not naturally share the same sequence of commands.
         */
        fun zeroLine() {
            lineTo(previousX, previousY)
        }

        /**
         * Create a cubic curve which looks like a line - used to help with interpolation
         * between [PathDefinition] instances that do not naturally share the same sequence of commands.
         * */
        fun cubicLineTo(x: Float, y: Float) {
            cubicTo(previousX, previousY, x, y, x, y)
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
