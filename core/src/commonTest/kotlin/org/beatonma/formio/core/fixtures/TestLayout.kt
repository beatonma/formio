package org.beatonma.formio.core.fixtures

import kotlinx.serialization.Serializable
import org.beatonma.formio.core.Clock
import org.beatonma.formio.core.geometry.HorizontalAlignment
import org.beatonma.formio.core.geometry.VerticalAlignment
import org.beatonma.formio.core.graphics.Color
import org.beatonma.formio.core.graphics.Paints
import org.beatonma.formio.core.graphics.StrokeCap
import org.beatonma.formio.core.graphics.StrokeJoin
import org.beatonma.formio.core.layout.ClockLayout
import org.beatonma.formio.core.options.GlyphOptions
import org.beatonma.formio.core.options.Layout
import org.beatonma.formio.core.options.LayoutOptions
import org.beatonma.formio.core.options.Options
import org.beatonma.formio.core.options.TimeFormat


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
