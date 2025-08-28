package org.beatonma.gclocks.io18

import org.beatonma.gclocks.core.BaseClockGlyph
import org.beatonma.gclocks.core.ClockGlyph
import org.beatonma.gclocks.core.GlyphCompanion
import org.beatonma.gclocks.core.GlyphRole
import org.beatonma.gclocks.core.GlyphState
import org.beatonma.gclocks.core.RenderGlyph
import org.beatonma.gclocks.core.geometry.NativeSize
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.Path
import org.beatonma.gclocks.core.util.decelerate2
import org.beatonma.gclocks.core.util.progress
import org.beatonma.gclocks.core.util.interpolate
import org.beatonma.gclocks.io18.animation.AnimatedPath
import org.beatonma.gclocks.io18.animation.Tube
import org.beatonma.gclocks.io18.characters.Eight
import org.beatonma.gclocks.io18.characters.Five
import org.beatonma.gclocks.io18.characters.Four
import org.beatonma.gclocks.io18.characters.GlyphCharacter
import org.beatonma.gclocks.io18.characters.Nine
import org.beatonma.gclocks.io18.characters.One
import org.beatonma.gclocks.io18.characters.Seven
import org.beatonma.gclocks.io18.characters.Six
import org.beatonma.gclocks.io18.characters.Three
import org.beatonma.gclocks.io18.characters.Two
import org.beatonma.gclocks.io18.characters.Zero

class GlyphAnimations(path: Path) {
    internal val animatedPath = AnimatedPath(path)
    internal val tube = Tube(path)
}

class Io18Glyph(
    animations: GlyphAnimations,
    role: GlyphRole,
    scale: Float = 1f,
    lock: GlyphState? = null,
    shuffleColors: Boolean = true,
) : BaseClockGlyph<Io18Paints>(role, scale, lock) {
    companion object : GlyphCompanion {
        override val maxSize: NativeSize =
            NativeSize(GlyphCharacter.DefaultWidth, GlyphCharacter.DefaultHeight)
    }

    override val companion: GlyphCompanion = Companion
    private val paintIndices: IntArray = when (shuffleColors) {
        true -> Io18Paints.getRandomPaintIndices()
        false -> intArrayOf(0, 1, 2, 3)
    }
    private val colors: Array<Color> = Array(4) { Color.Red }

    private val zero = Zero(animations.animatedPath)
    private val one = One(animations.animatedPath)
    private val two = Two(animations.animatedPath)
    private val three = Three(animations.animatedPath, animations.tube)
    private val four = Four(animations.animatedPath, animations.tube)
    private val five = Five(animations.animatedPath)
    private val six = Six(animations.tube)
    private val seven = Seven(animations.animatedPath)
    private val eight = Eight(animations.animatedPath)
    private val nine = Nine(animations.tube)

    override fun getWidthAtProgress(glyphProgress: Float): Float {
        val p = decelerate2(glyphProgress)
        return when (clockKey) {
            ClockGlyph.Key.Separator -> 24f

            ClockGlyph.Key.Zero,
            ClockGlyph.Key.ZeroOne,
                -> interpolate(p, Zero.size.width, One.size.width)

            ClockGlyph.Key.OneZero -> interpolate(p, One.size.width, Zero.size.width)

            ClockGlyph.Key.One,
            ClockGlyph.Key.OneTwo,
                -> interpolate(p, One.size.width, Two.size.width)

            ClockGlyph.Key.TwoOne -> interpolate(p, Two.size.width, One.size.width)

            ClockGlyph.Key.OneEmpty -> interpolate(p, One.size.width, 0f)
            ClockGlyph.Key.EmptyOne -> interpolate(p, 0f, One.size.width)

            else -> GlyphCharacter.DefaultWidth
        }
    }

    override fun draw(
        canvas: Canvas,
        glyphProgress: Float,
        paints: Io18Paints,
        renderGlyph: RenderGlyph?,
    ) {
        paintIndices.forEachIndexed { index, value -> colors[index] = paints.colors[value] }
        super.draw(canvas, glyphProgress, paints, renderGlyph)
    }

    override fun Canvas.drawZeroOne(
        glyphProgress: Float,
        paints: Io18Paints,
        renderGlyph: RenderGlyph?,
    ) {
        transition(glyphProgress, zero, one)
    }

    override fun Canvas.drawOneTwo(
        glyphProgress: Float,
        paints: Io18Paints,
        renderGlyph: RenderGlyph?,
    ) {
        transition(glyphProgress, one, two)
    }

    override fun Canvas.drawTwoThree(
        glyphProgress: Float,
        paints: Io18Paints,
        renderGlyph: RenderGlyph?,
    ) {
        transition(glyphProgress, two, three)
    }

    override fun Canvas.drawThreeFour(
        glyphProgress: Float,
        paints: Io18Paints,
        renderGlyph: RenderGlyph?,
    ) {
        transition(glyphProgress, three, four)
    }

    override fun Canvas.drawFourFive(
        glyphProgress: Float,
        paints: Io18Paints,
        renderGlyph: RenderGlyph?,
    ) {
        transition(glyphProgress, four, five)
    }

    override fun Canvas.drawFiveSix(
        glyphProgress: Float,
        paints: Io18Paints,
        renderGlyph: RenderGlyph?,
    ) {
        transition(glyphProgress, five, six)
    }

    override fun Canvas.drawSixSeven(
        glyphProgress: Float,
        paints: Io18Paints,
        renderGlyph: RenderGlyph?,
    ) {
        transition(glyphProgress, six, seven)
    }

    override fun Canvas.drawSevenEight(
        glyphProgress: Float,
        paints: Io18Paints,
        renderGlyph: RenderGlyph?,
    ) {
        transition(glyphProgress, seven, eight)
    }

    override fun Canvas.drawEightNine(
        glyphProgress: Float,
        paints: Io18Paints,
        renderGlyph: RenderGlyph?,
    ) {
        transition(glyphProgress, eight, nine)
    }

    override fun Canvas.drawNineZero(
        glyphProgress: Float,
        paints: Io18Paints,
        renderGlyph: RenderGlyph?,
    ) {
        transition(glyphProgress, nine, zero)
    }

    override fun Canvas.drawOneZero(
        glyphProgress: Float,
        paints: Io18Paints,
        renderGlyph: RenderGlyph?,
    ) {
        transition(glyphProgress, one, zero)
    }

    override fun Canvas.drawTwoZero(
        glyphProgress: Float,
        paints: Io18Paints,
        renderGlyph: RenderGlyph?,
    ) {
        transition(glyphProgress, two, zero)
    }

    override fun Canvas.drawTwoOne(
        glyphProgress: Float,
        paints: Io18Paints,
        renderGlyph: RenderGlyph?,
    ) {
        transition(glyphProgress, two, one)
    }

    override fun Canvas.drawThreeZero(
        glyphProgress: Float,
        paints: Io18Paints,
        renderGlyph: RenderGlyph?,
    ) {
        transition(glyphProgress, three, zero)
    }

    override fun Canvas.drawFiveZero(
        glyphProgress: Float,
        paints: Io18Paints,
        renderGlyph: RenderGlyph?,
    ) {

        transition(glyphProgress, five, zero)
    }

    override fun Canvas.drawOneEmpty(
        glyphProgress: Float,
        paints: Io18Paints,
        renderGlyph: RenderGlyph?,
    ) {
        transition(glyphProgress, one, null)
    }

    override fun Canvas.drawTwoEmpty(
        glyphProgress: Float,
        paints: Io18Paints,
        renderGlyph: RenderGlyph?,
    ) {
        transition(glyphProgress, two, null)
    }

    override fun Canvas.drawEmptyOne(
        glyphProgress: Float,
        paints: Io18Paints,
        renderGlyph: RenderGlyph?,
    ) {
        transition(glyphProgress, null, one)
    }

    override fun Canvas.drawSeparator(
        glyphProgress: Float,
        paints: Io18Paints,
        renderGlyph: RenderGlyph?,
    ) {
        // Intentionally blank
    }

    override fun Canvas.drawSpace(
        glyphProgress: Float,
        paints: Io18Paints,
        renderGlyph: RenderGlyph?,
    ) {
        // Intentionally blank
    }

    override fun Canvas.drawHash(
        glyphProgress: Float,
        paints: Io18Paints,
        renderGlyph: RenderGlyph?,
    ) {
        drawNotImplemented(glyphProgress, paints)
    }

    private inline fun Canvas.transition(
        glyphProgress: Float,
        start: GlyphCharacter?,
        end: GlyphCharacter?,
    ) {
        start?.drawExit(this, progress(glyphProgress, 0f, 0.6f), colors)
        if (glyphProgress > 0f) {
            end?.drawEnter(this, progress(glyphProgress, 0.4f, 1f), colors)
        }
    }
}
