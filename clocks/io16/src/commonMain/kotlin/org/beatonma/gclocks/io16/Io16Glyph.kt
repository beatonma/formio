package org.beatonma.gclocks.io16

import org.beatonma.gclocks.core.geometry.NativeSize
import org.beatonma.gclocks.core.glyph.ClockGlyph
import org.beatonma.gclocks.core.glyph.ClockGlyphDesynchronizedVisibility
import org.beatonma.gclocks.core.glyph.GlyphCompanion
import org.beatonma.gclocks.core.glyph.GlyphRole
import org.beatonma.gclocks.core.glyph.GlyphState
import org.beatonma.gclocks.core.glyph.RenderGlyph
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Paints
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
) : ClockGlyphDesynchronizedVisibility(role, scale, lock) {
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
        paints: Paints,
        renderGlyph: RenderGlyph?,
    ) {
        canvas.beginPath()

        // Plot path
        super.draw(canvas, ease(glyphProgress), paints, renderGlyph)

        // Render path to canvas
        renderGlyph?.invoke()
    }

    override fun Canvas.drawZeroOne(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?,
    ) {
        Io16GlyphPath.ZeroOne.plot(this, glyphProgress)
    }

    override fun Canvas.drawOneTwo(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?,
    ) {
        Io16GlyphPath.OneTwo.plot(this, glyphProgress)
    }

    override fun Canvas.drawTwoThree(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?,
    ) {
        Io16GlyphPath.TwoThree.plot(this, glyphProgress)
    }

    override fun Canvas.drawThreeFour(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?,
    ) {
        Io16GlyphPath.ThreeFour.plot(this, glyphProgress)
    }

    override fun Canvas.drawFourFive(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?,
    ) {
        Io16GlyphPath.FourFive.plot(this, glyphProgress)
    }

    override fun Canvas.drawFiveSix(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?,
    ) {
        Io16GlyphPath.FiveSix.plot(this, glyphProgress)
    }

    override fun Canvas.drawSixSeven(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?,
    ) {
        Io16GlyphPath.SixSeven.plot(this, glyphProgress)
    }

    override fun Canvas.drawSevenEight(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?,
    ) {
        Io16GlyphPath.SevenEight.plot(this, glyphProgress)
    }

    override fun Canvas.drawEightNine(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?,
    ) {
        Io16GlyphPath.EightNine.plot(this, glyphProgress)
    }

    override fun Canvas.drawNineZero(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?,
    ) {
        Io16GlyphPath.NineZero.plot(this, glyphProgress)
    }

    override fun Canvas.drawOneZero(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?,
    ) {
        Io16GlyphPath.OneZero.plot(this, glyphProgress)
    }

    override fun Canvas.drawTwoZero(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?,
    ) {
        Io16GlyphPath.TwoZero.plot(this, glyphProgress)
    }

    override fun Canvas.drawThreeZero(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?,
    ) {
        Io16GlyphPath.ThreeZero.plot(this, glyphProgress)
    }

    override fun Canvas.drawFiveZero(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?,
    ) {
        Io16GlyphPath.FiveZero.plot(this, glyphProgress)
    }

    override fun Canvas.drawTwoOne(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?,
    ) {
        Io16GlyphPath.OneTwo.plot(this, 1f - glyphProgress)
    }

    override fun Canvas.drawZeroEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        Io16GlyphPath.Zero.plot(this, glyphProgress)
    }

    override fun Canvas.drawOneEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?,
    ) {
        Io16GlyphPath.One.plot(this, glyphProgress)
    }

    override fun Canvas.drawTwoEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?,
    ) {
        Io16GlyphPath.Two.plot(this, glyphProgress)
    }

    override fun Canvas.drawThreeEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        Io16GlyphPath.Three.plot(this, glyphProgress)
    }

    override fun Canvas.drawFourEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        Io16GlyphPath.Four.plot(this, glyphProgress)
    }

    override fun Canvas.drawFiveEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        Io16GlyphPath.Five.plot(this, glyphProgress)
    }

    override fun Canvas.drawSixEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        Io16GlyphPath.Six.plot(this, glyphProgress)
    }

    override fun Canvas.drawSevenEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        Io16GlyphPath.Seven.plot(this, glyphProgress)
    }

    override fun Canvas.drawEightEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        Io16GlyphPath.Eight.plot(this, glyphProgress)
    }

    override fun Canvas.drawNineEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        Io16GlyphPath.Nine.plot(this, glyphProgress)
    }

    override fun Canvas.drawEmptyZero(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        Io16GlyphPath.Zero.plot(this, glyphProgress)
    }

    override fun Canvas.drawEmptyOne(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?,
    ) {
        Io16GlyphPath.One.plot(this, glyphProgress)
    }

    override fun Canvas.drawEmptyTwo(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        Io16GlyphPath.Two.plot(this, glyphProgress)
    }

    override fun Canvas.drawEmptyThree(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        Io16GlyphPath.Three.plot(this, glyphProgress)
    }

    override fun Canvas.drawEmptyFour(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        Io16GlyphPath.Four.plot(this, glyphProgress)
    }

    override fun Canvas.drawEmptyFive(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        Io16GlyphPath.Five.plot(this, glyphProgress)
    }

    override fun Canvas.drawEmptySix(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        Io16GlyphPath.Six.plot(this, glyphProgress)
    }

    override fun Canvas.drawEmptySeven(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        Io16GlyphPath.Seven.plot(this, glyphProgress)
    }

    override fun Canvas.drawEmptyEight(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        Io16GlyphPath.Eight.plot(this, glyphProgress)
    }

    override fun Canvas.drawEmptyNine(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        Io16GlyphPath.Nine.plot(this, glyphProgress)
    }

    override fun Canvas.drawSeparator(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?,
    ) {
        Io16GlyphPath.Separator.plot(this, glyphProgress, render = renderGlyph)
    }

    override fun Canvas.drawEmptySeparator(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        // Page intentionally blank
    }

    override fun Canvas.drawSeparatorEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        // Page intentionally blank
    }

    override fun Canvas.drawSpace(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?,
    ) {
        // Page intentionally blank
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

            ClockGlyph.Key.EmptyZero -> Width0
            ClockGlyph.Key.EmptyOne -> Width1
            ClockGlyph.Key.EmptyTwo -> Width2
            ClockGlyph.Key.EmptyThree -> Width3
            ClockGlyph.Key.EmptyFour -> Width4
            ClockGlyph.Key.EmptyFive -> Width5
            ClockGlyph.Key.EmptySix -> Width6
            ClockGlyph.Key.EmptySeven -> Width7
            ClockGlyph.Key.EmptyEight -> Width8
            ClockGlyph.Key.EmptyNine -> Width9

            ClockGlyph.Key.ZeroEmpty -> Width0
            ClockGlyph.Key.OneEmpty -> Width1
            ClockGlyph.Key.TwoEmpty -> Width2
            ClockGlyph.Key.ThreeEmpty -> Width3
            ClockGlyph.Key.FourEmpty -> Width4
            ClockGlyph.Key.FiveEmpty -> Width5
            ClockGlyph.Key.SixEmpty -> Width6
            ClockGlyph.Key.SevenEmpty -> Width7
            ClockGlyph.Key.EightEmpty -> Width8
            ClockGlyph.Key.NineEmpty -> Width9

            ClockGlyph.Key.TwoOne -> interpolate(p, Width2, Width1)
            ClockGlyph.Key.OneZero -> interpolate(p, Width1, Width0)
            ClockGlyph.Key.TwoZero -> interpolate(p, Width2, Width0)
            ClockGlyph.Key.ThreeZero -> interpolate(p, Width3, Width0)
            ClockGlyph.Key.FiveZero -> interpolate(p, Width5, Width0)
            ClockGlyph.Key.Separator,
            ClockGlyph.Key.SeparatorEmpty,
            ClockGlyph.Key.EmptySeparator -> when (role) {
                GlyphRole.SeparatorMinutesSeconds -> 0f
                else -> WidthSeparator
            }

            ClockGlyph.Key.Empty -> 0f
        }
    }
}
