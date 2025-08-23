package org.beatonma.gclocks.core.fixtures

import org.beatonma.gclocks.core.BaseClockGlyph
import org.beatonma.gclocks.core.GlyphCompanion
import org.beatonma.gclocks.core.GlyphRole
import org.beatonma.gclocks.core.geometry.NativeSize
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Paints


class TestGlyph(role: GlyphRole, scale: Float = 1f, val separatorWidth: Float) :
    BaseClockGlyph(role, scale) {
    override val companion: GlyphCompanion = TestGlyph

    companion object : GlyphCompanion {
        override val maxSize: NativeSize = NativeSize(100f, 100f)
    }

    override fun getWidthAtProgress(glyphProgress: Float): Float {
        return when (key) {
            ":" -> separatorWidth
            else -> Companion.maxSize.width
        }
    }

    override fun Canvas.drawZeroOne(
        glyphProgress: Float,
        paints: Paints,
    ) {
    }

    override fun Canvas.drawOneTwo(
        glyphProgress: Float,
        paints: Paints,
    ) {
    }

    override fun Canvas.drawTwoThree(
        glyphProgress: Float,
        paints: Paints,
    ) {
    }

    override fun Canvas.drawThreeFour(
        glyphProgress: Float,
        paints: Paints,
    ) {
    }

    override fun Canvas.drawFourFive(
        glyphProgress: Float,
        paints: Paints,
    ) {
    }

    override fun Canvas.drawFiveSix(
        glyphProgress: Float,
        paints: Paints,
    ) {
    }

    override fun Canvas.drawSixSeven(
        glyphProgress: Float,
        paints: Paints,
    ) {
    }

    override fun Canvas.drawSevenEight(
        glyphProgress: Float,
        paints: Paints,
    ) {
    }

    override fun Canvas.drawEightNine(
        glyphProgress: Float,
        paints: Paints,
    ) {
    }

    override fun Canvas.drawNineZero(
        glyphProgress: Float,
        paints: Paints,
    ) {
    }

    override fun Canvas.drawOneZero(
        glyphProgress: Float,
        paints: Paints,
    ) {
    }

    override fun Canvas.drawTwoZero(
        glyphProgress: Float,
        paints: Paints,
    ) {
    }

    override fun Canvas.drawThreeZero(
        glyphProgress: Float,
        paints: Paints,
    ) {
    }

    override fun Canvas.drawFiveZero(
        glyphProgress: Float,
        paints: Paints,
    ) {
    }

    override fun Canvas.drawOneEmpty(
        glyphProgress: Float,
        paints: Paints,
    ) {
    }

    override fun Canvas.drawTwoEmpty(
        glyphProgress: Float,
        paints: Paints,
    ) {
    }

    override fun Canvas.drawEmptyOne(
        glyphProgress: Float,
        paints: Paints,
    ) {
    }

    override fun Canvas.drawSeparator(
        glyphProgress: Float,
        paints: Paints,
    ) {
    }

    override fun Canvas.drawSpace(
        glyphProgress: Float,
        paints: Paints,
    ) {
    }

    override fun Canvas.drawHash(
        glyphProgress: Float,
        paints: Paints,
    ) {
    }
}