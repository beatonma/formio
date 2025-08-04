package org.beatonma.gclocks.dev

import org.beatonma.gclocks.core.BaseClockGlyph
import org.beatonma.gclocks.core.GlyphRole
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.ClockFont
import org.beatonma.gclocks.core.geometry.FloatSize
import org.beatonma.gclocks.core.geometry.Size
import org.beatonma.gclocks.core.graphics.Stroke
import org.beatonma.gclocks.core.options.HorizontalAlignment
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.core.options.TimeFormat
import org.beatonma.gclocks.core.options.TimeResolution
import org.beatonma.gclocks.core.options.VerticalAlignment

private const val SecondScale = 0.5f


data class DevOptions(
    override val horizontalAlignment: HorizontalAlignment = HorizontalAlignment.Default,
    override val verticalAlignment: VerticalAlignment = VerticalAlignment.Default,
    override val layout: Layout = Layout.Horizontal,
    override val format: TimeFormat = TimeFormat.HH_MM_SS_24,
    override val spacingPx: Int = 8,
    override val glyphMorphMillis: Int = 800,
) : Options


class DevFont : ClockFont<DevGlyph> {
    override fun getGlyphAt(index: Int, format: TimeFormat): DevGlyph {
        return DevGlyph()
    }

    override fun measure(
        format: TimeFormat,
        layout: Layout,
        spacingPx: Int,
    ): Size<Float> {
        val hasSeconds = format.resolution == TimeResolution.Seconds

        val lineHeight = 120f
        val separatorWidth = 48
        val pairWidth = 352 // max width of a pair of digits

        return when (layout) {
            Layout.Horizontal -> {
                val digitsWidth = 816
                val spacingWidth =
                    spacingPx.toFloat() * (if (hasSeconds) 5f + SecondScale else 4f)
                return FloatSize(
                    digitsWidth + separatorWidth + spacingWidth,
                    lineHeight
                )
            }

            Layout.Vertical -> FloatSize(
                (pairWidth + spacingPx).toFloat(),
                lineHeight * (2f + (if (hasSeconds) SecondScale else 0f) + (spacingPx.toFloat() * 2f))
            )

            Layout.Wrapped -> FloatSize(
                (pairWidth * 2f) + (spacingPx * 4f),
                (lineHeight * (1f + (if (hasSeconds) SecondScale else 0f))) + spacingPx.toFloat()
            )
        }
    }
}


class DevGlyph : BaseClockGlyph(GlyphRole.Default) {
    override val maxSize = FloatSize(
        x = 120f,
        y = 120f,
    )

    override val role: GlyphRole = GlyphRole.Second
    override var scale: Float = 1f

    private fun drawPlaceholder(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {
        canvas.drawCircle(60f, 60f, 60f, paints.colors.first(), Stroke())
    }

    override fun drawZeroOne(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(canvas, glyphProgress, paints)
    }

    override fun drawOneTwo(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(canvas, glyphProgress, paints)
    }

    override fun drawTwoThree(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(canvas, glyphProgress, paints)
    }

    override fun drawThreeFour(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(canvas, glyphProgress, paints)
    }

    override fun drawFourFive(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(canvas, glyphProgress, paints)
    }

    override fun drawFiveSix(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(canvas, glyphProgress, paints)
    }

    override fun drawSixSeven(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(canvas, glyphProgress, paints)
    }

    override fun drawSevenEight(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(canvas, glyphProgress, paints)
    }

    override fun drawEightNine(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(canvas, glyphProgress, paints)
    }

    override fun drawNineZero(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(canvas, glyphProgress, paints)
    }

    override fun drawOneZero(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(canvas, glyphProgress, paints)
    }

    override fun drawTwoZero(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(canvas, glyphProgress, paints)
    }

    override fun drawThreeZero(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(canvas, glyphProgress, paints)
    }

    override fun drawFiveZero(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(canvas, glyphProgress, paints)
    }

    override fun drawOneEmpty(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(canvas, glyphProgress, paints)
    }

    override fun drawTwoEmpty(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(canvas, glyphProgress, paints)
    }

    override fun drawEmptyOne(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(canvas, glyphProgress, paints)
    }

    override fun drawSeparator(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(canvas, glyphProgress, paints)
    }

    override fun drawSpace(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(canvas, glyphProgress, paints)
    }

    override fun drawUnderscore(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(canvas, glyphProgress, paints)
    }

    override fun drawHash(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(canvas, glyphProgress, paints)
    }

    override fun getWidthAtProgress(glyphProgress: Float): Float {
        return if (glyphProgress == 0f) maxSize.x else maxSize.x * glyphProgress
    }
}
