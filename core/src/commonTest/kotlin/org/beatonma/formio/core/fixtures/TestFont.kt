package org.beatonma.formio.core.fixtures

import org.beatonma.formio.core.ClockFont
import org.beatonma.formio.core.glyph.GlyphRole
import org.beatonma.formio.core.options.TimeFormat

class TestFont(
    override val measurements: ClockFont.Measurements = DefaultMeasurements,
) : ClockFont<TestGlyph> {
    override fun getGlyphAt(
        index: Int,
        format: TimeFormat,
        secondsGlyphScale: Float,
        previous: TestGlyph?,
        currentTimeMillis: Long,
    ): TestGlyph {
        val role = format.roles.getOrNull(index) ?: GlyphRole.Default
        val scale = when (role) {
            GlyphRole.Second -> secondsGlyphScale
            else -> 1f
        }
        return TestGlyph(TestGlyph.Type.Desynchronized, role, scale, measurements.separatorWidth)
    }

    companion object {
        val DefaultMeasurements = ClockFont.Measurements(
            lineHeight = TestGlyph.maxSize.height,
            separatorWidth = 0f,
            maxHours24ZeroPaddedWidth = TestGlyph.maxSize.width * 2f,
            maxHours12ZeroPaddedWidth = TestGlyph.maxSize.width * 2f,
            maxHours24Width = TestGlyph.maxSize.width * 2f,
            maxHours12Width = TestGlyph.maxSize.width * 2f,
            maxMinutesWidth = TestGlyph.maxSize.width * 2f,
            maxSecondsWidth = TestGlyph.maxSize.width * 2f,
        )
    }
}
