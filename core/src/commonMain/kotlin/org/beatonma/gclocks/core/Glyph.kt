package org.beatonma.gclocks.core

import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Paints

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
}

enum class GlyphStateLock {
    None,
    Active,
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
}

interface GlyphLayoutInfo {
    val height: Int
    val width: Int?
    val isMonospace: Boolean
}

private typealias OnStateChange = (newState: GlyphState) -> Unit

interface Glyph<T : GlyphKey> {
    val layoutInfo: GlyphLayoutInfo
    val role: GlyphRole
    val key: T
    val state: GlyphState
    var lock: GlyphStateLock
    var scale: Float
    var onStateChange: OnStateChange?

    fun draw(canvas: Canvas, progress: Float, paints: Paints)
    fun getWidthAtProgress(progress: Float): Float

    val canonicalStartGlyph: Char
    val canonicalEndGlyph: Char
}


private typealias GlyphStateSetter = (queue: Boolean) -> Unit

abstract class BaseGlyph<T : GlyphKey> : Glyph<T> {
    final override var state: GlyphState = GlyphState.Appearing
        private set(value) {
            field = value
            onStateChange?.invoke(value)
        }
    private var queuedState: GlyphState? = null

    override var lock = GlyphStateLock.None
    override var onStateChange: OnStateChange? = null

    var stateAnimationTime = 0
    var deactivationStartedTime = 0

    override val canonicalStartGlyph: Char
        get() = key.canonicalStartGlyph
    override val canonicalEndGlyph: Char
        get() = key.canonicalEndGlyph

    fun setState(newState: GlyphState, force: Boolean = false, queue: Boolean = false) {
        if (force) {
            state = newState
            return
        }
        if (lock != GlyphStateLock.None) return

        val setStateHandler: GlyphStateSetter = when (newState) {
            GlyphState.Activating -> ::setActivating
            GlyphState.Deactivating -> ::setDeactivating
            GlyphState.Inactive -> ::setInactive
            GlyphState.Active -> ::setActive
            GlyphState.Appearing -> ::setAppearing
            GlyphState.Disappeared -> ::setDisappeared
            GlyphState.Disappearing,
            GlyphState.DisappearingFromActive,
            GlyphState.DisappearingFromInactive,
                -> ::setDisappearing
        }
        return setStateHandler(queue)
    }

    private fun setActivating(queue: Boolean) {
        if (state == GlyphState.Inactive) {
            state = GlyphState.Activating
        } else if (queue) {
            queuedState = GlyphState.Activating
        }
    }

    private fun setDeactivating(queue: Boolean) {
        if (state == GlyphState.Active) {
            state = GlyphState.Deactivating
        } else if (queue) {
            queuedState = GlyphState.Deactivating
        }
    }

    private fun setActive(queue: Boolean) {
        if (state in arrayOf(GlyphState.Activating, GlyphState.Appearing)) {
            state = GlyphState.Active
        } else {
            setActivating(queue)
        }
    }

    private fun setInactive(queue: Boolean) {
        if (state == GlyphState.Deactivating) {
            state = GlyphState.Inactive
        } else {
            setDeactivating(queue)
        }
    }

    private fun setAppearing(queue: Boolean) {
        if (state == GlyphState.Disappeared) {
            state = GlyphState.Appearing
        } else if (queue) {
            queuedState = GlyphState.Appearing
        }
    }

    private fun setDisappearing(queue: Boolean) {
        if (state == GlyphState.Active) {
            state = GlyphState.DisappearingFromActive
        } else if (state == GlyphState.Inactive) {
            state = GlyphState.DisappearingFromInactive
        } else if (queue) {
            queuedState = GlyphState.Disappearing
        }
    }

    private fun setDisappeared(queue: Boolean) {
        if (state in arrayOf(
                GlyphState.Disappearing,
                GlyphState.DisappearingFromActive,
                GlyphState.DisappearingFromInactive
            )
        ) {
            state = GlyphState.Disappeared
        } else if (queue) {
            queuedState = GlyphState.Disappearing
        }
    }
}


interface GlyphKey {
    val canonicalStartGlyph: Char
    val canonicalEndGlyph: Char
}

enum class ClockGlyphKey(private val key: String) : GlyphKey {
    Zero("0"),
    ZeroOne("0_1"),
    One("1"),
    OneTwo("1_2"),
    Two("2"),
    TwoThree("2_3"),
    Three("3"),
    ThreeFour("3_4"),
    Four("4"),
    FourFive("4_5"),
    Six("6"),
    SixSeven("6_7"),
    Seven("7"),
    SevenEight("7_8"),
    Eight("8"),
    EightNine("8_9"),
    Nine("9"),
    NineZero("9_0"),
    OneZero("1_0"),
    TwoZero("2_0"),
    ThreeZero("3_0"),
    FiveZero("5_0"),
    OneEmpty("1_ "),
    TwoEmpty("2_ "),
    EmptyOne(" _1"),
    Separator(":"),
    Space(" "),
    Underscore("_"),
    ;

    override val canonicalStartGlyph: Char
        get() = key.first()
    override val canonicalEndGlyph: Char
        get() = key.last()
}

abstract class BaseClockGlyph : BaseGlyph<ClockGlyphKey>() {
    override fun draw(canvas: Canvas, progress: Float, paints: Paints) {
        val drawFunc: (canvas: Canvas, progress: Float, paints: Paints) -> Unit =
            when (key) {
                ClockGlyphKey.Zero -> ::drawZero
                ClockGlyphKey.ZeroOne -> ::drawZeroOne
                ClockGlyphKey.One -> ::drawOne
                ClockGlyphKey.OneTwo -> ::drawOneTwo
                ClockGlyphKey.Two -> ::drawTwo
                ClockGlyphKey.TwoThree -> ::drawTwoThree
                ClockGlyphKey.Three -> ::drawThree
                ClockGlyphKey.ThreeFour -> ::drawThreeFour
                ClockGlyphKey.Four -> ::drawFour
                ClockGlyphKey.FourFive -> ::drawFourFive
                ClockGlyphKey.Six -> ::drawSix
                ClockGlyphKey.SixSeven -> ::drawSixSeven
                ClockGlyphKey.Seven -> ::drawSeven
                ClockGlyphKey.SevenEight -> ::drawSevenEight
                ClockGlyphKey.Eight -> ::drawEight
                ClockGlyphKey.EightNine -> ::drawEightNine
                ClockGlyphKey.Nine -> ::drawNine
                ClockGlyphKey.NineZero -> ::drawNineZero
                ClockGlyphKey.OneZero -> ::drawOneZero
                ClockGlyphKey.TwoZero -> ::drawTwoZero
                ClockGlyphKey.ThreeZero -> ::drawThreeZero
                ClockGlyphKey.FiveZero -> ::drawFiveZero
                ClockGlyphKey.OneEmpty -> ::drawOneEmpty
                ClockGlyphKey.TwoEmpty -> ::drawTwoEmpty
                ClockGlyphKey.EmptyOne -> ::drawEmptyOne
                ClockGlyphKey.Separator -> ::drawSeparator
                ClockGlyphKey.Space -> ::drawSpace
                ClockGlyphKey.Underscore -> ::drawUnderscore
            }
        drawFunc(canvas, progress, paints)
    }

    abstract fun drawZero(canvas: Canvas, progress: Float, paints: Paints)
    abstract fun drawZeroOne(canvas: Canvas, progress: Float, paints: Paints)
    abstract fun drawOne(canvas: Canvas, progress: Float, paints: Paints)
    abstract fun drawOneTwo(canvas: Canvas, progress: Float, paints: Paints)
    abstract fun drawTwo(canvas: Canvas, progress: Float, paints: Paints)
    abstract fun drawTwoThree(canvas: Canvas, progress: Float, paints: Paints)
    abstract fun drawThree(canvas: Canvas, progress: Float, paints: Paints)
    abstract fun drawThreeFour(canvas: Canvas, progress: Float, paints: Paints)
    abstract fun drawFour(canvas: Canvas, progress: Float, paints: Paints)
    abstract fun drawFourFive(canvas: Canvas, progress: Float, paints: Paints)
    abstract fun drawSix(canvas: Canvas, progress: Float, paints: Paints)
    abstract fun drawSixSeven(canvas: Canvas, progress: Float, paints: Paints)
    abstract fun drawSeven(canvas: Canvas, progress: Float, paints: Paints)
    abstract fun drawSevenEight(canvas: Canvas, progress: Float, paints: Paints)
    abstract fun drawEight(canvas: Canvas, progress: Float, paints: Paints)
    abstract fun drawEightNine(canvas: Canvas, progress: Float, paints: Paints)
    abstract fun drawNine(canvas: Canvas, progress: Float, paints: Paints)
    abstract fun drawNineZero(canvas: Canvas, progress: Float, paints: Paints)
    abstract fun drawOneZero(canvas: Canvas, progress: Float, paints: Paints)
    abstract fun drawTwoZero(canvas: Canvas, progress: Float, paints: Paints)
    abstract fun drawThreeZero(canvas: Canvas, progress: Float, paints: Paints)
    abstract fun drawFiveZero(canvas: Canvas, progress: Float, paints: Paints)
    abstract fun drawOneEmpty(canvas: Canvas, progress: Float, paints: Paints)
    abstract fun drawTwoEmpty(canvas: Canvas, progress: Float, paints: Paints)
    abstract fun drawEmptyOne(canvas: Canvas, progress: Float, paints: Paints)
    abstract fun drawSeparator(canvas: Canvas, progress: Float, paints: Paints)
    abstract fun drawSpace(canvas: Canvas, progress: Float, paints: Paints)
    abstract fun drawUnderscore(canvas: Canvas, progress: Float, paints: Paints)
}