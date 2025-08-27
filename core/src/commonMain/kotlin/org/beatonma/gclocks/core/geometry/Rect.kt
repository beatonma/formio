package org.beatonma.gclocks.core.geometry

import org.beatonma.gclocks.core.util.debug
import kotlin.math.min
import kotlin.math.max
import kotlin.properties.Delegates


interface Rect<T : Number> {
    val left: T
    val top: T
    val right: T
    val bottom: T

    val width: T
    val height: T
    val area: T
    val isEmpty: Boolean
    val center: Position

    /** Return false if any of the boundaries are unset/NaN */
    val isValid: Boolean

    fun toSize(): Size<T>

    operator fun component1(): T = left
    operator fun component2(): T = top
    operator fun component3(): T = right
    operator fun component4(): T = bottom
}

interface FloatRect : Rect<Float> {
    override val width: Float
        get() = right - left
    override val height: Float
        get() = bottom - top
    override val area: Float
        get() = width * height
    override val isEmpty: Boolean
        get() = !isValid || area == 0f
    override val isValid: Boolean
        get() = !(left.isNaN() || top.isNaN() || right.isNaN() || bottom.isNaN())

    override val center: Position
        get() = FloatPoint(left + (right - left) / 2f, top + (bottom - top) / 2f)

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
    fun include(other: Point<T>): Boolean
    fun include(x: T, y: T): Boolean
    fun set(left: T, top: T, right: T, bottom: T): MutableRect<T>
    fun set(other: Rect<T>) = set(other.left, other.top, other.right, other.bottom)

    /**
     * Move the bounds of this rect towards its center by the given amounts.
     */
    fun inset(left: T, top: T = left, right: T = left, bottom: T = top): MutableRect<T>

    /**
     * Move the bounds of this rect away from its center by the given amounts.
     */
    fun extrude(left: T, top: T = left, right: T = left, bottom: T = top): MutableRect<T>

    fun add(left: T, top: T = left, right: T = left, bottom: T = top): MutableRect<T>

    /**
     * Move the boundaries of the Rect while keeping the same size and shape.
     */
    fun translate(x: T, y: T): MutableRect<T>

    /** Equivalent to set(Nan, NaN, NaN, NaN) */
    fun clear(): MutableRect<T>
}

data class RectF(
    override val left: Float,
    override val top: Float,
    override val right: Float,
    override val bottom: Float,
) : FloatRect


class MutableRectF(
    left: Float = Float.NaN,
    top: Float = Float.NaN,
    right: Float = Float.NaN,
    bottom: Float = Float.NaN,
) : MutableRect<Float>, FloatRect {
    override var left by Delegates.notNull<Float>()
    override var top by Delegates.notNull<Float>()
    override var right by Delegates.notNull<Float>()
    override var bottom by Delegates.notNull<Float>()

    constructor(other: Rect<Float>) : this(other.left, other.top, other.right, other.bottom)

    init {
        set(left, top, right, bottom)
    }

    override fun set(left: Float, top: Float, right: Float, bottom: Float): MutableRect<Float> {
        this.left = min(left, right)
        this.top = min(top, bottom)
        this.right = max(left, right)
        this.bottom = max(top, bottom)
        return this
    }

    override fun inset(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
    ): MutableRect<Float> = set(
        this.left + left,
        this.top + top,
        this.right - right,
        this.bottom - bottom,
    )

    override fun extrude(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
    ): MutableRect<Float> = set(
        this.left - left,
        this.top - top,
        this.right + right,
        this.bottom + bottom,
    )

    override fun add(left: Float, top: Float, right: Float, bottom: Float): MutableRect<Float> =
        set(
            this.left + left,
            this.top + top,
            this.right + right,
            this.bottom + bottom,
        )


    override fun translate(
        x: Float,
        y: Float,
    ): MutableRect<Float> =
        set(left + x, top + y, right + x, bottom + y)

    override fun include(other: Rect<Float>): Boolean {
        if (!isValid) {
            set(other)
            return isValid
        }
        if (!other.isValid) {
            debug("Rect.include with invalid rect")
            return false
        }

        val original = area
        set(
            min(left, other.left),
            min(top, other.top),
            max(right, other.right),
            max(bottom, other.bottom)
        )
        return area != original
    }

    override fun include(x: Float, y: Float): Boolean {
        if (!isValid) {
            set(x, y, x, y)
        }

        val original = area
        set(
            min(left, x),
            min(top, y),
            max(right, x),
            max(bottom, y)
        )
        return area != original
    }

    override fun include(other: Point<Float>): Boolean = include(other.x, other.y)

    override fun clear(): MutableRect<Float> = set(Float.NaN, Float.NaN, Float.NaN, Float.NaN)

    override fun toString(): String {
        return "MutableFloatRect($left, $top, $right, $bottom)"
    }
}
