package org.beatonma.gclocks.form

import org.beatonma.gclocks.core.ClockRenderer
import org.beatonma.gclocks.core.glyph.GlyphRenderer
import org.beatonma.gclocks.core.graphics.Paints

class FormClockRenderer(
    override var paints: Paints,
    override val renderer: GlyphRenderer<FormGlyph>? = null,
) : ClockRenderer<FormGlyph>
