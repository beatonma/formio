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

        return listOfNotNull(
            hour.let {
                if (is24Hour) it else (hour % 12).let { if (it == 0) 12 else it }
            }.toString().let {
                if (isZeroPadded) it.padStart(2, '0')
                else it.padStart(2, ' ')
            },
            minute.toString().padStart(2, '0'),
            if (resolution == TimeResolution.Seconds) second.toString().padStart(2, '0') else null
        ).joinToString(":")
    }
}
