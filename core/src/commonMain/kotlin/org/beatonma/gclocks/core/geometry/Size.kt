package org.beatonma.gclocks.core.geometry

import org.beatonma.gclocks.core.util.debug
import kotlin.jvm.JvmInline

interface Size<T : Number> : Vector2<T> {
    val width: T
    val height: T

    /* Returns true if both width and height are zero. */
    val isZero: Boolean

    /* Returns true if either width and height is zero. */
    val isZeroArea: Boolean
    fun aspectRatio(): Float

    operator fun times(factor: Float): Size<Float>
    fun toRect(): Rect<T>
}

/**
 * We use [Size]<Float> for tracking several different kinds of size in
 * close proximity to each other, and it's easy to confuse them.
 * Here we have several equivalent implementations, different only in their names,
 * to help avoid accidentally passing around the wrong kind of measurement.
 *
 * - [FloatSize]: General-use
 * - [NativeSize]: Size defined using some internal coordinate system.
 *   Used by [org.beatonma.gclocks.core.Glyph]s and
 *   [org.beatonma.gclocks.core.ClockFont]s to define their 'default' size.
 *   [ScaledSize]: The result of scaling a [NativeSize] to fit some
 *   externally-defined space.
 */
interface SizeF : Size<Float>, FloatVector2 {
    override fun aspectRatio(): Float = x / y
    override val isZero: Boolean get() = x == 0f && y == 0f
    override val isZeroArea: Boolean get() = x == 0f || y == 0f
    override fun toRect(): MutableRect<Float> = MutableRectF(0f, 0f, width, height)
}


/** General-purpose implementation of [Size]<Float>. */
@JvmInline
value class FloatSize private constructor(val packedValue: Long) : SizeF {
    constructor(
        x: Float = 0f,
        y: Float = 0f,
    ) : this(packFloats(x, y))

    init {
        debug {
            require(x >= 0f || x.isNaN()) { "FloatSize width must be >= 0, got $x" }
            require(y >= 0f || y.isNaN()) { "FloatSize height must be >= 0, got $y" }
        }
    }

    @Suppress("OVERRIDE_BY_INLINE")
    override val x: Float inline get() = unpackX(packedValue)

    @Suppress("OVERRIDE_BY_INLINE")
    override val y: Float inline get() = unpackY(packedValue)

    @Suppress("OVERRIDE_BY_INLINE")
    override val width: Float inline get() = x

    @Suppress("OVERRIDE_BY_INLINE")
    override val height: Float inline get() = y

    override fun times(factor: Float): FloatSize = FloatSize(
        x * factor,
        y * factor,
    )

    override fun toString(): String = Vector2.toString(this)

    companion object {
        val Init: Size<Float> = FloatSize(Float.NaN, Float.NaN)
        val Zero: Size<Float> = FloatSize(0f, 0f)
    }
}


/** Implementation of [Size]<Float> for storing sizes with an internal coordinate system.
 * Entirely equivalent to FloatSize except for its name. */
@JvmInline
value class NativeSize private constructor(val packedValue: Long) : SizeF {
    constructor(
        x: Float = 0f,
        y: Float = 0f,
    ) : this(packFloats(x, y))

    init {
        debug {
            require(x >= 0f || x.isNaN()) { "NativeSize width must be >= 0, got $x" }
            require(y >= 0f || y.isNaN()) { "NativeSize height must be >= 0, got $y" }
        }
    }

    @Suppress("OVERRIDE_BY_INLINE")
    override val x: Float inline get() = unpackX(packedValue)

    @Suppress("OVERRIDE_BY_INLINE")
    override val y: Float inline get() = unpackY(packedValue)

    @Suppress("OVERRIDE_BY_INLINE")
    override val width: Float inline get() = x

    @Suppress("OVERRIDE_BY_INLINE")
    override val height: Float inline get() = y

    override fun times(factor: Float): ScaledSize = ScaledSize(
        x * factor,
        y * factor,
    )

    override fun toString(): String = Vector2.toString(this)

    companion object {
        val Init: NativeSize = NativeSize(Float.NaN, Float.NaN)
        val Zero: NativeSize = NativeSize(0f, 0f)
    }
}

/** Implementation of [Size]<Float> for storing sizes with an internal coordinate system.
 * Entirely equivalent to FloatSize except for its name. */
@JvmInline
value class ScaledSize private constructor(val packedValue: Long) : SizeF {
    constructor(
        x: Float = 0f,
        y: Float = 0f,
    ) : this(packFloats(x, y))

    init {
        debug {
            require(y >= 0f || y.isNaN()) { "ScaledSize width must be >= 0, got $x" }
            require(y >= 0f || y.isNaN()) { "ScaledSize height must be >= 0, got $y" }
        }
    }

    @Suppress("OVERRIDE_BY_INLINE")
    override val x: Float inline get() = unpackX(packedValue)

    @Suppress("OVERRIDE_BY_INLINE")
    override val y: Float inline get() = unpackY(packedValue)

    @Suppress("OVERRIDE_BY_INLINE")
    override val width: Float inline get() = x

    @Suppress("OVERRIDE_BY_INLINE")
    override val height: Float inline get() = y

    override fun times(factor: Float): ScaledSize = ScaledSize(
        x * factor,
        y * factor,
    )

    override fun toString(): String = Vector2.toString(this)

    companion object {
        val Init: ScaledSize = ScaledSize(Float.NaN, Float.NaN)
        val Zero: ScaledSize = ScaledSize(0f, 0f)
    }
}
