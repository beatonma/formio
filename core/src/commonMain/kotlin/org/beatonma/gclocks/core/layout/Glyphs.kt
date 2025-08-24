package org.beatonma.gclocks.core.layout

import org.beatonma.gclocks.core.ClockFont
import org.beatonma.gclocks.core.Glyph
import org.beatonma.gclocks.core.GlyphState
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.core.util.TimeOfDay
import org.beatonma.gclocks.core.util.nextSecond
import org.beatonma.gclocks.core.util.progress


internal class Glyphs<P : Paints, G : Glyph<P>>(
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

    fun update(time: TimeOfDay) {
        val nowString = format.apply(time)
        val nextString = format.apply(time.nextSecond())

        animationTimeMillis = time.millisecond.toFloat()
        updateGlyphs(nowString, nextString)
    }

    private fun updateGlyphs(now: String, next: String) {
        for (index in 0 until stringLength) {
            var isAnimated = false
            val fromChar = now[index]
            val nextChar = next[index]

            val glyph = mutableGlyphs[index]

            if (fromChar == nextChar) {
                glyph.key = fromChar.toString()
            } else {
                isAnimated = true
                glyph.key = "${fromChar}_${nextChar}"
                glyph.setState(GlyphState.Active)
            }

            updateGlyph(glyph, isAnimated)
        }
    }

    private fun updateGlyph(status: MutableGlyphStatus<G>, isAnimated: Boolean): GlyphStatus<G> {
        val glyph = status.glyph
        val index = status.index
        glyph.tickState(options)

        var progress: Float = when (isAnimated) {
            true -> progress(animationTimeMillis, 0f, options.glyphMorphMillis.toFloat())
            false -> {
                // No animation, no transition -> short-circuit
                return status.set(
                    isVisible = true,
                    progress = 0f,
                    nativeWidth = glyph.getWidthAtProgress(0f),
                )
            }
        }

        if (progress == 1f) {
            glyph.key = glyph.canonicalEndGlyph.toString()
            progress = 0f
        }

        if (progress != 0f) {
            glyph.setState(GlyphState.Active)
            if (index > 0) {
                val previous = mutableGlyphs[index - 1]
                if (previous.glyph.canonicalStartGlyph.isDigit()) {
                    previous.setState(GlyphState.Active)
                }
            }
        }

        return status.set(
            isVisible = true,
            progress = progress,
            nativeWidth = glyph.getWidthAtProgress(progress),
        )
    }
}


interface GlyphStatus<G : Glyph<*>> {
    val glyph: G
    val key: String
    val isVisible: Boolean
    val progress: Float
    val nativeWidth: Float
    val scaledWidth: Float
    val nativeHeight: Float
    val scaledHeight: Float
}

private class MutableGlyphStatus<G : Glyph<*>>(
    override val glyph: G,
    val index: Int,
) : GlyphStatus<G> {
    override var isVisible: Boolean = false
        private set
    override var progress: Float = 0f
        private set
    override var nativeWidth: Float = 0f
        private set(value) {
            field = value
            scaledWidth = value * glyph.scale
        }
    override var scaledWidth: Float = nativeWidth * glyph.scale
        private set
    override val nativeHeight: Float = glyph.maxSize.height
    override val scaledHeight: Float = nativeHeight * glyph.scale

    override var key: String
        get() = glyph.key
        set(value) {
            glyph.key = value
        }

    fun set(
        isVisible: Boolean,
        progress: Float = 0f,
        nativeWidth: Float = 0f,
    ): GlyphStatus<G> {
        this.isVisible = isVisible
        this.progress = progress
        this.nativeWidth = nativeWidth
        return this
    }

    fun setState(state: GlyphState) {
        glyph.setState(state)
    }

    override fun toString(): String {
        return "$glyph: ${scaledWidth}x${scaledHeight} | ${nativeWidth}x${nativeHeight}"
    }
}