package org.beatonma.gclocks.core.graphics

enum class PaintStyle {
    Fill,
    Stroke,
    ;
}

interface Paints {
    val colors: Array<Color>
        get() = arrayOf(Color.Red, Color.Green, Color.Blue, Color.White)
}