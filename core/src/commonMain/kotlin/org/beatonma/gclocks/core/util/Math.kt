package org.beatonma.gclocks.core.util

import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Returns min if value < min.
 * Returns max if value > max.
 * Returns value otherwise.
 */
fun constrain(value: Float, min: Float, max: Float): Float =
    max(min(value, max), min)

fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float =
    sqrt((x2 - x1).pow(2) + (y1 - y2).pow(2))

/**
 * Convert value (min..max) to the range 0..1.
 * Return value is always in the range 0..1, even if value falls outside the
 * given min..max range.
 */
fun progress(value: Float, min: Float, max: Float): Float =
    constrain((value - min) / (max - min), 0f, 1f)

fun progress(value: Long, min: Long, max: Long): Float =
    constrain(((value - min).toFloat() / (max - min).toFloat()), 0f, 1f)

fun progress(value: Int, min: Int, max: Int): Float =
    constrain(((value - min).toFloat() / (max - min).toFloat()), 0f, 1f)

/**
 * Convert progress (0..1) to a value in the range defined my min..max.
 */
fun interpolate(progress: Float, min: Float, max: Float): Float =
    when (progress) {
        0f -> min
        1f -> max
        else -> min + (max - min) * progress
    }

fun accelerate(value: Float, power: Int) = (1f - value).pow(power)
fun decelerate(value: Float, power: Int) = 1f - accelerate(value, power)

fun accelerate5(value: Float) = accelerate(value, 5)
fun decelerate5(value: Float) = decelerate(value, 5)
fun decelerate3(value: Float) = decelerate(value, 3)
fun decelerate2(value: Float) = decelerate(value, 2)


@Suppress("NOTHING_TO_INLINE")
inline fun Float.progressIn(min: Float, max: Float): Float = progress(this, min, max)

@Suppress("NOTHING_TO_INLINE")
inline fun Int.progressIn(min: Int, max: Int): Float = progress(this, min, max)

@Suppress("NOTHING_TO_INLINE")
inline fun Long.progressIn(min: Long, max: Long): Float = progress(this, min, max)

@Suppress("NOTHING_TO_INLINE")
inline fun Float.lerp(start: Float, end: Float) = interpolate(this, start, end)
