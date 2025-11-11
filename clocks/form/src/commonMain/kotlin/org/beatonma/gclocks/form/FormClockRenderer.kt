package org.beatonma.gclocks.form

import org.beatonma.gclocks.core.ClockRenderer
import org.beatonma.gclocks.core.glyph.GlyphRenderer

class FormClockRenderer(
    override var paints: FormPaints,
    override val renderer: GlyphRenderer<FormPaints, FormGlyph>? = null,
) : ClockRenderer<FormPaints, FormGlyph>
