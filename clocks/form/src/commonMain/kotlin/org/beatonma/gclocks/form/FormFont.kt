package org.beatonma.gclocks.form

import org.beatonma.gclocks.core.BaseClockGlyph
import org.beatonma.gclocks.core.GlyphRole
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.ClockFont
import org.beatonma.gclocks.core.geometry.FloatSize
import org.beatonma.gclocks.core.geometry.Size
import org.beatonma.gclocks.core.geometry.degrees
import org.beatonma.gclocks.core.geometry.rawDegrees
import org.beatonma.gclocks.core.graphics.Fill
import org.beatonma.gclocks.core.options.HorizontalAlignment
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.core.options.TimeFormat
import org.beatonma.gclocks.core.options.TimeResolution
import org.beatonma.gclocks.core.options.VerticalAlignment
import org.beatonma.gclocks.core.util.decelerate5
import org.beatonma.gclocks.core.util.interpolate
import org.beatonma.gclocks.core.util.progress

private const val FormGlyphHeight = 144f
private const val SecondScale = 0.5f

data class FormOptions(
    override val horizontalAlignment: HorizontalAlignment = HorizontalAlignment.Default,
    override val verticalAlignment: VerticalAlignment = VerticalAlignment.Default,
    override val layout: Layout = Layout.Horizontal,
    override val format: TimeFormat = TimeFormat.HH_MM_SS_24,
    override val spacingPx: Int = 8,
    override val glyphMorphMillis: Int = 800,
) : Options


class FormFont : ClockFont<FormGlyph> {
    override fun getGlyphAt(index: Int, format: TimeFormat): FormGlyph {
        val role = format.roles.getOrNull(index) ?: GlyphRole.Default
        val scale = when (role) {
            GlyphRole.Second -> SecondScale
            else -> 1f
        }
        return FormGlyph(role, scale)
    }

    override fun measure(
        format: TimeFormat,
        layout: Layout,
        spacingPx: Int,
    ): Size<Float> {
        val hasSeconds = format.resolution == TimeResolution.Seconds

        val lineHeight = FormGlyphHeight
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


class FormGlyph(
    role: GlyphRole,
    scale: Float = 1f,
) : BaseClockGlyph(role, scale) {
    override val maxSize = FloatSize(
        x = 144f,
        y = 120f,
    )

    override fun drawZeroOne(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {
        val (_, color2, color3) = paints.colors
        val d1 = decelerate5(progress(glyphProgress, 0f, 0.5f))
        val d2 = decelerate5(progress(glyphProgress, 0.5f, 1f))

        // 0
        canvas.withCheckpoint {
            val stretchX = interpolate(d1, 0f, interpolate(d2, 72f, -36f))

            withTranslation(
                interpolate(d1, interpolate(d1, 0f, 24f), interpolate(d2, 24f, 0f)),
                0f
            ) {
                withScale(interpolate(d1, 1f, 2f / 3f), 72f, 144f) {
                    withScale(interpolate(d2, 1f, 0.7f), 72f, 96f) {
                        withRotation(interpolate(d1, 45f, 0f).degrees, 72f, 72f) {
                            drawPath(color2, Fill) {
                                moveTo(72f - stretchX, 144f)
                                boundedArc(
                                    -stretchX,
                                    0f,
                                    144f - stretchX,
                                    144f,
                                    90f.degrees,
                                    180f.degrees
                                )
                                lineTo(72f + stretchX, 0f)
                                lineTo(72f + stretchX, 144f)
                                lineTo(72f - stretchX, 144f)
                            }

                            drawPath(color3, Fill) {
                                boundedArc(
                                    stretchX,
                                    0f,
                                    144f + stretchX,
                                    144f,
                                    (-90f).rawDegrees,
                                    180f.degrees
                                )
                            }
                        }
                    }
                }
            }
        }

        // 1
        if (d2 > 0f) {
            canvas.drawRect(
                left = interpolate(d2, 28f, 0f),
                top = interpolate(d2, 72f, 0f),
                right = 100f,
                bottom = interpolate(d2, 144f, 48f),
                color = color2,
                style = Fill,
            )
            canvas.drawRect(
                left = 28f,
                top = interpolate(d2, 144f, 48f),
                right = 100f,
                bottom = 144f,
                color = color3,
                style = Fill
            )
        }
    }

    override fun drawOneTwo(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {

    }

    override fun drawTwoThree(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {

    }

    override fun drawThreeFour(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {

    }

    override fun drawFourFive(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {

    }

    override fun drawFiveSix(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {

    }

    override fun drawSixSeven(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {

    }

    override fun drawSevenEight(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {

    }

    override fun drawEightNine(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {

    }

    override fun drawNineZero(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {

    }

    override fun drawOneZero(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {

    }

    override fun drawTwoZero(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {

    }

    override fun drawThreeZero(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {

    }

    override fun drawFiveZero(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {

    }

    override fun drawOneEmpty(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {

    }

    override fun drawTwoEmpty(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {

    }

    override fun drawEmptyOne(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {

    }

    override fun drawSeparator(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {

    }

    override fun drawSpace(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {

    }

    override fun drawUnderscore(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {

    }

    override fun drawHash(
        canvas: Canvas<*>,
        glyphProgress: Float,
        paints: Paints,
    ) {

    }

    override fun getWidthAtProgress(glyphProgress: Float): Float {
        return when (key) {
            "0", "0_1" -> interpolate(
                decelerate5(progress(glyphProgress, 0.5f, 1f)),
                interpolate(
                    decelerate5(progress(glyphProgress, 0f, 0.4f)),
                    144f,
                    192f
                ),
                100f,
            )

            "1", "1_2" -> interpolate(decelerate5(progress(glyphProgress, 0f, 0.5f)), 100f, 144f)
            "2", "2_3" -> interpolate(decelerate5(progress(glyphProgress, 0f, 0.5f)), 144f, 128f)
            "3", "3_4" -> interpolate(decelerate5(progress(glyphProgress, 0f, 0.5f)), 128f, 144f)
            "4", "4_5" -> interpolate(decelerate5(progress(glyphProgress, 0f, 0.5f)), 144f, 128f)
            "5", "5_6" -> interpolate(decelerate5(progress(glyphProgress, 0.1f, 1f)), 128f, 144f)

            "6", "6_7" -> {
                val turningPoint = 0.8f
                val maxChange = 31f
                val d = decelerate5(glyphProgress)
                if (d < turningPoint) {
                    144f + interpolate(progress(d, 0f, turningPoint), 0f, maxChange)
                } else {
                    144f + maxChange - interpolate(progress(d, turningPoint, 1f), 0f, maxChange)
                }
            }

            "7", "7_8" -> 144f
            "8", "8_9" -> 144f
            "9", "9_0" -> 144f

            " _1" -> interpolate(
                decelerate5(progress(glyphProgress, 0f, 0.5f)),
                0f,
                100f,
            )

            "1_ " -> interpolate(
                decelerate5(progress(glyphProgress, 0.5f, 1f)),
                100f,
                0f,
            )

            "2_ " -> interpolate(
                decelerate5(progress(glyphProgress, 0f, 0.5f)),
                144f,
                interpolate(decelerate5(progress(glyphProgress, 0.5f, 1f)), 72f, 0f),
            )

            "2_1" -> interpolate(decelerate5(progress(glyphProgress, 0f, 0.5f)), 144f, 100f)
            "3_0" -> interpolate(decelerate5(progress(glyphProgress, 0f, 0.5f)), 128f, 144f)
            "5_0" -> interpolate(decelerate5(progress(glyphProgress, 0f, 0.5f)), 128f, 144f)
            "2_0" -> 144f
            " ", "_" -> 0f
            ":" -> 48f

            else -> throw IllegalArgumentException("getWidthAtProgress unhandled key $key")
        }
    }
}
