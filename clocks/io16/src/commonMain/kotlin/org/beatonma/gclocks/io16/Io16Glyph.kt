package org.beatonma.gclocks.io16

import org.beatonma.gclocks.core.BaseClockGlyph
import org.beatonma.gclocks.core.ClockGlyph
import org.beatonma.gclocks.core.GlyphCompanion
import org.beatonma.gclocks.core.GlyphRole
import org.beatonma.gclocks.core.GlyphState
import org.beatonma.gclocks.core.RenderGlyph
import org.beatonma.gclocks.core.geometry.NativeSize
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.types.ProgressFloat
import org.beatonma.gclocks.core.util.decelerate2
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
private val WidthSeparator = Io16GlyphPath.Separator.canonical.width


private fun ease(t: Float) = decelerate2(overshoot(anticipate(t)))


class Io16Glyph(
    role: GlyphRole,
    scale: Float = 1f,
    lock: GlyphState? = null,
    val animationOffset: ProgressFloat,
) : BaseClockGlyph<Io16Paints>(role, scale, lock) {
    companion object : GlyphCompanion {
        override val maxSize = NativeSize(
            x = Io16GlyphPath.Zero.canonical.width,
            y = Io16GlyphPath.Zero.canonical.height,
        )
    }

    override val companion: GlyphCompanion = Io16Glyph

    override fun draw(
        canvas: Canvas,
        glyphProgress: Float,
        paints: Io16Paints,
        renderGlyph: RenderGlyph?,
    ) {
        canvas.beginPath()
        super.draw(canvas, ease(glyphProgress), paints, renderGlyph)
        renderGlyph?.invoke()
    }

    override fun Canvas.drawZeroOne(
        glyphProgress: Float,
        paints: Io16Paints,
        renderGlyph: RenderGlyph?,
    ) {
        Io16GlyphPath.ZeroOne.plot(this, glyphProgress)
    }

    override fun Canvas.drawOneTwo(
        glyphProgress: Float,
        paints: Io16Paints,
        renderGlyph: RenderGlyph?,
    ) {
        Io16GlyphPath.OneTwo.plot(this, glyphProgress)
    }

    override fun Canvas.drawTwoThree(
        glyphProgress: Float,
        paints: Io16Paints,
        renderGlyph: RenderGlyph?,
    ) {
        Io16GlyphPath.TwoThree.plot(this, glyphProgress)
    }

    override fun Canvas.drawThreeFour(
        glyphProgress: Float,
        paints: Io16Paints,
        renderGlyph: RenderGlyph?,
    ) {
        Io16GlyphPath.ThreeFour.plot(this, glyphProgress)
    }

    override fun Canvas.drawFourFive(
        glyphProgress: Float,
        paints: Io16Paints,
        renderGlyph: RenderGlyph?,
    ) {
        Io16GlyphPath.FourFive.plot(this, glyphProgress)
    }

    override fun Canvas.drawFiveSix(
        glyphProgress: Float,
        paints: Io16Paints,
        renderGlyph: RenderGlyph?,
    ) {
        Io16GlyphPath.FiveSix.plot(this, glyphProgress)
    }

    override fun Canvas.drawSixSeven(
        glyphProgress: Float,
        paints: Io16Paints,
        renderGlyph: RenderGlyph?,
    ) {
        Io16GlyphPath.SixSeven.plot(this, glyphProgress)
    }

    override fun Canvas.drawSevenEight(
        glyphProgress: Float,
        paints: Io16Paints,
        renderGlyph: RenderGlyph?,
    ) {
        Io16GlyphPath.SevenEight.plot(this, glyphProgress)
    }

    override fun Canvas.drawEightNine(
        glyphProgress: Float,
        paints: Io16Paints,
        renderGlyph: RenderGlyph?,
    ) {
        Io16GlyphPath.EightNine.plot(this, glyphProgress)
    }

    override fun Canvas.drawNineZero(
        glyphProgress: Float,
        paints: Io16Paints,
        renderGlyph: RenderGlyph?,
    ) {
        Io16GlyphPath.NineZero.plot(this, glyphProgress)
    }

    override fun Canvas.drawOneZero(
        glyphProgress: Float,
        paints: Io16Paints,
        renderGlyph: RenderGlyph?,
    ) {
        Io16GlyphPath.OneZero.plot(this, glyphProgress)
    }

    override fun Canvas.drawTwoZero(
        glyphProgress: Float,
        paints: Io16Paints,
        renderGlyph: RenderGlyph?,
    ) {
        Io16GlyphPath.TwoZero.plot(this, glyphProgress)
    }

    override fun Canvas.drawThreeZero(
        glyphProgress: Float,
        paints: Io16Paints,
        renderGlyph: RenderGlyph?,
    ) {
        Io16GlyphPath.ThreeZero.plot(this, glyphProgress)
    }

    override fun Canvas.drawFiveZero(
        glyphProgress: Float,
        paints: Io16Paints,
        renderGlyph: RenderGlyph?,
    ) {
        Io16GlyphPath.FiveZero.plot(this, glyphProgress)
    }

    override fun Canvas.drawOneEmpty(
        glyphProgress: Float,
        paints: Io16Paints,
        renderGlyph: RenderGlyph?,
    ) {
        // TODO state = Disappearing
        Io16GlyphPath.One.plot(this, glyphProgress)
    }

    override fun Canvas.drawTwoEmpty(
        glyphProgress: Float,
        paints: Io16Paints,
        renderGlyph: RenderGlyph?,
    ) {
        // TODO state = Disappearing
        drawNotImplemented(glyphProgress, paints)
        Io16GlyphPath.Two.plot(this, glyphProgress)
    }

    override fun Canvas.drawEmptyOne(
        glyphProgress: Float,
        paints: Io16Paints,
        renderGlyph: RenderGlyph?,
    ) {
        // TODO state = Appearing
        drawNotImplemented(glyphProgress, paints)
        Io16GlyphPath.One.plot(this, glyphProgress)
    }

    override fun Canvas.drawSeparator(
        glyphProgress: Float,
        paints: Io16Paints,
        renderGlyph: RenderGlyph?,
    ) {
        Io16GlyphPath.Separator.plot(this, glyphProgress, render = renderGlyph)
    }

    override fun Canvas.drawSpace(
        glyphProgress: Float,
        paints: Io16Paints,
        renderGlyph: RenderGlyph?,
    ) {
        // Page intentionally blank
    }

    override fun Canvas.drawHash(
        glyphProgress: Float,
        paints: Io16Paints,
        renderGlyph: RenderGlyph?,
    ) {
        // TODO
        drawNotImplemented(glyphProgress, paints)
    }

    override fun getWidthAtProgress(glyphProgress: Float): Float {
        val p = ease(glyphProgress)

        return when (clockKey) {
            ClockGlyph.Key.Zero, ClockGlyph.Key.ZeroOne -> interpolate(p, Width0, Width1)
            ClockGlyph.Key.One, ClockGlyph.Key.OneTwo -> interpolate(p, Width1, Width2)
            ClockGlyph.Key.Two, ClockGlyph.Key.TwoThree -> interpolate(p, Width2, Width3)
            ClockGlyph.Key.Three, ClockGlyph.Key.ThreeFour -> interpolate(p, Width3, Width4)
            ClockGlyph.Key.Four, ClockGlyph.Key.FourFive -> interpolate(p, Width4, Width5)
            ClockGlyph.Key.Five, ClockGlyph.Key.FiveSix -> interpolate(p, Width5, Width6)
            ClockGlyph.Key.Six, ClockGlyph.Key.SixSeven -> interpolate(p, Width6, Width7)
            ClockGlyph.Key.Seven, ClockGlyph.Key.SevenEight -> interpolate(p, Width7, Width8)
            ClockGlyph.Key.Eight, ClockGlyph.Key.EightNine -> interpolate(p, Width8, Width9)
            ClockGlyph.Key.Nine, ClockGlyph.Key.NineZero -> interpolate(p, Width9, Width0)
            ClockGlyph.Key.EmptyOne -> interpolate(p, 0f, Width1)
            ClockGlyph.Key.OneEmpty -> interpolate(p, Width1, 0f)
            ClockGlyph.Key.TwoEmpty -> interpolate(p, Width2, 0f)
            ClockGlyph.Key.OneZero -> interpolate(p, Width1, Width0)
            ClockGlyph.Key.TwoZero -> interpolate(p, Width2, Width0)
            ClockGlyph.Key.ThreeZero -> interpolate(p, Width3, Width0)
            ClockGlyph.Key.FiveZero -> interpolate(p, Width5, Width0)
            ClockGlyph.Key.Separator -> when (role) {
                GlyphRole.SeparatorMinutesSeconds -> 0f
                else -> WidthSeparator
            }

            ClockGlyph.Key.HashTag, ClockGlyph.Key.Empty -> 0f
        }
    }
}
