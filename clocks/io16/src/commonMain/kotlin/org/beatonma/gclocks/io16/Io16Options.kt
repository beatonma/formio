package org.beatonma.gclocks.io16

import kotlinx.serialization.Serializable
import org.beatonma.gclocks.core.geometry.HorizontalAlignment
import org.beatonma.gclocks.core.geometry.VerticalAlignment
import org.beatonma.gclocks.core.options.GlyphOptions
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.core.options.LayoutOptions
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.core.options.TimeFormat

@Serializable
data class Io16Options(
    override val glyph: Io16GlyphOptions = Io16GlyphOptions(),
    override val layout: Io16LayoutOptions = Io16LayoutOptions(),
    override val paints: Io16Paints = Io16Paints(),
) : Options<Io16Paints>

@Serializable
data class Io16GlyphOptions(
    override val activeStateDurationMillis: Int = 5000,
    override val stateChangeDurationMillis: Int = 1200,
    override val visibilityChangeDurationMillis: Int = 1000,
    override val glyphMorphMillis: Int = 600,
    // How long a path segment takes to complete a circuit
    val colorCycleDurationMillis: Int = 5000,
) : GlyphOptions

@Serializable
data class Io16LayoutOptions(
    override val layout: Layout = Layout.Wrapped,
    override val horizontalAlignment: HorizontalAlignment = HorizontalAlignment.End,
    override val verticalAlignment: VerticalAlignment = VerticalAlignment.Top,
    override val format: TimeFormat = TimeFormat.HH_MM_SS_24,
    override val spacingPx: Int = 8,
    override val secondsGlyphScale: Float = Options.DefaultSecondsGlyphScale,
) : LayoutOptions
