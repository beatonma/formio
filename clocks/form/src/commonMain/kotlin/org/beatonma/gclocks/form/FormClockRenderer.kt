package org.beatonma.gclocks.form

import org.beatonma.gclocks.core.ClockRenderer
import org.beatonma.gclocks.core.GlyphRenderer

class FormClockRenderer(
    override var paints: FormPaints,
    override val renderer: GlyphRenderer<FormGlyph> = GlyphRenderer.Default(),
) : ClockRenderer<FormGlyph, FormPaints>
