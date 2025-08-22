package org.beatonma.gclocks.core.util

import org.beatonma.gclocks.test.shouldbe
import kotlin.test.Test

class TimeTest {
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