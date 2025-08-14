package org.beatonma.gclocks.core

import org.beatonma.gclocks.core.geometry.FloatSize
import org.beatonma.gclocks.core.geometry.MutableFloatRect
import org.beatonma.gclocks.core.geometry.MutableRect
import org.beatonma.gclocks.core.geometry.Rect
import org.beatonma.gclocks.core.geometry.Size
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.core.util.TimeOfDay
import org.beatonma.gclocks.core.util.nextSecond
import org.beatonma.gclocks.core.util.progress


fun interface LayoutPassCallback<G> {
    fun callback(glyph: G, glyphAnimationProgress: Float, rect: Rect<Float>)
}
private typealias DrawWithTransform = (translationX: Float, translationY: Float, scale: Float) -> Unit

class ClockLayout<G : BaseClockGlyph>(
    val font: ClockFont<G>,
    options: Options,
    val measureStrategy: MeasureStrategy,
) {
    var options: Options = options
        set(value) {
            field = value
            onOptionsChange(value)
        }
    private val glyphStatus: GlyphStatus<G> = GlyphStatus()

    init {
        onOptionsChange(options)
    }

    private lateinit var glyphs: MutableList<G>  // Initialized in onOptionsChange
    private lateinit var locks: List<GlyphStateLock>  // Initialized in onOptionsChange
    private var stringLength: Int = 0

    /**
     * The maximum size the clock can be with 1x scaling.
     * Based on the values of `options.format` and `options.layout`.
     */
    private lateinit var nativeSize: Size<Float>  // Initialized in onOptionsChange


    /**
     * The size that is given to us for rendering within.
     */
    private var availableSize: Size<Float> = FloatSize()

    /**
     * The size that the clock actually uses *at some point*. The actual rendered
     * clock may not use all of this space at any given time, but will need it
     * at least once per day.
     *
     * This must be <= availableSize.
     *
     * Horizontal alignment is applied relative to this area.
     *
     * Equivalent to nativeSize * scale.
     */
    var measuredSize: Size<Float> = FloatSize()
        private set

    var scale: Float = 1f
        private set

    var isDrawable: Boolean = false
        private set

    var animationTimeMillis = 0
        private set
    private val animatedGlyphIndices: MutableList<Int> = mutableListOf()
    private var animatedGlyphCount: Int = 0

    private fun onOptionsChange(value: Options) {
        nativeSize = font.measure(
            value.format,
            value.layout,
            value.spacingPx,
            value.secondsGlyphScale,
        )
        stringLength = value.format.stringLength
        glyphs = MutableList(stringLength) { i ->
            font.getGlyphAt(
                i,
                value.format,
                value.secondsGlyphScale
            )
        }
        locks = List(stringLength) { GlyphStateLock.None }
    }

    fun setAvailableSize(available: Size<Float>): Size<Float> {
        this.availableSize = available

        if (available.isEmpty) {
            scale = 0f
            return FloatSize.Zero
        }

        return setScale(
            measureStrategy.measureScale(nativeSize, availableSize)
        )
    }

    private fun setScale(scale: Float): Size<Float> {
        this.scale = scale
        measuredSize = nativeSize.scaledBy(scale)
        isDrawable = scale > 0f && !measuredSize.isEmpty
        return measuredSize
    }

    /**
     * Values used during measurement of each frame.
     * Mutable classes used to minimise per-frame object creation.
     */
    private object MeasureProps {
        val rowSizes: MutableList<Size<Float>> = mutableListOf()
        val drawBounds: MutableRect<Float> = MutableFloatRect()
        val currentRowSize: MutableRect<Float> = MutableFloatRect()
        val tempRect = MutableFloatRect()

        fun reset() {
            rowSizes.clear()
            drawBounds.clear()
            currentRowSize.clear()
            tempRect.clear()
        }
    }

    /**
     * Measure the size of the clock for the current animation frame and
     * pass the determined transforms to the draw function.
     *
     * This is required for correct application of alignment relative to the
     * drawable space available for each row/component of the clock.
     */
    fun measure(draw: DrawWithTransform) {
        MeasureProps.reset()

        layoutPass { glyph, glyphAnimationProgress, rect ->
            MeasureProps.drawBounds.include(rect)

            if (rect.top != MeasureProps.currentRowSize.top) {
                if (!MeasureProps.currentRowSize.isEmpty) {
                    MeasureProps.rowSizes.add(MeasureProps.currentRowSize.toSize())
                }
                MeasureProps.currentRowSize.set(rect)
            } else {
                MeasureProps.currentRowSize.include(rect)
            }
        }

        if (!MeasureProps.currentRowSize.isEmpty) {
            MeasureProps.rowSizes.add(MeasureProps.currentRowSize.toSize())
        }

        val translationX: Float = this.options.horizontalAlignment.apply(
            MeasureProps.drawBounds.width * scale,
            measuredSize.x
        )
        val translationY: Float = this.options.verticalAlignment.apply(
            MeasureProps.drawBounds.height * scale,
            measuredSize.y
        )

        draw(translationX, translationY, scale)
        MeasureProps.rowSizes.clear()
    }

    fun layoutPass(callback: LayoutPassCallback<G>) {
        return when (options.layout) {
            Layout.Horizontal -> layoutPassHorizontal(callback)
            Layout.Vertical -> layoutPassVertical(callback)
            Layout.Wrapped -> layoutPassWrapped(callback)
        }
    }

    private fun layoutPassHorizontal(visitGlyph: LayoutPassCallback<G>) {
        val spacingPx = options.spacingPx
        val verticalAlignment = options.verticalAlignment

        var x = 0f

        for (i in 0 until stringLength) {
            val glyphState = updateGlyph(i)
            if (!glyphState.isVisible) continue

            val glyph = glyphState.glyph
            val glyphWidth = glyphState.width
            val glyphHeight = glyphState.height

            val left = x
            val top = verticalAlignment.apply(
                glyphHeight,
                glyph.maxSize.y
            )
            val right = left + glyphWidth
            val bottom = top + glyphHeight

            visitGlyph.callback(
                glyph,
                glyphState.progress,
                MeasureProps.tempRect.set(left, top, right, bottom)
            )
            x += glyphWidth + spacingPx * glyph.scale
        }
    }

    private fun maxLineWidth(): Float = MeasureProps.rowSizes.maxOfOrNull { it.x } ?: 0f
    private fun newLineX(index: Int, maxLineWidth: Float): Float =
        options.horizontalAlignment.apply(MeasureProps.rowSizes[index].x, maxLineWidth)

    private fun layoutPassVertical(visitGlyph: LayoutPassCallback<G>) {
        val spacingPx = options.spacingPx

        var currentLineIndex = 0
        val maxLineWidth = maxLineWidth()
        var x = newLineX(0, maxLineWidth)
        var y = 0f

        for (index in 0 until stringLength) {
            val glyphState = updateGlyph(index)

            val glyph = glyphState.glyph
            val glyphWidth = glyphState.width
            val glyphHeight = glyphState.height

            if (glyph.role.isSeparator) {
                currentLineIndex += 1
                x = newLineX(index, maxLineWidth)
                y += glyph.maxSize.y + spacingPx
                continue
            }
            if (!glyphState.isVisible) continue

            visitGlyph.callback(
                glyph,
                glyphState.progress,
                MeasureProps.tempRect.set(x, y, x + glyphWidth, y + glyphHeight)
            )
            x += glyphWidth + spacingPx * glyph.scale
        }
    }

    private fun layoutPassWrapped(visitGlyph: LayoutPassCallback<G>) {
        val spacingPx = options.spacingPx
        val alignment = options.horizontalAlignment
        val maxLineWidth = maxLineWidth()

        var currentLineIndex = 0
        var x = newLineX(0, maxLineWidth)
        var y = 0f

        for (index in 0 until stringLength) {
            val glyphState = updateGlyph(index)

            val glyph = glyphState.glyph
            val glyphWidth = glyphState.width
            val glyphHeight = glyphState.height

            if (glyph.role == GlyphRole.SeparatorMinutesSeconds) {
                currentLineIndex += 1

                x = alignment.apply(MeasureProps.rowSizes[currentLineIndex].x, maxLineWidth)
                y += glyph.maxSize.y + spacingPx
                continue
            }

            if (!glyphState.isVisible) continue

            visitGlyph.callback(
                glyph,
                glyphState.progress,
                MeasureProps.tempRect.set(x, y, x + glyphWidth, y + glyphHeight)
            )
        }
    }

    private fun updateGlyph(index: Int): GlyphStatus<G> {
        val glyph = glyphs.get(index)

        if (glyph.scale == 0f || glyph.role == GlyphRole.SeparatorMinutesSeconds) {
            return glyphStatus.set(glyph, isVisible = false)
        }

        var glyphProgress = getGlyphAnimationProgress(index)

        if (glyphProgress == 1f) {
            glyph.key = glyph.canonicalEndGlyph.toString()
            glyphProgress = 0f
        }

        if (glyphProgress != 0f) {
            glyph.setState(GlyphState.Activating)
            if (index > 0) {
                val previousGlyph = this.glyphs[index - 1]
                val previousCanonical = previousGlyph.canonicalStartGlyph
                if (previousCanonical != '#' && previousCanonical != ':') {
                    previousGlyph.setState(GlyphState.Activating)
                }
            }
        }

        val width = (glyph.getWidthAtProgress(glyphProgress) * glyph.scale)
        val height = (glyph.maxSize.y * glyph.scale)

        return glyphStatus.set(
            glyph,
            isVisible = true,
            progress = glyphProgress,
            width = width,
            height = height
        )
    }

    private fun getGlyphAnimationProgress(glyphIndex: Int): Float {
        var index = -1
        for (i in 0 until animatedGlyphCount) {
            if (animatedGlyphIndices[i] == glyphIndex) {
                index = i
                break
            }
        }

        if (index < 0) return 0f

        return progress(
            animationTimeMillis.toFloat(),
            0f,
            options.glyphMorphMillis.toFloat()
        )
    }

    fun update(time: TimeOfDay) {
        val nowString = options.format.apply(time)
        val nextString = options.format.apply(time.nextSecond())

        animationTimeMillis = time.millisecond
        updateGlyphs(nowString, nextString)
    }

    private fun updateGlyphs(now: String, next: String) {
        animatedGlyphIndices.clear()
        var animatedGlyphCount = 0
        var glyphCount = 0

        for (index in 0 until stringLength) {
            val fromChar = now[index]
            val nextChar = next[index]

            val glyph = glyphs[index]

            if (fromChar == nextChar) {
                glyph.key = fromChar.toString()
                glyph.setState(GlyphState.Activating)
            } else {
                animatedGlyphCount++
                animatedGlyphIndices.add(index)
                glyph.key = "${fromChar}_${nextChar}"
                glyph.setState(GlyphState.Deactivating)
            }
            glyphs[glyphCount++] = glyph
        }
        this.animatedGlyphCount = animatedGlyphCount
    }
}


private class GlyphStatus<G : Glyph>(
) {
    lateinit var glyph: G
        private set
    var isVisible: Boolean = false
        private set
    var progress: Float = 0f
        private set
    var width: Float = 0f
        private set
    var height: Float = 0f
        private set

    fun set(
        glyph: G,
        isVisible: Boolean,
        progress: Float = 0f,
        width: Float = 0f,
        height: Float = 0f,
    ): GlyphStatus<G> {
        this.glyph = glyph
        this.isVisible = isVisible
        this.progress = progress
        this.width = width
        this.height = height
        return this
    }
}