package org.beatonma.gclocks.core.options

import org.beatonma.gclocks.core.GlyphRole
import org.beatonma.gclocks.core.util.TimeOfDay

enum class TimeResolution {
    Minutes,
    Seconds,
}

/** Naming is important!
 *
 * Hours:
 * - `HH` (uppercase): zero-padding
 * - `hh` (lowercase): no zero-padding
 *
 * Seconds will be shown only if `SS` is present.
 *
 * Suffix:
 * - `_12`: 12-hour clock
 * - `_24`: 24-hour clock
 */
@Suppress("EnumEntryName")
enum class TimeFormat {
    HH_MM_SS_24,
    hh_MM_SS_24,
    HH_MM_24,
    hh_MM_24,
    HH_MM_SS_12,
    hh_MM_SS_12,
    HH_MM_12,
    hh_MM_12,
    ;

    val is24Hour: Boolean get() = this.name.endsWith("24")
    val isZeroPadded: Boolean get() = this.name.startsWith("HH")
    val resolution: TimeResolution get() = if (this.name.contains("SS")) TimeResolution.Seconds else TimeResolution.Minutes
    val showSeconds: Boolean get() = this.resolution == TimeResolution.Seconds
    val roles: List<GlyphRole>
        get() {
            var separatorCount = 0

            return this.name.removeSuffix("_24").removeSuffix("_12").map { char ->
                when (char) {
                    'h', 'H' -> GlyphRole.Hour
                    'M' -> GlyphRole.Minute
                    'S' -> GlyphRole.Second
                    '_' -> {
                        if (separatorCount++ == 0) GlyphRole.SeparatorHoursMinutes
                        else GlyphRole.SeparatorMinutesSeconds
                    }

                    else -> GlyphRole.Default
                }
            }
        }
    val stringLength: Int
        get() = when (resolution) {
            TimeResolution.Minutes -> 5 // HH_MM: Two 'hour' digits reserved even if zero-padding is not used
            TimeResolution.Seconds -> 8 // HH_MM_SS: Two 'hour' digits reserved even if zero-padding is not used
        }

    fun apply(time: TimeOfDay): String {
        val (hour, minute, second) = time

        val formattedHour =
            when (is24Hour) {
                true -> hour
                false -> {
                    val mod12 = hour % 12
                    if (mod12 == 0) 12 else mod12
                }
            }.toString()
                .let { hourString ->
                    if (isZeroPadded) hourString.padStart(2, '0')
                    else hourString.padStart(2, ' ')
                }
        val formattedMinute = minute.toString().padStart(2, '0')
        val formattedSecond = when (resolution) {
            TimeResolution.Seconds -> second.toString().padStart(2, '0')
            else -> null
        }

        return listOfNotNull(
            formattedHour,
            formattedMinute,
            formattedSecond
        ).joinToString(":")
    }

    companion object {
        fun build(
            is24Hour: Boolean,
            isZeroPadded: Boolean,
            showSeconds: Boolean,
        ): TimeFormat {
            val enumString = listOfNotNull(
                if (isZeroPadded) "HH" else "hh",
                "MM",
                if (showSeconds) "SS" else null,
                if (is24Hour) "24" else "12"
            ).joinToString("_")
            return TimeFormat.valueOf(enumString)
        }
    }
}
