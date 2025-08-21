package org.beatonma.gclocks.core.fixtures

import org.beatonma.gclocks.core.BaseClockGlyph
import org.beatonma.gclocks.core.GlyphCompanion
import org.beatonma.gclocks.core.GlyphRole
import org.beatonma.gclocks.core.geometry.NativeSize
import org.beatonma.gclocks.core.graphics.GenericCanvas
import org.beatonma.gclocks.core.graphics.Paints


class TestGlyph(role: GlyphRole, scale: Float = 1f, val separatorWidth: Float) :
    BaseClockGlyph(role, scale) {
    override val companion: GlyphCompanion = TestGlyph

    companion object : GlyphCompanion {
        override val maxSize: NativeSize = NativeSize(100f, 100f)
        override val aspectRatio: Float = maxSize.aspectRatio()
    }

    override fun getWidthAtProgress(glyphProgress: Float): Float {
        return when (key) {
            ":" -> separatorWidth
            else -> Companion.maxSize.width
        }
    }

    override fun GenericCanvas.drawZeroOne(
        glyphProgress: Float,
        paints: Paints,
    ) {
    }

    override fun GenericCanvas.drawOneTwo(
        glyphProgress: Float,
        paints: Paints,
    ) {
    }

    override fun GenericCanvas.drawTwoThree(
        glyphProgress: Float,
        paints: Paints,
    ) {
    }

    override fun GenericCanvas.drawThreeFour(
        glyphProgress: Float,
        paints: Paints,
    ) {
    }

    override fun GenericCanvas.drawFourFive(
        glyphProgress: Float,
        paints: Paints,
    ) {
    }

    override fun GenericCanvas.drawFiveSix(
        glyphProgress: Float,
        paints: Paints,
    ) {
    }

    override fun GenericCanvas.drawSixSeven(
        glyphProgress: Float,
        paints: Paints,
    ) {
    }

    override fun GenericCanvas.drawSevenEight(
        glyphProgress: Float,
        paints: Paints,
    ) {
    }

    override fun GenericCanvas.drawEightNine(
        glyphProgress: Float,
        paints: Paints,
    ) {
    }

    override fun GenericCanvas.drawNineZero(
        glyphProgress: Float,
        paints: Paints,
    ) {
    }

    override fun GenericCanvas.drawOneZero(
        glyphProgress: Float,
        paints: Paints,
    ) {
    }

    override fun GenericCanvas.drawTwoZero(
        glyphProgress: Float,
        paints: Paints,
    ) {
    }

    override fun GenericCanvas.drawThreeZero(
        glyphProgress: Float,
        paints: Paints,
    ) {
    }

    override fun GenericCanvas.drawFiveZero(
        glyphProgress: Float,
        paints: Paints,
    ) {
    }

    override fun GenericCanvas.drawOneEmpty(
        glyphProgress: Float,
        paints: Paints,
    ) {
    }

    override fun GenericCanvas.drawTwoEmpty(
        glyphProgress: Float,
        paints: Paints,
    ) {
    }

    override fun GenericCanvas.drawEmptyOne(
        glyphProgress: Float,
        paints: Paints,
    ) {
    }

    override fun GenericCanvas.drawSeparator(
        glyphProgress: Float,
        paints: Paints,
    ) {
    }

    override fun GenericCanvas.drawSpace(
        glyphProgress: Float,
        paints: Paints,
    ) {
    }

    override fun GenericCanvas.drawHash(
        glyphProgress: Float,
        paints: Paints,
    ) {
    }
}