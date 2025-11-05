package org.beatonma.gclocks.core.geometry

import org.beatonma.gclocks.core.util.distance
import kotlin.jvm.JvmInline

interface Point<T : Number> : Vector2<T> {
    fun distanceTo(other: Point<T>): Float {
        return distance(x.toFloat(), y.toFloat(), other.x.toFloat(), other.y.toFloat())
    }
}

interface MutablePoint<T : Number> : Point<T> {
    override var x: T
    override var y: T
}


@JvmInline
value class FloatPoint private constructor(val packedValue: Long) : Point<Float>, FloatVector2 {
    constructor(x: Float, y: Float) : this(packFloats(x, y))

    @Suppress("OVERRIDE_BY_INLINE")
    override val x inline get() = unpackX(packedValue)

    @Suppress("OVERRIDE_BY_INLINE")
    override val y inline get() = unpackY(packedValue)
}
typealias Position = Point<Float>
