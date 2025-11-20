package org.beatonma.gclocks.core.graphics.paths

import org.beatonma.gclocks.core.geometry.Angle
import org.beatonma.gclocks.core.graphics.Matrix
import org.beatonma.gclocks.core.graphics.Path
import org.beatonma.gclocks.core.util.fastForEach

typealias RenderCallback = () -> Unit


/**
 * A container for a set of Path commands which can be accessed post-definition.
 * Main selling-point: Enables relatively simple interpolation between states.
 */
class PathDefinition private constructor(
    val width: Float,
    val height: Float,
    internal val commands: List<PathCommand>,
) {
    fun plot(path: Path, render: RenderCallback? = null) {
        commands.fastForEach { command ->
            command.plot(path)
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
        path: Path,
        other: PathDefinition,
        progress: Float,
        render: RenderCallback? = null,
    ) {
        commands.zip(other.commands).fastForEach { (a, b) ->
            a.plotInterpolated(path, b, progress)
            maybeRender(a, render)
        }
    }

    fun canInterpolateWith(other: PathDefinition): Boolean {
        if (commands.size != other.commands.size) return false
        for (index in commands.indices) {
            if (commands[index]::class != other.commands[index]::class) return false
        }
        return true
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

        // Track current endpoint of the path so we can easily add 'filler' via [zeroCubic] and [zeroLine].
        private var x: Float? = null
        private var y: Float? = null

        private fun addCommand(command: PathCommand) {
            _commands.add(command)
            command.endPoint?.let {
                x = it.x
                y = it.y
            }
        }

        private val previousX: Float
            get() = x
                ?: throw IllegalStateException("PathDefinition.Builder tried to use a relative path command without a recorded previous position.")
        private val previousY: Float
            get() = y
                ?: throw IllegalStateException("PathDefinition.Builder tried to use a relative path command without a recorded previous position.")

        override fun moveTo(x: Float, y: Float) {
            addCommand(MoveTo(x, y))
        }

        override fun lineTo(x: Float, y: Float) {
            addCommand(LineTo(x, y))
        }

        override fun cubicTo(
            x1: Float,
            y1: Float,
            x2: Float,
            y2: Float,
            x3: Float,
            y3: Float,
        ) {
            addCommand(CubicTo(x1, y1, x2, y2, x3, y3))
        }

        override fun arcTo(
            left: Float,
            top: Float,
            right: Float,
            bottom: Float,
            startAngle: Angle,
            sweepAngle: Angle,
            forceMoveTo: Boolean
        ) {
            addCommand(ArcTo(left, top, right, bottom, startAngle, sweepAngle, forceMoveTo))
        }

        override fun boundedArc(
            left: Float,
            top: Float,
            right: Float,
            bottom: Float,
            startAngle: Angle,
            sweepAngle: Angle,
        ) {
            addCommand(BoundedArc(left, top, right, bottom, startAngle, sweepAngle))
        }

        fun boundedArc(
            left: Float,
            top: Float,
            right: Float,
            bottom: Float,
            startAngle: Angle = Angle.TwoSeventy,
            sweepAngle: Angle = Angle.OneEighty,
            close: Boolean,
        ) {
            addCommand(
                BoundedArc(left, top, right, bottom, startAngle, sweepAngle, close)
            )
        }

        @Suppress("NOTHING_TO_INLINE")
        inline fun boundedArc(
            centerX: Float,
            centerY: Float,
            radius: Float,
            startAngle: Angle = Angle.TwoSeventy,
            sweepAngle: Angle = Angle.OneEighty,
            close: Boolean,
        ) {
            boundedArc(
                left = centerX - radius,
                top = centerY - radius,
                right = centerX + radius,
                bottom = centerY + radius,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                close = close,
            )
        }

        override fun sector(
            centerX: Float,
            centerY: Float,
            radius: Float,
            startAngle: Angle,
            sweepAngle: Angle
        ) {
            addCommand(Sector(centerX, centerY, radius, startAngle, sweepAngle))
        }

        override fun circle(
            centerX: Float,
            centerY: Float,
            radius: Float,
            direction: Path.Direction,
        ) {
            addCommand(Circle(centerX, centerY, radius, direction))
        }

        override fun rect(
            left: Float,
            top: Float,
            right: Float,
            bottom: Float,
            direction: Path.Direction,
        ) {
            addCommand(Rect(left, top, right, bottom, direction))
        }

        override fun beginPath() {
            addCommand(BeginPath)
        }

        override fun closePath() {
            addCommand(ClosePath)
        }

        override fun transform(matrix: Matrix) {
            throw Exception("Use rotate(), translate(), and scale() instead of PathDefinition.Builder.transform(matrix).")
        }

        override fun transform(
            rotation: Angle,
            translateX: Float,
            translateY: Float,
            scaleX: Float,
            scaleY: Float,
            pivotX: Float,
            pivotY: Float,
        ) {
            addCommand(
                Transform(rotation, translateX, translateY, scaleX, scaleY, pivotX, pivotY)
            )
        }

        override fun rotate(angle: Angle) {
            addCommand(
                Transform(rotation = angle)
            )
        }

        override fun rotate(angle: Angle, pivotX: Float, pivotY: Float) {
            addCommand(
                Transform(rotation = angle, pivotX = pivotX, pivotY = pivotY)
            )
        }

        override fun scale(scale: Float) {
            addCommand(
                Transform(scaleX = scale, scaleY = scale)
            )
        }

        override fun scale(x: Float, y: Float) {
            addCommand(
                Transform(scaleX = x, scaleY = y)
            )
        }

        override fun scale(scale: Float, pivotX: Float, pivotY: Float) {
            addCommand(
                Transform(scaleX = scale, scaleY = scale, pivotX = pivotX, pivotY = pivotY)
            )
        }

        override fun scale(x: Float, y: Float, pivotX: Float, pivotY: Float) {
            addCommand(
                Transform(scaleX = x, scaleY = y, pivotX = pivotX, pivotY = pivotY)
            )
        }

        override fun translate(x: Float, y: Float) {
            addCommand(Transform(translateX = x, translateY = y))
        }

        /**
         * Placeholder for a `rotate` command, enabling a non-rotated path to
         * interpolate with a rotated path.
         *
         * Path interpolation requires that the start and end states share the
         * same sequence of operation types, so insert noop commands as necessary
         * to make those sequences match up correctly.
         */
        fun rotateNoop(pivotX: Float = 0f, pivotY: Float = 0f) =
            rotate(Angle.Zero, pivotX = pivotX, pivotY = pivotY)

        /**
         * Placeholder for a `scale` command, enabling a non-scaled path to
         * interpolate with a scaled path.
         *
         * Path interpolation requires that the start and end states share the
         * same sequence of operation types, so insert noop commands as necessary
         * to make those sequences match up correctly.
         */
        fun scaleNoop(pivotX: Float = 0f, pivotY: Float = 0f) =
            scale(1f, pivotX = pivotX, pivotY = pivotY)

        /**
         * Placeholder for a `translate` command, enabling a non-translated path to
         * interpolate with a translated path.
         *
         * Path interpolation requires that the start and end states share the
         * same sequence of operation types, so insert noop commands as necessary
         * to make those sequences match up correctly.
         */
        fun translateNoop() = translate(0f, 0f)

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
                width ?: Float.NaN,
                height ?: Float.NaN,
                _commands
            )
        }
    }
}

inline fun PathDefinition(width: Float, height: Float, init: PathDefinition.Builder.() -> Unit) =
    PathDefinition.Builder(width, height).apply(init).build()

inline fun PathDefinition(init: PathDefinition.Builder.() -> Unit) =
    PathDefinition.Builder(null, null).apply(init).build()
