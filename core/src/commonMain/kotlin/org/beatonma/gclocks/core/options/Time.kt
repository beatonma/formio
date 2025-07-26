package org.beatonma.gclocks.core.options

import org.beatonma.gclocks.core.GlyphRole
import org.beatonma.gclocks.core.util.TimeOfDay

enum class TimeResolution {
    Minutes,
    Seconds,
}

enum class TimeFormat {
    HH_MM_SS_24,
    H_MM_SS_24,
    HH_MM_24,
    H_MM_24,
    HH_MM_SS_12,
    H_MM_SS_12,
    HH_MM_12,
    H_MM_12,
    ;

    val is24Hour: Boolean get() = this.name.endsWith("24")
    val isZeroPadded: Boolean get() = this.name.startsWith("HH")
    val resolution: TimeResolution get() = if (this.name.contains("SS")) TimeResolution.Seconds else TimeResolution.Minutes
    val roles: List<GlyphRole>
        get() {
            var separatorCount = 0

            return this.name.removeSuffix("_24").removeSuffix("_12").map { char ->
                when (char) {
                    'H' -> GlyphRole.Hour
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

    fun apply(time: TimeOfDay): String {
        val (hour, minute, second) = time

        return listOfNotNull(
            hour.let {
                if (is24Hour) it else (hour % 12).let { if (it == 0) 12 else it }
            }.toString().let {
                if (isZeroPadded) it.padStart(2, '0')
                else it
            },
            minute.toString().padStart(2, '0'),
            if (resolution == TimeResolution.Seconds) second.toString().padStart(2, '0') else null
        ).joinToString(":")
    }
}
