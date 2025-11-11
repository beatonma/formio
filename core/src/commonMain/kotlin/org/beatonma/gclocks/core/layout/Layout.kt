package org.beatonma.gclocks.core.layout

import org.beatonma.gclocks.core.geometry.HorizontalAlignment
import org.beatonma.gclocks.core.geometry.MeasureConstraints
import org.beatonma.gclocks.core.geometry.MutableRect
import org.beatonma.gclocks.core.geometry.MutableRectF
import org.beatonma.gclocks.core.geometry.NativeSize
import org.beatonma.gclocks.core.geometry.Rect
import org.beatonma.gclocks.core.geometry.ScaledSize
import org.beatonma.gclocks.core.glyph.Glyph
import org.beatonma.gclocks.core.glyph.GlyphRole
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.options.LayoutOptions
import org.beatonma.gclocks.core.util.fastForEach
import org.beatonma.gclocks.core.options.Layout as LayoutOption


internal typealias GlyphCallback<G> = (
    glyph: G,
    glyphAnimationProgress: Float,
    rect: Rect<Float>,
) -> Unit


/**
 * Values made available in the receiver scope of the [OnMeasure] callback,
 * mostly for debugging purposes.
 */
interface OnMeasureScope {
    val nativeSize: NativeSize
    val rowSizes: List<NativeSize>
    val drawBounds: Rect<Float>
}
internal typealias OnMeasure = OnMeasureScope.(
    translationX: Float,
    translationY: Float,
    scale: Float,
) -> Unit


fun <P : Paints, G : Glyph<P>> getLayout(
    options: LayoutOptions,
    paints: P,
    nativeSize: NativeSize,
): Layout<P, G> = when (options.layout) {
    LayoutOption.Horizontal -> HorizontalLayout(options, paints, nativeSize)
    LayoutOption.Vertical -> VerticalLayout(options, paints, nativeSize)
    LayoutOption.Wrapped -> WrappedLayout(options, paints, nativeSize)
}


sealed class Layout<P : Paints, G : Glyph<P>>(
    protected val options: LayoutOptions,
    paints: P,
    final override val nativeSize: NativeSize,
) : OnMeasureScope {
    /* Size of each row in the current frame */
    private val _rowSizes: MutableList<NativeSize> = mutableListOf()
    final override val rowSizes: List<NativeSize> get() = _rowSizes

    /* Total boundary of the current frame */
    private val _drawBounds: MutableRect<Float> = MutableRectF()
    final override val drawBounds: Rect<Float>
        get() = _drawBounds

    /* Track current row boundary (used to construct [_rowSizes]) */
    private val _currentRowSize: MutableRect<Float> = MutableRectF()

    /* Reusable Rect for GlyphCallback */
    protected val tempRect = MutableRectF()

    private val paintStrokeWidth: Float = paints.strokeWidth
    protected var strokeWidth: Float = paints.strokeWidth
    protected var halfStrokeWidth: Float = paintStrokeWidth / 2f

    protected enum class LayoutPass {
        // Measure the current size of each glyph in the layout
        Measurement,

        // Draw each glyph with correct position and scale
        Rendering,
        ;
    }

    protected var stage: LayoutPass = LayoutPass.Measurement
        private set

    var scale: Float = 1f
        private set

    /**
     * The maximum scaled size that this layout requires for the current [constraints].
     * For the size of the current render frame use [drawBounds] instead.
     */
    private var measuredSize: ScaledSize = ScaledSize.Init

    lateinit var constraints: MeasureConstraints

    abstract fun isLineBreak(glyph: G): Boolean

    fun setScale(scale: Float): ScaledSize {
        this.scale = scale
        this.measuredSize = nativeSize * scale
        return measuredSize
    }

    fun measureFrame(
        glyphs: List<GlyphStatus<G>>,
        onMeasure: OnMeasure,
    ) {
        layoutPass(glyphs) { glyph, _, rect ->
            if (isLineBreak(glyph)) {
                // Start a new row
                storeCurrentRowSize()
                _currentRowSize.set(rect)
            } else {
                // Expand current row
                _currentRowSize.include(rect)
            }
        }
        storeCurrentRowSize()

        val translationX: Float = options.outerHorizontalAlignment.apply(
            measuredSize.width,
            constraints.maxWidth
        )
        val translationY: Float = options.outerVerticalAlignment.apply(
            measuredSize.height,
            constraints.maxHeight,
        )

        stage = LayoutPass.Rendering
        onMeasure(translationX, translationY, scale)
        reset()
    }

    private fun storeCurrentRowSize() {
        _drawBounds.include(_currentRowSize)
        if (!_currentRowSize.isEmpty) {
            _rowSizes.add(
                NativeSize(_currentRowSize.width, _currentRowSize.height)
            )
        }
    }

    private fun reset() {
        _rowSizes.clear()
        _drawBounds.clear()
        _currentRowSize.clear()
        tempRect.clear()
        stage = LayoutPass.Measurement
    }

    fun layoutPass(
        glyphs: List<GlyphStatus<G>>,
        callback: GlyphCallback<G>,
    ) {
        val spacingPx = options.spacingPx.toFloat()
        val horizontalAlignment = options.innerHorizontalAlignment
        val verticalAlignment = options.innerVerticalAlignment
        var lineIndex = 0
        var lineHeight = -1f

        var x = getAlignedXForRow(horizontalAlignment, lineIndex)
        var y = 0f

        glyphs.fastForEach { status ->
            val glyph = status.glyph
            if (lineHeight < 0f) {
                lineHeight = status.scaledHeight
            }

            val isNewRow = isLineBreak(glyph)
            val strokeWidth = this.strokeWidth * glyph.scale
            val halfStrokeWidth = this.halfStrokeWidth * glyph.scale
            val spacingPx = spacingPx * glyph.scale

            if (isNewRow) {
                // New line
                x = getAlignedXForRow(horizontalAlignment, ++lineIndex)
                y += lineHeight + spacingPx + strokeWidth
                lineHeight = -1f
            }

            val left = x
            val top = y + when (isNewRow) {
                true -> 0f
                false -> verticalAlignment.apply(status.scaledHeight, lineHeight)
            }
            val right = left + when (isNewRow) {
                true -> 0f
                false -> status.scaledWidth
            }
            val bottom = top + when (isNewRow) {
                true -> 0f
                false -> status.scaledHeight
            }

            callback(
                glyph,
                status.progress,
                tempRect.set(left, top, right, bottom)
                    .applyStrokeOffset(strokeWidth, halfStrokeWidth)
            )

            if (!isNewRow) {
                x = when (stage) {
                    LayoutPass.Measurement -> tempRect.right + spacingPx
                    LayoutPass.Rendering -> tempRect.right + halfStrokeWidth + spacingPx
                }
            }
        }
    }

    /**
     * During [LayoutPass.Rendering], returns the aligned 'zero-point' of the given line.
     */
    private fun getAlignedXForRow(alignment: HorizontalAlignment, lineIndex: Int): Float =
        when (stage) {
            LayoutPass.Rendering -> alignment.apply(rowSizes[lineIndex].width, nativeSize.width)

            /*
             * Alignment not possible (or necessary) yet - we need to know
             * the current measured width before alignment can be applied.
             */
            LayoutPass.Measurement -> 0f
        }

    private fun MutableRect<Float>.applyStrokeOffset(
        glyphScaledStrokeWidth: Float,
        glyphScaledHalfStrokeWidth: Float,
    ): MutableRect<Float> = apply {
        if (width > 0f) {
            when (stage) {
                LayoutPass.Measurement -> add(
                    0f, 0f,
                    right = glyphScaledStrokeWidth,
                    bottom = glyphScaledStrokeWidth
                )

                LayoutPass.Rendering -> translate(
                    glyphScaledHalfStrokeWidth,
                    glyphScaledHalfStrokeWidth
                )
            }
        }
    }
}

private class HorizontalLayout<P : Paints, G : Glyph<P>>(
    options: LayoutOptions, paints: P,
    nativeSize: NativeSize,
) : Layout<P, G>(options, paints, nativeSize) {
    override fun isLineBreak(glyph: G): Boolean = false
}

private class VerticalLayout<P : Paints, G : Glyph<P>>(
    options: LayoutOptions, paints: P,
    nativeSize: NativeSize,
) : Layout<P, G>(options, paints, nativeSize) {
    override fun isLineBreak(glyph: G): Boolean = glyph.role.isSeparator
}

private class WrappedLayout<P : Paints, G : Glyph<P>>(
    options: LayoutOptions, paints: P,
    nativeSize: NativeSize,
) : Layout<P, G>(options, paints, nativeSize) {
    override fun isLineBreak(glyph: G): Boolean =
        glyph.role == GlyphRole.SeparatorMinutesSeconds
}
