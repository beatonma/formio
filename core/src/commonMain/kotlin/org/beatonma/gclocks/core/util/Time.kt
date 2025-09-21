package org.beatonma.gclocks.core.util

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.beatonma.gclocks.core.options.TimeFormat
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalTime::class)
fun getCurrentTimeMillis(): Long {
    return Clock.System.now().toEpochMilliseconds()
}

@OptIn(ExperimentalTime::class)
fun getTime(): TimeOfDay {
    val now = Clock.System.now()
    val time = now.toLocalDateTime(TimeZone.currentSystemDefault()).time

    return TimeOfDay(
        time.hour,
        time.minute,
        time.second,
        time.nanosecond / 1_000_000
    )
}


data class TimeOfDay(
    val hour: Int,
    val minute: Int,
    val second: Int,
    val millisecond: Int = 0,
) {
    init {
        debug {
            require(hour in 0..23) { "Invalid time: $this" }
            require(minute in 0..59) { "Invalid time: $this" }
            require(second in 0..59) { "Invalid time: $this" }
            require(millisecond in 0..999) { "Invalid time: $this" }
        }
    }

    override fun toString(): String {
        return TimeFormat.HH_MM_SS_24.apply(this)
    }
}

fun TimeOfDay.nextSecond(): TimeOfDay {
    val second = (this.second + 1) % 60
    val minute = when (second) {
        0 -> (minute + 1) % 60
        else -> minute
    }
    val hour = when (minute) {
        0 if second == 0 -> (hour + 1) % 24
        else -> hour
    }

    return TimeOfDay(
        hour = hour,
        minute = minute,
        second = second,
        millisecond = 0,
    )
}
