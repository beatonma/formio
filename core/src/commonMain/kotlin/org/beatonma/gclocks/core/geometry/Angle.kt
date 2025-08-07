package org.beatonma.gclocks.core.geometry

import kotlin.math.PI

private const val PiFloat = PI.toFloat()
private const val CircleRadians = (2.0 * PI).toFloat()
private const val DegreesToRadiansFactor: Float = PiFloat / 180f
private const val RadiansToDegreesFactor: Float = 180f / PiFloat

@JvmInline
value class Angle internal constructor(override val value: Float) : Scalar {
    val asRadians: Float get() = value
    val asDegrees: Float get() = value * RadiansToDegreesFactor

    operator fun plus(other: Angle) = Angle(this.value + other.value)
    operator fun minus(other: Angle) = Angle(this.value - other.value)

    operator fun times(factor: Float) = Angle(this.value * factor)

    operator fun div(divisor: Int) = Angle(this.value / divisor)
    operator fun div(divisor: Float) = Angle(this.value / divisor)

    operator fun unaryMinus(): Angle = Angle(-value)

    override fun toString(): String = "$asDegrees°"

    companion object {
        val Zero = Angle(0f)
        val Ninety = 90f.degrees
        val OneEighty = 180f.degrees
        val TwoSeventy = 270f.degrees
    }
}

/** Normalise the value to the range 0 <= n <= 2pi*/
val Float.positiveRadians: Angle
    get() = when {
        this < 0f -> Angle(CircleRadians - (-this % CircleRadians))
        else -> Angle(this % CircleRadians)
    }
val Float.radians: Angle get() = Angle(this)
val Float.positiveDegrees: Angle get() = (this * DegreesToRadiansFactor).positiveRadians
val Float.degrees: Angle get() = Angle(this * DegreesToRadiansFactor)
