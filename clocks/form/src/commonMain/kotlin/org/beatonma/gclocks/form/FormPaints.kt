package org.beatonma.gclocks.form

import kotlinx.serialization.Serializable
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.graphics.StrokeCap
import org.beatonma.gclocks.core.graphics.StrokeJoin


@Serializable
data class FormPaints(
    override val colors: Array<Color> = DefaultColors,
) : Paints {
    init {
        require(colors.size == 3)
    }

    override val strokeWidth: Float = 0f
    override val strokeCap: StrokeCap = StrokeCap.Square
    override val strokeJoin: StrokeJoin = StrokeJoin.Miter

    companion object {
        val DefaultColors: Array<Color>
            get() = arrayOf(
                Color(0xffFF6D00), // Orange
                Color(0xffFFC400), // Yellow
                Color(0xffFFFFFF), // White
            )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false

        other as FormPaints

        if (strokeWidth != other.strokeWidth) return false
        if (!colors.contentEquals(other.colors)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = strokeWidth.hashCode()
        result = 31 * result + colors.contentHashCode()
        return result
    }
}
