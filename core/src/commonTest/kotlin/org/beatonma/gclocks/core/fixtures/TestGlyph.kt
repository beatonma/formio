package org.beatonma.gclocks.core.fixtures

import org.beatonma.gclocks.core.BaseClockGlyph
import org.beatonma.gclocks.core.ClockGlyph
import org.beatonma.gclocks.core.GlyphCompanion
import org.beatonma.gclocks.core.GlyphRole
import org.beatonma.gclocks.core.GlyphState
import org.beatonma.gclocks.core.RenderGlyph
import org.beatonma.gclocks.core.geometry.NativeSize
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.util.getCurrentTimeMillis


open class TestGlyph(
    role: GlyphRole,
    scale: Float = 1f,
    val separatorWidth: Float = 0f,
    lock: GlyphState? = null,
    currentTimeMillis: Long = getCurrentTimeMillis()
) : BaseClockGlyph<TestPaints>(role, scale, lock, currentTimeMillis = currentTimeMillis) {
    override val companion: GlyphCompanion = TestGlyph

    companion object : GlyphCompanion {
        override val maxSize: NativeSize = NativeSize(100f, 100f)
    }

    override fun getWidthAtProgress(glyphProgress: Float): Float {
        return when (clockKey) {
            ClockGlyph.Key.Separator -> separatorWidth
            else -> Companion.maxSize.width
        }
    }

    override fun Canvas.drawZeroOne(
        glyphProgress: Float,
        paints: TestPaints,
        renderGlyph: RenderGlyph?,
    ) {
    }

    override fun Canvas.drawOneTwo(
        glyphProgress: Float,
        paints: TestPaints,
        renderGlyph: RenderGlyph?,
    ) {
    }

    override fun Canvas.drawTwoThree(
        glyphProgress: Float,
        paints: TestPaints,
        renderGlyph: RenderGlyph?,
    ) {
    }

    override fun Canvas.drawThreeFour(
        glyphProgress: Float,
        paints: TestPaints,
        renderGlyph: RenderGlyph?,
    ) {
    }

    override fun Canvas.drawFourFive(
        glyphProgress: Float,
        paints: TestPaints,
        renderGlyph: RenderGlyph?,
    ) {
    }

    override fun Canvas.drawFiveSix(
        glyphProgress: Float,
        paints: TestPaints,
        renderGlyph: RenderGlyph?,
    ) {
    }

    override fun Canvas.drawSixSeven(
        glyphProgress: Float,
        paints: TestPaints,
        renderGlyph: RenderGlyph?,
    ) {
    }

    override fun Canvas.drawSevenEight(
        glyphProgress: Float,
        paints: TestPaints,
        renderGlyph: RenderGlyph?,
    ) {
    }

    override fun Canvas.drawEightNine(
        glyphProgress: Float,
        paints: TestPaints,
        renderGlyph: RenderGlyph?,
    ) {
    }

    override fun Canvas.drawNineZero(
        glyphProgress: Float,
        paints: TestPaints,
        renderGlyph: RenderGlyph?,
    ) {
    }

    override fun Canvas.drawOneZero(
        glyphProgress: Float,
        paints: TestPaints,
        renderGlyph: RenderGlyph?,
    ) {
    }

    override fun Canvas.drawTwoZero(
        glyphProgress: Float,
        paints: TestPaints,
        renderGlyph: RenderGlyph?,
    ) {
    }

    override fun Canvas.drawTwoOne(
        glyphProgress: Float,
        paints: TestPaints,
        renderGlyph: RenderGlyph?,
    ) {
    }

    override fun Canvas.drawThreeZero(
        glyphProgress: Float,
        paints: TestPaints,
        renderGlyph: RenderGlyph?,
    ) {
    }

    override fun Canvas.drawFiveZero(
        glyphProgress: Float,
        paints: TestPaints,
        renderGlyph: RenderGlyph?,
    ) {
    }

    override fun Canvas.drawOneEmpty(
        glyphProgress: Float,
        paints: TestPaints,
        renderGlyph: RenderGlyph?,
    ) {
    }

    override fun Canvas.drawTwoEmpty(
        glyphProgress: Float,
        paints: TestPaints,
        renderGlyph: RenderGlyph?,
    ) {
    }

    override fun Canvas.drawEmptyOne(
        glyphProgress: Float,
        paints: TestPaints,
        renderGlyph: RenderGlyph?,
    ) {
    }

    override fun Canvas.drawSeparator(
        glyphProgress: Float,
        paints: TestPaints,
        renderGlyph: RenderGlyph?,
    ) {
    }

    override fun Canvas.drawSpace(
        glyphProgress: Float,
        paints: TestPaints,
        renderGlyph: RenderGlyph?,
    ) {
    }

    override fun Canvas.drawHash(
        glyphProgress: Float,
        paints: TestPaints,
        renderGlyph: RenderGlyph?,
    ) {
    }
}
