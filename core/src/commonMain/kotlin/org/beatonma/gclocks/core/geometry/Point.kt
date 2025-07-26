package org.beatonma.gclocks.core.geometry

import org.beatonma.gclocks.core.util.distance

interface Point<T : Number> {
    val x: T
    val y: T

    fun distanceTo(other: Point<T>): Float {
        return distance(x.toFloat(), y.toFloat(), other.x.toFloat(), other.y.toFloat())
    }
}

interface MutablePoint<T : Number> : Point<T> {
    override var x: T
    override var y: T
}
