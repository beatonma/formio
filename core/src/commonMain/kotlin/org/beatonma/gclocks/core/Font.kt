package org.beatonma.gclocks.core

import org.beatonma.gclocks.core.geometry.Size
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.core.options.TimeFormat


interface ClockFont<G : Glyph> {
    fun getGlyphAt(index: Int, format: TimeFormat): G
    fun measure(format: TimeFormat, layout: Layout, spacingPx: Int): Size<Float>
}
