package org.beatonma.gclocks.form

import org.beatonma.gclocks.core.BaseClockGlyph
import org.beatonma.gclocks.core.GlyphRole
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.ClockFont
import org.beatonma.gclocks.core.GlyphCompanion
import org.beatonma.gclocks.core.geometry.FloatSize
import org.beatonma.gclocks.core.geometry.Size
import org.beatonma.gclocks.core.geometry.degrees
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
        val separatorWidth = 48f
        val pairWidth = 352f // max width of a pair of digits

        return when (layout) {
            Layout.Horizontal -> {
                val digitsWidth = 816f
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
    companion object : GlyphCompanion {
        override val maxSize = FloatSize(
            x = 192f,
            y = 144f,
        )
        override val aspectRatio: Float = maxSize.aspectRatio()
    }

    override val companion: GlyphCompanion = FormGlyph

    override fun Canvas<*>.drawZeroOne(
        glyphProgress: Float,
        paints: Paints,
    ) {
        val (_, color2, color3) = paints.colors
        val d1 = decelerate5(progress(glyphProgress, 0f, 0.5f))
        val d2 = decelerate5(progress(glyphProgress, 0.5f, 1f))

        // 0f
        withCheckpoint {
            val stretchX = interpolate(d1, 0f, interpolate(d2, 72f, -36f))

            withTranslation(
                interpolate(d1, interpolate(d1, 0f, 24f), interpolate(d2, 24f, 0f)),
                0f
            ) {
                withScale(interpolate(d1, 1f, 2f / 3f), 72f, 144f) {
                    withScale(interpolate(d2, 1f, 0.7f), 72f, 96f) {
                        withRotation(interpolate(d1, 45f, 0f).degrees, 72f, 72f) {
                            drawPath(color2) {
                                moveTo(72f - stretchX, 144f)
                                boundedArc(
                                    -stretchX,
                                    0f,
                                    144f - stretchX,
                                    144f,
                                    90f.degrees,
                                )
                                lineTo(72f + stretchX, 0f)
                                lineTo(72f + stretchX, 144f)
                                lineTo(72f - stretchX, 144f)
                            }

                            drawPath(color3) {
                                boundedArc(
                                    stretchX,
                                    0f,
                                    144f + stretchX,
                                    144f,
                                )
                            }
                        }
                    }
                }
            }
        }

        // 1f
        if (d2 > 0f) {
            drawRect(
                left = interpolate(d2, 28f, 0f),
                top = interpolate(d2, 72f, 0f),
                right = 100f,
                bottom = interpolate(d2, 144f, 48f),
                color = color2,
            )
            drawRect(
                left = 28f,
                top = interpolate(d2, 144f, 48f),
                right = 100f,
                bottom = 144f,
                color = color3,
            )
        }
    }

    override fun Canvas<*>.drawOneTwo(
        glyphProgress: Float,
        paints: Paints,
    ) {
        val (color1, color2, color3) = paints.colors
        val d = 1f - decelerate5(progress(glyphProgress, 0f, 0.5f))
        val d1 = decelerate5(progress(glyphProgress, 0.3f, 0.8f));
        val d2 = decelerate5(progress(glyphProgress, 0.5f, 1.0f));

        // 2f
        if (d1 > 0f) {
            withTranslation(interpolate(d2, 72f, 0f), 0f) {
                drawPath(color3) {
                    moveTo(0f, 144f)
                    lineTo(72f, 72f)
                    lineTo(72f, 144f)
                    lineTo(0f, 144f)
                }
            }

            withTranslation(108f, interpolate(d1, 72f, 0f)) {
                drawPath(color1) {
                    boundedArc(-36f, 0f, 36f, 72f)
                }
            }

            withTranslation(0f, interpolate(d1, 72f, 0f)) {
                drawRect(color1, interpolate(d2, 72f, 8f), 0f, interpolate(d2, 144f, 108f), 72f)
            }

            drawRect(color2, 72f, 72f, 144f, 144f)
        }

        // 1f
        if (d > 0f) {
            withTranslation(interpolate(d, 44f, 0f), 0f) {
                drawRect(
                    color2,
                    interpolate(d, 28f, 0f),
                    interpolate(d, 72f, 0f),
                    100f,
                    interpolate(d, 144f, 48f)
                )
                drawRect(color3, 28f, interpolate(d, 144f, 48f), 100f, 144f)
            }
        }
    }

    override fun Canvas<*>.drawTwoThree(
        glyphProgress: Float,
        paints: Paints,
    ) {
        val (color1, color2, color3) = paints.colors;
        val d1 = decelerate5(progress(glyphProgress, 0f, 0.5f));
        val d2 = decelerate5(progress(glyphProgress, 0.5f, 1.0f));

        // 2f
        if (d1 < 1f) {
            withTranslation(interpolate(d1, 0f, -16f), 0f) {
                withTranslation(interpolate(d1, 0f, 72f), 0f) {
                    drawPath(color3) {
                        moveTo(0f, 144f);
                        lineTo(72f, 72f);
                        lineTo(72f, 144f);
                        lineTo(0f, 144f);
                    }
                }

                if (d1 == 0f) {
                    drawPath(color1) {
                        moveTo(8f, 0f);
                        lineTo(108f, 0f);
                        boundedArc(108f - 36f, 0f, 108f + 36f, 72f);
                        lineTo(108f, 72f);
                        lineTo(8f, 72f);
                        lineTo(8f, 0f);
                    }
                } else {
                    drawPath(color1) {
                        boundedArc(
                            108f - 36f,
                            interpolate(d1, 0f, 72f),
                            108f + 36f,
                            72f + interpolate(d1, 0f, 72f)
                        )
                    }

                    drawRect(
                        color1,
                        interpolate(d1, 8f, 72f),
                        interpolate(d1, 0f, 72f),
                        interpolate(d1, 108f, 144f),
                        interpolate(d1, 72f, 144f),
                    );
                }
                drawRect(color2, 72f, 72f, 144f, 144f);
            }
            return;
        }
        // 3f
        // half-circle
        withScale(interpolate(d2, 0.7f, 1f), 128f, 144f) {
            drawPath(color3) {
                boundedArc(32f, 48f, 128f, 144f);
            }
        }

        // bottom rectangle
        drawRect(
            color1,
            interpolate(d2, 56f, 0f),
            interpolate(d2, 72f, 96f),
            interpolate(d2, 128f, 80f),
            interpolate(d2, 144f, 144f),
        );

        // top part with triangle
        withTranslation(0f, interpolate(d2, 72f, 0f)) {
            drawPath(color3) {
                moveTo(128f, 0f);
                lineTo(80f, 48f);
                lineTo(80f, 0f);
            }
            drawRect(
                color3,
                interpolate(d2, 56f, 0f),
                0f,
                interpolate(d2, 128f, 80f),
                interpolate(d2, 72f, 48f),
            )
        }

        // middle rectangle
        drawRect(
            color2,
            interpolate(d2, 56f, 32f),
            interpolate(d2, 72f, 48f),
            interpolate(d2, 128f, 80f),
            interpolate(d2, 144f, 96f),
        )
    }

    override fun Canvas<*>.drawThreeFour(
        glyphProgress: Float,
        paints: Paints,
    ) {
        val (color1, color2, color3) = paints.colors;
        val d1 = 1f - decelerate5(progress(glyphProgress, 0f, 0.5f));
        val d2 = decelerate5(progress(glyphProgress, 0.5f, 1f));

        // 3f
        if (d1 > 0f) {
            withTranslation(interpolate(d1, 16f, 0f), 0f) {
                // middle rectangle
                drawRect(
                    color2,
                    interpolate(d1, 56f, 32f),
                    interpolate(d1, 72f, 48f),
                    interpolate(d1, 128f, 80f),
                    interpolate(d1, 144f, 96f),
                );

                // half-circle
                withScale(
                    interpolate(d1, 0.7f, 1f),
                    128f,
                    144f,
                ) {
                    drawPath(color3) {
                        boundedArc(
                            32f,
                            48f,
                            128f,
                            144f
                        )
                    }
                }

                // bottom rectangle
                drawRect(
                    color1,
                    interpolate(d1, 56f, 0f),
                    interpolate(d1, 72f, 96f),
                    interpolate(d1, 128f, 80f),
                    interpolate(d1, 144f, 144f),
                );

                // top part with triangle
                withTranslation(0f, interpolate(d1, 72f, 0f)) {
                    drawPath(color3) {
                        moveTo(80f, 0f);
                        lineTo(128f, 0f);
                        lineTo(80f, 48f);

                        if (d1 == 1f) {
                            lineTo(0f, 48f);
                            lineTo(0f, 0f);
                            lineTo(80f, 0f);
                        }
                    }
                    if (d1 != 1f) {
                        drawRect(
                            color3,
                            interpolate(d1, 56f, 0f),
                            0f,
                            interpolate(d1, 128f, 80f),
                            interpolate(d1, 72f, 48f),
                        );
                    }
                }
            }
        } else {
            // 4f
            // bottom rectangle
            drawRect(color2, 72f, interpolate(d2, 144f, 108f), 144f, 144f);

            // middle rectangle
            drawRect(
                color1,
                interpolate(d2, 72f, 0f),
                interpolate(d2, 144f, 72f),
                144f,
                interpolate(d2, 144f, 108f),
            );

            // triangle
            withScale(d2, 144f, 144f) {
                drawPath(color2) {
                    moveTo(72f, 72f);
                    lineTo(72f, 0f);
                    lineTo(0f, 72f);
                    lineTo(72f, 72f);
                }
            }

            // top rectangle
            drawRect(
                color3,
                72f,
                interpolate(d2, 72f, 0f),
                144f,
                interpolate(d2, 144f, 72f),
            );
        }
    }

    override fun Canvas<*>.drawFourFive(
        glyphProgress: Float,
        paints: Paints,
    ) {

    }

    override fun Canvas<*>.drawFiveSix(
        glyphProgress: Float,
        paints: Paints,
    ) {

    }

    override fun Canvas<*>.drawSixSeven(
        glyphProgress: Float,
        paints: Paints,
    ) {

    }

    override fun Canvas<*>.drawSevenEight(
        glyphProgress: Float,
        paints: Paints,
    ) {

    }

    override fun Canvas<*>.drawEightNine(
        glyphProgress: Float,
        paints: Paints,
    ) {

    }

    override fun Canvas<*>.drawNineZero(
        glyphProgress: Float,
        paints: Paints,
    ) {

    }

    override fun Canvas<*>.drawOneZero(
        glyphProgress: Float,
        paints: Paints,
    ) {

    }

    override fun Canvas<*>.drawTwoZero(
        glyphProgress: Float,
        paints: Paints,
    ) {

    }

    override fun Canvas<*>.drawThreeZero(
        glyphProgress: Float,
        paints: Paints,
    ) {

    }

    override fun Canvas<*>.drawFiveZero(
        glyphProgress: Float,
        paints: Paints,
    ) {

    }

    override fun Canvas<*>.drawOneEmpty(
        glyphProgress: Float,
        paints: Paints,
    ) {

    }

    override fun Canvas<*>.drawTwoEmpty(
        glyphProgress: Float,
        paints: Paints,
    ) {

    }

    override fun Canvas<*>.drawEmptyOne(
        glyphProgress: Float,
        paints: Paints,
    ) {

    }

    override fun Canvas<*>.drawSeparator(
        glyphProgress: Float,
        paints: Paints,
    ) {

    }

    override fun Canvas<*>.drawSpace(
        glyphProgress: Float,
        paints: Paints,
    ) {

    }

    override fun Canvas<*>.drawUnderscore(
        glyphProgress: Float,
        paints: Paints,
    ) {

    }

    override fun Canvas<*>.drawHash(
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
