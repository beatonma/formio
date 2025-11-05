package org.beatonma.gclocks.form

import kotlinx.serialization.Serializable
import org.beatonma.gclocks.core.geometry.HorizontalAlignment
import org.beatonma.gclocks.core.geometry.VerticalAlignment
import org.beatonma.gclocks.core.options.GlyphOptions
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.core.options.LayoutOptions
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.core.options.TimeFormat


@Serializable
data class FormOptions(
    override val glyph: FormGlyphOptions = FormGlyphOptions(),
    override val layout: FormLayoutOptions = FormLayoutOptions(),
    override val paints: FormPaints = FormPaints(),
) : Options<FormPaints>

@Serializable
data class FormGlyphOptions(
    override val activeStateDurationMillis: Int = 0,
    override val stateChangeDurationMillis: Int = 0,
    override val visibilityChangeDurationMillis: Int = 600,
    override val glyphMorphMillis: Int = 800,
) : GlyphOptions

@Serializable
data class FormLayoutOptions(
    override val layout: Layout = Layout.Wrapped,
    override val horizontalAlignment: HorizontalAlignment = HorizontalAlignment.End,
    override val verticalAlignment: VerticalAlignment = VerticalAlignment.Top,
    override val format: TimeFormat = TimeFormat.HH_MM_SS_24,
    override val spacingPx: Int = 8,
    override val secondsGlyphScale: Float = Options.DefaultSecondsGlyphScale,
) : LayoutOptions
