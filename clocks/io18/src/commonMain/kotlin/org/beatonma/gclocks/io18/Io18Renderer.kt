package org.beatonma.gclocks.io18

import org.beatonma.gclocks.core.ClockRenderer
import org.beatonma.gclocks.core.glyph.GlyphRenderer
import org.beatonma.gclocks.core.graphics.Paints

class Io18Renderer(
    override val paints: Paints,
    override val renderer: GlyphRenderer<Io18Glyph>? = null,
) : ClockRenderer<Io18Glyph>
