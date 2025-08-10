package org.beatonma.gclocks.dev

import org.beatonma.gclocks.core.BaseClockGlyph
import org.beatonma.gclocks.core.GlyphRole
import org.beatonma.gclocks.core.graphics.GenericCanvas
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.ClockFont
import org.beatonma.gclocks.core.GlyphCompanion
import org.beatonma.gclocks.core.geometry.FloatSize
import org.beatonma.gclocks.core.geometry.Size
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
    companion object : GlyphCompanion {
        override val maxSize = FloatSize(
            x = 120f,
            y = 120f,
        )
        override val aspectRatio: Float = maxSize.aspectRatio()
    }

    override val companion: GlyphCompanion = DevGlyph

    override val role: GlyphRole = GlyphRole.Second
    override var scale: Float = 1f

    private fun GenericCanvas.drawPlaceholder(
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawCircle(paints.colors.first(), 60f, 60f, 60f)
    }

    override fun GenericCanvas.drawZeroOne(
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(glyphProgress, paints)
    }

    override fun GenericCanvas.drawOneTwo(
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(glyphProgress, paints)
    }

    override fun GenericCanvas.drawTwoThree(
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(glyphProgress, paints)
    }

    override fun GenericCanvas.drawThreeFour(
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(glyphProgress, paints)
    }

    override fun GenericCanvas.drawFourFive(
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(glyphProgress, paints)
    }

    override fun GenericCanvas.drawFiveSix(
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(glyphProgress, paints)
    }

    override fun GenericCanvas.drawSixSeven(
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(glyphProgress, paints)
    }

    override fun GenericCanvas.drawSevenEight(
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(glyphProgress, paints)
    }

    override fun GenericCanvas.drawEightNine(
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(glyphProgress, paints)
    }

    override fun GenericCanvas.drawNineZero(
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(glyphProgress, paints)
    }

    override fun GenericCanvas.drawOneZero(
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(glyphProgress, paints)
    }

    override fun GenericCanvas.drawTwoZero(
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(glyphProgress, paints)
    }

    override fun GenericCanvas.drawThreeZero(
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(glyphProgress, paints)
    }

    override fun GenericCanvas.drawFiveZero(
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(glyphProgress, paints)
    }

    override fun GenericCanvas.drawOneEmpty(
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(glyphProgress, paints)
    }

    override fun GenericCanvas.drawTwoEmpty(
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(glyphProgress, paints)
    }

    override fun GenericCanvas.drawEmptyOne(
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(glyphProgress, paints)
    }

    override fun GenericCanvas.drawSeparator(
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(glyphProgress, paints)
    }

    override fun GenericCanvas.drawSpace(
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(glyphProgress, paints)
    }

    override fun GenericCanvas.drawHash(
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawPlaceholder(glyphProgress, paints)
    }

    override fun getWidthAtProgress(glyphProgress: Float): Float {
        return if (glyphProgress == 0f) maxSize.x else maxSize.x * glyphProgress
    }
}
