package org.beatonma.gclocks.core.geometry

interface Size<T : Number> : Vector2<T> {
    val isEmpty: Boolean
    fun scaledBy(factor: Float): Size<Float>
    fun aspectRatio(): Float
}


@JvmInline
value class FloatSize private constructor(val packedValue: Long) : Size<Float>, FloatVector2 {
    constructor(x: Float = 0f, y: Float = 0f) : this(packFloats(x, y))

    override inline val x: Float get() = unpackX(packedValue)
    override inline val y: Float get() = unpackY(packedValue)
    override val isEmpty: Boolean
        get() = x == 0f && y == 0f

    override fun scaledBy(factor: Float): Size<Float> = FloatSize(
        x * factor,
        y * factor,
    )

    override fun aspectRatio(): Float = x / y

    override fun toString(): String = Vector2.toString(this)

    companion object {
        val Zero: Size<Float> = FloatSize()
    }
}