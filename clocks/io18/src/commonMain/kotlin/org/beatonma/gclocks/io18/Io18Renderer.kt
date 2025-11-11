package org.beatonma.gclocks.io18

import org.beatonma.gclocks.core.ClockRenderer
import org.beatonma.gclocks.core.glyph.GlyphRenderer

class Io18Renderer(
    override val paints: Io18Paints,
    override val renderer: GlyphRenderer<Io18Paints, Io18Glyph>? = null,
) : ClockRenderer<Io18Paints, Io18Glyph>
