package org.beatonma.gclocks.io18

import kotlinx.serialization.Serializable
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.graphics.Stroke
import org.beatonma.gclocks.core.graphics.StrokeCap
import org.beatonma.gclocks.core.graphics.StrokeJoin


@Serializable
data class Io18Paints(
    override val colors: Array<Color> = DefaultColors,
    override val strokeCap: StrokeCap = StrokeCap.Butt,
    override val strokeJoin: StrokeJoin = StrokeJoin.Round,
) : Paints {
    init {
        require(colors.size == 4)
    }

    // Stroke width is not configurable.
    override val strokeWidth: Float = 0f

    companion object {
        const val ThickStrokeWidth: Float = 26f
        const val HalfStrokeWidth: Float = 13f
        const val ThinStrokeWidth: Float = 3f

        val thickStroke = Stroke(ThickStrokeWidth)
        val thinStroke = Stroke(ThinStrokeWidth, cap = StrokeCap.Round)

        val DefaultColors = arrayOf(
            Color(0xff1de9b6), // Turquoiseish
            Color(0xffff6c00), // Orangeish
            Color(0xfffdd835), // Yellowish
            Color(0xff536dfe), // Blueish
        )

        fun getRandomPaintIndices(): IntArray = IntArray(4) { it }.apply { shuffle() }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Io18Paints

        if (strokeWidth != other.strokeWidth) return false
        if (!colors.contentEquals(other.colors)) return false
        if (strokeCap != other.strokeCap) return false
        if (strokeJoin != other.strokeJoin) return false

        return true
    }

    override fun hashCode(): Int {
        var result = strokeWidth.hashCode()
        result = 31 * result + colors.contentHashCode()
        result = 31 * result + strokeCap.hashCode()
        result = 31 * result + strokeJoin.hashCode()
        return result
    }
}
