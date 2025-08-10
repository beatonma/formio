package org.beatonma.gclocks.core

import org.beatonma.gclocks.core.geometry.Size
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.core.options.TimeFormat


interface ClockFont<G : Glyph> {
    /**
     * Create a new glyph instance for the given position.
     */
    fun getGlyphAt(index: Int, format: TimeFormat, secondsGlyphScale: Float): G

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
    ): Size<Float>
}
