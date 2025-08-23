package org.beatonma.gclocks.core.types

import org.beatonma.gclocks.core.util.debug

/**
 * [Float] wrapper which requires a value between 0..1.
 * This requirement is only enforced during
 */
@JvmInline
value class NormalFloat(val value: Float) {
    init {
        debug {
            require(value in 0f..1f) {
                "Normal float should be in 0..1 (got $value)"
            }
        }
    }

    operator fun compareTo(other: NormalFloat): Int = this.value.compareTo(other.value)
    operator fun compareTo(other: Float): Int = this.value.compareTo(other)

    operator fun plus(other: NormalFloat): Float = this.value + other.value
    operator fun plus(other: Float): Float = this.value + other

    operator fun minus(other: NormalFloat): Float = this.value - other.value
    operator fun minus(other: Float): Float = this.value - other

    operator fun times(other: NormalFloat): NormalFloat = NormalFloat(this.value * other.value)
    operator fun times(other: Float): Float = this.value * other

    operator fun div(other: NormalFloat): Float = this.value / other.value
    operator fun div(other: Float): Float = this.value / other
    operator fun div(other: Int): NormalFloat = NormalFloat(this.value / other)

    operator fun rem(other: Float): Float = this.value % other


    val isOne inline get() = value == 1f
    val isZero inline get() = value == 0f
    val isNotZero inline get() = value > 0f
    val isNotOne inline get() = value > 1f
    val reversed inline get() = NormalFloat(1f - value)

    companion object {
        val Zero = NormalFloat(0f)
        val One = NormalFloat(1f)
    }
}

val Float.nf inline get() = NormalFloat(this)
