package org.beatonma.gclocks.form

import org.beatonma.gclocks.core.options.GlyphOptions
import org.beatonma.gclocks.core.geometry.HorizontalAlignment
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.core.options.LayoutOptions
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.core.options.TimeFormat
import org.beatonma.gclocks.core.geometry.VerticalAlignment


data class FormOptions(
    override val glyph: FormGlyphOptions = FormGlyphOptions(),
    override val layout: FormLayoutOptions = FormLayoutOptions(),
    override val strokeWidth: Float = 0f,
) : Options

data class FormGlyphOptions(
    override val activeStateDurationMillis: Int = 0,
    override val stateChangeDurationMillis: Int = 0,
    override val glyphMorphMillis: Int = 800,
) : GlyphOptions

data class FormLayoutOptions(
    override val horizontalAlignment: HorizontalAlignment = HorizontalAlignment.End,
    override val verticalAlignment: VerticalAlignment = VerticalAlignment.Top,
    override val layout: Layout = Layout.Vertical,
    override val format: TimeFormat = TimeFormat.HH_MM_SS_24,
    override val spacingPx: Int = 8,
    override val secondsGlyphScale: Float = Options.DefaultSecondsGlyphScale,
) : LayoutOptions