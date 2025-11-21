package org.beatonma.gclocks.core.fixtures

import kotlinx.serialization.Serializable
import org.beatonma.gclocks.core.Clock
import org.beatonma.gclocks.core.geometry.HorizontalAlignment
import org.beatonma.gclocks.core.geometry.VerticalAlignment
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.graphics.StrokeCap
import org.beatonma.gclocks.core.graphics.StrokeJoin
import org.beatonma.gclocks.core.layout.ClockLayout
import org.beatonma.gclocks.core.options.GlyphOptions
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.core.options.LayoutOptions
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.core.options.TimeFormat


object TestClock : Clock

fun TestPaints(
    colors: List<Color> = emptyList(),
    strokeWidth: Float = 0f,
) = Paints(colors, strokeWidth, StrokeCap.Square, StrokeJoin.Miter)

typealias TestOptions = Options<TestGlyphOptions>

fun createTestOptions(
    paints: Paints = TestPaints(),
    layout: LayoutOptions = TestLayoutOptions(),
    glyph: TestGlyphOptions = TestGlyphOptions(),
) = Options(TestClock, paints, layout, glyph)

@Serializable
data class TestGlyphOptions(
    override val activeStateDurationMillis: Int = 100,
    override val stateChangeDurationMillis: Int = 100,
    override val visibilityChangeDurationMillis: Int = 100,
    override val glyphMorphMillis: Int = 100,
) : GlyphOptions

fun TestLayoutOptions(
    layout: Layout = Layout.Horizontal,
    format: TimeFormat = TimeFormat.HH_MM_SS_24,
    spacingPx: Int = 0,
    horizontalAlignment: HorizontalAlignment = HorizontalAlignment.Start,
    verticalAlignment: VerticalAlignment = VerticalAlignment.Top,
    secondsGlyphScale: Float = 0.5f,
) = LayoutOptions(
    layout,
    format,
    spacingPx,
    horizontalAlignment,
    verticalAlignment,
    secondsGlyphScale,
)


fun getTestLayout(
    options: TestOptions = createTestOptions(),
    font: TestFont = TestFont(),
) = ClockLayout(
    font,
    options,
)
