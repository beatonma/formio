package org.beatonma.gclocks.core

import org.beatonma.gclocks.core.geometry.NativeSize
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.options.GlyphOptions
import org.beatonma.gclocks.core.util.debug
import org.beatonma.gclocks.core.util.getCurrentTimeMillis
import org.beatonma.gclocks.core.util.progress


enum class GlyphVisibility {
    Visible,
    Appearing,
    Disappearing,
    Hidden,
    ;
}

enum class GlyphState {
    Active,
    Activating,
    Deactivating,
    Inactive,
    ;
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

interface GlyphCompanion {
    val maxSize: NativeSize
}

typealias RenderGlyph = () -> Unit

interface Glyph<P : Paints> {
    val companion: GlyphCompanion
    val maxSize: NativeSize get() = companion.maxSize
    val role: GlyphRole
    val state: GlyphState
    val visibility: GlyphVisibility
    val lock: GlyphState?
    val scale: Float
    val stateChangeProgress: Float
    val visibilityChangedProgress: Float
    var key: String

    val canonicalStartGlyph: Char
    val canonicalEndGlyph: Char

    fun draw(canvas: Canvas, glyphProgress: Float, paints: P, renderGlyph: RenderGlyph? = null)
    fun getWidthAtProgress(glyphProgress: Float): Float
    fun setState(
        newState: GlyphState,
        newVisibility: GlyphVisibility,
        force: Boolean = false,
        currentTimeMillis: Long = getCurrentTimeMillis()
    )

    fun setState(newState: GlyphState, force: Boolean = false, currentTimeMillis: Long = getCurrentTimeMillis())
    fun setState(
        newVisibility: GlyphVisibility,
        force: Boolean = false,
        currentTimeMillis: Long = getCurrentTimeMillis()
    )

    fun tickState(options: GlyphOptions, currentTimeMillis: Long = getCurrentTimeMillis())
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

interface GlyphRenderer<P : Paints, G : Glyph<P>> {
    fun update(currentTimeMillis: Long) {}
    fun draw(glyph: G, canvas: Canvas, paints: P)
}

abstract class BaseGlyph<P : Paints> internal constructor(
    override val role: GlyphRole,
    override val scale: Float = 1f,
    override val lock: GlyphState? = null,
    lock: GlyphState? = null,
    currentTimeMillis: Long = getCurrentTimeMillis()
) : Glyph<P> {
    final override var state: GlyphState = GlyphState.Active
        private set

    final override var visibility = GlyphVisibility.Visible
        private set

    private var stateChangedAt: Long = currentTimeMillis
    private var visibilityChangedAt: Long = currentTimeMillis

    final override var stateChangeProgress: Float = 0f
        private set
    final override var visibilityChangedProgress: Float = 0f
        private set

    override var key: String = " "
        set(value) {
            field = value
            val canonicalStartGlyph = value.first()
            val canonicalEndGlyph = value.last()

            if (canonicalStartGlyph == ' ' && canonicalEndGlyph != ' ') {
                setState(GlyphVisibility.Visible)
            } else if (canonicalEndGlyph == ' ' && canonicalStartGlyph != ' ') {
                setState(GlyphVisibility.Hidden)
            }
            this.canonicalStartGlyph = canonicalStartGlyph
            this.canonicalEndGlyph = canonicalEndGlyph
        }

    final override var canonicalStartGlyph: Char = ' '
        protected set
    final override var canonicalEndGlyph: Char = ' '
        protected set

    override fun toString(): String = key

    override fun setState(
        newState: GlyphState,
        newVisibility: GlyphVisibility,
        force: Boolean,
        currentTimeMillis: Long
    ) {
        setState(newState, force, currentTimeMillis)
        setState(newVisibility, force, currentTimeMillis)
    }

    override fun setState(newState: GlyphState, force: Boolean) {
    override fun setState(newState: GlyphState, force: Boolean, currentTimeMillis: Long) {
        if (force) {
            if (newState == GlyphState.Active || newState != state) {
                stateChangedAt = currentTimeMillis
                stateChangeProgress = 0f
            }
            state = newState
            return
        }
        if (state == lock) {
            // Lock is overruled when current state is transitional and can tick over to a steady state
            return
        }

        when (newState) {
            GlyphState.Activating,
            GlyphState.Active,
                -> setActive(currentTimeMillis)

            GlyphState.Deactivating,
            GlyphState.Inactive,
                -> setInactive(currentTimeMillis)
        }
    }

    override fun setState(newVisibility: GlyphVisibility, force: Boolean, currentTimeMillis: Long) {
        if (force) {
            if (newVisibility != visibility) {
                visibilityChangedAt = currentTimeMillis
                visibilityChangedProgress = 0f
            }
            visibility = newVisibility
            return
        }

        when (newVisibility) {
            GlyphVisibility.Visible, GlyphVisibility.Appearing -> appear(currentTimeMillis)
            GlyphVisibility.Disappearing, GlyphVisibility.Hidden -> disappear(currentTimeMillis)
        }
    }

    override fun tickState(options: GlyphOptions, currentTimeMillis: Long) {
        val millisSinceStateChange = currentTimeMillis - stateChangedAt
        val millisSinceVisibilityChange = currentTimeMillis - visibilityChangedAt

        stateChangeProgress =
            progress(millisSinceStateChange.toFloat(), 0f, options.stateChangeDurationMillis.toFloat())
        visibilityChangedProgress =
            progress(millisSinceVisibilityChange.toFloat(), 0f, options.stateChangeDurationMillis.toFloat())

        val isStateTransitionExpired: Boolean = millisSinceStateChange > options.stateChangeDurationMillis
        val isVisibilityTransitionExpired: Boolean = millisSinceVisibilityChange > options.stateChangeDurationMillis

        var newState: GlyphState? = null
        var newVisibility: GlyphVisibility? = null

        when (state) {
            GlyphState.Active -> {
                // Active state decays after period of inactivity
                if (millisSinceStateChange > options.activeStateDurationMillis) {
                    newState = GlyphState.Deactivating
                }
            }

            GlyphState.Activating -> {
                if (isStateTransitionExpired) {
                    newState = GlyphState.Active
                }
            }

            GlyphState.Deactivating -> {
                if (isStateTransitionExpired) {
                    newState = GlyphState.Inactive
                }
            }

            else -> {}
        }

        if (isVisibilityTransitionExpired) {
            when (visibility) {
                GlyphVisibility.Appearing -> newVisibility = GlyphVisibility.Visible
                GlyphVisibility.Disappearing -> newVisibility = GlyphVisibility.Hidden
                else -> {}
            }
        }

        when {
            newState != null && newVisibility != null -> setState(
                newState,
                newVisibility,
                force = true,
                currentTimeMillis = currentTimeMillis
            )

            newState != null -> setState(newState, force = true, currentTimeMillis = currentTimeMillis)
            newVisibility != null -> setState(newVisibility, force = true, currentTimeMillis = currentTimeMillis)
        }
    }

    private fun setActive(currentTimeMillis: Long) {
        when (state) {
            GlyphState.Active -> setState(GlyphState.Active, force = true, currentTimeMillis = currentTimeMillis)

            GlyphState.Inactive, GlyphState.Deactivating -> setState(
                GlyphState.Activating,
                force = true,
                currentTimeMillis = currentTimeMillis
            )

            GlyphState.Activating -> debug(false) {
                debug("setActive has no effect when state == $state")
            }
        }
    }

    private fun setInactive(currentTimeMillis: Long) {
        when (state) {
            GlyphState.Active, GlyphState.Activating -> setState(
                GlyphState.Deactivating,
                force = true,
                currentTimeMillis = currentTimeMillis
            )

            else -> debug(false) {
                debug("setInactive has no effect when state == $state")
            }
        }
    }

    private fun appear(currentTimeMillis: Long) {
        when (visibility) {
            GlyphVisibility.Hidden, GlyphVisibility.Disappearing -> {
                setState(GlyphVisibility.Appearing, force = true, currentTimeMillis = currentTimeMillis)
            }

            else -> debug(false) {
                debug("appear() has no effect when visibility == $visibility")
            }
        }
    }

    private fun disappear(currentTimeMillis: Long) {
        when (visibility) {
            GlyphVisibility.Visible, GlyphVisibility.Appearing -> {
                setState(GlyphVisibility.Disappearing, force = true, currentTimeMillis = currentTimeMillis)
            }

            else -> debug(false) {
                debug("disappear() has no effect when state == $state")
            }
        }
    }
}

abstract class BaseClockGlyph<P : Paints>(
    role: GlyphRole,
    scale: Float = 1f,
    lock: GlyphState? = null,
    currentTimeMillis: Long = getCurrentTimeMillis()
) : BaseGlyph<P>(role, scale, lock, currentTimeMillis), ClockGlyph<P> {
    var clockKey: ClockGlyph.Key = ClockGlyph.Key.Empty
        private set
    override var key: String
        get() = clockKey.key
        set(value) {
            clockKey = ClockGlyph.Key.entries.firstOrNull { it.key == value }
                ?: throw IllegalStateException("Unknown key: $value")
            super.key = value
        }


    override fun draw(canvas: Canvas, glyphProgress: Float, paints: P, renderGlyph: RenderGlyph?) {
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
