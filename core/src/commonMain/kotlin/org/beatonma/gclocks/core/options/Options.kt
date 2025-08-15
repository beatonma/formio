package org.beatonma.gclocks.core.options

interface Options {
    val horizontalAlignment: HorizontalAlignment
    val verticalAlignment: VerticalAlignment
    val layout: Layout
    val format: TimeFormat
    val spacingPx: Int
    val strokeWidth: Float

    /* How long it takes to animate from one glyph to the next */
    val glyphMorphMillis: Int

    /* Relative scale of glyphs with GlyphRole.Second */
    val secondsGlyphScale: Float

    /* How long a glyph remains in the active state */
    val activeStateDurationMillis: Int

    /* How long the transition between active/inactive takes */
    val stateChangeDurationMillis: Int

    companion object {
        const val DefaultSecondsGlyphScale = 0.5f
    }
}