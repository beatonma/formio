package org.beatonma.formio.io18

import kotlinx.serialization.Serializable
import org.beatonma.formio.core.Clock
import org.beatonma.formio.core.geometry.HorizontalAlignment
import org.beatonma.formio.core.geometry.VerticalAlignment
import org.beatonma.formio.core.graphics.Paints
import org.beatonma.formio.core.options.GlyphOptions
import org.beatonma.formio.core.options.Layout
import org.beatonma.formio.core.options.LayoutOptions
import org.beatonma.formio.core.options.Options
import org.beatonma.formio.core.options.TimeFormat

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
