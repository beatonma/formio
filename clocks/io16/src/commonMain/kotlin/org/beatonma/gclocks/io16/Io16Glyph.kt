package org.beatonma.gclocks.io16

import org.beatonma.gclocks.core.BaseClockGlyph
import org.beatonma.gclocks.core.GlyphCompanion
import org.beatonma.gclocks.core.GlyphRole
import org.beatonma.gclocks.core.geometry.FloatSize
import org.beatonma.gclocks.core.graphics.GenericCanvas
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.graphics.Path
import org.beatonma.gclocks.core.util.interpolate

private val Width0 = Io16GlyphPath.Zero.canonical.width
private val Width1 = Io16GlyphPath.One.canonical.width
private val Width2 = Io16GlyphPath.Two.canonical.width
private val Width3 = Io16GlyphPath.Three.canonical.width
private val Width4 = Io16GlyphPath.Four.canonical.width
private val Width5 = Io16GlyphPath.Five.canonical.width
private val Width6 = Io16GlyphPath.Six.canonical.width
private val Width7 = Io16GlyphPath.Seven.canonical.width
private val Width8 = Io16GlyphPath.Eight.canonical.width
private val Width9 = Io16GlyphPath.Nine.canonical.width
private const val WidthSeparator = 16f


private fun ease(t: Float) = accelerateDecelerate(overshoot(anticipate(t)))


class Io16Glyph(
    role: GlyphRole,
    scale: Float = 1f,
) : BaseClockGlyph(role, scale) {
    companion object : GlyphCompanion {
        override val maxSize = FloatSize(
            x = 118f,
            y = 118f,
        )
        override val aspectRatio: Float = maxSize.aspectRatio()
    }

    override val companion: GlyphCompanion = Io16Glyph

    override fun draw(
        canvas: GenericCanvas,
        glyphProgress: Float,
        paints: Paints,
    ) {
        canvas.beginPath()
        super.draw(canvas, ease(glyphProgress), paints)
    }

    override fun GenericCanvas.drawZeroOne(
        glyphProgress: Float,
        paints: Paints,
    ) {
//
        Io16GlyphPath.ZeroOne.plot(this, glyphProgress)
    }

    override fun GenericCanvas.drawOneTwo(
        glyphProgress: Float,
        paints: Paints,
    ) {
        Io16GlyphPath.OneTwo.plot(this, glyphProgress)
    }

    override fun GenericCanvas.drawTwoThree(
        glyphProgress: Float,
        paints: Paints,
    ) {
        Io16GlyphPath.TwoThree.plot(this, glyphProgress)
    }

    override fun GenericCanvas.drawThreeFour(
        glyphProgress: Float,
        paints: Paints,
    ) {
        Io16GlyphPath.ThreeFour.plot(this, glyphProgress)
    }

    override fun GenericCanvas.drawFourFive(
        glyphProgress: Float,
        paints: Paints,
    ) {
        Io16GlyphPath.FourFive.plot(this, glyphProgress)
    }

    override fun GenericCanvas.drawFiveSix(
        glyphProgress: Float,
        paints: Paints,
    ) {
        Io16GlyphPath.FiveSix.plot(this, glyphProgress)
    }

    override fun GenericCanvas.drawSixSeven(
        glyphProgress: Float,
        paints: Paints,
    ) {
        Io16GlyphPath.SixSeven.plot(this, glyphProgress)
    }

    override fun GenericCanvas.drawSevenEight(
        glyphProgress: Float,
        paints: Paints,
    ) {
        Io16GlyphPath.SevenEight.plot(this, glyphProgress)
    }

    override fun GenericCanvas.drawEightNine(
        glyphProgress: Float,
        paints: Paints,
    ) {
        Io16GlyphPath.EightNine.plot(this, glyphProgress)
    }

    override fun GenericCanvas.drawNineZero(
        glyphProgress: Float,
        paints: Paints,
    ) {
        Io16GlyphPath.NineZero.plot(this, glyphProgress)
    }

    override fun GenericCanvas.drawOneZero(
        glyphProgress: Float,
        paints: Paints,
    ) {
        Io16GlyphPath.OneZero.plot(this, glyphProgress)
    }

    override fun GenericCanvas.drawTwoZero(
        glyphProgress: Float,
        paints: Paints,
    ) {
        Io16GlyphPath.TwoZero.plot(this, glyphProgress)
    }

    override fun GenericCanvas.drawThreeZero(
        glyphProgress: Float,
        paints: Paints,
    ) {
        Io16GlyphPath.ThreeZero.plot(this, glyphProgress)
    }

    override fun GenericCanvas.drawFiveZero(
        glyphProgress: Float,
        paints: Paints,
    ) {
        Io16GlyphPath.FiveZero.plot(this, glyphProgress)
    }

    override fun GenericCanvas.drawOneEmpty(
        glyphProgress: Float,
        paints: Paints,
    ) {
        // TODO state = Disappearing
        Io16GlyphPath.One.plot(this, glyphProgress)
    }

    override fun GenericCanvas.drawTwoEmpty(
        glyphProgress: Float,
        paints: Paints,
    ) {
        // TODO state = Disappearing
        drawNotImplemented(glyphProgress, paints)
        Io16GlyphPath.Two.plot(this, glyphProgress)
    }

    override fun GenericCanvas.drawEmptyOne(
        glyphProgress: Float,
        paints: Paints,
    ) {
        // TODO state = Appearing
        drawNotImplemented(glyphProgress, paints)
        Io16GlyphPath.One.plot(this, glyphProgress)
    }

    override fun GenericCanvas.drawSeparator(
        glyphProgress: Float,
        paints: Paints,
    ) {
        circle(8f, 48f, 8f, Path.Direction.Clockwise)
        circle(8f, 96f, 8f, Path.Direction.Clockwise)
    }

    override fun GenericCanvas.drawSpace(
        glyphProgress: Float,
        paints: Paints,
    ) {
        // Page intentionally blank
    }

    override fun GenericCanvas.drawHash(
        glyphProgress: Float,
        paints: Paints,
    ) {
        // TODO
        drawNotImplemented(glyphProgress, paints)
    }

    override fun getWidthAtProgress(glyphProgress: Float): Float {
        val p = ease(glyphProgress)
        return when (key) {
            "0", "0_1" -> interpolate(p, Width0, Width1)
            "1", "1_2" -> interpolate(p, Width1, Width2)
            "2", "2_3" -> interpolate(p, Width2, Width3)
            "3", "3_4" -> interpolate(p, Width3, Width4)
            "4", "4_5" -> interpolate(p, Width4, Width5)
            "5", "5_6" -> interpolate(p, Width5, Width6)
            "6", "6_7" -> interpolate(p, Width6, Width7)
            "7", "7_8" -> interpolate(p, Width7, Width8)
            "8", "8_9" -> interpolate(p, Width8, Width9)
            "9", "9_0" -> interpolate(p, Width9, Width0)
            " _1" -> interpolate(p, 0f, Width1)
            "1_ " -> interpolate(p, Width1, 0f)
            "2_ " -> interpolate(p, Width2, 0f)
            "2_1" -> interpolate(p, Width2, Width1)
            "2_0" -> interpolate(p, Width2, Width0)
            "3_0" -> interpolate(p, Width3, Width0)
            "5_0" -> interpolate(p, Width5, Width0)
            ":" -> when (role) {
                GlyphRole.SeparatorMinutesSeconds -> 0f
                else -> WidthSeparator
            }

            "#" -> 49f
            " ", "_" -> 0f

            else -> throw IllegalArgumentException("getWidthAtProgress unhandled key $key")
        }
    }
}
