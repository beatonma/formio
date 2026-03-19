package org.beatonma.formio.form

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
object FormClock : Clock

typealias FormOptions = Options<FormGlyphOptions>

fun FormOptions(
    paints: Paints = FormPaints(),
    layout: LayoutOptions = FormLayoutOptions(),
    glyph: FormGlyphOptions = FormGlyphOptions(),
): FormOptions = Options(FormClock, paints, layout, glyph)

fun FormLayoutOptions(
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
data class FormGlyphOptions(
    override val activeStateDurationMillis: Int = 0,
    override val stateChangeDurationMillis: Int = 0,
    override val visibilityChangeDurationMillis: Int = 600,
    override val glyphMorphMillis: Int = 800,
) : GlyphOptions
