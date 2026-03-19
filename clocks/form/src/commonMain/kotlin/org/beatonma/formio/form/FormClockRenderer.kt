package org.beatonma.formio.form

import org.beatonma.formio.core.ClockRenderer
import org.beatonma.formio.core.glyph.GlyphRenderer
import org.beatonma.formio.core.graphics.Paints

class FormClockRenderer(
    override var paints: Paints,
    override val renderer: GlyphRenderer<FormGlyph>? = null,
) : ClockRenderer<FormGlyph>
