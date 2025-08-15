package org.beatonma.gclocks.core

import org.beatonma.gclocks.core.geometry.Size
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.GenericCanvas
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.options.Options
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

    fun draw(canvas: GenericCanvas, glyphProgress: Float, paints: Paints)
    fun getWidthAtProgress(glyphProgress: Float): Float

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

    fun setState(newState: GlyphState, force: Boolean = false) {
        if (force) {
            state = newState
            return
        }
        if (lock != GlyphStateLock.None) return

        return when (newState) {
            GlyphState.Activating -> setActivating()
            GlyphState.Active -> setActive()
            GlyphState.Deactivating -> setDeactivating()
            GlyphState.Inactive -> setInactive()
            GlyphState.Appearing -> setAppearing()
            GlyphState.Disappeared -> setDisappeared()
            GlyphState.Disappearing,
            GlyphState.DisappearingFromActive,
            GlyphState.DisappearingFromInactive,
                -> setDisappearing()
        }
    }

    fun tickState(options: Options) {
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
                    setState(GlyphState.Active)
                }
            }

            GlyphState.Deactivating -> {
                if (transitionStateExpired) {
                    setState(GlyphState.Inactive)
                }
            }

            GlyphState.Disappearing, GlyphState.DisappearingFromActive, GlyphState.DisappearingFromInactive -> {
                if (transitionStateExpired) {
                    setState(GlyphState.Disappeared)
                }
            }
        }
    }

    private fun setActivating() {
        when (state) {
            GlyphState.Active -> state = GlyphState.Active
            GlyphState.Inactive -> state = GlyphState.Activating
            else -> {}
        }
    }

    private fun setDeactivating() {
        if (state == GlyphState.Active) {
            state = GlyphState.Deactivating
        }
    }

    private fun setActive() {
        when (state) {
            GlyphState.Active -> {
                stateChangedAt = getCurrentTimeMillis()
            }

            GlyphState.Activating, GlyphState.Appearing -> {
                state = GlyphState.Active
            }

            else -> state = GlyphState.Activating
        }
    }

    private fun setInactive() {
        if (state == GlyphState.Deactivating) {
            state = GlyphState.Inactive
        }
    }

    private fun setAppearing() {
        if (state == GlyphState.Disappeared) {
            state = GlyphState.Appearing
        }
    }

    private fun setDisappearing() {
        when (state) {
            GlyphState.Active -> {
                state = GlyphState.DisappearingFromActive
            }

            GlyphState.Inactive -> {
                state = GlyphState.DisappearingFromInactive
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
    override fun draw(canvas: GenericCanvas, glyphProgress: Float, paints: Paints) {
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

    fun GenericCanvas.drawNotImplemented(glyphProgress: Float, paints: Paints) {
        val (width, height) = companion.maxSize
        drawLine(Color.Red, 0f, 0f, width, height)
        drawLine(Color.Red, width, 0f, 0f, height)
    }

    abstract fun GenericCanvas.drawZeroOne(glyphProgress: Float, paints: Paints)
    abstract fun GenericCanvas.drawOneTwo(glyphProgress: Float, paints: Paints)
    abstract fun GenericCanvas.drawTwoThree(glyphProgress: Float, paints: Paints)
    abstract fun GenericCanvas.drawThreeFour(glyphProgress: Float, paints: Paints)
    abstract fun GenericCanvas.drawFourFive(glyphProgress: Float, paints: Paints)
    abstract fun GenericCanvas.drawFiveSix(glyphProgress: Float, paints: Paints)
    abstract fun GenericCanvas.drawSixSeven(glyphProgress: Float, paints: Paints)
    abstract fun GenericCanvas.drawSevenEight(glyphProgress: Float, paints: Paints)
    abstract fun GenericCanvas.drawEightNine(glyphProgress: Float, paints: Paints)
    abstract fun GenericCanvas.drawNineZero(glyphProgress: Float, paints: Paints)
    abstract fun GenericCanvas.drawOneZero(glyphProgress: Float, paints: Paints)
    abstract fun GenericCanvas.drawTwoZero(glyphProgress: Float, paints: Paints)
    abstract fun GenericCanvas.drawThreeZero(glyphProgress: Float, paints: Paints)
    abstract fun GenericCanvas.drawFiveZero(glyphProgress: Float, paints: Paints)
    abstract fun GenericCanvas.drawOneEmpty(glyphProgress: Float, paints: Paints)
    abstract fun GenericCanvas.drawTwoEmpty(glyphProgress: Float, paints: Paints)
    abstract fun GenericCanvas.drawEmptyOne(glyphProgress: Float, paints: Paints)
    abstract fun GenericCanvas.drawSeparator(glyphProgress: Float, paints: Paints)
    abstract fun GenericCanvas.drawSpace(glyphProgress: Float, paints: Paints)
    abstract fun GenericCanvas.drawHash(glyphProgress: Float, paints: Paints)
}


interface GlyphRenderer<G : Glyph> {
    fun draw(glyph: G, canvas: GenericCanvas, glyphProgress: Float, paints: Paints) {
        glyph.draw(canvas, glyphProgress, paints)
        val color = when (glyph.state) {
            GlyphState.Active -> Color.Green
            GlyphState.Activating -> Color.Yellow
            GlyphState.Deactivating -> Color.Orange
            GlyphState.Inactive -> Color.Red
            GlyphState.Appearing -> Color.Cyan
            else -> Color.Magenta
        }
        canvas.drawPoint(10f, 10f, color, alpha = .7f)
        canvas.drawText(glyph.state.name)
    }

    companion object {
        fun <G : Glyph> Default() = object : GlyphRenderer<G> {}
    }
}
