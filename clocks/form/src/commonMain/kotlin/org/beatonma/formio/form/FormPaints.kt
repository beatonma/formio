package org.beatonma.formio.form

import org.beatonma.formio.core.graphics.Color
import org.beatonma.formio.core.graphics.Paints
import org.beatonma.formio.core.graphics.StrokeCap
import org.beatonma.formio.core.graphics.StrokeJoin


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
