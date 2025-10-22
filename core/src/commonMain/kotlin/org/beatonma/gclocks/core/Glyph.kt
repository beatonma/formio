package org.beatonma.gclocks.core

import org.beatonma.gclocks.core.geometry.NativeSize
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.options.GlyphOptions
import org.beatonma.gclocks.core.util.debug
import org.beatonma.gclocks.core.util.getCurrentTimeMillis

enum class GlyphState {
    Inactive,
    Active,
    Activating,
    Deactivating,
    Appearing,
    Disappearing,
    DisappearingFromActive,
    DisappearingFromInactive,
    Disappeared,
    ;

    val affectsVisibility: Boolean
        get() = when (this) {
            Appearing, Disappeared, Disappearing, DisappearingFromActive, DisappearingFromInactive -> true
            else -> false
        }
}

enum class GlyphRole {
    Default,
    Hour,
    Minute,
    Second,
    SeparatorHoursMinutes,
    SeparatorMinutesSeconds,
    ;

    val isSeparator: Boolean get() = this == SeparatorMinutesSeconds || this == SeparatorHoursMinutes
}

private typealias OnStateChange = (newState: GlyphState) -> Unit

interface GlyphCompanion {
    val maxSize: NativeSize
}

typealias RenderGlyph = () -> Unit

interface Glyph<P : Paints> {
    val companion: GlyphCompanion
    val maxSize: NativeSize get() = companion.maxSize
    val role: GlyphRole
    val state: GlyphState
    val lock: GlyphState?
    val scale: Float
    var key: String
    var stateChangedAt: Long
    var onStateChange: OnStateChange?

    val canonicalStartGlyph: Char
    val canonicalEndGlyph: Char

    fun draw(canvas: Canvas, glyphProgress: Float, paints: P, renderGlyph: RenderGlyph? = null)
    fun getWidthAtProgress(glyphProgress: Float): Float
    fun setState(newState: GlyphState, force: Boolean = false)
    fun tickState(options: GlyphOptions)
}

interface ClockGlyph<P : Paints> : Glyph<P> {
    fun Canvas.drawZeroOne(glyphProgress: Float, paints: P, renderGlyph: RenderGlyph?)
    fun Canvas.drawOneTwo(glyphProgress: Float, paints: P, renderGlyph: RenderGlyph?)
    fun Canvas.drawTwoThree(glyphProgress: Float, paints: P, renderGlyph: RenderGlyph?)
    fun Canvas.drawThreeFour(glyphProgress: Float, paints: P, renderGlyph: RenderGlyph?)
    fun Canvas.drawFourFive(glyphProgress: Float, paints: P, renderGlyph: RenderGlyph?)
    fun Canvas.drawFiveSix(glyphProgress: Float, paints: P, renderGlyph: RenderGlyph?)
    fun Canvas.drawSixSeven(glyphProgress: Float, paints: P, renderGlyph: RenderGlyph?)
    fun Canvas.drawSevenEight(glyphProgress: Float, paints: P, renderGlyph: RenderGlyph?)
    fun Canvas.drawEightNine(glyphProgress: Float, paints: P, renderGlyph: RenderGlyph?)
    fun Canvas.drawNineZero(glyphProgress: Float, paints: P, renderGlyph: RenderGlyph?)
    fun Canvas.drawOneZero(glyphProgress: Float, paints: P, renderGlyph: RenderGlyph?)
    fun Canvas.drawTwoZero(glyphProgress: Float, paints: P, renderGlyph: RenderGlyph?)
    fun Canvas.drawTwoOne(glyphProgress: Float, paints: P, renderGlyph: RenderGlyph?)
    fun Canvas.drawThreeZero(glyphProgress: Float, paints: P, renderGlyph: RenderGlyph?)
    fun Canvas.drawFiveZero(glyphProgress: Float, paints: P, renderGlyph: RenderGlyph?)
    fun Canvas.drawOneEmpty(glyphProgress: Float, paints: P, renderGlyph: RenderGlyph?)
    fun Canvas.drawTwoEmpty(glyphProgress: Float, paints: P, renderGlyph: RenderGlyph?)
    fun Canvas.drawEmptyOne(glyphProgress: Float, paints: P, renderGlyph: RenderGlyph?)
    fun Canvas.drawSeparator(glyphProgress: Float, paints: P, renderGlyph: RenderGlyph?)
    fun Canvas.drawSpace(glyphProgress: Float, paints: P, renderGlyph: RenderGlyph?)
    fun Canvas.drawHash(glyphProgress: Float, paints: P, renderGlyph: RenderGlyph?)

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
        OneEmpty("1_ "),
        TwoEmpty("2_ "),
        EmptyOne(" _1"),
        Empty(" "),
        Separator(":"),
        HashTag("#"),
        ;
    }
}

fun interface GlyphRenderer<P : Paints, G : Glyph<P>> {
    fun draw(glyph: G, canvas: Canvas, paints: P)
}


abstract class BaseGlyph<P : Paints> internal constructor(
    override val role: GlyphRole,
    override val scale: Float = 1f,
    override val lock: GlyphState? = null,
) : Glyph<P> {
    final override var state: GlyphState = GlyphState.Appearing
        private set(value) {
            if (value != field || value == GlyphState.Active) {
                // Setting as active resets the timeout, even if already active
                stateChangedAt = getCurrentTimeMillis()
            }

            field = value
            onStateChange?.invoke(value)
        }

    override var onStateChange: OnStateChange? = null

    override var stateChangedAt: Long = getCurrentTimeMillis()

    override var key: String = " "
        set(value) {
            field = value
            canonicalStartGlyph = value.first()
            canonicalEndGlyph = value.last()
        }

    final override var canonicalStartGlyph: Char = ' '
        protected set
    final override var canonicalEndGlyph: Char = ' '
        protected set

    override fun toString(): String {
        return key
    }

    override fun setState(newState: GlyphState, force: Boolean) {
        if (force) {
            state = newState
            return
        }
        if (state == lock && !newState.affectsVisibility) {
            /*
            * Lock is overruled when:
            * - Current state is transitional, so we can tick over to a steady state
            * - New state affects visibility (appearing/disappearing)
            */
            return
        }

        return when (newState) {
            GlyphState.Activating,
            GlyphState.Active,
                -> setActive()

            GlyphState.Deactivating,
            GlyphState.Inactive,
                -> setInactive()

            GlyphState.Appearing -> setAppearing()
            GlyphState.Disappeared,
            GlyphState.Disappearing,
            GlyphState.DisappearingFromActive,
            GlyphState.DisappearingFromInactive,
                -> setDisappeared()
        }
    }

    override fun tickState(options: GlyphOptions) {
        val now = getCurrentTimeMillis()
        val millisSinceChange = now - stateChangedAt
        val transitionStateExpired = millisSinceChange > options.stateChangeDurationMillis

        when (state) {
            GlyphState.Inactive, GlyphState.Disappeared -> {}

            GlyphState.Active -> {
                if (millisSinceChange > options.activeStateDurationMillis) {
                    setState(GlyphState.Deactivating)
                }
            }

            GlyphState.Appearing, GlyphState.Activating -> {
                if (transitionStateExpired) {
                    setState(GlyphState.Active, force = true)
                }
            }

            GlyphState.Deactivating -> {
                if (transitionStateExpired) {
                    setState(GlyphState.Inactive, force = true)
                }
            }

            GlyphState.Disappearing, GlyphState.DisappearingFromActive, GlyphState.DisappearingFromInactive -> {
                if (transitionStateExpired) {
                    setState(GlyphState.Disappeared, force = true)
                }
            }
        }
    }

    private fun setActive() {
        when (state) {
            GlyphState.Active -> {
                stateChangedAt = getCurrentTimeMillis()
            }

            GlyphState.Activating, GlyphState.Appearing -> {
                // State will be updated directly via tickState
            }

            GlyphState.Inactive, GlyphState.Deactivating -> state = GlyphState.Activating

            else -> {
                debug("setActive has no effect when state == $state")
            }
        }
    }

    private fun setInactive() {
        when (state) {
            GlyphState.Active, GlyphState.Activating -> {
                state = GlyphState.Deactivating
            }

            else -> {
                debug("setInactive has no effect when state == $state")
            }
        }
    }

    private fun setAppearing() {
        when (state) {
            GlyphState.Disappeared,
            GlyphState.Disappearing,
            GlyphState.DisappearingFromActive,
            GlyphState.DisappearingFromInactive,
                -> {
                state = GlyphState.Appearing
            }

            else -> {
                debug("setAppearing has no effect when state == $state")
            }
        }
    }

    private fun setDisappeared() {
        when (state) {
            GlyphState.Disappearing,
            GlyphState.DisappearingFromActive,
            GlyphState.DisappearingFromInactive,
                -> {
                state = GlyphState.Disappeared
            }

            else -> {
                debug("setDisappeared has no effect when state == $state")
            }
        }
    }
}

abstract class BaseClockGlyph<P : Paints>(
    role: GlyphRole,
    scale: Float = 1f,
    lock: GlyphState? = null,
) : BaseGlyph<P>(role, scale, lock), ClockGlyph<P> {
    var clockKey: ClockGlyph.Key = ClockGlyph.Key.Empty
        private set
    override var key: String
        get() = clockKey.key
        set(value) {
            clockKey =
                ClockGlyph.Key.entries.firstOrNull { it.key == value }
                    ?: throw IllegalStateException("Unknown key: $value")
            canonicalStartGlyph = value.first()
            canonicalEndGlyph = value.last()
        }


    override fun draw(
        canvas: Canvas,
        glyphProgress: Float,
        paints: P,
        renderGlyph: RenderGlyph?,
    ) {
        canvas.delegateDrawMethod(glyphProgress, paints, renderGlyph)
    }

    /**
     * Mapping of [ClockGlyph.Key] to abstract draw methods.
     */
    private fun Canvas.delegateDrawMethod(
        glyphProgress: Float,
        paints: P,
        renderGlyph: RenderGlyph?,
    ) {
        when (clockKey) {
            ClockGlyph.Key.Zero,
            ClockGlyph.Key.ZeroOne,
                -> drawZeroOne(glyphProgress, paints, renderGlyph)

            ClockGlyph.Key.One,
            ClockGlyph.Key.OneTwo,
                -> drawOneTwo(glyphProgress, paints, renderGlyph)

            ClockGlyph.Key.Two,
            ClockGlyph.Key.TwoThree,
                -> drawTwoThree(glyphProgress, paints, renderGlyph)

            ClockGlyph.Key.Three,
            ClockGlyph.Key.ThreeFour,
                -> drawThreeFour(glyphProgress, paints, renderGlyph)

            ClockGlyph.Key.Four,
            ClockGlyph.Key.FourFive,
                -> drawFourFive(glyphProgress, paints, renderGlyph)

            ClockGlyph.Key.Five,
            ClockGlyph.Key.FiveSix,
                -> drawFiveSix(glyphProgress, paints, renderGlyph)

            ClockGlyph.Key.Six,
            ClockGlyph.Key.SixSeven,
                -> drawSixSeven(glyphProgress, paints, renderGlyph)

            ClockGlyph.Key.Seven,
            ClockGlyph.Key.SevenEight,
                -> drawSevenEight(glyphProgress, paints, renderGlyph)

            ClockGlyph.Key.Eight,
            ClockGlyph.Key.EightNine,
                -> drawEightNine(glyphProgress, paints, renderGlyph)

            ClockGlyph.Key.Nine,
            ClockGlyph.Key.NineZero,
                -> drawNineZero(glyphProgress, paints, renderGlyph)

            ClockGlyph.Key.OneZero -> drawOneZero(glyphProgress, paints, renderGlyph)
            ClockGlyph.Key.TwoZero -> drawTwoZero(glyphProgress, paints, renderGlyph)
            ClockGlyph.Key.TwoOne -> drawTwoOne(glyphProgress, paints, renderGlyph)
            ClockGlyph.Key.ThreeZero -> drawThreeZero(glyphProgress, paints, renderGlyph)
            ClockGlyph.Key.FiveZero -> drawFiveZero(glyphProgress, paints, renderGlyph)
            ClockGlyph.Key.OneEmpty -> drawOneEmpty(glyphProgress, paints, renderGlyph)
            ClockGlyph.Key.TwoEmpty -> drawTwoEmpty(glyphProgress, paints, renderGlyph)
            ClockGlyph.Key.EmptyOne -> drawEmptyOne(glyphProgress, paints, renderGlyph)
            ClockGlyph.Key.Separator -> drawSeparator(glyphProgress, paints, renderGlyph)
            ClockGlyph.Key.Empty -> drawSpace(glyphProgress, paints, renderGlyph)
            ClockGlyph.Key.HashTag -> drawHash(glyphProgress, paints, renderGlyph)
        }
    }

    fun Canvas.drawNotImplemented(glyphProgress: Float, paints: P) {
        val (width, height) = companion.maxSize
        drawLine(Color.Red, 0f, 0f, width, height)
        drawLine(Color.Red, width, 0f, 0f, height)
    }
}
