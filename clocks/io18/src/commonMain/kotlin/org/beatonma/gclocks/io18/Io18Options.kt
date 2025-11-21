package org.beatonma.gclocks.io18

import kotlinx.serialization.Serializable
import org.beatonma.gclocks.core.Clock
import org.beatonma.gclocks.core.geometry.HorizontalAlignment
import org.beatonma.gclocks.core.geometry.VerticalAlignment
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.options.GlyphOptions
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.core.options.LayoutOptions
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.core.options.TimeFormat

@Serializable
object Io18Clock : Clock

typealias Io18Options = Options<Io18GlyphOptions>

fun Io18Options(
    paints: Paints = Io18Paints(),
    layout: LayoutOptions = Io18LayoutOptions(),
    glyph: Io18GlyphOptions = Io18GlyphOptions(),
): Io18Options = Options(Io18Clock, paints, layout, glyph)

fun Io18LayoutOptions(
    layout: Layout = Layout.Wrapped,
    format: TimeFormat = TimeFormat.HH_MM_SS_24,
    spacingPx: Int = 13,
    horizontalAlignment: HorizontalAlignment = HorizontalAlignment.End,
    verticalAlignment: VerticalAlignment = VerticalAlignment.Top,
    secondsGlyphScale: Float = Options.DefaultSecondsGlyphScale,
) = LayoutOptions(
    layout,
    format,
    spacingPx,
    horizontalAlignment,
    verticalAlignment,
    secondsGlyphScale,
)

@Serializable
data class Io18GlyphOptions(
    override val activeStateDurationMillis: Int = 5000,
    override val stateChangeDurationMillis: Int = 1200,
    override val visibilityChangeDurationMillis: Int = 600,
    override val glyphMorphMillis: Int = 600,
) : GlyphOptions
