package org.beatonma.gclocks.core.geometry

interface Size<T : Number> : Vector2<T> {
    val isEmpty: Boolean
    fun scaledBy(factor: Float): Size<Float>
}

interface MutableSize<T : Number> : Size<T> {
    override var x: T
    override var y: T
}

class IntSize(
    override val x: Int = 0,
    override val y: Int = 0,
) : Size<Int>, IntVector2 {
    override val isEmpty: Boolean
        get() = x == 0 && y == 0

    override fun scaledBy(factor: Float): Size<Float> = FloatSize(
        x.toFloat() * factor,
        y.toFloat() * factor,
    )

    override fun toString(): String = Vector2.toString(this)

    companion object {
        val Zero: Size<Int> = IntSize()
    }
}

class FloatSize(
    override val x: Float = 0f,
    override val y: Float = 0f,
) : Size<Float>, FloatVector2 {
    override val isEmpty: Boolean
        get() = x == 0f && y == 0f

    override fun scaledBy(factor: Float): Size<Float> = FloatSize(
        x * factor,
        y * factor,
    )

    override fun toString(): String = Vector2.toString(this)

    companion object {
        val Zero: Size<Float> = FloatSize()
    }
}