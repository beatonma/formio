package org.beatonma.gclocks.core.fixtures

import org.beatonma.gclocks.core.layout.ClockLayout
import org.beatonma.gclocks.core.options.GlyphOptions
import org.beatonma.gclocks.core.geometry.HorizontalAlignment
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.core.options.LayoutOptions
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.core.options.TimeFormat
import org.beatonma.gclocks.core.geometry.VerticalAlignment


data class TestOptions(
    override val strokeWidth: Float = 0f,
    override val layout: TestLayoutOptions = TestLayoutOptions(),
    override val glyph: TestGlyphOptions = TestGlyphOptions(),
) : Options

data class TestLayoutOptions(
    override val horizontalAlignment: HorizontalAlignment = HorizontalAlignment.Start,
    override val verticalAlignment: VerticalAlignment = VerticalAlignment.Top,
    override val layout: Layout = Layout.Horizontal,
    override val format: TimeFormat = TimeFormat.HH_MM_SS_24,
    override val spacingPx: Int = 0,
    override val secondsGlyphScale: Float = 0.5f,
) : LayoutOptions

data class TestGlyphOptions(
    override val activeStateDurationMillis: Int = 100,
    override val stateChangeDurationMillis: Int = 100,
    override val glyphMorphMillis: Int = 100,
) : GlyphOptions


fun getTestLayout(
    options: TestOptions = TestOptions(),
    font: TestFont = TestFont(),
) = ClockLayout(
    font,
    options,
)