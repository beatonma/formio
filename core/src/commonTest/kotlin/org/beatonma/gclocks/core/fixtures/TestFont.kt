package org.beatonma.gclocks.core.fixtures

import org.beatonma.gclocks.core.ClockFont
import org.beatonma.gclocks.core.GlyphRole
import org.beatonma.gclocks.core.options.TimeFormat

class TestFont(
    override val lineHeight: Float = TestGlyph.maxSize.height,
    override val separatorWidth: Float = 0f,
    override val maxHours24ZeroPaddedWidth: Float = TestGlyph.maxSize.width * 2f,
    override val maxHours12ZeroPaddedWidth: Float = TestGlyph.maxSize.width * 2f,
    override val maxHours24Width: Float = TestGlyph.maxSize.width * 2f,
    override val maxHours12Width: Float = TestGlyph.maxSize.width * 2f,
    override val maxMinutesWidth: Float = TestGlyph.maxSize.width * 2f,
    override val maxSecondsWidth: Float = TestGlyph.maxSize.width * 2f,
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
        return TestGlyph(role, scale, separatorWidth)
    }
}