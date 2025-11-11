package org.beatonma.gclocks.core.layout

import org.beatonma.gclocks.core.ClockFont
import org.beatonma.gclocks.core.glyph.ClockGlyph
import org.beatonma.gclocks.core.glyph.Glyph
import org.beatonma.gclocks.core.glyph.GlyphState
import org.beatonma.gclocks.core.glyph.GlyphVisibility
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.core.util.currentTimeMillis
import org.beatonma.gclocks.core.util.fastForEach
import org.beatonma.gclocks.core.util.nextSecond
import org.beatonma.gclocks.core.util.progress
import org.beatonma.gclocks.core.util.timeOfDay
import kotlin.time.Instant


internal class Glyphs<P : Paints, G : ClockGlyph<P>>(
    font: ClockFont<P, G>,
    options: Options<P>,
) {
    private val stringLength = options.layout.format.stringLength
    private var mutableGlyphs: List<MutableGlyphStatus<G>> = List(stringLength) { index ->
        MutableGlyphStatus(
            font.getGlyphAt(
                index,
                options.layout.format,
                options.layout.secondsGlyphScale
            ),
            index
        )
    }
    val glyphs: List<GlyphStatus<G>> get() = mutableGlyphs

    private val options = options.glyph
    private val format = options.layout.format

    var animationTimeMillis = 0f
        private set

    fun update(instant: Instant) {
        val time = instant.timeOfDay
        val isNewSecond = time.millisecond < animationTimeMillis
        animationTimeMillis = time.millisecond.toFloat()

        val now = format.apply(time)
        val next = format.apply(time.nextSecond())

        for (index in 0 until stringLength) {
            val fromChar = now[index]
            val nextChar = next[index]

            val glyph = mutableGlyphs[index]

            if (isNewSecond) {
                glyph.glyph.onSecondChange(instant.currentTimeMillis)
            }

            updateGlyph(glyph, Glyph.createKey(fromChar, nextChar))
        }
    }


    fun setState(state: GlyphState, visibility: GlyphVisibility, force: Boolean) {
        glyphs.fastForEach { it.glyph.setState(state, visibility, force) }
    }

    fun setState(state: GlyphState, force: Boolean) {
        glyphs.fastForEach { it.glyph.setState(state, force) }
    }

    fun setState(visibility: GlyphVisibility, force: Boolean) {
        glyphs.fastForEach { it.glyph.setState(visibility, force) }
    }

    private fun updateGlyph(status: MutableGlyphStatus<G>, key: String): GlyphStatus<G> {
        val glyph = status.glyph
        glyph.setKey(key)
        glyph.tick(options)

        var progress: Float = when (glyph.isAnimating) {
            true -> progress(animationTimeMillis, 0f, options.glyphMorphMillis.toFloat())
            false -> {
                // No animation, no transition -> short-circuit
                return status.withProgress(0f)
            }
        }

        if (progress == 1f) {
            glyph.setKey(Glyph.createKey(glyph.canonicalEndGlyph), force = true)
            progress = 0f
        }

        val index = status.index
        if (progress != 0f) {
            glyph.setState(GlyphState.Active)
            if (index > 0) {
                val previous = mutableGlyphs[index - 1]
                if (previous.glyph.canonicalStartGlyph.isDigit()) {
                    previous.setState(GlyphState.Active)
                }
            }
        }

        return status.withProgress(progress)
    }
}


interface GlyphStatus<G : Glyph<*>> {
    val glyph: G

    val progress: Float
    val nativeWidth: Float
    val nativeHeight: Float
    val scaledWidth: Float
    val scaledHeight: Float
}

private class MutableGlyphStatus<G : Glyph<*>>(
    override val glyph: G,
    val index: Int,
) : GlyphStatus<G> {
    override var nativeWidth: Float = 0f
        private set(value) {
            field = value
            scaledWidth = value * glyph.scale
        }
    override val nativeHeight: Float = glyph.maxSize.height
    override var scaledWidth: Float = nativeWidth * glyph.scale
        private set
    override val scaledHeight: Float = nativeHeight * glyph.scale
    override var progress: Float = 0f
        private set

    fun withProgress(value: Float): MutableGlyphStatus<G> {
        progress = value
        nativeWidth = glyph.getWidthAtProgress(value)
        return this
    }

    fun setState(state: GlyphState) {
        glyph.setState(state)
    }

    override fun toString(): String {
        return "$glyph: ${scaledWidth}x${scaledHeight} | ${nativeWidth}x${nativeHeight}"
    }
}
