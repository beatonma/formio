package org.beatonma.gclocks.core.layout

import org.beatonma.gclocks.core.BaseClockGlyph
import org.beatonma.gclocks.core.geometry.NativeSize
import org.beatonma.gclocks.core.geometry.MutableRectF
import org.beatonma.gclocks.core.geometry.MutableRect
import org.beatonma.gclocks.core.geometry.Rect
import org.beatonma.gclocks.core.geometry.ScaledSize
import org.beatonma.gclocks.core.geometry.HorizontalAlignment
import org.beatonma.gclocks.core.geometry.MeasureConstraints
import org.beatonma.gclocks.core.options.LayoutOptions
import org.beatonma.gclocks.core.options.Layout as LayoutOption
import org.beatonma.gclocks.core.util.fastForEach


internal typealias GlyphCallback<G> = (
    glyph: G,
    glyphAnimationProgress: Float,
    rect: Rect<Float>,
) -> Unit


/** Values made available via context of the [OnMeasure] callback. */
interface OnMeasureScope {
    val nativeSize: NativeSize
    val scale: Float
    val rowSizes: List<NativeSize>
    val drawBounds: Rect<Float>
}
internal typealias OnMeasure = OnMeasureScope.(
    translationX: Float,
    translationY: Float,
    scale: Float,
) -> Unit


internal fun <G : BaseClockGlyph> getLayout(
    options: LayoutOptions,
    nativeSize: NativeSize,
): Layout<G> = when (options.layout) {
    LayoutOption.Horizontal -> HorizontalLayout(options, nativeSize)
    LayoutOption.Vertical -> VerticalLayout(options, nativeSize)
    LayoutOption.Wrapped -> throw NotImplementedError()
}


internal sealed class Layout<G : BaseClockGlyph>(
    protected val options: LayoutOptions,
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

    protected enum class LayoutPass {
        // Measure the current size of each glyph in the layout
        Measurement,

        // Draw each glyph with correct position and scale
        Rendering,
        ;
    }

    protected var stage: LayoutPass = LayoutPass.Measurement
        private set

    final override var scale: Float = 1f
        private set

    private var measuredSize: ScaledSize = ScaledSize.Init

    lateinit var constraints: MeasureConstraints

    abstract fun layoutPass(
        glyphs: List<GlyphStatus<G>>,
        callback: GlyphCallback<G>,
    )

    fun setScale(scale: Float): ScaledSize {
        this.scale = scale
        this.measuredSize = nativeSize * scale
        return measuredSize
    }

    fun measureFrame(
        glyphs: List<GlyphStatus<G>>,
        onMeasure: OnMeasure,
    ) {
        layoutPass(glyphs) { glyph, glyphAnimationProgress, rect ->
            if (rect.top != _currentRowSize.top) {
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
}

private class HorizontalLayout<G : BaseClockGlyph>(options: LayoutOptions, nativeSize: NativeSize) :
    Layout<G>(options, nativeSize) {
    override fun layoutPass(
        glyphs: List<GlyphStatus<G>>,
        callback: GlyphCallback<G>,
    ) {
        val spacingPx = options.spacingPx
        val alignment = options.innerVerticalAlignment

        var x = 0f

        glyphs.fastForEach { status ->
            val glyph = status.glyph

            val left = x
            val top = alignment.apply(status.scaledHeight, nativeSize.height)
            val right = left + status.scaledWidth
            val bottom = top + status.scaledHeight

            callback(
                glyph,
                status.progress,
                tempRect.set(left, top, right, bottom)
            )
            x = right + (spacingPx * glyph.scale)
        }
    }
}


private class VerticalLayout<G : BaseClockGlyph>(
    options: LayoutOptions,
    nativeSize: NativeSize,
) : Layout<G>(options, nativeSize) {
    private fun getAlignedXForRow(alignment: HorizontalAlignment, lineIndex: Int): Float =
        when (stage) {
            LayoutPass.Rendering -> alignment.apply(
                rowSizes[lineIndex].width,
                nativeSize.width
            )

            /*
             * Alignment not possible (or necessary) yet - we need to know
             * the current measured width before alignment can be applied.
             */
            LayoutPass.Measurement -> 0f
        }

    override fun layoutPass(
        glyphs: List<GlyphStatus<G>>,
        callback: GlyphCallback<G>,
    ) {
        val spacingPx = options.spacingPx
        val alignment = options.innerHorizontalAlignment
        var lineIndex = 0

        var x = getAlignedXForRow(alignment, lineIndex)
        var y = 0f

        glyphs.fastForEach { status ->
            val glyph = status.glyph
            val isNewRow = glyph.role.isSeparator
            val scaledSpacingPx = spacingPx * glyph.scale

            if (isNewRow) {
                // New line
                x = getAlignedXForRow(alignment, ++lineIndex)
                y += status.scaledHeight + scaledSpacingPx
            }

            val left = x
            val top = y
            val right = left + when (isNewRow) {
                true -> 0f
                false -> status.scaledWidth
            }
            val bottom = top + status.scaledHeight

            callback(
                glyph,
                status.progress,
                tempRect.set(left, top, right, bottom)
            )

            x = right + when (isNewRow) {
                true -> 0f
                false -> scaledSpacingPx
            }
        }
    }
}
