package org.beatonma.gclocks.core

import org.beatonma.gclocks.core.geometry.NativeSize
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.core.options.TimeFormat
import org.beatonma.gclocks.core.options.TimeResolution


interface ClockFont<G : Glyph> {
    /**
     * Create a new glyph instance for the given position.
     */
    fun getGlyphAt(index: Int, format: TimeFormat, secondsGlyphScale: Float): G

    fun measure(options: Options<*>) = measure(
        format = options.layout.format,
        layout = options.layout.layout,
        spacingPx = options.layout.spacingPx,
        secondsGlyphScale = options.layout.secondsGlyphScale,
        strokeWidth = options.paints.strokeWidth,
    )

    /**
     * Returns the maximum size needed to render a time in this font (at its
     * native size) with the given options.
     *
     * The resulting space will be reserved - the clock may not use all the
     * space all the time, but there exists some time when it will need to
     * use all the space. Reserving this space means that we can maintain a
     * constant font size throughout the day.
     */
    fun measure(
        format: TimeFormat,
        layout: Layout,
        spacingPx: Int,
        secondsGlyphScale: Float,
        strokeWidth: Float,
    ): NativeSize {
        val hasSeconds = format.resolution == TimeResolution.Seconds
        val spacingPx = spacingPx.toFloat()
        val maxHourWidth = getMaxHoursWidth(format)
        val lineHeight = this.lineHeight + strokeWidth

        val separatorWithSpacingWidth = separatorWidth + strokeWidth + (2f * spacingPx)
        val hoursWidth = maxHourWidth + spacingPx + (2f * strokeWidth)
        val minutesWidth = maxMinutesWidth + spacingPx + (2f * strokeWidth)
        val secondsWidth = secondsGlyphScale * ((maxSecondsWidth + spacingPx) + (2f * strokeWidth))
        val secondsHeight = secondsGlyphScale * lineHeight

        return when (layout) {
            Layout.Horizontal -> {
                return when (hasSeconds) {
                    true -> NativeSize(
                        hoursWidth + minutesWidth + secondsWidth + separatorWithSpacingWidth + spacingPx,
                        lineHeight
                    )

                    false -> NativeSize(
                        hoursWidth + minutesWidth + separatorWithSpacingWidth,
                        lineHeight
                    )
                }
            }

            Layout.Vertical -> when (hasSeconds) {
                true -> NativeSize(
                    maxOf(hoursWidth, minutesWidth, secondsWidth),
                    (lineHeight * 2f) + secondsHeight + (2f * spacingPx)
                )

                false -> NativeSize(
                    maxOf(hoursWidth, minutesWidth),
                    (lineHeight * 2f) + spacingPx
                )
            }

            Layout.Wrapped -> when (hasSeconds) {
                true -> NativeSize(
                    hoursWidth + separatorWithSpacingWidth + minutesWidth,
                    lineHeight + spacingPx + secondsHeight
                )

                false -> NativeSize(
                    maxHourWidth + maxMinutesWidth + (spacingPx * 4f) + separatorWidth,
                    lineHeight
                )
            }
        }
    }

    /** Max combined width of hour glyphs, depending on [format]. */
    private fun getMaxHoursWidth(format: TimeFormat): Float = when (format.isZeroPadded) {
        true -> {
            when (format.is24Hour) {
                true -> maxHours24ZeroPaddedWidth
                false -> maxHours12ZeroPaddedWidth
            }
        }

        false -> {
            when (format.is24Hour) {
                true -> maxHours24Width
                false -> maxHours12Width
            }
        }
    }

    /* Glyph height */
    val lineHeight: Float

    /* Separator width */
    val separatorWidth: Float

    /* Maximum width used to render hours in 24-hour format with zero-padding */
    val maxHours24ZeroPaddedWidth: Float

    /* Maximum width used to render hours in 12-hour format with zero-padding */
    val maxHours12ZeroPaddedWidth: Float

    /* Maximum width used to render hours in 24-hour format without zero-padding */
    val maxHours24Width: Float

    /* Maximum width used to render hours in 12-hour format without zero-padding */
    val maxHours12Width: Float

    /* Maximum width used to render minutes */
    val maxMinutesWidth: Float

    /* Maximum width used to render seconds */
    val maxSecondsWidth: Float
}

