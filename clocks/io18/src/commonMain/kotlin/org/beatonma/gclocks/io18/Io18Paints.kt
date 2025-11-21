package org.beatonma.gclocks.io18

import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.graphics.Stroke
import org.beatonma.gclocks.core.graphics.StrokeCap
import org.beatonma.gclocks.core.graphics.StrokeJoin
import org.beatonma.gclocks.io18.Io18Paints.DefaultColors


object Io18Paints {
    const val ThickStrokeWidth: Float = 26f
    const val HalfStrokeWidth: Float = 13f
    const val ThinStrokeWidth: Float = 3f

    val thickStroke = Stroke(ThickStrokeWidth)
    val thinStroke = Stroke(ThinStrokeWidth, cap = StrokeCap.Round)

    val DefaultColors = listOf(
        Color(0xff1de9b6), // Turquoiseish
        Color(0xffff6c00), // Orangeish
        Color(0xfffdd835), // Yellowish
        Color(0xff536dfe), // Blueish
    )

    fun getRandomPaintIndices(): IntArray = IntArray(4) { it }.apply { shuffle() }
}

fun Io18Paints(
    colors: List<Color> = DefaultColors,
): Paints {
    require(colors.size == 4) {
        "Io18Paints require 4 colors but got ${colors.size}"
    }
    return Paints(
        colors, strokeWidth = 0f, StrokeCap.Butt, StrokeJoin.Round,
    )
}
