package org.beatonma.gclocks.core.graphics

import kotlinx.serialization.Serializable


@Serializable
data class Paints(
    val colors: List<Color>,
    val strokeWidth: Float,
    val strokeCap: StrokeCap,
    val strokeJoin: StrokeJoin,
) {
    operator fun get(index: Int) = colors[index]
}
