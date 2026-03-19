package org.beatonma.formio.io16

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
object Io16Clock : Clock
typealias Io16Options = Options<Io16GlyphOptions>

fun Io16Options(
    paints: Paints = Io16Paints(),
    layout: LayoutOptions = Io16LayoutOptions(),
    glyph: Io16GlyphOptions = Io16GlyphOptions(),
): Io16Options = Options(Io16Clock, paints, layout, glyph)

fun Io16LayoutOptions(
    layout: Layout = Layout.Wrapped,
    format: TimeFormat = TimeFormat.HH_MM_SS_24,
    spacingPx: Int = 8,
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
data class Io16GlyphOptions(
    override val activeStateDurationMillis: Int = 5000,
    override val stateChangeDurationMillis: Int = 1200,
    override val visibilityChangeDurationMillis: Int = 600,
    override val glyphMorphMillis: Int = 600,
    // How long a path segment takes to complete a circuit
    val colorCycleDurationMillis: Int = 5000,
) : GlyphOptions
