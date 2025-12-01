package org.beatonma.gclocks.io16

import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.graphics.StrokeCap
import org.beatonma.gclocks.core.graphics.StrokeJoin


object Io16Paints {
    val DefaultColors: List<Color> = listOf(
        Color(0xffef5350), // Reddish
        Color(0xff8cf2f2), // Cyanish
        Color(0xff33c9dc), // DarkCyanish
        Color(0xff5c6bc0), // Indigoish
        Color(0xff78909c), // Inactive
    )
}

fun Io16Paints(
    colors: List<Color> = Io16Paints.DefaultColors,
    strokeWidth: Float = 4f,
): Paints {
    require(colors.size == 5) {
        "Io16Paints require 5 colors but got ${colors.size}"
    }
    return Paints(
        colors, strokeWidth, StrokeCap.Round, StrokeJoin.Round,
    )
}
