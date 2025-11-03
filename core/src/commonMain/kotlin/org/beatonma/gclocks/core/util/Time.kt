package org.beatonma.gclocks.core.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.beatonma.gclocks.core.options.TimeFormat
import kotlin.jvm.JvmName
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


@OptIn(ExperimentalTime::class)
fun getInstant(): Instant = Clock.System.now()


@OptIn(ExperimentalTime::class)
fun getCurrentTimeMillis(instant: Instant): Long {
    return instant.toEpochMilliseconds()
}

/*
 * Explicit overload instead of default parameter so that consumers do
 * not need to opt in for ExperimentalTime.
 */
@OptIn(ExperimentalTime::class)
fun getCurrentTimeMillis(): Long {
    return getCurrentTimeMillis(getInstant())
}


@OptIn(ExperimentalTime::class)
fun getTime(instant: Instant): TimeOfDay {
    val time = instant.toLocalDateTime(TimeZone.currentSystemDefault()).time

    return TimeOfDay(
        time.hour,
        time.minute,
        time.second,
        time.nanosecond / 1_000_000
    )
}

/*
 * Explicit overload instead of default parameter so that consumers do
 * not need to opt in for ExperimentalTime.
 */
@OptIn(ExperimentalTime::class)
fun getTime(): TimeOfDay = getTime(getInstant())


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


inline val Instant.timeOfDay
    get() = getTime(this)
inline val Instant.currentTimeMillis
    @JvmName("getCurrentTimeMillisFromInstant") get() = getCurrentTimeMillis(this)

fun Instant.withTimeOfDay(time: TimeOfDay): Instant {
    val timezone = TimeZone.currentSystemDefault()
    val date = this.toLocalDateTime(timezone).date
    val dateTime = LocalDateTime(
        year = date.year, month = date.month, day = date.day,
        hour = time.hour, minute = time.minute, second = time.second,
        nanosecond = time.millisecond * 1_000_000
    )

    return dateTime.toInstant(timezone)
}
