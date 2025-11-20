package org.beatonma.gclocks.form

import org.beatonma.gclocks.core.geometry.NativeSize
import org.beatonma.gclocks.core.glyph.ClockGlyph
import org.beatonma.gclocks.core.glyph.ClockGlyphSynchronizedVisibility
import org.beatonma.gclocks.core.glyph.GlyphCompanion
import org.beatonma.gclocks.core.glyph.GlyphRole
import org.beatonma.gclocks.core.glyph.GlyphState
import org.beatonma.gclocks.core.glyph.RenderGlyph
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.util.decelerate5
import org.beatonma.gclocks.core.util.lerp
import org.beatonma.gclocks.core.util.progressIn
import org.beatonma.gclocks.form.characters.canonical.EightWidth
import org.beatonma.gclocks.form.characters.canonical.FiveWidth
import org.beatonma.gclocks.form.characters.canonical.FourWidth
import org.beatonma.gclocks.form.characters.canonical.NineWidth
import org.beatonma.gclocks.form.characters.canonical.OneWidth
import org.beatonma.gclocks.form.characters.canonical.Separator
import org.beatonma.gclocks.form.characters.canonical.SevenWidth
import org.beatonma.gclocks.form.characters.canonical.SixWidth
import org.beatonma.gclocks.form.characters.canonical.ThreeWidth
import org.beatonma.gclocks.form.characters.canonical.TwoWidth
import org.beatonma.gclocks.form.characters.canonical.ZeroWidth
import org.beatonma.gclocks.form.characters.transitional.EightEmpty
import org.beatonma.gclocks.form.characters.transitional.EightNine
import org.beatonma.gclocks.form.characters.transitional.EmptyEight
import org.beatonma.gclocks.form.characters.transitional.EmptyFive
import org.beatonma.gclocks.form.characters.transitional.EmptyFour
import org.beatonma.gclocks.form.characters.transitional.EmptyNine
import org.beatonma.gclocks.form.characters.transitional.EmptyOne
import org.beatonma.gclocks.form.characters.transitional.EmptySeparator
import org.beatonma.gclocks.form.characters.transitional.EmptySeven
import org.beatonma.gclocks.form.characters.transitional.EmptySix
import org.beatonma.gclocks.form.characters.transitional.EmptyThree
import org.beatonma.gclocks.form.characters.transitional.EmptyTwo
import org.beatonma.gclocks.form.characters.transitional.EmptyZero
import org.beatonma.gclocks.form.characters.transitional.FiveEmpty
import org.beatonma.gclocks.form.characters.transitional.FiveSix
import org.beatonma.gclocks.form.characters.transitional.FiveZero
import org.beatonma.gclocks.form.characters.transitional.FourEmpty
import org.beatonma.gclocks.form.characters.transitional.FourFive
import org.beatonma.gclocks.form.characters.transitional.NineEmpty
import org.beatonma.gclocks.form.characters.transitional.NineZero
import org.beatonma.gclocks.form.characters.transitional.OneEmpty
import org.beatonma.gclocks.form.characters.transitional.OneTwo
import org.beatonma.gclocks.form.characters.transitional.OneZero
import org.beatonma.gclocks.form.characters.transitional.SeparatorEmpty
import org.beatonma.gclocks.form.characters.transitional.SevenEight
import org.beatonma.gclocks.form.characters.transitional.SevenEmpty
import org.beatonma.gclocks.form.characters.transitional.SixEmpty
import org.beatonma.gclocks.form.characters.transitional.SixSeven
import org.beatonma.gclocks.form.characters.transitional.ThreeEmpty
import org.beatonma.gclocks.form.characters.transitional.ThreeFour
import org.beatonma.gclocks.form.characters.transitional.ThreeZero
import org.beatonma.gclocks.form.characters.transitional.TwoEmpty
import org.beatonma.gclocks.form.characters.transitional.TwoOne
import org.beatonma.gclocks.form.characters.transitional.TwoThree
import org.beatonma.gclocks.form.characters.transitional.TwoZero
import org.beatonma.gclocks.form.characters.transitional.ZeroEmpty
import org.beatonma.gclocks.form.characters.transitional.ZeroOne


private fun ease(f: Float) = decelerate5(f)
private fun easeProgress(value: Float, min: Float, max: Float) = ease(value.progressIn(min, max))

class FormGlyph(
    role: GlyphRole,
    scale: Float = 1f,
    lock: GlyphState? = null
) : ClockGlyphSynchronizedVisibility<FormPaints>(role, scale, lock) {
    companion object : GlyphCompanion {
        override val maxSize = NativeSize(
            x = 192f,
            y = 144f,
        )
    }

    override val companion: GlyphCompanion = FormGlyph
    override fun Canvas.drawZeroOne(
        glyphProgress: Float,
        paints: FormPaints,
        renderGlyph: RenderGlyph?
    ) {
        ZeroOne.plot(this, glyphProgress, { drawPath(paints[it]) })
    }

    override fun Canvas.drawOneTwo(
        glyphProgress: Float,
        paints: FormPaints,
        renderGlyph: RenderGlyph?
    ) {
        OneTwo.plot(this, glyphProgress) { drawPath(paints[it]) }
    }

    override fun Canvas.drawTwoThree(
        glyphProgress: Float,
        paints: FormPaints,
        renderGlyph: RenderGlyph?
    ) {
        TwoThree.plot(this, glyphProgress) { drawPath(paints[it]) }
    }

    override fun Canvas.drawThreeFour(
        glyphProgress: Float,
        paints: FormPaints,
        renderGlyph: RenderGlyph?
    ) {
        ThreeFour.plot(this, glyphProgress) { drawPath(paints[it]) }
    }

    override fun Canvas.drawFourFive(
        glyphProgress: Float,
        paints: FormPaints,
        renderGlyph: RenderGlyph?
    ) {
        FourFive.plot(this, glyphProgress) { drawPath(paints[it]) }
    }

    override fun Canvas.drawFiveSix(
        glyphProgress: Float,
        paints: FormPaints,
        renderGlyph: RenderGlyph?
    ) {
        FiveSix.plot(this, glyphProgress) { drawPath(paints[it]) }
    }

    override fun Canvas.drawSixSeven(
        glyphProgress: Float,
        paints: FormPaints,
        renderGlyph: RenderGlyph?
    ) {
        SixSeven.plot(this, glyphProgress) { drawPath(paints[it]) }
    }

    override fun Canvas.drawSevenEight(
        glyphProgress: Float,
        paints: FormPaints,
        renderGlyph: RenderGlyph?
    ) {
        SevenEight.plot(this, glyphProgress) { drawPath(paints[it]) }
    }

    override fun Canvas.drawEightNine(
        glyphProgress: Float,
        paints: FormPaints,
        renderGlyph: RenderGlyph?
    ) {
        EightNine.plot(this, glyphProgress) { drawPath(paints[it]) }
    }

    override fun Canvas.drawNineZero(
        glyphProgress: Float,
        paints: FormPaints,
        renderGlyph: RenderGlyph?
    ) {
        NineZero.plot(this, glyphProgress) { drawPath(paints[it]) }
    }

    override fun Canvas.drawOneZero(
        glyphProgress: Float,
        paints: FormPaints,
        renderGlyph: RenderGlyph?
    ) {
        OneZero.plot(this, glyphProgress) { drawPath(paints[it]) }
    }

    override fun Canvas.drawTwoZero(
        glyphProgress: Float,
        paints: FormPaints,
        renderGlyph: RenderGlyph?
    ) {
        TwoZero.plot(this, glyphProgress) { drawPath(paints[it]) }
    }

    override fun Canvas.drawTwoOne(
        glyphProgress: Float,
        paints: FormPaints,
        renderGlyph: RenderGlyph?
    ) {
        TwoOne.plot(this, glyphProgress) { drawPath(paints[it]) }
    }

    override fun Canvas.drawThreeZero(
        glyphProgress: Float,
        paints: FormPaints,
        renderGlyph: RenderGlyph?
    ) {
        ThreeZero.plot(this, glyphProgress) { drawPath(paints[it]) }
    }

    override fun Canvas.drawFiveZero(
        glyphProgress: Float,
        paints: FormPaints,
        renderGlyph: RenderGlyph?
    ) {
        FiveZero.plot(this, glyphProgress) { drawPath(paints[it]) }
    }

    override fun Canvas.drawZeroEmpty(
        glyphProgress: Float,
        paints: FormPaints,
        renderGlyph: RenderGlyph?
    ) {
        ZeroEmpty.plot(this, glyphProgress) { drawPath(paints[it]) }
    }

    override fun Canvas.drawOneEmpty(
        glyphProgress: Float,
        paints: FormPaints,
        renderGlyph: RenderGlyph?
    ) {
        OneEmpty.plot(this, glyphProgress) { drawPath(paints[it]) }
    }

    override fun Canvas.drawTwoEmpty(
        glyphProgress: Float,
        paints: FormPaints,
        renderGlyph: RenderGlyph?
    ) {
        TwoEmpty.plot(this, glyphProgress) { drawPath(paints[it]) }
    }

    override fun Canvas.drawThreeEmpty(
        glyphProgress: Float,
        paints: FormPaints,
        renderGlyph: RenderGlyph?
    ) {
        ThreeEmpty.plot(this, glyphProgress) { drawPath(paints[it]) }
    }

    override fun Canvas.drawFourEmpty(
        glyphProgress: Float,
        paints: FormPaints,
        renderGlyph: RenderGlyph?
    ) {
        FourEmpty.plot(this, glyphProgress) { drawPath(paints[it]) }
    }

    override fun Canvas.drawFiveEmpty(
        glyphProgress: Float,
        paints: FormPaints,
        renderGlyph: RenderGlyph?
    ) {
        FiveEmpty.plot(this, glyphProgress) { drawPath(paints[it]) }
    }

    override fun Canvas.drawSixEmpty(
        glyphProgress: Float,
        paints: FormPaints,
        renderGlyph: RenderGlyph?
    ) {
        SixEmpty.plot(this, glyphProgress) { drawPath(paints[it]) }
    }

    override fun Canvas.drawSevenEmpty(
        glyphProgress: Float,
        paints: FormPaints,
        renderGlyph: RenderGlyph?
    ) {
        SevenEmpty.plot(this, glyphProgress) { drawPath(paints[it]) }
    }

    override fun Canvas.drawEightEmpty(
        glyphProgress: Float,
        paints: FormPaints,
        renderGlyph: RenderGlyph?
    ) {
        EightEmpty.plot(this, glyphProgress) { drawPath(paints[it]) }
    }

    override fun Canvas.drawNineEmpty(
        glyphProgress: Float,
        paints: FormPaints,
        renderGlyph: RenderGlyph?
    ) {
        NineEmpty.plot(this, glyphProgress) { drawPath(paints[it]) }
    }

    override fun Canvas.drawEmptyZero(
        glyphProgress: Float,
        paints: FormPaints,
        renderGlyph: RenderGlyph?
    ) {
        EmptyZero.plot(this, glyphProgress) { drawPath(paints[it]) }
    }

    override fun Canvas.drawEmptyOne(
        glyphProgress: Float,
        paints: FormPaints,
        renderGlyph: RenderGlyph?
    ) {
        EmptyOne.plot(this, glyphProgress) { drawPath(paints[it]) }
    }

    override fun Canvas.drawEmptyTwo(
        glyphProgress: Float,
        paints: FormPaints,
        renderGlyph: RenderGlyph?
    ) {
        EmptyTwo.plot(this, glyphProgress) { drawPath(paints[it]) }
    }

    override fun Canvas.drawEmptyThree(
        glyphProgress: Float,
        paints: FormPaints,
        renderGlyph: RenderGlyph?
    ) {
        EmptyThree.plot(this, glyphProgress) { drawPath(paints[it]) }
    }

    override fun Canvas.drawEmptyFour(
        glyphProgress: Float,
        paints: FormPaints,
        renderGlyph: RenderGlyph?
    ) {
        EmptyFour.plot(this, glyphProgress) { drawPath(paints[it]) }
    }

    override fun Canvas.drawEmptyFive(
        glyphProgress: Float,
        paints: FormPaints,
        renderGlyph: RenderGlyph?
    ) {
        EmptyFive.plot(this, glyphProgress) { drawPath(paints[it]) }
    }

    override fun Canvas.drawEmptySix(
        glyphProgress: Float,
        paints: FormPaints,
        renderGlyph: RenderGlyph?
    ) {
        EmptySix.plot(this, glyphProgress) { drawPath(paints[it]) }
    }

    override fun Canvas.drawEmptySeven(
        glyphProgress: Float,
        paints: FormPaints,
        renderGlyph: RenderGlyph?
    ) {
        EmptySeven.plot(this, glyphProgress) { drawPath(paints[it]) }
    }

    override fun Canvas.drawEmptyEight(
        glyphProgress: Float,
        paints: FormPaints,
        renderGlyph: RenderGlyph?
    ) {
        EmptyEight.plot(this, glyphProgress) { drawPath(paints[it]) }
    }

    override fun Canvas.drawEmptyNine(
        glyphProgress: Float,
        paints: FormPaints,
        renderGlyph: RenderGlyph?
    ) {
        EmptyNine.plot(this, glyphProgress) { drawPath(paints[it]) }
    }

    override fun Canvas.drawSeparator(
        glyphProgress: Float,
        paints: FormPaints,
        renderGlyph: RenderGlyph?
    ) {
        Separator.plot(this) { drawPath(paints[it]) }
    }

    override fun Canvas.drawEmptySeparator(
        glyphProgress: Float,
        paints: FormPaints,
        renderGlyph: RenderGlyph?
    ) {
        EmptySeparator.plot(this, glyphProgress) { drawPath(paints[it]) }
    }

    override fun Canvas.drawSeparatorEmpty(
        glyphProgress: Float,
        paints: FormPaints,
        renderGlyph: RenderGlyph?
    ) {
        SeparatorEmpty.plot(this, glyphProgress) { drawPath(paints[it]) }
    }

    override fun Canvas.drawSpace(
        glyphProgress: Float,
        paints: FormPaints,
        renderGlyph: RenderGlyph?,
    ) {
        // Page intentionally blank
    }

    override fun getWidthAtProgress(glyphProgress: Float): Float {
        val d1 = easeProgress(glyphProgress, 0f, 0.5f)
        val d2 = easeProgress(glyphProgress, 0.5f, 1f)

        return when (clockKey) {
            ClockGlyph.Key.Zero, ClockGlyph.Key.ZeroOne -> d2.lerp(
                easeProgress(glyphProgress, 0f, 0.4f).lerp(ZeroWidth, 192f),
                OneWidth,
            )

            ClockGlyph.Key.One, ClockGlyph.Key.OneTwo ->
                d1.lerp(OneWidth, TwoWidth)

            ClockGlyph.Key.Two, ClockGlyph.Key.TwoThree ->
                d1.lerp(TwoWidth, ThreeWidth)

            ClockGlyph.Key.Three, ClockGlyph.Key.ThreeFour ->
                d1.lerp(ThreeWidth, FourWidth)

            ClockGlyph.Key.Four, ClockGlyph.Key.FourFive ->
                d1.lerp(FourWidth, FiveWidth)

            ClockGlyph.Key.Five, ClockGlyph.Key.FiveSix ->
                easeProgress(glyphProgress, 0.1f, 1f).lerp(
                    FiveWidth, SixWidth
                )

            ClockGlyph.Key.Six, ClockGlyph.Key.SixSeven -> {
                val baseWidth = SixWidth // 6 and 7 have the same width when static
                val turningPoint = 0.8f
                val maxChange = 31f
                val d = ease(glyphProgress)
                if (d < turningPoint) {
                    baseWidth + d.progressIn(0f, turningPoint).lerp(0f, maxChange)
                } else {
                    baseWidth + d.progressIn(turningPoint, 1f).lerp(maxChange, 0f)
                }
            }

            ClockGlyph.Key.Seven, ClockGlyph.Key.SevenEight -> SevenWidth // Both digits are same width
            ClockGlyph.Key.Eight, ClockGlyph.Key.EightNine -> EightWidth // Both digits are same width
            ClockGlyph.Key.Nine, ClockGlyph.Key.NineZero -> NineWidth // Both digits are same width

            ClockGlyph.Key.OneZero ->
                easeProgress(1f - glyphProgress, 0.5f, 1f).lerp(
                    easeProgress(1f - glyphProgress, 0f, 0.4f).lerp(ZeroWidth, 192f),
                    OneWidth,
                )

            ClockGlyph.Key.TwoZero -> ZeroWidth // Both digits are same width
            ClockGlyph.Key.TwoOne -> easeProgress(1f - glyphProgress, 0f, 0.5f).lerp(
                OneWidth,
                TwoWidth
            )

            ClockGlyph.Key.ThreeZero -> d1.lerp(ThreeWidth, ZeroWidth)
            ClockGlyph.Key.FiveZero -> d1.lerp(FiveWidth, ZeroWidth)

            ClockGlyph.Key.Separator,
            ClockGlyph.Key.SeparatorEmpty,
            ClockGlyph.Key.EmptySeparator -> when (role) {
                GlyphRole.SeparatorHoursMinutes -> 48f
                else -> 0f
            }

            ClockGlyph.Key.ZeroEmpty -> ZeroWidth
            ClockGlyph.Key.OneEmpty -> OneWidth
            ClockGlyph.Key.TwoEmpty -> TwoWidth
            ClockGlyph.Key.ThreeEmpty -> ThreeWidth
            ClockGlyph.Key.FourEmpty -> FourWidth
            ClockGlyph.Key.FiveEmpty -> FiveWidth
            ClockGlyph.Key.SixEmpty -> SixWidth
            ClockGlyph.Key.SevenEmpty -> SevenWidth
            ClockGlyph.Key.EightEmpty -> EightWidth
            ClockGlyph.Key.NineEmpty -> NineWidth

            ClockGlyph.Key.EmptyZero -> ZeroWidth
            ClockGlyph.Key.EmptyOne -> OneWidth
            ClockGlyph.Key.EmptyTwo -> TwoWidth
            ClockGlyph.Key.EmptyThree -> ThreeWidth
            ClockGlyph.Key.EmptyFour -> FourWidth
            ClockGlyph.Key.EmptyFive -> FiveWidth
            ClockGlyph.Key.EmptySix -> SixWidth
            ClockGlyph.Key.EmptySeven -> SevenWidth
            ClockGlyph.Key.EmptyEight -> EightWidth
            ClockGlyph.Key.EmptyNine -> NineWidth

            ClockGlyph.Key.Empty -> 0f
        }
    }
}
