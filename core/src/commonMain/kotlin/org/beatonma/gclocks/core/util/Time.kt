package org.beatonma.gclocks.core.util


expect fun getCurrentTimeMillis(): Long

interface TimeOfDay {
    val hour: Int
    val minute: Int
    val second: Int

    operator fun component1() = hour
    operator fun component2() = minute
    operator fun component3() = second
}

expect fun getTime(): TimeOfDay
