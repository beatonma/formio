package org.beatonma.gclocks.core.util

expect fun getCurrentTimeMillis(): Long

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
}

expect fun getTime(): TimeOfDay
expect fun TimeOfDay.nextSecond(): TimeOfDay
