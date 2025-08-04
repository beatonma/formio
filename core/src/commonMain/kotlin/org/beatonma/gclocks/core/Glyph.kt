package org.beatonma.gclocks.core

import org.beatonma.gclocks.core.geometry.Size
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

//class GlyphLayoutInfo(
//    val height: Float,
//    val maxWidth: Float,
//)

private typealias OnStateChange = (newState: GlyphState) -> Unit

interface Glyph {
    val maxSize: Size<Float>
    val role: GlyphRole
    var key: String
    val state: GlyphState
    var lock: GlyphStateLock
    var scale: Float
    var onStateChange: OnStateChange?

    fun draw(canvas: Canvas<*>, glyphProgress: Float, paints: Paints)
    fun getWidthAtProgress(glyphProgress: Float): Float

    val canonicalStartGlyph: Char
    val canonicalEndGlyph: Char
}


private typealias GlyphStateSetter = (queue: Boolean) -> Unit

abstract class BaseGlyph : Glyph {
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

    override var key: String = " "
        set(value) {
            field = value
            canonicalStartGlyph = value.first()
            canonicalEndGlyph = value.last()
        }

    final override var canonicalStartGlyph: Char = key.first()
        private set
    final override var canonicalEndGlyph: Char = key.first()
        private set

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

    override fun toString(): String {
        return key
    }
}


abstract class BaseClockGlyph(
    override val role: GlyphRole,
    override var scale: Float = 1f,
) : BaseGlyph() {
    override fun draw(canvas: Canvas<*>, glyphProgress: Float, paints: Paints) {
        when (key) {
            "0", "0_1" -> ::drawZeroOne
            "1", "1_2" -> ::drawOneTwo
            "2", "2_3" -> ::drawTwoThree
            "3", "3_4" -> ::drawThreeFour
            "4", "4_5" -> ::drawFourFive
            "5", "5_6" -> ::drawFiveSix
            "6", "6_7" -> ::drawSixSeven
            "7", "7_8" -> ::drawSevenEight
            "8", "8_9" -> ::drawEightNine
            "9", "9_0" -> ::drawNineZero
            "1_0" -> ::drawOneZero
            "2_0" -> ::drawTwoZero
            "3_0" -> ::drawThreeZero
            "5_0" -> ::drawFiveZero
            "1_ " -> ::drawOneEmpty
            "2_ " -> ::drawTwoEmpty
            " _1" -> ::drawEmptyOne
            ":" -> ::drawSeparator
            " " -> ::drawSpace
            "_" -> ::drawUnderscore
            "#" -> ::drawHash
            else -> throw Exception("Unhandled glyph key '${key}'")
        }(canvas, glyphProgress, paints)
    }

    abstract fun drawZeroOne(canvas: Canvas<*>, glyphProgress: Float, paints: Paints)
    abstract fun drawOneTwo(canvas: Canvas<*>, glyphProgress: Float, paints: Paints)
    abstract fun drawTwoThree(canvas: Canvas<*>, glyphProgress: Float, paints: Paints)
    abstract fun drawThreeFour(canvas: Canvas<*>, glyphProgress: Float, paints: Paints)
    abstract fun drawFourFive(canvas: Canvas<*>, glyphProgress: Float, paints: Paints)
    abstract fun drawFiveSix(canvas: Canvas<*>, glyphProgress: Float, paints: Paints)
    abstract fun drawSixSeven(canvas: Canvas<*>, glyphProgress: Float, paints: Paints)
    abstract fun drawSevenEight(canvas: Canvas<*>, glyphProgress: Float, paints: Paints)
    abstract fun drawEightNine(canvas: Canvas<*>, glyphProgress: Float, paints: Paints)
    abstract fun drawNineZero(canvas: Canvas<*>, glyphProgress: Float, paints: Paints)
    abstract fun drawOneZero(canvas: Canvas<*>, glyphProgress: Float, paints: Paints)
    abstract fun drawTwoZero(canvas: Canvas<*>, glyphProgress: Float, paints: Paints)
    abstract fun drawThreeZero(canvas: Canvas<*>, glyphProgress: Float, paints: Paints)
    abstract fun drawFiveZero(canvas: Canvas<*>, glyphProgress: Float, paints: Paints)
    abstract fun drawOneEmpty(canvas: Canvas<*>, glyphProgress: Float, paints: Paints)
    abstract fun drawTwoEmpty(canvas: Canvas<*>, glyphProgress: Float, paints: Paints)
    abstract fun drawEmptyOne(canvas: Canvas<*>, glyphProgress: Float, paints: Paints)
    abstract fun drawSeparator(canvas: Canvas<*>, glyphProgress: Float, paints: Paints)
    abstract fun drawSpace(canvas: Canvas<*>, glyphProgress: Float, paints: Paints)
    abstract fun drawUnderscore(canvas: Canvas<*>, glyphProgress: Float, paints: Paints)
    abstract fun drawHash(canvas: Canvas<*>, glyphProgress: Float, paints: Paints)
}