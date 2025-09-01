package org.beatonma.gclocks.io18

import kotlinx.serialization.Serializable
import org.beatonma.gclocks.core.geometry.HorizontalAlignment
import org.beatonma.gclocks.core.geometry.VerticalAlignment
import org.beatonma.gclocks.core.options.GlyphOptions
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.core.options.LayoutOptions
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.core.options.TimeFormat

@Serializable
data class Io18Options(
    override val paints: Io18Paints = Io18Paints(),
    override val layout: Io18LayoutOptions = Io18LayoutOptions(),
    override val glyph: Io18GlyphOptions = Io18GlyphOptions(),
) : Options<Io18Paints>

@Serializable
data class Io18LayoutOptions(
    override val layout: Layout = Layout.Wrapped,
    override val horizontalAlignment: HorizontalAlignment = HorizontalAlignment.End,
    override val verticalAlignment: VerticalAlignment = VerticalAlignment.Top,
    override val format: TimeFormat = TimeFormat.HH_MM_SS_24,
    override val spacingPx: Int = 13,
    override val secondsGlyphScale: Float = Options.DefaultSecondsGlyphScale,
) : LayoutOptions

@Serializable
data class Io18GlyphOptions(
    override val activeStateDurationMillis: Int = 5000,
    override val stateChangeDurationMillis: Int = 1200,
    override val glyphMorphMillis: Int = 600,
) : GlyphOptions
