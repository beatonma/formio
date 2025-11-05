package org.beatonma.gclocks.core.fixtures

import org.beatonma.gclocks.core.ClockFont
import org.beatonma.gclocks.core.GlyphRole
import org.beatonma.gclocks.core.options.TimeFormat

class TestFont(
    override val measurements: ClockFont.Measurements = ClockFont.Measurements(
        lineHeight = TestGlyph.maxSize.height,
        separatorWidth = 0f,
        maxHours24ZeroPaddedWidth = TestGlyph.maxSize.width * 2f,
        maxHours12ZeroPaddedWidth = TestGlyph.maxSize.width * 2f,
        maxHours24Width = TestGlyph.maxSize.width * 2f,
        maxHours12Width = TestGlyph.maxSize.width * 2f,
        maxMinutesWidth = TestGlyph.maxSize.width * 2f,
        maxSecondsWidth = TestGlyph.maxSize.width * 2f,
    )
) : ClockFont<TestPaints, TestGlyph> {
    override fun getGlyphAt(
        index: Int,
        format: TimeFormat,
        secondsGlyphScale: Float,
    ): TestGlyph {
        val role = format.roles.getOrNull(index) ?: GlyphRole.Default
        val scale = when (role) {
            GlyphRole.Second -> secondsGlyphScale
            else -> 1f
        }
        return TestGlyph(role, scale, measurements.separatorWidth)
    }
}
