package org.beatonma.gclocks.core.geometry

import org.beatonma.gclocks.core.util.warn
import kotlin.math.sqrt

interface Vector2<T : Number> : Comparable<Vector2<T>> {
    val x: T
    val y: T
    val magnitude: Float

    operator fun component1() = x
    operator fun component2() = y

    companion object {
        val toString: (v: Vector2<*>) -> String = { v -> "[${v.x}, ${v.y}]" }
    }
}

interface IntVector2 : Vector2<Int> {
    override val magnitude get() = sqrt((x * x).toFloat() + (y * y).toFloat())

    override fun compareTo(other: Vector2<Int>): Int {
        if (this::class != other::class) {
            throw IllegalArgumentException(
                "Cannot compare different Vector2 implementations: ${this::class} vs ${other::class}"
            )
        }
        warn("Default Vector2D compareTo is only intended for use in tests.")

        return (this.magnitude - other.magnitude).toInt()
    }
}


interface FloatVector2 : Vector2<Float> {
    override val magnitude get() = sqrt((x * x) + (y * y))

    override fun compareTo(other: Vector2<Float>): Int {
        if (this::class != other::class) {
            throw IllegalArgumentException(
                "Cannot compare different Vector2 implementations: ${this::class} vs ${other::class}"
            )
        }
        warn("Default Vector2D compareTo is only intended for use in tests.")

        return (this.magnitude - other.magnitude).toInt()
    }
}