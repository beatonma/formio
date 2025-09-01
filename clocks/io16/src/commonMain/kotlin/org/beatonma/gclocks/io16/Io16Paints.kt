package org.beatonma.gclocks.io16

import kotlinx.serialization.Serializable
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.graphics.StrokeCap
import org.beatonma.gclocks.core.graphics.StrokeJoin


@Serializable
data class Io16Paints(
    override val colors: Array<Color> = DefaultColors,
    override val strokeWidth: Float = 8f,
) : Paints {
    init {
        require(colors.size == 5)
    }

    override val strokeCap: StrokeCap = StrokeCap.Round
    override val strokeJoin: StrokeJoin = StrokeJoin.Round

    val active: Array<Color> = colors.copyOfRange(0, 4)
    val inactive: Color = colors.last()

    companion object {
        val DefaultColors = arrayOf(
            Color(0xffef5350), // Reddish
            Color(0xff8cf2f2), // Cyanish
            Color(0xff33c9dc), // DarkCyanish
            Color(0xff5c6bc0), // Indigoish
            Color(0xff78909c), // Inactive
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Io16Paints

        if (strokeWidth != other.strokeWidth) return false
        if (!active.contentEquals(other.active)) return false
        if (inactive != other.inactive) return false

        return true
    }

    override fun hashCode(): Int {
        var result = strokeWidth.hashCode()
        result = 31 * result + active.contentHashCode()
        result = 31 * result + inactive.hashCode()
        return result
    }
}