package org.beatonma.gclocks.core.util

expect fun getCurrentTimeMillis(): Long

interface TimeOfDay {
    val hour: Int
    val minute: Int
    val second: Int
    val millisecond: Int

    operator fun component1() = hour
    operator fun component2() = minute
    operator fun component3() = second
    operator fun component4() = millisecond
}

expect fun getTime(): TimeOfDay
expect fun TimeOfDay.nextSecond(): TimeOfDay