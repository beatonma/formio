package org.beatonma.gclocks.form

import org.beatonma.gclocks.core.BaseClockGlyph
import org.beatonma.gclocks.core.GlyphRole
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.ClockFont
import org.beatonma.gclocks.core.GlyphCompanion
import org.beatonma.gclocks.core.geometry.Angle
import org.beatonma.gclocks.core.geometry.FloatSize
import org.beatonma.gclocks.core.geometry.Size
import org.beatonma.gclocks.core.geometry.degrees
import org.beatonma.gclocks.core.graphics.GenericCanvas
import org.beatonma.gclocks.core.options.HorizontalAlignment
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.core.options.TimeFormat
import org.beatonma.gclocks.core.options.TimeResolution
import org.beatonma.gclocks.core.options.VerticalAlignment
import org.beatonma.gclocks.core.util.decelerate5
import org.beatonma.gclocks.core.util.interpolate
import org.beatonma.gclocks.core.util.progress


private const val TwoThirds = 2f / 3f

data class FormOptions(
    override val horizontalAlignment: HorizontalAlignment = HorizontalAlignment.Default,
    override val verticalAlignment: VerticalAlignment = VerticalAlignment.Default,
    override val layout: Layout = Layout.Horizontal,
    override val format: TimeFormat = TimeFormat.HH_MM_SS_24,
    override val spacingPx: Int = 8,
    override val glyphMorphMillis: Int = 800,
    override val secondsGlyphScale: Float = Options.DefaultSecondsGlyphScale,
    override val strokeWidth: Float = 0f,
) : Options


class FormFont : ClockFont<FormGlyph> {
    override fun getGlyphAt(
        index: Int, format: TimeFormat,
        secondsGlyphScale: Float,
    ): FormGlyph {
        val role = format.roles.getOrNull(index) ?: GlyphRole.Default
        val scale = when (role) {
            GlyphRole.Second -> secondsGlyphScale
            else -> 1f
        }
        return FormGlyph(role, scale)
    }

    override fun measure(
        format: TimeFormat,
        layout: Layout,
        spacingPx: Int,
        secondsGlyphScale: Float,
    ): Size<Float> {
        val hasSeconds = format.resolution == TimeResolution.Seconds

        val lineHeight = FormGlyph.maxSize.y
        val separatorWidth = 48f
        val pairWidth = 352f // max width of a pair of digits

        return when (layout) {
            Layout.Horizontal -> {
                val digitsWidth = 816f
                val spacingWidth =
                    spacingPx.toFloat() * (if (hasSeconds) 5f + secondsGlyphScale else 4f)
                return FloatSize(
                    digitsWidth + separatorWidth + spacingWidth, lineHeight
                )
            }

            Layout.Vertical -> FloatSize(
                (pairWidth + spacingPx).toFloat(),
                lineHeight * (2f + (if (hasSeconds) secondsGlyphScale else 0f) + (spacingPx.toFloat() * 2f))
            )

            Layout.Wrapped -> FloatSize(
                (pairWidth * 2f) + (spacingPx * 4f),
                (lineHeight * (1f + (if (hasSeconds) secondsGlyphScale else 0f))) + spacingPx.toFloat()
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

    override fun GenericCanvas.drawZeroOne(
        glyphProgress: Float,
        paints: Paints,
    ) {
        val (_, color2, color3) = paints.colors
        val d1 = decelerate5(progress(glyphProgress, 0f, 0.5f))
        val d2 = decelerate5(progress(glyphProgress, 0.5f, 1f))

        // 0
        withTranslation(
            interpolate(d1, interpolate(d1, 0f, 24f), interpolate(d2, 24f, 0f)),
            0f
        ) {
            withScale(interpolate(d1, 1f, TwoThirds), 72f, 144f) {
                withScale(interpolate(d2, 1f, 0.7f), 72f, 96f) {
                    withRotation(interpolate(d1, 45f, 0f).degrees, 72f, 72f) {
                        val stretchX = interpolate(d1, 0f, interpolate(d2, 72f, -36f))
                        drawPath(color2) {
                            moveTo(72f - stretchX, 144f)
                            boundedArc(
                                -stretchX,
                                0f,
                                144f - stretchX,
                                144f,
                                Angle.Ninety,
                            )
                            lineTo(72f + stretchX, 0f)
                            lineTo(72f + stretchX, 144f)
                            lineTo(72f - stretchX, 144f)
                        }

                        drawBoundedArc(
                            color3,
                            stretchX,
                            0f,
                            144f + stretchX,
                            144f,
                        )
                    }
                }
            }
        }

        // 1
        if (d2 > 0f) {
            drawRect(
                color2,
                interpolate(d2, 28f, 0f),
                interpolate(d2, 72f, 0f),
                100f,
                interpolate(d2, 144f, 48f)
            )
            drawRect(color3, 28f, interpolate(d2, 144f, 48f), 100f, 144f)
        }
    }

    override fun GenericCanvas.drawOneTwo(
        glyphProgress: Float,
        paints: Paints,
    ) {
        val (color1, color2, color3) = paints.colors
        val d = 1f - decelerate5(progress(glyphProgress, 0f, 0.5f))
        val d1 = decelerate5(progress(glyphProgress, 0.3f, 0.8f))
        val d2 = decelerate5(progress(glyphProgress, 0.5f, 1.0f))

        // 2
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
                drawBoundedArc(color1, -36f, 0f, 36f, 72f)
            }

            withTranslation(0f, interpolate(d1, 72f, 0f)) {
                drawRect(color1, interpolate(d2, 72f, 8f), 0f, interpolate(d2, 144f, 108f), 72f)
            }

            drawRect(color2, 72f, 72f, 144f, 144f)
        }

        // 1
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

    override fun GenericCanvas.drawTwoThree(
        glyphProgress: Float,
        paints: Paints,
    ) {
        val (color1, color2, color3) = paints.colors
        val d1 = decelerate5(progress(glyphProgress, 0f, 0.5f))
        val d2 = decelerate5(progress(glyphProgress, 0.5f, 1.0f))

        // 2
        if (d1 < 1f) {
            withTranslation(interpolate(d1, 0f, -16f), 0f) {
                withTranslation(interpolate(d1, 0f, 72f), 0f) {
                    drawPath(color3) {
                        moveTo(0f, 144f)
                        lineTo(72f, 72f)
                        lineTo(72f, 144f)
                        lineTo(0f, 144f)
                    }
                }

                if (d1 == 0f) {
                    drawPath(color1) {
                        moveTo(8f, 0f)
                        lineTo(108f, 0f)
                        boundedArc(108f - 36f, 0f, 108f + 36f, 72f)
                        lineTo(108f, 72f)
                        lineTo(8f, 72f)
                        lineTo(8f, 0f)
                    }
                } else {
                    drawBoundedArc(
                        color1,
                        108f - 36f,
                        interpolate(d1, 0f, 72f),
                        108f + 36f,
                        72f + interpolate(d1, 0f, 72f)
                    )

                    drawRect(
                        color1,
                        interpolate(d1, 8f, 72f),
                        interpolate(d1, 0f, 72f),
                        interpolate(d1, 108f, 144f),
                        interpolate(d1, 72f, 144f),
                    )
                }
                drawRect(color2, 72f, 72f, 144f, 144f)
            }
        } else {
            // 3
            // half-circle
            withScale(interpolate(d2, 0.7f, 1f), 128f, 144f) {
                drawBoundedArc(color3, 32f, 48f, 128f, 144f)
            }

            // bottom rectangle
            drawRect(
                color1,
                interpolate(d2, 56f, 0f),
                interpolate(d2, 72f, 96f),
                interpolate(d2, 128f, 80f),
                interpolate(d2, 144f, 144f),
            )

            // top part with triangle
            withTranslation(0f, interpolate(d2, 72f, 0f)) {
                drawPath(color3) {
                    moveTo(128f, 0f)
                    lineTo(80f, 48f)
                    lineTo(80f, 0f)
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
    }

    override fun GenericCanvas.drawThreeFour(
        glyphProgress: Float,
        paints: Paints,
    ) {
        val (color1, color2, color3) = paints.colors
        val d1 = 1f - decelerate5(progress(glyphProgress, 0f, 0.5f))
        val d2 = decelerate5(progress(glyphProgress, 0.5f, 1f))

        // 3
        if (d1 > 0f) {
            withTranslation(interpolate(d1, 16f, 0f), 0f) {
                // middle rectangle
                drawRect(
                    color2,
                    interpolate(d1, 56f, 32f),
                    interpolate(d1, 72f, 48f),
                    interpolate(d1, 128f, 80f),
                    interpolate(d1, 144f, 96f),
                )

                // half-circle
                withScale(
                    interpolate(d1, 0.7f, 1f),
                    128f,
                    144f,
                ) {
                    drawBoundedArc(color3, 32f, 48f, 128f, 144f)
                }

                // bottom rectangle
                drawRect(
                    color1,
                    interpolate(d1, 56f, 0f),
                    interpolate(d1, 72f, 96f),
                    interpolate(d1, 128f, 80f),
                    interpolate(d1, 144f, 144f),
                )

                // top part with triangle
                withTranslation(0f, interpolate(d1, 72f, 0f)) {
                    drawPath(color3) {
                        moveTo(80f, 0f)
                        lineTo(128f, 0f)
                        lineTo(80f, 48f)

                        if (d1 == 1f) {
                            lineTo(0f, 48f)
                            lineTo(0f, 0f)
                            lineTo(80f, 0f)
                        }
                    }
                    if (d1 != 1f) {
                        drawRect(
                            color3,
                            interpolate(d1, 56f, 0f),
                            0f,
                            interpolate(d1, 128f, 80f),
                            interpolate(d1, 72f, 48f),
                        )
                    }
                }
            }
        } else {
            // 4
            // bottom rectangle
            drawRect(color2, 72f, interpolate(d2, 144f, 108f), 144f, 144f)

            // middle rectangle
            drawRect(
                color1,
                interpolate(d2, 72f, 0f),
                interpolate(d2, 144f, 72f),
                144f,
                interpolate(d2, 144f, 108f),
            )

            // triangle
            withScale(d2, 144f, 144f) {
                drawPath(color2) {
                    moveTo(72f, 72f)
                    lineTo(72f, 0f)
                    lineTo(0f, 72f)
                    lineTo(72f, 72f)
                }
            }

            // top rectangle
            drawRect(
                color3,
                72f,
                interpolate(d2, 72f, 0f),
                144f,
                interpolate(d2, 144f, 72f),
            )
        }
    }

    override fun GenericCanvas.drawFourFive(
        glyphProgress: Float,
        paints: Paints,
    ) {
        val (color1, color2, color3) = paints.colors
        val d = decelerate5(progress(glyphProgress, 0f, 0.5f))
        val d1 = decelerate5(progress(glyphProgress, 0.5f, 1f))

        // 4
        if (d < 1f) {
            // bottom rectangle
            drawRect(
                color2,
                interpolate(d, 72f, 0f),
                108f,
                interpolate(d, 144f, 72f),
                144f,
            )

            // top rectangle
            drawRect(
                color3,
                interpolate(d, 72f, 0f),
                interpolate(d, 0f, 72f),
                interpolate(d, 144f, 72f),
                interpolate(d, 72f, 144f),
            )

            // triangle
            withScale(1f - d, 0f, 144f) {
                drawPath(color2) {
                    moveTo(72f, 72f)
                    lineTo(72f, 0f)
                    lineTo(0f, 72f)
                    lineTo(72f, 72f)
                }
            }

            // middle rectangle
            drawRect(
                color1,
                0f,
                72f,
                interpolate(d, 144f, 72f),
                interpolate(d, 108f, 144f),
            )
        } else {
            // 5
            // wing rectangle
            drawRect(
                color2,
                80f,
                interpolate(d1, 72f, 0f),
                interpolate(d1, 80f, 128f),
                interpolate(d1, 144f, 48f),
            )

            // half-circle
            withScale(interpolate(d1, 0.75f, 1f), 0f, 144f) {
                withTranslation(interpolate(d1, -48f, 0f), 0f) {
                    drawBoundedArc(color3, 32f, 48f, 128f, 144f)
                }
            }

            // bottom rectangle
            drawRect(color2, 0f, 96f, 80f, 144f)

            // middle rectangle
            drawRect(
                color1,
                0f,
                interpolate(d1, 72f, 0f),
                80f,
                interpolate(d1, 144f, 96f),
            )
        }
    }

    override fun GenericCanvas.drawFiveSix(
        glyphProgress: Float,
        paints: Paints,
    ) {
        val (color1, color2, color3) = paints.colors
        val d = decelerate5(progress(glyphProgress, 0f, 0.7f))
        val d1 = decelerate5(progress(glyphProgress, 0.1f, 1f))

        // 5 (except half-circle)
        if (d < 1f) {
            withScale(interpolate(d, 1f, 0.25f), 108f, 96f) {
                // wing rectangle
                drawRect(color2, 80f, 0f, 128f, 48f)

                // bottom rectangle
                drawRect(color2, 0f, 96f, 80f, 144f)

                // middle rectangle
                drawRect(color1, 0f, 0f, 80f, 96f)
            }
        }

        // half-circle
        withRotation(interpolate(d1, 0f, 90f).degrees, 72f, 72f) {
            if (d1 == 0f) {
                drawBoundedArc(color3, 32f, 48f, 128f, 144f)
            } else {
                withScale(
                    interpolate(d1, TwoThirds, 1f),
                    80f,
                    144f,
                ) {
                    withTranslation(interpolate(d1, 8f, 0f), 0f) {
                        drawBoundedArc(color3, 0f, 0f, 144f, 144f)
                    }
                }
            }

            // 6 (just the parallelogram)
            if (d1 > 0f) {
                withRotation(Angle.TwoSeventy, 72f, 72f) {
                    drawPath(color2) {
                        moveTo(0f, 72f)
                        lineTo(
                            interpolate(d1, 0f, 36f),
                            interpolate(d1, 72f, 0f),
                        )
                        lineTo(
                            interpolate(d1, 72f, 108f),
                            interpolate(d1, 72f, 0f),
                        )
                        lineTo(72f, 72f)
                        lineTo(0f, 72f)
                    }
                }
            }
        }
    }

    override fun GenericCanvas.drawSixSeven(
        glyphProgress: Float,
        paints: Paints,
    ) {
        val (_, color2, color3) = paints.colors
        val d = decelerate5(glyphProgress)

        // 7 rectangle
        drawRect(color3, interpolate(d, 72f, 0f), 0f, 72f, 72f)

        // 6 circle
        withTranslation(interpolate(d, 0f, 36f), 0f) {
            if (d < 1f) {
                drawBoundedArc(
                    color3,
                    0f, 0f, 144f, 144f,
                    interpolate(d, 180f, -64f).degrees, (-180f).degrees
                )
            }

            // parallelogram
            drawPath(color2) {
                moveTo(36f, 0f)
                lineTo(108f, 0f)
                lineTo(interpolate(d, 72f, 36f), interpolate(d, 72f, 144f))
                lineTo(interpolate(d, 0f, -36f), interpolate(d, 72f, 144f))
            }
        }
    }

    override fun GenericCanvas.drawSevenEight(
        glyphProgress: Float,
        paints: Paints,
    ) {
        val (color1, color2, color3) = paints.colors
        val d1 = decelerate5(progress(glyphProgress, 0f, 0.5f))
        val d2 = decelerate5(progress(glyphProgress, 0.2f, 0.5f))
        val d3 = decelerate5(progress(glyphProgress, 0.5f, 1f))

        if (d1 == 0f) {
            // 7 'rectangle', drawn as a path to avoid unnecessary overlapping
            // (because overlapping looks weird with transparent colors)
            drawPath(color3) {
                moveTo(0f, 0f)
                lineTo(72f, 0f)
                lineTo(36f, 72f)
                lineTo(0f, 72f)
            }

            // 7 parallelogram
            drawPath(color2) {
                moveTo(interpolate(d1, 72f, 48f), interpolate(d1, 0f, 96f))
                lineTo(interpolate(d1, 144f, 96f), interpolate(d1, 0f, 96f))
                lineTo(interpolate(d1, 72f, 96f), 144f)
                lineTo(interpolate(d1, 0f, 48f), 144f)
            }
        } else {
            // 8
            if (d2 > 0f) {
                if (d3 > 0f) {
                    // top
                    withTranslation(0f, interpolate(d3, 96f, 0f)) {
                        drawRoundRect(color3, 24f, 0f, 120f, 48f, 24f)
                    }
                }

                // left bottom
                withTranslation(interpolate(d2, 24f, 0f), 0f) {
                    withScale(
                        interpolate(d3, 0.5f, 1f),
                        48f,
                        144f,
                    ) {
                        drawBoundedArc(color1, 0f, 48f, 96f, 144f, Angle.Ninety)
                    }
                }

                // right bottom
                withTranslation(interpolate(d2, -24f, 0f), 0f) {
                    withScale(
                        interpolate(d3, 0.5f, 1f),
                        96f,
                        144f,
                    ) {
                        drawBoundedArc(color2, 48f, 48f, 144f, 144f)
                    }
                }

                // bottom middle
                withScale(interpolate(d2, 0f, 1f), 1f, 72f, 0f) {
                    drawRect(
                        color1,
                        48f,
                        interpolate(d3, 96f, 48f),
                        96f,
                        144f,
                    )
                    drawRect(
                        color2,
                        interpolate(d3, 48f, 96f),
                        interpolate(d3, 96f, 48f),
                        96f,
                        144f,
                    )
                }
            }

            if (d1 < 1f) {
                // 7 rectangle
                drawRect(
                    color3,
                    interpolate(d1, 0f, 48f),
                    interpolate(d1, 0f, 96f),
                    interpolate(d1, 72f, 96f),
                    interpolate(d1, 72f, 144f),
                )

                // 7 parallelogram
                drawPath(color2) {
                    moveTo(
                        interpolate(d1, 72f, 48f),
                        interpolate(d1, 0f, 96f),
                    )
                    lineTo(
                        interpolate(d1, 144f, 96f),
                        interpolate(d1, 0f, 96f),
                    )
                    lineTo(interpolate(d1, 72f, 96f), 144f)
                    lineTo(interpolate(d1, 0f, 48f), 144f)
                }
            }
        }
    }

    override fun GenericCanvas.drawEightNine(
        glyphProgress: Float,
        paints: Paints,
    ) {
        val (color1, color2, color3) = paints.colors
        val d1 = decelerate5(progress(glyphProgress, 0f, 0.5f))
        val d2 = decelerate5(progress(glyphProgress, 0.5f, 1f))

        if (d1 < 1f) {
            // top
            withTranslation(0f, interpolate(d1, 0f, 48f)) {
                drawRoundRect(color3, 24f, 0f, 120f, 48f, 24f)
            }

            if (d1 == 0f) {
                // left + middle bottom
                drawPath(color1) {
                    moveTo(48f, 48f)
                    lineTo(96f, 48f);
                    lineTo(96f, 144f);
                    lineTo(48f, 144f);
                    boundedArc(0f, 48f, 96f, 144f, Angle.Ninety);
                }

                // right bottom
                drawBoundedArc(color2, 48f, 48f, 144f, 144f)
            } else {
                // bottom middle
                drawRect(
                    color1,
                    interpolate(d1, 48f, 72f) - 2f,
                    interpolate(d1, 48f, 0f),
                    interpolate(d1, 96f, 72f) + 2f,
                    144f
                )

                // left bottom
                withScale(interpolate(d1, TwoThirds, 1f), 0f, 144f) {
                    drawBoundedArc(color1, 0f, 0f, 144f, 144f, Angle.Ninety)
                }

                // right bottom
                withScale(interpolate(d1, TwoThirds, 1f), 144f, 144f) {
                    drawBoundedArc(color2, 0f, 0f, 144f, 144f)
                }
            }
        } else {
            // 9
            withRotation(interpolate(d2, -90f, -180f).degrees, 72f, 72f) {
                drawPath(color3) {
                    moveTo(0f, 72f);
                    lineTo(
                        interpolate(d2, 0f, 36f),
                        interpolate(d2, 72f, 0f),
                    );
                    lineTo(
                        interpolate(d2, 72f, 108f),
                        interpolate(d2, 72f, 0f),
                    );
                    lineTo(72f, 72f);
                    lineTo(0f, 72f);
                }

                // vanishing arc
                drawBoundedArc(
                    color1,
                    0f,
                    0f,
                    144f,
                    144f,
                    interpolate(d2, 180f, 0f).degrees,
                );

                // primary arc
                drawBoundedArc(color2, 0f, 0f, 144f, 144f, Angle.Zero);
            }
        }
    }

    override fun GenericCanvas.drawNineZero(
        glyphProgress: Float,
        paints: Paints,
    ) {
        val (_, color2, color3) = paints.colors;
        val d = decelerate5(glyphProgress);

        // 9
        withRotation(interpolate(d, -180f, -225f).degrees, 72f, 72f) {
            // parallelogram
            drawPath(color3) {
                moveTo(0f, 72f);
                lineTo(interpolate(d, 36f, 0f), interpolate(d, 0f, 72f));
                lineTo(interpolate(d, 108f, 72f), interpolate(d, 0f, 72f));
                lineTo(72f, 72f);
                lineTo(0f, 72f);
            }

            drawBoundedArc(
                color3,
                0f,
                0f,
                144f,
                144f,
                interpolate(d, 180f, 0f).degrees,
                (-180f).degrees
            );
            drawBoundedArc(color2, 0f, 0f, 144f, 144f, Angle.Zero);
        }
    }

    override fun GenericCanvas.drawOneZero(
        glyphProgress: Float,
        paints: Paints,
    ) {
        drawZeroOne(1f - glyphProgress, paints)
    }

    override fun GenericCanvas.drawTwoZero(
        glyphProgress: Float,
        paints: Paints,
    ) {
        val (color1, color2, color3) = paints.colors
        val d1 = decelerate5(progress(glyphProgress, 0f, 0.5f))
        val d2 = decelerate5(progress(glyphProgress, 0.5f, 1f))

        // 2
        if (d2 == 0f) {
            withTranslation(interpolate(d1, 0f, -16f), 0f) {
                withTranslation(interpolate(d1, 0f, 72f), 0f) {
                    drawPath(color3) {
                        moveTo(0f, 144f)
                        lineTo(72f, 72f)
                        lineTo(72f, 144f)
                        lineTo(0f, 144f)
                    }
                }

                if (d1 == 0f) {
                    drawPath(color1) {
                        moveTo(8f, 0f)
                        lineTo(108f, 0f)
                        boundedArc(72f, 0f, 144f, 72f)
                        lineTo(108f, 72f)
                        lineTo(8f, 72f)
                        lineTo(8f, 0f)
                    }
                } else {
                    drawBoundedArc(
                        color1,
                        108f - 36f,
                        interpolate(d1, 0f, 72f),
                        108f + 36f,
                        72f + interpolate(d1, 0f, 72f)
                    )

                    drawRect(
                        color1,
                        interpolate(d1, 8f, 72f),
                        interpolate(d1, 0f, 72f),
                        interpolate(d1, 108f, 144f),
                        interpolate(d1, 72f, 144f),
                    )
                }
                drawBoundedArc(color3, 108f, 72f, 180f, 144f, interpolate(d1, 90f, 270f).degrees)
                drawRect(color2, 72f, 72f, 144f, 144f)
            }
        }

        // 0
        // half-circles
        if (d2 > 0f) {
            withRotation(interpolate(d2, 0f, 45f).degrees, 72f, 72f) {
                withTranslation(interpolate(d2, -16f, 0f), 0f) {
                    drawRect(
                        color2,
                        72f,
                        72f,
                        interpolate(d2, 144f, 72f),
                        144f
                    )
                    drawBoundedArc(
                        color2,
                        interpolate(d2, 108f, 0f),
                        interpolate(d2, 72f, 0f),
                        interpolate(d2, 180f, 144f),
                        144f,
                        interpolate(d2, -90f, 90f).degrees,
                    )
                    drawBoundedArc(
                        color3,
                        interpolate(d2, 108f, 0f),
                        interpolate(d2, 72f, 0f),
                        interpolate(d2, 180f, 144f),
                        144f
                    )
                }
            }
        }
    }

    override fun GenericCanvas.drawThreeZero(
        glyphProgress: Float,
        paints: Paints,
    ) {
        val (color1, color2, color3) = paints.colors;
        val d1 = 1f - decelerate5(progress(glyphProgress, 0f, 0.5f));
        val d2 = decelerate5(progress(glyphProgress, 0.5f, 1f));

        withRotation(interpolate(d2, 0f, 45f).degrees, 72f, 72f) {
            withTranslation(interpolate(d1, interpolate(d2, 16f, -8f), 0f), 0f) {
                if (d1 > 0f) {
                    // top part of 3 with triangle
                    withTranslation(0f, interpolate(d1, 48f, 0f)) {
                        drawPath(color3) {
                            val x = interpolate(d1, 48f, 0f);
                            moveTo(128f - x, 0f);
                            lineTo(80f - x, 48f);
                            lineTo(80f - x, 0f);
                        }
                        drawRect(color3, interpolate(d1, 32f, 0f), 0f, 80f, 48f);
                    }
                }

                // bottom rectangle in 3
                drawRect(
                    color1,
                    interpolate(d1, interpolate(d2, 32f, 80f), 0f),
                    96f,
                    80f,
                    144f,
                );

                // middle rectangle
                drawRect(color2, interpolate(d2, 32f, 80f), 48f, 80f, 96f);

                // 0
                // half-circles
                withScale(interpolate(d2, TwoThirds, 1f), 80f, 144f) {
                    withTranslation(8f, 0f) {
                        if (d2 > 0f) {
                            withRotation(interpolate(d2, -180f, 0f).degrees, 72f, 72f) {
                                drawBoundedArc(
                                    color2,
                                    0f,
                                    0f,
                                    144f,
                                    144f,
                                    Angle.Ninety,
                                );
                            }
                        }

                        drawBoundedArc(color3, 0f, 0f, 144f, 144f);
                    }
                }
            }
        }
    }

    override fun GenericCanvas.drawFiveZero(
        glyphProgress: Float,
        paints: Paints,
    ) {
        val (color1, color2, color3) = paints.colors;
        val d = decelerate5(progress(glyphProgress, 0f, 0.5f))
        val d1 = decelerate5(progress(glyphProgress, 0.5f, 1f))

        withRotation(interpolate(d1, 0f, 45f).degrees, 72f, 72f) {
            // 5f (except half-circle)
            if (d < 1f) {
                // wing rectangle
                drawRect(
                    color2,
                    80f,
                    interpolate(d, 0f, 48f),
                    interpolate(d, 128f, 80f),
                    interpolate(d, 48f, 144f),
                )

                // bottom rectangle
                drawRect(color2, 0f, 96f, 80f, 144f)
            }

            // middle rectangle
            drawRect(
                color1,
                interpolate(d1, 0f, 80f),
                interpolate(d, 0f, interpolate(d1, 48f, 0f)),
                80f,
                interpolate(d, 96f, 144f),
            )


            withScale(interpolate(d1, TwoThirds, 1f), 80f, 144f) {
                // half-circles
                if (d1 > 0f) {
                    withRotation(interpolate(d1, -180f, 0f).degrees, 72f, 72f) {
                        drawBoundedArc(color2, 0f, 0f, 144f, 144f, Angle.Ninety)
                    }
                }

                withTranslation(interpolate(d1, 8f, 0f), 0f) {
                    drawBoundedArc(color3, 0f, 0f, 144f, 144f)
                }
            }
        }
    }

    override fun GenericCanvas.drawOneEmpty(
        glyphProgress: Float,
        paints: Paints,
    ) {
        val (_, color2, color3) = paints.colors;
        val d1 = decelerate5(progress(glyphProgress, 0f, 0.5f));
        val d2 = decelerate5(progress(glyphProgress, 0.5f, 1f));

        withScale(interpolate(d2, 1f, 0f), 0f, 144f) {
            drawRect(
                color2,
                interpolate(d1, 0f, 28f),
                interpolate(d1, 0f, 72f),
                100f,
                interpolate(d1, 48f, 144f),
            );

            if (d1 < 1f) {
                drawRect(color3, 28f, interpolate(d1, 48f, 144f), 100f, 144f);
            }
        }
    }

    override fun GenericCanvas.drawTwoEmpty(
        glyphProgress: Float,
        paints: Paints,
    ) {
        val (color1, color2, color3) = paints.colors;
        val d = decelerate5(progress(glyphProgress, 0f, 0.5f));
        val d1 = decelerate5(progress(glyphProgress, 0.5f, 1.0f));

        // 2f
        withTranslation(interpolate(d, 0f, -72f), 0f) {
            if (d < 1f) {
                withTranslation(interpolate(d, 0f, 72f), 0f) {
                    drawPath(color3) {
                        beginPath();
                        moveTo(0f, 144f);
                        lineTo(72f, 72f);
                        lineTo(72f, 144f);
                        lineTo(0f, 144f);
                    }
                }

                withTranslation(108f, interpolate(d, 0f, 72f)) {
                    drawBoundedArc(color1, -36f, 0f, 36f, 72f);
                }

                drawRect(
                    color1,
                    interpolate(d, 8f, 72f),
                    interpolate(d, 0f, 72f),
                    interpolate(d, 108f, 144f),
                    interpolate(d, 72f, 144f),
                );
            }

            withScale(interpolate(d1, 1f, 0f), 72f, 144f) {
                drawRect(color2, 72f, 72f, 144f, 144f);
            }
        }
    }

    override fun GenericCanvas.drawEmptyOne(
        glyphProgress: Float,
        paints: Paints,
    ) {
        val (_, color2, color3) = paints.colors;
        val d1 = decelerate5(progress(glyphProgress, 0f, 0.5f))
        val d2 = decelerate5(progress(glyphProgress, 0.5f, 1f))

        // 1f
        withScale(interpolate(d1, 0f, 1f), 0f, 144f) {
            drawRect(
                color2,
                interpolate(d2, 28f, 0f),
                interpolate(d2, 72f, 0f),
                100f,
                interpolate(d2, 144f, 48f),
            )

            if (d2 > 0f) {
                drawRect(color3, 28f, interpolate(d2, 144f, 48f), 100f, 144f)
            }
        }
    }

    override fun GenericCanvas.drawSeparator(
        glyphProgress: Float,
        paints: Paints,
    ) {
        val (_, color2, color3) = paints.colors
        drawCircle(color2, 24f, 24f, 24f);
        drawCircle(color3, 24f, 120f, 24f);
    }

    override fun GenericCanvas.drawSpace(
        glyphProgress: Float,
        paints: Paints,
    ) {
        // Page intentionally blank
    }

    override fun GenericCanvas.drawHash(
        glyphProgress: Float,
        paints: Paints,
    ) {
        // Page intentionally blank
    }

    override fun getWidthAtProgress(glyphProgress: Float): Float {
        return when (key) {
            "0", "0_1" -> interpolate(
                decelerate5(progress(glyphProgress, 0.5f, 1f)),
                interpolate(
                    decelerate5(progress(glyphProgress, 0f, 0.4f)), 144f, 192f
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
            ":" -> when (role) {
                GlyphRole.SeparatorHoursMinutes -> 48f
                else -> 0f
            }

            "#", " ", "_" -> 0f

            else -> throw IllegalArgumentException("getWidthAtProgress unhandled key $key")
        }
    }
}
