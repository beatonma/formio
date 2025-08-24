package org.beatonma.gclocks.core

import org.beatonma.gclocks.core.geometry.NativeSize
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.options.GlyphOptions
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
}


abstract class BaseGlyph<P : Paints>(
    override val role: GlyphRole,
    override val scale: Float = 1f,
    override val lock: GlyphState? = null,
) : Glyph<P> {
    final override var state: GlyphState = GlyphState.Appearing
        private set(value) {
            stateChangedAt = getCurrentTimeMillis()

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

    fun tickState(options: GlyphOptions) {
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

abstract class BaseClockGlyph<P : Paints>(
    role: GlyphRole,
    scale: Float = 1f,
    lock: GlyphState? = null,
) : BaseGlyph<P>(role, scale, lock) {
    override fun draw(
        canvas: Canvas,
        glyphProgress: Float,
        paints: P,
        renderGlyph: RenderGlyph<P>?,
    ) {
        with(canvas) {
            when (key) {
                "0", "0_1" -> drawZeroOne(glyphProgress, paints, renderGlyph)
                "1", "1_2" -> drawOneTwo(glyphProgress, paints, renderGlyph)
                "2", "2_3" -> drawTwoThree(glyphProgress, paints, renderGlyph)
                "3", "3_4" -> drawThreeFour(glyphProgress, paints, renderGlyph)
                "4", "4_5" -> drawFourFive(glyphProgress, paints, renderGlyph)
                "5", "5_6" -> drawFiveSix(glyphProgress, paints, renderGlyph)
                "6", "6_7" -> drawSixSeven(glyphProgress, paints, renderGlyph)
                "7", "7_8" -> drawSevenEight(glyphProgress, paints, renderGlyph)
                "8", "8_9" -> drawEightNine(glyphProgress, paints, renderGlyph)
                "9", "9_0" -> drawNineZero(glyphProgress, paints, renderGlyph)
                "1_0" -> drawOneZero(glyphProgress, paints, renderGlyph)
                "2_0" -> drawTwoZero(glyphProgress, paints, renderGlyph)
                "3_0" -> drawThreeZero(glyphProgress, paints, renderGlyph)
                "5_0" -> drawFiveZero(glyphProgress, paints, renderGlyph)
                "1_ " -> drawOneEmpty(glyphProgress, paints, renderGlyph)
                "2_ " -> drawTwoEmpty(glyphProgress, paints, renderGlyph)
                " _1" -> drawEmptyOne(glyphProgress, paints, renderGlyph)
                ":" -> drawSeparator(glyphProgress, paints, renderGlyph)
                " ", "_" -> drawSpace(glyphProgress, paints, renderGlyph)
                "#" -> drawHash(glyphProgress, paints, renderGlyph)
                else -> throw Exception("Unhandled glyph key '${key}'")
            }
        }
    }

    fun Canvas.drawNotImplemented(glyphProgress: Float, paints: P) {
        val (width, height) = companion.maxSize
        drawLine(Color.Red, 0f, 0f, width, height)
        drawLine(Color.Red, width, 0f, 0f, height)
    }

    abstract fun Canvas.drawZeroOne(glyphProgress: Float, paints: P, renderGlyph: RenderGlyph<P>?)
    abstract fun Canvas.drawOneTwo(glyphProgress: Float, paints: P, renderGlyph: RenderGlyph<P>?)
    abstract fun Canvas.drawTwoThree(glyphProgress: Float, paints: P, renderGlyph: RenderGlyph<P>?)
    abstract fun Canvas.drawThreeFour(glyphProgress: Float, paints: P, renderGlyph: RenderGlyph<P>?)
    abstract fun Canvas.drawFourFive(glyphProgress: Float, paints: P, renderGlyph: RenderGlyph<P>?)
    abstract fun Canvas.drawFiveSix(glyphProgress: Float, paints: P, renderGlyph: RenderGlyph<P>?)
    abstract fun Canvas.drawSixSeven(glyphProgress: Float, paints: P, renderGlyph: RenderGlyph<P>?)
    abstract fun Canvas.drawSevenEight(
        glyphProgress: Float,
        paints: P,
        renderGlyph: RenderGlyph<P>?,
    )

    abstract fun Canvas.drawEightNine(glyphProgress: Float, paints: P, renderGlyph: RenderGlyph<P>?)
    abstract fun Canvas.drawNineZero(glyphProgress: Float, paints: P, renderGlyph: RenderGlyph<P>?)
    abstract fun Canvas.drawOneZero(glyphProgress: Float, paints: P, renderGlyph: RenderGlyph<P>?)
    abstract fun Canvas.drawTwoZero(glyphProgress: Float, paints: P, renderGlyph: RenderGlyph<P>?)
    abstract fun Canvas.drawThreeZero(glyphProgress: Float, paints: P, renderGlyph: RenderGlyph<P>?)
    abstract fun Canvas.drawFiveZero(glyphProgress: Float, paints: P, renderGlyph: RenderGlyph<P>?)
    abstract fun Canvas.drawOneEmpty(glyphProgress: Float, paints: P, renderGlyph: RenderGlyph<P>?)
    abstract fun Canvas.drawTwoEmpty(glyphProgress: Float, paints: P, renderGlyph: RenderGlyph<P>?)
    abstract fun Canvas.drawEmptyOne(glyphProgress: Float, paints: P, renderGlyph: RenderGlyph<P>?)
    abstract fun Canvas.drawSeparator(glyphProgress: Float, paints: P, renderGlyph: RenderGlyph<P>?)
    abstract fun Canvas.drawSpace(glyphProgress: Float, paints: P, renderGlyph: RenderGlyph<P>?)
    abstract fun Canvas.drawHash(glyphProgress: Float, paints: P, renderGlyph: RenderGlyph<P>?)
}


fun interface GlyphRenderer<P : Paints, G : Glyph<P>> {
    fun draw(glyph: G, canvas: Canvas, paints: P)
}
