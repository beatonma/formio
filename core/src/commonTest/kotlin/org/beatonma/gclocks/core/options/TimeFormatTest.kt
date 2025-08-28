package org.beatonma.gclocks.core.options

import org.beatonma.gclocks.core.GlyphRole
import org.beatonma.gclocks.core.util.TimeOfDay
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
}
