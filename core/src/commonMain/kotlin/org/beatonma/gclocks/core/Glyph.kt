package org.beatonma.gclocks.core

import org.beatonma.gclocks.core.geometry.NativeSize
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.Canvas
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
    ;

    val isSeparator: Boolean get() = this == SeparatorMinutesSeconds || this == SeparatorHoursMinutes
}

private typealias OnStateChange = (newState: GlyphState) -> Unit

interface GlyphCompanion {
    val maxSize: NativeSize
    val aspectRatio: Float
}

interface Glyph {
    val companion: GlyphCompanion
    val maxSize: NativeSize get() = companion.maxSize
    val role: GlyphRole
    var key: String
    val state: GlyphState
    var lock: GlyphStateLock
    var scale: Float
    var onStateChange: OnStateChange?

    fun draw(canvas: Canvas, glyphProgress: Float, paints: Paints)
    fun getWidthAtProgress(glyphProgress: Float): Float
    fun setState(newState: GlyphState, force: Boolean = false)

    val canonicalStartGlyph: Char
    val canonicalEndGlyph: Char
}


abstract class BaseGlyph : Glyph {
    final override var state: GlyphState = GlyphState.Appearing
        private set(value) {
            stateChangedAt = getCurrentTimeMillis()

            field = value
            onStateChange?.invoke(value)
        }

    override var lock = GlyphStateLock.None
    override var onStateChange: OnStateChange? = null

    var stateChangedAt: Long = getCurrentTimeMillis()

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

    override fun toString(): String {
        return key
    }

    override fun setState(newState: GlyphState, force: Boolean) {
        if (force) {
            state = newState
            return
        }
        if (lock != GlyphStateLock.None) return

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

    fun tickState(options: GlyphOptions) {
        val now = getCurrentTimeMillis()
        val millisSinceChange = now - stateChangedAt
        val transitionStateExpired = millisSinceChange > options.stateChangeDurationMillis

        when (state) {
            GlyphState.Inactive, GlyphState.Disappeared -> {}

            GlyphState.Active -> {
                if (millisSinceChange > options.activeStateDurationMillis) {
                    state = GlyphState.Deactivating
                }
            }

            GlyphState.Appearing, GlyphState.Activating -> {
                if (transitionStateExpired) {
                    state = GlyphState.Active
                }
            }

            GlyphState.Deactivating -> {
                if (transitionStateExpired) {
                    state = GlyphState.Inactive
                }
            }

            GlyphState.Disappearing, GlyphState.DisappearingFromActive, GlyphState.DisappearingFromInactive -> {
                if (transitionStateExpired) {
                    state = GlyphState.Disappeared
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

            else -> {}
        }
    }

    private fun setInactive() {
        when (state) {
            GlyphState.Active, GlyphState.Activating -> {
                state = GlyphState.Deactivating
            }

            else -> {}
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

            else -> {}
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

            else -> {}
        }
    }
}


abstract class BaseClockGlyph(
    override val role: GlyphRole,
    override var scale: Float = 1f,
) : BaseGlyph() {
    override fun draw(canvas: Canvas, glyphProgress: Float, paints: Paints) {
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
                " ", "_" -> drawSpace(glyphProgress, paints)
                "#" -> drawHash(glyphProgress, paints)
                else -> throw Exception("Unhandled glyph key '${key}'")
            }
        }
    }

    fun Canvas.drawNotImplemented(glyphProgress: Float, paints: Paints) {
        val (width, height) = companion.maxSize
        drawLine(Color.Red, 0f, 0f, width, height)
        drawLine(Color.Red, width, 0f, 0f, height)
    }

    abstract fun Canvas.drawZeroOne(glyphProgress: Float, paints: Paints)
    abstract fun Canvas.drawOneTwo(glyphProgress: Float, paints: Paints)
    abstract fun Canvas.drawTwoThree(glyphProgress: Float, paints: Paints)
    abstract fun Canvas.drawThreeFour(glyphProgress: Float, paints: Paints)
    abstract fun Canvas.drawFourFive(glyphProgress: Float, paints: Paints)
    abstract fun Canvas.drawFiveSix(glyphProgress: Float, paints: Paints)
    abstract fun Canvas.drawSixSeven(glyphProgress: Float, paints: Paints)
    abstract fun Canvas.drawSevenEight(glyphProgress: Float, paints: Paints)
    abstract fun Canvas.drawEightNine(glyphProgress: Float, paints: Paints)
    abstract fun Canvas.drawNineZero(glyphProgress: Float, paints: Paints)
    abstract fun Canvas.drawOneZero(glyphProgress: Float, paints: Paints)
    abstract fun Canvas.drawTwoZero(glyphProgress: Float, paints: Paints)
    abstract fun Canvas.drawThreeZero(glyphProgress: Float, paints: Paints)
    abstract fun Canvas.drawFiveZero(glyphProgress: Float, paints: Paints)
    abstract fun Canvas.drawOneEmpty(glyphProgress: Float, paints: Paints)
    abstract fun Canvas.drawTwoEmpty(glyphProgress: Float, paints: Paints)
    abstract fun Canvas.drawEmptyOne(glyphProgress: Float, paints: Paints)
    abstract fun Canvas.drawSeparator(glyphProgress: Float, paints: Paints)
    abstract fun Canvas.drawSpace(glyphProgress: Float, paints: Paints)
    abstract fun Canvas.drawHash(glyphProgress: Float, paints: Paints)
}


interface GlyphRenderer<G : Glyph> {
    fun draw(glyph: G, canvas: Canvas, glyphProgress: Float, paints: Paints) {
        glyph.draw(canvas, glyphProgress, paints)

        debug(false) {
            canvas.drawText(glyph.state.name)
        }
    }

    companion object {
        fun <G : Glyph> Default() = object : GlyphRenderer<G> {}
    }
}
