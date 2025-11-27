package org.beatonma.gclocks.core.glyph

import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.util.getCurrentTimeMillis

interface SecondChangedObserver {
    /**
     * Must be called as soon as a change in second is detected, before any
     * other glyph values are accessed or updated.
     */
    fun onSecondChange(currentTimeMillis: Long)
}


sealed class BaseClockGlyph(
    override val role: GlyphRole,
    override val scale: Float = 1f,
    lock: GlyphState? = null,
    initialTimeMillis: Long = getCurrentTimeMillis(),
) : BaseGlyph, ClockGlyph {
    final override var key: String = " "
        protected set(value) {
            field = value
            clockKey = resolveClockKey(value)
            canonicalStartGlyph = value.first()
            canonicalEndGlyph = value.last()
        }
    final override var clockKey: ClockGlyph.Key = resolveClockKey(key)
        private set

    final override var canonicalStartGlyph: Char = ' '
        protected set
    final override var canonicalEndGlyph: Char = ' '
        protected set

    override val stateController: GlyphStateController =
        DefaultGlyphStateController(lock, initialTimeMillis)

    override val lock: GlyphState? get() = stateController.lock

    override val state: GlyphState get() = stateController.state
    override val visibility: GlyphVisibility get() = visibilityController.visibility
}

/**
 * A [Glyph] with specific animations for transitioning between [GlyphVisibility] states.
 * These transitions replace the typical second-to-second animations entirely,
 * and so must be synchronized with the current time to avoid visual jumps.
 */
abstract class ClockGlyphSynchronizedVisibility(
    role: GlyphRole,
    scale: Float = 1f,
    lock: GlyphState? = null,
    currentTimeMillis: Long = getCurrentTimeMillis(),
) : BaseClockGlyph(role, scale, lock, currentTimeMillis) {
    override val visibilityController: GlyphVisibilityController =
        SynchronizedVisibilityController { newVisibility, currentTimeMillis ->
            if (newVisibility == GlyphVisibility.Visible || newVisibility == GlyphVisibility.Appearing) {
                setState(GlyphState.Active, force = true, currentTimeMillis = currentTimeMillis)
            }
        }

    override fun setKey(value: String, force: Boolean) {
        key = when {
            force -> value
            visibility == GlyphVisibility.Appearing -> Glyph.createKey(' ', value.last())
            visibility == GlyphVisibility.Disappearing -> Glyph.createKey(value.first(), ' ')
            else -> value
        }
    }

    override fun onSecondChange(currentTimeMillis: Long) {
        visibilityController.onSecondChange(currentTimeMillis)
    }
}

/**
 * A [Glyph] which applies [GlyphVisibility] transitions on top of the existing
 * second-to-second animations, instead of replacing those animations entirely.
 */
abstract class ClockGlyphDesynchronizedVisibility(
    role: GlyphRole,
    scale: Float = 1f,
    lock: GlyphState? = null,
    currentTimeMillis: Long = getCurrentTimeMillis(),
) : BaseClockGlyph(role, scale, lock, currentTimeMillis) {
    override val visibilityController =
        DesynchronizedGlyphVisibilityController { newVisibility, currentTimeMillis ->
            if (newVisibility == GlyphVisibility.Visible || newVisibility == GlyphVisibility.Appearing) {
                setState(GlyphState.Active, force = true, currentTimeMillis = currentTimeMillis)
            }
        }
    val visibilityChangedProgress get() = visibilityController.visibilityChangedProgress

    override fun setKey(value: String, force: Boolean) {
        key = value
    }

    override fun onSecondChange(currentTimeMillis: Long) {
        // no-op
    }
}


interface ClockGlyph : Glyph, SecondChangedObserver {
    val clockKey: Key

    fun resolveClockKey(value: String): Key =
        Key.entries.first { it.key == value }

    override fun draw(
        canvas: Canvas,
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    ) {
        if (this.visibility == GlyphVisibility.Hidden) return

        canvas.delegateDrawMethod(glyphProgress, paints, renderGlyph)
    }

    fun Canvas.drawZeroOne(glyphProgress: Float, paints: Paints, renderGlyph: RenderGlyph?)
    fun Canvas.drawOneTwo(glyphProgress: Float, paints: Paints, renderGlyph: RenderGlyph?)
    fun Canvas.drawTwoThree(glyphProgress: Float, paints: Paints, renderGlyph: RenderGlyph?)
    fun Canvas.drawThreeFour(glyphProgress: Float, paints: Paints, renderGlyph: RenderGlyph?)
    fun Canvas.drawFourFive(glyphProgress: Float, paints: Paints, renderGlyph: RenderGlyph?)
    fun Canvas.drawFiveSix(glyphProgress: Float, paints: Paints, renderGlyph: RenderGlyph?)
    fun Canvas.drawSixSeven(glyphProgress: Float, paints: Paints, renderGlyph: RenderGlyph?)
    fun Canvas.drawSevenEight(glyphProgress: Float, paints: Paints, renderGlyph: RenderGlyph?)
    fun Canvas.drawEightNine(glyphProgress: Float, paints: Paints, renderGlyph: RenderGlyph?)
    fun Canvas.drawNineZero(glyphProgress: Float, paints: Paints, renderGlyph: RenderGlyph?)

    fun Canvas.drawOneZero(glyphProgress: Float, paints: Paints, renderGlyph: RenderGlyph?)
    fun Canvas.drawTwoZero(glyphProgress: Float, paints: Paints, renderGlyph: RenderGlyph?)
    fun Canvas.drawTwoOne(glyphProgress: Float, paints: Paints, renderGlyph: RenderGlyph?)
    fun Canvas.drawThreeZero(glyphProgress: Float, paints: Paints, renderGlyph: RenderGlyph?)
    fun Canvas.drawFiveZero(glyphProgress: Float, paints: Paints, renderGlyph: RenderGlyph?)

    fun Canvas.drawZeroEmpty(glyphProgress: Float, paints: Paints, renderGlyph: RenderGlyph?)
    fun Canvas.drawOneEmpty(glyphProgress: Float, paints: Paints, renderGlyph: RenderGlyph?)
    fun Canvas.drawTwoEmpty(glyphProgress: Float, paints: Paints, renderGlyph: RenderGlyph?)
    fun Canvas.drawThreeEmpty(glyphProgress: Float, paints: Paints, renderGlyph: RenderGlyph?)
    fun Canvas.drawFourEmpty(glyphProgress: Float, paints: Paints, renderGlyph: RenderGlyph?)
    fun Canvas.drawFiveEmpty(glyphProgress: Float, paints: Paints, renderGlyph: RenderGlyph?)
    fun Canvas.drawSixEmpty(glyphProgress: Float, paints: Paints, renderGlyph: RenderGlyph?)
    fun Canvas.drawSevenEmpty(glyphProgress: Float, paints: Paints, renderGlyph: RenderGlyph?)
    fun Canvas.drawEightEmpty(glyphProgress: Float, paints: Paints, renderGlyph: RenderGlyph?)
    fun Canvas.drawNineEmpty(glyphProgress: Float, paints: Paints, renderGlyph: RenderGlyph?)

    fun Canvas.drawEmptyZero(glyphProgress: Float, paints: Paints, renderGlyph: RenderGlyph?)
    fun Canvas.drawEmptyOne(glyphProgress: Float, paints: Paints, renderGlyph: RenderGlyph?)
    fun Canvas.drawEmptyTwo(glyphProgress: Float, paints: Paints, renderGlyph: RenderGlyph?)
    fun Canvas.drawEmptyThree(glyphProgress: Float, paints: Paints, renderGlyph: RenderGlyph?)
    fun Canvas.drawEmptyFour(glyphProgress: Float, paints: Paints, renderGlyph: RenderGlyph?)
    fun Canvas.drawEmptyFive(glyphProgress: Float, paints: Paints, renderGlyph: RenderGlyph?)
    fun Canvas.drawEmptySix(glyphProgress: Float, paints: Paints, renderGlyph: RenderGlyph?)
    fun Canvas.drawEmptySeven(glyphProgress: Float, paints: Paints, renderGlyph: RenderGlyph?)
    fun Canvas.drawEmptyEight(glyphProgress: Float, paints: Paints, renderGlyph: RenderGlyph?)
    fun Canvas.drawEmptyNine(glyphProgress: Float, paints: Paints, renderGlyph: RenderGlyph?)

    fun Canvas.drawSeparator(glyphProgress: Float, paints: Paints, renderGlyph: RenderGlyph?)
    fun Canvas.drawEmptySeparator(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    )

    fun Canvas.drawSeparatorEmpty(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?
    )

    fun Canvas.drawSpace(glyphProgress: Float, paints: Paints, renderGlyph: RenderGlyph?)

    fun Canvas.delegateDrawMethod(
        glyphProgress: Float,
        paints: Paints,
        renderGlyph: RenderGlyph?,
    ) {
        when (clockKey) {
            Key.Zero,
            Key.ZeroOne,
                -> drawZeroOne(glyphProgress, paints, renderGlyph)

            Key.One,
            Key.OneTwo,
                -> drawOneTwo(glyphProgress, paints, renderGlyph)

            Key.Two,
            Key.TwoThree,
                -> drawTwoThree(glyphProgress, paints, renderGlyph)

            Key.Three,
            Key.ThreeFour,
                -> drawThreeFour(glyphProgress, paints, renderGlyph)

            Key.Four,
            Key.FourFive,
                -> drawFourFive(glyphProgress, paints, renderGlyph)

            Key.Five,
            Key.FiveSix,
                -> drawFiveSix(glyphProgress, paints, renderGlyph)

            Key.Six,
            Key.SixSeven,
                -> drawSixSeven(glyphProgress, paints, renderGlyph)

            Key.Seven,
            Key.SevenEight,
                -> drawSevenEight(glyphProgress, paints, renderGlyph)

            Key.Eight,
            Key.EightNine,
                -> drawEightNine(glyphProgress, paints, renderGlyph)

            Key.Nine,
            Key.NineZero,
                -> drawNineZero(glyphProgress, paints, renderGlyph)

            Key.OneZero -> drawOneZero(glyphProgress, paints, renderGlyph)
            Key.TwoZero -> drawTwoZero(glyphProgress, paints, renderGlyph)
            Key.TwoOne -> drawTwoOne(glyphProgress, paints, renderGlyph)
            Key.ThreeZero -> drawThreeZero(glyphProgress, paints, renderGlyph)
            Key.FiveZero -> drawFiveZero(glyphProgress, paints, renderGlyph)
            Key.ZeroEmpty -> drawZeroEmpty(glyphProgress, paints, renderGlyph)
            Key.OneEmpty -> drawOneEmpty(glyphProgress, paints, renderGlyph)
            Key.TwoEmpty -> drawTwoEmpty(glyphProgress, paints, renderGlyph)
            Key.ThreeEmpty -> drawThreeEmpty(glyphProgress, paints, renderGlyph)
            Key.FourEmpty -> drawFourEmpty(glyphProgress, paints, renderGlyph)
            Key.FiveEmpty -> drawFiveEmpty(glyphProgress, paints, renderGlyph)
            Key.SixEmpty -> drawSixEmpty(glyphProgress, paints, renderGlyph)
            Key.SevenEmpty -> drawSevenEmpty(glyphProgress, paints, renderGlyph)
            Key.EightEmpty -> drawEightEmpty(glyphProgress, paints, renderGlyph)
            Key.NineEmpty -> drawNineEmpty(glyphProgress, paints, renderGlyph)
            Key.EmptyZero -> drawEmptyZero(glyphProgress, paints, renderGlyph)
            Key.EmptyOne -> drawEmptyOne(glyphProgress, paints, renderGlyph)
            Key.EmptyTwo -> drawEmptyTwo(glyphProgress, paints, renderGlyph)
            Key.EmptyThree -> drawEmptyThree(glyphProgress, paints, renderGlyph)
            Key.EmptyFour -> drawEmptyFour(glyphProgress, paints, renderGlyph)
            Key.EmptyFive -> drawEmptyFive(glyphProgress, paints, renderGlyph)
            Key.EmptySix -> drawEmptySix(glyphProgress, paints, renderGlyph)
            Key.EmptySeven -> drawEmptySeven(glyphProgress, paints, renderGlyph)
            Key.EmptyEight -> drawEmptyEight(glyphProgress, paints, renderGlyph)
            Key.EmptyNine -> drawEmptyNine(glyphProgress, paints, renderGlyph)
            Key.Separator -> drawSeparator(glyphProgress, paints, renderGlyph)
            Key.EmptySeparator -> drawEmptySeparator(glyphProgress, paints, renderGlyph)
            Key.SeparatorEmpty -> drawSeparatorEmpty(glyphProgress, paints, renderGlyph)
            Key.Empty -> drawSpace(glyphProgress, paints, renderGlyph)
        }
    }

    enum class Key(val key: String) {
        Zero("0"),
        One("1"),
        Two("2"),
        Three("3"),
        Four("4"),
        Five("5"),
        Six("6"),
        Seven("7"),
        Eight("8"),
        Nine("9"),
        ZeroOne("0_1"),
        OneTwo("1_2"),
        TwoThree("2_3"),
        ThreeFour("3_4"),
        FourFive("4_5"),
        FiveSix("5_6"),
        SixSeven("6_7"),
        SevenEight("7_8"),
        EightNine("8_9"),
        OneZero("1_0"),
        TwoZero("2_0"),
        TwoOne("2_1"),
        ThreeZero("3_0"),
        FiveZero("5_0"),
        NineZero("9_0"),
        ZeroEmpty("0_ "),
        OneEmpty("1_ "),
        TwoEmpty("2_ "),
        ThreeEmpty("3_ "),
        FourEmpty("4_ "),
        FiveEmpty("5_ "),
        SixEmpty("6_ "),
        SevenEmpty("7_ "),
        EightEmpty("8_ "),
        NineEmpty("9_ "),
        EmptyZero(" _0"),
        EmptyOne(" _1"),
        EmptyTwo(" _2"),
        EmptyThree(" _3"),
        EmptyFour(" _4"),
        EmptyFive(" _5"),
        EmptySix(" _6"),
        EmptySeven(" _7"),
        EmptyEight(" _8"),
        EmptyNine(" _9"),
        Empty(" "),
        Separator(":"),
        SeparatorEmpty(":_ "),
        EmptySeparator(" _:"),
        ;
    }
}
