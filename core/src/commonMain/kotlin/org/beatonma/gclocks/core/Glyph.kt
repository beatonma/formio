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

private typealias OnStateChange = (newState: GlyphState) -> Unit

interface GlyphCompanion {
    val maxSize: Size<Float>
    val aspectRatio: Float
}

interface Glyph {
    val companion: GlyphCompanion
    val maxSize: Size<Float> get() = companion.maxSize
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
        with(canvas) {
            when (key) {
                "0", "0_1" -> drawZeroOne(glyphProgress, paints)
                "1", "1_2" -> drawOneTwo(glyphProgress, paints)
                "2", "2_3" -> drawTwoThree(glyphProgress, paints)
                "3", "3_4" -> drawThreeFour(glyphProgress, paints)
                "4", "4_5" -> drawFourFive(glyphProgress, paints)
                "5", "5_6" -> drawFiveSix(glyphProgress, paints)
                "6", "6_7" -> drawSixSeven(glyphProgress, paints)
                "7", "7_8" -> drawSevenEight(glyphProgress, paints)
                "8", "8_9" -> drawEightNine(glyphProgress, paints)
                "9", "9_0" -> drawNineZero(glyphProgress, paints)
                "1_0" -> drawOneZero(glyphProgress, paints)
                "2_0" -> drawTwoZero(glyphProgress, paints)
                "3_0" -> drawThreeZero(glyphProgress, paints)
                "5_0" -> drawFiveZero(glyphProgress, paints)
                "1_ " -> drawOneEmpty(glyphProgress, paints)
                "2_ " -> drawTwoEmpty(glyphProgress, paints)
                " _1" -> drawEmptyOne(glyphProgress, paints)
                ":" -> drawSeparator(glyphProgress, paints)
                " " -> drawSpace(glyphProgress, paints)
                "_" -> drawUnderscore(glyphProgress, paints)
                "#" -> drawHash(glyphProgress, paints)
                else -> throw Exception("Unhandled glyph key '${key}'")
            }
        }
    }

    abstract fun Canvas<*>.drawZeroOne(glyphProgress: Float, paints: Paints)
    abstract fun Canvas<*>.drawOneTwo(glyphProgress: Float, paints: Paints)
    abstract fun Canvas<*>.drawTwoThree(glyphProgress: Float, paints: Paints)
    abstract fun Canvas<*>.drawThreeFour(glyphProgress: Float, paints: Paints)
    abstract fun Canvas<*>.drawFourFive(glyphProgress: Float, paints: Paints)
    abstract fun Canvas<*>.drawFiveSix(glyphProgress: Float, paints: Paints)
    abstract fun Canvas<*>.drawSixSeven(glyphProgress: Float, paints: Paints)
    abstract fun Canvas<*>.drawSevenEight(glyphProgress: Float, paints: Paints)
    abstract fun Canvas<*>.drawEightNine(glyphProgress: Float, paints: Paints)
    abstract fun Canvas<*>.drawNineZero(glyphProgress: Float, paints: Paints)
    abstract fun Canvas<*>.drawOneZero(glyphProgress: Float, paints: Paints)
    abstract fun Canvas<*>.drawTwoZero(glyphProgress: Float, paints: Paints)
    abstract fun Canvas<*>.drawThreeZero(glyphProgress: Float, paints: Paints)
    abstract fun Canvas<*>.drawFiveZero(glyphProgress: Float, paints: Paints)
    abstract fun Canvas<*>.drawOneEmpty(glyphProgress: Float, paints: Paints)
    abstract fun Canvas<*>.drawTwoEmpty(glyphProgress: Float, paints: Paints)
    abstract fun Canvas<*>.drawEmptyOne(glyphProgress: Float, paints: Paints)
    abstract fun Canvas<*>.drawSeparator(glyphProgress: Float, paints: Paints)
    abstract fun Canvas<*>.drawSpace(glyphProgress: Float, paints: Paints)
    abstract fun Canvas<*>.drawUnderscore(glyphProgress: Float, paints: Paints)
    abstract fun Canvas<*>.drawHash(glyphProgress: Float, paints: Paints)
}