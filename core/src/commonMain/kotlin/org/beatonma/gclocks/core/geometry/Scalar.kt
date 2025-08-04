package org.beatonma.gclocks.core.geometry

interface Scalar : Comparable<Scalar> {
    val value: Float

    override fun compareTo(other: Scalar): Int {
        if (this::class == other::class) {
            return value.compareTo(other.value)
        }
        throw IllegalArgumentException(
            "Cannot compare different Scalar implementations: ${this::class} vs ${other::class}"
        )
    }

    fun isZero(): Boolean = value == 0f
}

operator fun <T : Scalar> T.div(other: T): Float = this.value / other.value
