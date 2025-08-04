@file:JvmName("TimeJvmKt")

package org.beatonma.gclocks.core.util

import java.time.LocalDateTime

actual fun getCurrentTimeMillis(): Long = System.currentTimeMillis()

class JvmTimeOfDay(
    override val hour: Int,
    override val minute: Int,
    override val second: Int,
    override val millisecond: Int,
) : TimeOfDay

actual fun getTime(): TimeOfDay {
    val now = LocalDateTime.now()

    return JvmTimeOfDay(
        hour = now.hour,
        minute = now.minute,
        second = now.second,
        millisecond = now.nano / 1_000_000,
    )
}

actual fun TimeOfDay.nextSecond(): TimeOfDay {
    var second = (this.second + 1) % 60
    var minute = if (second == 0) (minute + 1) % 60 else minute
    var hour = if (minute == 0) (hour + 1) % 24 else hour

    return JvmTimeOfDay(
        hour = hour,
        minute = minute,
        second = second,
        millisecond = 0,
    )
}