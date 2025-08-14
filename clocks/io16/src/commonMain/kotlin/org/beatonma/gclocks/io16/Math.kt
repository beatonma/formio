package org.beatonma.gclocks.io16


fun overshoot(value: Float): Float {
    val tension = 0.6f
    val v = value - 1.0f
    return v * v * ((tension + 1) * v + tension) + 1.0f
}

fun accelerateDecelerate(t: Float): Float = 1.0f - (1.0f - t) * (1.0f - t)
fun anticipate(t: Float, tension: Float = 0.2f): Float = t * t * ((tension + 1f) * t - tension)
