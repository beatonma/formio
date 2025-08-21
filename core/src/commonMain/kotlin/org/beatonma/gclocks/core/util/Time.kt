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

fun timeOfDay(hour: Int, minute: Int, second: Int, millisecond: Int = 0) = object : TimeOfDay {
    override val hour: Int = hour
    override val minute: Int = minute
    override val second: Int = second
    override val millisecond: Int = millisecond
}