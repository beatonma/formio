package org.beatonma.gclocks.core.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.beatonma.gclocks.test.shouldbe
import kotlin.test.Test
import kotlin.time.Instant

class InstantTest {
    private fun instantOf(hour: Int, minute: Int, second: Int): Instant {
        return LocalDateTime(
            year = 2025,
            month = 10,
            day = 31,
            hour = hour,
            minute = minute,
            second = second,
        ).toInstant(TimeZone.UTC)
    }

    @Test
    fun `instant withTimeOfDay is correct`() {
        val instant = instantOf(14, 11, 0)

        instant.withTimeOfDay(TimeOfDay(15, 51, 2)) shouldbe instantOf(15, 51, 2)
    }
}

class TimeOfDayTest {
    @Test
    fun `nextSecond is correct`() {
        TimeOfDay(0, 0, 0).nextSecond() shouldbe TimeOfDay(0, 0, 1)
        TimeOfDay(0, 0, 1).nextSecond() shouldbe TimeOfDay(0, 0, 2)
        TimeOfDay(0, 0, 59).nextSecond() shouldbe TimeOfDay(0, 1, 0)
        TimeOfDay(0, 1, 0).nextSecond() shouldbe TimeOfDay(0, 1, 1)
        TimeOfDay(11, 59, 59).nextSecond() shouldbe TimeOfDay(12, 0, 0)
        TimeOfDay(12, 0, 59).nextSecond() shouldbe TimeOfDay(12, 1, 0)
        TimeOfDay(23, 59, 59).nextSecond() shouldbe TimeOfDay(0, 0, 0)
    }
}
