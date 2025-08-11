package org.beatonma.gclocks.core.geometry

import kotlin.math.PI

private const val PiFloat = PI.toFloat()
private const val DegreesToRadiansFactor: Float = PiFloat / 180f
private const val RadiansToDegreesFactor: Float = 180f / PiFloat

@JvmInline
value class Angle(val degrees: Float) {
    val asRadians: Float get() = degrees * DegreesToRadiansFactor
    val asDegrees: Float inline get() = degrees

    operator fun plus(other: Angle) = Angle(this.degrees + other.degrees)
    operator fun minus(other: Angle) = Angle(this.degrees - other.degrees)
    operator fun times(factor: Float) = Angle(degrees * factor)

    operator fun div(divisor: Int) = Angle(degrees / divisor)
    operator fun div(divisor: Float) = Angle(degrees / divisor)

    operator fun unaryMinus(): Angle = Angle(-degrees)

    override fun toString(): String = "$asDegrees°"

    companion object {
        val Zero = 0f.degrees
        val Ninety = 90f.degrees
        val OneEighty = 180f.degrees
        val TwoSeventy = 270f.degrees
    }
}

val Float.radians: Angle get() = Angle(this * RadiansToDegreesFactor)
val Float.positiveDegrees: Angle
    inline get() = when {
        this < 0f -> Angle(360f - (-this % 360f))
        else -> Angle(this % 360f)
    }
val Float.degrees: Angle inline get() = Angle(this)
