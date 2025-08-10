package org.beatonma.gclocks.form

import org.beatonma.gclocks.core.ClockRenderer
import org.beatonma.gclocks.core.GlyphRenderer
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.Paints

class FormPaints : Paints {
    override val colors: Array<Color>
        get() = arrayOf(
            Color(0xffFF6D00),
            Color(0xffFFC400),
            Color(0xffFFFFFF),
        )
}

class FormClockRenderer(
    override var paints: Paints = FormPaints(),
    override val renderer: GlyphRenderer<FormGlyph> = GlyphRenderer.Default(),
) : ClockRenderer<FormGlyph>
