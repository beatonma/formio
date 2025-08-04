package org.beatonma.gclocks.core.geometry

import kotlin.math.min
import kotlin.math.max


interface Rect<T : Number> {
    val left: T
    val top: T
    val right: T
    val bottom: T

    val width: T
    val height: T
    val area: T
    val isEmpty: Boolean

    fun toSize(): Size<T>

    operator fun component1(): T = left
    operator fun component2(): T = top
    operator fun component3(): T = right
    operator fun component4(): T = bottom
}

interface IntRect : Rect<Int> {
    override val width: Int
        get() = right - left
    override val height: Int
        get() = bottom - top
    override val area: Int
        get() = width * height
    override val isEmpty: Boolean
        get() = area == 0

    override fun toSize(): Size<Int> = IntSize(width, height)
}

interface FloatRect : Rect<Float> {
    override val width: Float
        get() = right - left
    override val height: Float
        get() = bottom - top
    override val area: Float
        get() = width * height
    override val isEmpty: Boolean
        get() = area == 0f

    override fun toSize(): Size<Float> = FloatSize(width, height)
}


interface MutableRect<T : Number> : Rect<T> {
    override var left: T
    override var top: T
    override var right: T
    override var bottom: T

    /** Update the boundaries of the Rect to fully include the other.
     * Returns true if this results in our boundaries changing.
     * Returns false if the other Rect was already within our boundaries.*/
    fun include(other: Rect<T>): Boolean
    fun set(left: T, top: T, right: T, bottom: T): MutableRect<T>
    fun set(other: Rect<T>) = set(other.left, other.top, other.right, other.bottom)

    /** Equivalent to set(0, 0, 0, 0) */
    fun reset(): MutableRect<T>
}

class MutableIntRect(
    override var left: Int = 0,
    override var top: Int = 0,
    override var right: Int = 0,
    override var bottom: Int = 0,
) : MutableRect<Int>, IntRect {
    override fun set(left: Int, top: Int, right: Int, bottom: Int): MutableIntRect {
        this.left = min(left, right)
        this.top = max(top, bottom)
        this.right = max(left, right)
        this.bottom = min(top, bottom)
        return this
    }

    override fun reset() = set(0, 0, 0, 0)

    override fun include(other: Rect<Int>): Boolean {
        val original = this.area
        this.set(
            min(left, other.left),
            max(top, other.top),
            max(left, other.right),
            min(bottom, other.bottom)
        )
        return this.area != original
    }
}

class MutableFloatRect(
    override var left: Float = 0f,
    override var top: Float = 0f,
    override var right: Float = 0f,
    override var bottom: Float = 0f,
) : MutableRect<Float>, FloatRect {
    override fun set(left: Float, top: Float, right: Float, bottom: Float): MutableFloatRect {
        this.left = min(left, right)
        this.top = max(top, bottom)
        this.right = max(left, right)
        this.bottom = min(top, bottom)
        return this
    }

    override fun reset() = set(0f, 0f, 0f, 0f)

    override fun include(other: Rect<Float>): Boolean {
        val original = this.area
        this.set(
            min(left, other.left),
            max(top, other.top),
            max(left, other.right),
            min(bottom, other.bottom)
        )
        return this.area != original
    }
}
