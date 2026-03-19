package org.beatonma.formio.io18

import org.beatonma.formio.core.ClockRenderer
import org.beatonma.formio.core.glyph.GlyphRenderer
import org.beatonma.formio.core.graphics.Paints

class Io18Renderer(
    override val paints: Paints,
    override val renderer: GlyphRenderer<Io18Glyph>? = null,
) : ClockRenderer<Io18Glyph>
