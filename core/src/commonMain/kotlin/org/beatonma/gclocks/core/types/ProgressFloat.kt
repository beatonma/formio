package org.beatonma.gclocks.core.types

import org.beatonma.gclocks.core.util.debug
import kotlin.jvm.JvmInline

/**
 * [Float] which (almost always) falls in the range 0..1.
 *
 * Used for tracking animation progress. Animation interpolation
 * may bump the value outside of that range, but only by a small margin.
 *
 * Value requirement is only enforced in debug builds to catch unintended
 * usage (e.g. trying to use a value that stores distance as an animation progress.)
 */
@JvmInline
value class ProgressFloat(val value: Float) {
    init {
        debug {
            val tolerance = 0.15f
            val range = (0f - tolerance)..(1f + tolerance)
            require(value in range) {
                "ProgressFloat not in expected range $range (got $value)"
            }
        }
    }

    operator fun compareTo(other: ProgressFloat): Int = this.value.compareTo(other.value)
    operator fun compareTo(other: Float): Int = this.value.compareTo(other)

    operator fun plus(other: ProgressFloat): Float = this.value + other.value
    operator fun plus(other: Float): Float = this.value + other

    operator fun minus(other: ProgressFloat): Float = this.value - other.value
    operator fun minus(other: Float): Float = this.value - other

    operator fun times(other: ProgressFloat): ProgressFloat =
        ProgressFloat(this.value * other.value)

    operator fun times(other: Float): Float = this.value * other
    operator fun times(other: Int): Float = this.value * other

    operator fun div(other: ProgressFloat): Float = this.value / other.value
    operator fun div(other: Float): Float = this.value / other
    operator fun div(other: Int): ProgressFloat = ProgressFloat(this.value / other)

    operator fun rem(other: Float): Float = this.value % other


    val isOne inline get() = value == 1f
    val isZero inline get() = value == 0f
    val isNotZero inline get() = value > 0f
    val isNotOne inline get() = value > 1f
    val reversed inline get() = ProgressFloat(1f - value)

    companion object {
        val Zero = ProgressFloat(0f)
        val One = ProgressFloat(1f)
    }
}

val Float.pf inline get() = ProgressFloat(this)
