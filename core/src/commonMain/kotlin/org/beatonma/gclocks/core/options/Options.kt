package org.beatonma.gclocks.core.options

import org.beatonma.gclocks.core.geometry.HorizontalAlignment
import org.beatonma.gclocks.core.geometry.VerticalAlignment
import org.beatonma.gclocks.core.graphics.Paints

interface Options<P : Paints> {
    val paints: P
    val layout: LayoutOptions
    val glyph: GlyphOptions

    companion object {
        const val DefaultSecondsGlyphScale = 0.5f
    }
}

interface LayoutOptions {
    val layout: Layout
    val format: TimeFormat
    val spacingPx: Int

    val horizontalAlignment: HorizontalAlignment
    val verticalAlignment: VerticalAlignment

    /* Alignment of the ClockLayout within the available space given to it */
    val outerHorizontalAlignment: HorizontalAlignment get() = horizontalAlignment

    /* Alignment of the ClockLayout within the available space given to it */
    val outerVerticalAlignment: VerticalAlignment get() = verticalAlignment

    /* Alignment of glyphs within the ClockLayout */
    val innerHorizontalAlignment: HorizontalAlignment get() = horizontalAlignment

    /* Alignment of glyphs within the ClockLayout */
    val innerVerticalAlignment: VerticalAlignment get() = verticalAlignment

    /* Relative scale of glyphs with GlyphRole.Second */
    val secondsGlyphScale: Float
}

interface GlyphOptions {
    /* How long a glyph remains in the active state */
    val activeStateDurationMillis: Int

    /* How long the transition between active/inactive takes */
    val stateChangeDurationMillis: Int

    /* How long it takes to animate from one glyph to the next */
    val glyphMorphMillis: Int
}