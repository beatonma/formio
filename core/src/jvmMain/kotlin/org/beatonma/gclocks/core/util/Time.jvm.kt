@file:JvmName("TimeJvmKt")

package org.beatonma.gclocks.core.util

import java.time.LocalDateTime

actual fun getCurrentTimeMillis(): Long = System.currentTimeMillis()

actual fun getTime(): TimeOfDay {
    val now = LocalDateTime.now()

    return timeOfDay(
        hour = now.hour,
        minute = now.minute,
        second = now.second,
        millisecond = now.nano / 1_000_000,
    )
}

actual fun TimeOfDay.nextSecond(): TimeOfDay {
    val second = (this.second + 1) % 60
    val minute = if (second == 0) (minute + 1) % 60 else minute
    val hour = if (minute == 0) (hour + 1) % 24 else hour

    return timeOfDay(
        hour = hour,
        minute = minute,
        second = second,
        millisecond = 0,
    )
}