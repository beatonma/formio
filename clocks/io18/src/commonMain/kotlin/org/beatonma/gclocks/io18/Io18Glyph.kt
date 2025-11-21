package org.beatonma.gclocks.io18

import org.beatonma.gclocks.core.geometry.NativeSize
import org.beatonma.gclocks.core.glyph.ClockGlyph
import org.beatonma.gclocks.core.glyph.ClockGlyphSynchronizedVisibility
import org.beatonma.gclocks.core.glyph.GlyphCompanion
import org.beatonma.gclocks.core.glyph.GlyphRole
import org.beatonma.gclocks.core.glyph.GlyphState
import org.beatonma.gclocks.core.glyph.RenderGlyph
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.graphics.Path
import org.beatonma.gclocks.core.util.decelerate2
import org.beatonma.gclocks.core.util.interpolate
import org.beatonma.gclocks.core.util.progress
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
    colorsOffset: Int = 0,
    shuffleColors: Boolean = true,
) : ClockGlyphSynchronizedVisibility(role, scale, lock) {
    companion object : GlyphCompanion {
        override val maxSize: NativeSize = NativeSize(
            GlyphCharacter.DefaultWidth,
            GlyphCharacter.DefaultHeight
        )
    }

    override val companion: GlyphCompanion = Companion
    private val paintIndices: IntArray = when (shuffleColors) {
        true -> Io18Paints.getRandomPaintIndices()
        else -> {
            val arr = intArrayOf(0, 1, 2, 3)
            val size = arr.size
            arr.forEachIndexed { index, value ->
                arr[index] = (value + colorsOffset) % size
            }
            arr
        }
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
            ClockGlyph.Key.Separator,
            ClockGlyph.Key.SeparatorEmpty,
            ClockGlyph.Key.EmptySeparator -> 24f

            ClockGlyph.Key.Zero,
            ClockGlyph.Key.ZeroOne,
                -> interpolate(p, Zero.size.width, One.size.width)

            ClockGlyph.Key.OneZero -> interpolate(p, One.size.width, Zero.size.width)

            ClockGlyph.Key.One,
            ClockGlyph.Key.OneTwo,
                -> interpolate(p, One.size.width, Two.size.width)

            ClockGlyph.Key.TwoOne -> interpolate(p, Two.size.width, One.size.width)

            ClockGlyph.Key.ZeroEmpty -> Zero.size.width
            ClockGlyph.Key.OneEmpty -> One.size.width
            ClockGlyph.Key.TwoEmpty -> Two.size.width
            ClockGlyph.Key.ThreeEmpty -> Three.size.width
            ClockGlyph.Key.FourEmpty -> Four.size.width
            ClockGlyph.Key.FiveEmpty -> Five.size.width
            ClockGlyph.Key.SixEmpty -> Six.size.width
            ClockGlyph.Key.SevenEmpty -> Seven.size.width
            ClockGlyph.Key.EightEmpty -> Eight.size.width
            ClockGlyph.Key.NineEmpty -> Nine.size.width

            ClockGlyph.Key.EmptyZero -> Zero.size.width
            ClockGlyph.Key.EmptyOne -> One.size.width
            ClockGlyph.Key.EmptyTwo -> Two.size.width
            ClockGlyph.Key.EmptyThree -> Three.size.width
            ClockGlyph.Key.EmptyFour -> Four.size.width
            ClockGlyph.Key.EmptyFive -> Five.size.width
            ClockGlyph.Key.EmptySix -> Six.size.width
            ClockGlyph.Key.EmptySeven -> Seven.size.width
            ClockGlyph.Key.EmptyEight -> Eight.size.width
            ClockGlyph.Key.EmptyNine -> Nine.size.width

            else -> GlyphCharacter.DefaultWidth
        }
    }

    override fun draw(
        canvas: Canvas,
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?,
    ) {
        paintIndices.forEachIndexed { index, value ->
            colors[index] = paints[value]
        }
        super.draw(canvas, glyphProgress, paints, renderGlyph)
    }

    override fun Canvas.drawZeroOne(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?,
    ) {
        transition(glyphProgress, zero, one)
    }

    override fun Canvas.drawOneTwo(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?,
    ) {
        transition(glyphProgress, one, two)
    }

    override fun Canvas.drawTwoThree(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?,
    ) {
        transition(glyphProgress, two, three)
    }

    override fun Canvas.drawThreeFour(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?,
    ) {
        transition(glyphProgress, three, four)
    }

    override fun Canvas.drawFourFive(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?,
    ) {
        transition(glyphProgress, four, five)
    }

    override fun Canvas.drawFiveSix(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?,
    ) {
        transition(glyphProgress, five, six)
    }

    override fun Canvas.drawSixSeven(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?,
    ) {
        transition(glyphProgress, six, seven)
    }

    override fun Canvas.drawSevenEight(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?,
    ) {
        transition(glyphProgress, seven, eight)
    }

    override fun Canvas.drawEightNine(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?,
    ) {
        transition(glyphProgress, eight, nine)
    }

    override fun Canvas.drawNineZero(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?,
    ) {
        transition(glyphProgress, nine, zero)
    }

    override fun Canvas.drawOneZero(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?,
    ) {
        transition(glyphProgress, one, zero)
    }

    override fun Canvas.drawTwoZero(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?,
    ) {
        transition(glyphProgress, two, zero)
    }

    override fun Canvas.drawTwoOne(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?,
    ) {
        transition(glyphProgress, two, one)
    }

    override fun Canvas.drawThreeZero(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?,
    ) {
        transition(glyphProgress, three, zero)
    }

    override fun Canvas.drawFiveZero(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?,
    ) {

        transition(glyphProgress, five, zero)
    }

    override fun Canvas.drawZeroEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        transition(glyphProgress, zero, null)
    }

    override fun Canvas.drawOneEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        transition(glyphProgress, one, null)
    }

    override fun Canvas.drawTwoEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        transition(glyphProgress, two, null)
    }

    override fun Canvas.drawThreeEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        transition(glyphProgress, three, null)
    }

    override fun Canvas.drawFourEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        transition(glyphProgress, four, null)
    }

    override fun Canvas.drawFiveEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        transition(glyphProgress, five, null)
    }

    override fun Canvas.drawSixEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        transition(glyphProgress, six, null)
    }

    override fun Canvas.drawSevenEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        transition(glyphProgress, seven, null)
    }

    override fun Canvas.drawEightEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        transition(glyphProgress, eight, null)
    }

    override fun Canvas.drawNineEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        transition(glyphProgress, nine, null)
    }

    override fun Canvas.drawEmptyZero(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        transition(glyphProgress, null, zero)
    }

    override fun Canvas.drawEmptyOne(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        transition(glyphProgress, null, one)
    }

    override fun Canvas.drawEmptyTwo(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        transition(glyphProgress, null, two)
    }

    override fun Canvas.drawEmptyThree(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        transition(glyphProgress, null, three)
    }

    override fun Canvas.drawEmptyFour(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        transition(glyphProgress, null, four)
    }

    override fun Canvas.drawEmptyFive(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        transition(glyphProgress, null, five)
    }

    override fun Canvas.drawEmptySix(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        transition(glyphProgress, null, six)
    }

    override fun Canvas.drawEmptySeven(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        transition(glyphProgress, null, seven)
    }

    override fun Canvas.drawEmptyEight(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        transition(glyphProgress, null, eight)
    }

    override fun Canvas.drawEmptyNine(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        transition(glyphProgress, null, nine)
    }

    override fun Canvas.drawSeparator(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?,
    ) {
        // Intentionally blank
    }

    override fun Canvas.drawEmptySeparator(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        // Intentionally blank
    }

    override fun Canvas.drawSeparatorEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        // Intentionally blank
    }

    override fun Canvas.drawSpace(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?,
    ) {
        // Intentionally blank
    }

    private fun Canvas.transition(
        glyphProgress: Float,
        start: GlyphCharacter?,
        end: GlyphCharacter?,
    ) {
        if (glyphProgress <= 0.6f) {
            start?.drawExit(this, progress(glyphProgress, 0f, 0.6f), colors)
        }
        if (glyphProgress >= 0.4f) {
            end?.drawEnter(this, progress(glyphProgress, 0.4f, 1f), colors)
        }
    }
}
