package org.beatonma.gclocks.core.options

interface Options {
    val horizontalAlignment: HorizontalAlignment
    val verticalAlignment: VerticalAlignment
    val layout: Layout
    val format: TimeFormat
    val spacingPx: Int
    val glyphMorphMillis: Int

    /* Relative scale of glyphs with GlyphRole.Second */
    val secondsGlyphScale: Float

    companion object {
        const val DefaultSecondsGlyphScale = 0.5f
    }
}