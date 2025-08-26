package org.beatonma.gclocks.io16


fun overshoot(value: Float, tension: Float = 0.6f): Float {
    val v = value - 1f
    return v * v * ((tension + 1) * v + tension) + 1f
}

fun anticipate(t: Float, tension: Float = 0.2f): Float = t * t * ((tension + 1f) * t - tension)
