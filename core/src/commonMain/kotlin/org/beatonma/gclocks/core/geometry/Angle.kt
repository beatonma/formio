package org.beatonma.gclocks.core.geometry

import kotlin.math.PI


@JvmInline
value class Degrees(val value: Float) {
    fun toRadians(): Float = (value * PI).toFloat() / 180f
}
