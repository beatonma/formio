package org.beatonma.gclocks.core.glyph

import org.beatonma.gclocks.core.geometry.NativeSize
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.options.GlyphOptions
import org.beatonma.gclocks.core.util.getCurrentTimeMillis

enum class GlyphVisibility {
    Visible,
    Appearing,
    Disappearing,
    Hidden,
    ;

    val isTransitional: Boolean get() = this == Appearing || this == Disappearing
}

enum class GlyphState {
    Active,
    Activating,
    Deactivating,
    Inactive,
    ;

    val isTransitional: Boolean get() = this == Activating || this == Deactivating
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

interface GlyphRenderer<P : Paints, G : Glyph<P>> {
    fun update(currentTimeMillis: Long) {}
    fun draw(glyph: G, canvas: Canvas, paints: P)
}

interface Glyph<P : Paints> {
    val companion: GlyphCompanion
    val maxSize: NativeSize get() = companion.maxSize
    val role: GlyphRole
    val state: GlyphState
    val lock: GlyphState?
    val visibility: GlyphVisibility
    val scale: Float
    val key: String

    val canonicalStartGlyph: Char
    val canonicalEndGlyph: Char

    val isAnimating: Boolean get() = canonicalStartGlyph != canonicalEndGlyph

    /**
     * If [force], the given key is guaranteed to become the value of [key].
     * Otherwise, the actual value of [key] may be altered depending on [visibility].
     */
    fun setKey(value: String, force: Boolean = false)

    fun draw(canvas: Canvas, glyphProgress: Float, paints: P, renderGlyph: RenderGlyph? = null)
    fun getWidthAtProgress(glyphProgress: Float): Float
    fun setState(
        newState: GlyphState,
        newVisibility: GlyphVisibility,
        force: Boolean = false,
        currentTimeMillis: Long = getCurrentTimeMillis()
    ) {
        setState(newState, force, currentTimeMillis)
        setState(newVisibility, force, currentTimeMillis)
    }

    fun setState(newState: GlyphState, force: Boolean = false, currentTimeMillis: Long = getCurrentTimeMillis())
    fun setState(
        newVisibility: GlyphVisibility,
        force: Boolean = false,
        currentTimeMillis: Long = getCurrentTimeMillis()
    )

    fun tick(options: GlyphOptions, currentTimeMillis: Long = getCurrentTimeMillis())

    companion object {
        fun createKey(start: Char, end: Char = start): String {
            if (start == end) return start.toString()
            return "${start}_$end"
        }
    }
}

sealed interface BaseGlyph<P : Paints> : Glyph<P> {
    val stateController: GlyphStateController
    val visibilityController: GlyphVisibilityController

    val stateChangeProgress get() = stateController.stateChangeProgress

    override fun setState(newState: GlyphState, force: Boolean, currentTimeMillis: Long) {
        stateController.setState(newState, force, currentTimeMillis)
    }

    override fun setState(newVisibility: GlyphVisibility, force: Boolean, currentTimeMillis: Long) {
        visibilityController.setState(newVisibility, force, currentTimeMillis)
    }

    override fun tick(options: GlyphOptions, currentTimeMillis: Long) {
        val newState = stateController.tick(options, currentTimeMillis)
        val newVisibility = visibilityController.tick(options, currentTimeMillis)

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

    fun Canvas.drawNotImplemented(glyphProgress: Float, paints: P) {
        val (width, height) = companion.maxSize
        drawLine(Color.Red, 0f, 0f, width, height)
        drawLine(Color.Red, width, 0f, 0f, height)
    }
}
