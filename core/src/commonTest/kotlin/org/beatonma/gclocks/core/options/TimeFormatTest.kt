package org.beatonma.gclocks.core.options

import org.beatonma.gclocks.core.glyph.GlyphRole
import org.beatonma.gclocks.core.util.TimeOfDay
import org.beatonma.gclocks.test.shouldbe
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class TimeFormatTest {
    @Test
    fun is24Hour() {
        assertTrue(TimeFormat.HH_MM_SS_24.is24Hour)
        assertTrue(TimeFormat.HH_MM_24.is24Hour)
        assertFalse(TimeFormat.HH_MM_SS_12.is24Hour)
        assertFalse(TimeFormat.hh_MM_12.is24Hour)
    }

    @Test
    fun isZeroPadded() {
        assertTrue(TimeFormat.HH_MM_SS_24.isZeroPadded)
        assertTrue(TimeFormat.HH_MM_24.isZeroPadded)
        assertTrue(TimeFormat.HH_MM_SS_12.isZeroPadded)
        assertTrue(TimeFormat.HH_MM_12.isZeroPadded)

        assertFalse(TimeFormat.hh_MM_SS_24.isZeroPadded)
        assertFalse(TimeFormat.hh_MM_24.isZeroPadded)
        assertFalse(TimeFormat.hh_MM_SS_12.isZeroPadded)
        assertFalse(TimeFormat.hh_MM_12.isZeroPadded)
    }

    @Test
    fun getResolution() {
        assertEquals(TimeResolution.Seconds, TimeFormat.HH_MM_SS_24.resolution)
        assertEquals(TimeResolution.Minutes, TimeFormat.HH_MM_24.resolution)
        assertEquals(TimeResolution.Seconds, TimeFormat.hh_MM_SS_12.resolution)
        assertEquals(TimeResolution.Minutes, TimeFormat.hh_MM_12.resolution)
    }

    @Test
    fun getRoles() {
        assertEquals(
            listOf(
                GlyphRole.Hour,
                GlyphRole.Hour,
                GlyphRole.SeparatorHoursMinutes,
                GlyphRole.Minute,
                GlyphRole.Minute,
            ),
            TimeFormat.HH_MM_24.roles,
        )
        assertEquals(
            listOf(
                GlyphRole.Hour,
                GlyphRole.Hour,
                GlyphRole.SeparatorHoursMinutes,
                GlyphRole.Minute,
                GlyphRole.Minute,
            ),
            TimeFormat.hh_MM_12.roles,
        )
        assertEquals(
            listOf(
                GlyphRole.Hour,
                GlyphRole.Hour,
                GlyphRole.SeparatorHoursMinutes,
                GlyphRole.Minute,
                GlyphRole.Minute,
                GlyphRole.SeparatorMinutesSeconds,
                GlyphRole.Second,
                GlyphRole.Second,
            ),
            TimeFormat.HH_MM_SS_24.roles,
        )
        assertEquals(
            listOf(
                GlyphRole.Hour,
                GlyphRole.Hour,
                GlyphRole.SeparatorHoursMinutes,
                GlyphRole.Minute,
                GlyphRole.Minute,
                GlyphRole.SeparatorMinutesSeconds,
                GlyphRole.Second,
                GlyphRole.Second,
            ),
            TimeFormat.hh_MM_SS_24.roles,
        )
    }

    @Test
    fun `apply is correct with 24 hours and zero padding`() {
        assertEquals(
            "00:00:00",
            TimeFormat.HH_MM_SS_24.apply(TimeOfDay(0, 0, 0)),
        )
        assertEquals(
            "01:02:03",
            TimeFormat.HH_MM_SS_24.apply(TimeOfDay(1, 2, 3)),
        )
        assertEquals(
            "09:02",
            TimeFormat.HH_MM_24.apply(TimeOfDay(9, 2, 3)),
        )
        assertEquals(
            "23:02",
            TimeFormat.HH_MM_24.apply(TimeOfDay(23, 2, 57)),
        )
    }

    @Test
    fun `apply is correct with 12 hours and zero padding`() {
        assertEquals(
            "12:00:00",
            TimeFormat.HH_MM_SS_12.apply(TimeOfDay(0, 0, 0)),
        )
        assertEquals(
            "01:02:03",
            TimeFormat.HH_MM_SS_12.apply(TimeOfDay(1, 2, 3)),
        )
        assertEquals(
            "09:02",
            TimeFormat.HH_MM_12.apply(TimeOfDay(9, 2, 3)),
        )
        assertEquals(
            "11:02",
            TimeFormat.HH_MM_12.apply(TimeOfDay(23, 2, 57)),
        )
    }

    @Test
    fun `apply is correct with 24 hours and no padding`() {
        assertEquals(
            " 0:00:00",
            TimeFormat.hh_MM_SS_24.apply(TimeOfDay(0, 0, 0)),
        )
        assertEquals(
            " 1:02:03",
            TimeFormat.hh_MM_SS_24.apply(TimeOfDay(1, 2, 3)),
        )
        assertEquals(
            " 9:02",
            TimeFormat.hh_MM_24.apply(TimeOfDay(9, 2, 3)),
        )
        assertEquals(
            "23:02",
            TimeFormat.hh_MM_24.apply(TimeOfDay(23, 2, 57)),
        )
    }

    @Test
    fun `apply is correct with 12 hours and no padding`() {
        assertEquals(
            "12:00:00",
            TimeFormat.hh_MM_SS_12.apply(TimeOfDay(0, 0, 0)),
        )
        assertEquals(
            " 1:02:03",
            TimeFormat.hh_MM_SS_12.apply(TimeOfDay(1, 2, 3)),
        )
        assertEquals(
            "12:00:00",
            TimeFormat.hh_MM_SS_12.apply(TimeOfDay(12, 0, 0)),
        )
        assertEquals(
            " 9:02",
            TimeFormat.hh_MM_12.apply(TimeOfDay(9, 2, 3)),
        )
        assertEquals(
            "11:02",
            TimeFormat.hh_MM_12.apply(TimeOfDay(23, 2, 57)),
        )
    }

    @Test
    fun `TimeFormat build is correct`() {
        TimeFormat.build(
            is24Hour = true,
            isZeroPadded = true,
            showSeconds = true,
        ) shouldbe TimeFormat.HH_MM_SS_24

        TimeFormat.build(
            is24Hour = false,
            isZeroPadded = true,
            showSeconds = true,
        ) shouldbe TimeFormat.HH_MM_SS_12

        TimeFormat.build(
            is24Hour = true,
            isZeroPadded = false,
            showSeconds = true,
        ) shouldbe TimeFormat.hh_MM_SS_24

        TimeFormat.build(
            is24Hour = true,
            isZeroPadded = true,
            showSeconds = false,
        ) shouldbe TimeFormat.HH_MM_24

        TimeFormat.build(
            is24Hour = false,
            isZeroPadded = false,
            showSeconds = true,
        ) shouldbe TimeFormat.hh_MM_SS_12

        TimeFormat.build(
            is24Hour = true,
            isZeroPadded = false,
            showSeconds = false,
        ) shouldbe TimeFormat.hh_MM_24

        TimeFormat.build(
            is24Hour = false,
            isZeroPadded = true,
            showSeconds = false,
        ) shouldbe TimeFormat.HH_MM_12

        TimeFormat.build(
            is24Hour = false,
            isZeroPadded = false,
            showSeconds = false,
        ) shouldbe TimeFormat.hh_MM_12
    }
}
