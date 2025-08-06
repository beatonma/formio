package org.beatonma.gclocks.core.geometry

import kotlin.math.PI

private const val PI_FLOAT = PI.toFloat()
private const val CIRCLE_RADIANS = (2.0 * PI).toFloat()

@JvmInline
value class Angle internal constructor(override val value: Float) : Scalar {
    val asRadians: Float get() = value
    val asDegrees: Float get() = value * (180f / PI_FLOAT)

    operator fun plus(other: Angle) = Angle(this.value + other.value)
    operator fun minus(other: Angle) = Angle(this.value - other.value)

    operator fun times(factor: Float) = Angle(this.value * factor)

    operator fun div(divisor: Int) = Angle(this.value / divisor)
    operator fun div(divisor: Float) = Angle(this.value / divisor)

    operator fun unaryMinus(): Angle = Angle(-value)

    override fun toString(): String = "$asDegrees°"
}

/** Normalise the value to the range 0 <= n <= 2pi*/
val Float.positiveRadians: Angle
    get() = when {
        this < 0f -> Angle(CIRCLE_RADIANS - (-this % CIRCLE_RADIANS))
        else -> Angle(this % CIRCLE_RADIANS)
    }
val Float.radians: Angle get() = Angle(this)
val Float.positiveDegrees: Angle get() = (this * PI_FLOAT / 180f).positiveRadians
val Float.degrees: Angle get() = Angle(this * PI_FLOAT / 180f)
