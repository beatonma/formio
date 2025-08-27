package org.beatonma.gclocks.core.graphics

interface Paints {
    val colors: Array<Color>
    val strokeWidth: Float
    val strokeCap: StrokeCap
    val strokeJoin: StrokeJoin

    operator fun get(index: Int) = colors[index]
}