package org.beatonma.gclocks.form

import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.graphics.StrokeCap
import org.beatonma.gclocks.core.graphics.StrokeJoin


object FormPaints {
    val DefaultColors: List<Color> = listOf(
        Color(0xffFF6D00), // Orange
        Color(0xffFFC400), // Yellow
        Color(0xffFFFFFF), // White
    )
}

fun FormPaints(
    colors: List<Color> = FormPaints.DefaultColors,
    strokeWidth: Float = 0f,
): Paints {
    require(colors.size == 3) {
        "FormPaints require 3 colors but got ${colors.size}"
    }
    return Paints(
        colors, strokeWidth, StrokeCap.Square, StrokeJoin.Miter
    )
}
