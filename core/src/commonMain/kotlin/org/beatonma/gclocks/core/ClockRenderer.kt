package org.beatonma.gclocks.core

import org.beatonma.gclocks.core.geometry.Rect
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.graphics.Stroke
import org.beatonma.gclocks.core.layout.ClockLayout
import org.beatonma.gclocks.core.util.debug


/**
 * Draws a [ClockLayout] to a [org.beatonma.gclocks.core.graphics.Canvas].
 */
interface ClockRenderer<P : Paints, G : BaseClockGlyph<P>> {
    val renderer: GlyphRenderer<P, G>?
    val paints: P

    fun draw(canvas: Canvas, layout: ClockLayout<P, G>) {
        if (!layout.isDrawable) {
            debug("layout is not drawable! $layout")
            return
        }

        layout.measureFrame { x, y, scale ->
            canvas.withTranslationAndScale(x, y, scale) {
                layout.layoutPass { glyph, glyphAnimationProgress, rect ->
                    if (rect.isEmpty) return@layoutPass

                    debug(false) {
                        drawGlyphBoundary(canvas, paints, rect)
                    }

                    canvas.withTranslationAndScale(rect.left, rect.top, glyph.scale) {
                        drawGlyph(glyph, canvas, glyphAnimationProgress, paints)
                    }
                }

                debug(false) {
                    // Show scaled bounds
                    canvas.drawRect(Color.Green, drawBounds, Stroke.Default)
                    canvas.drawRect(Color.Yellow, nativeSize.toRect(), Stroke.Default)
                }
            }

            debug(false) {
                // Show bounds at native scale
                canvas.drawRect(Color.Green, drawBounds, Stroke.Default)
                canvas.drawRect(Color.Yellow, nativeSize.toRect(), Stroke.Default)
            }
        }
    }

    fun drawGlyphBoundary(canvas: Canvas, paints: P, boundary: Rect<Float>) {
        canvas.drawRect(Color.Grey, boundary, Stroke.Default)
        canvas.drawLine(
            Color.Grey,
            boundary.left,
            boundary.top,
            boundary.right,
            boundary.bottom
        )
        canvas.drawLine(
            Color.Grey,
            boundary.right,
            boundary.top,
            boundary.left,
            boundary.bottom
        )
    };

    fun drawGlyph(
        glyph: G,
        canvas: Canvas,
        glyphAnimationProgress: Float,
        paints: P,
    ) {
        glyph.draw(canvas, glyphAnimationProgress, paints, renderer?.let { renderer ->
            { renderer.draw(glyph, canvas, paints) }
        })

        debug(false) {
            canvas.drawPath(Color.Black, Stroke.Default)
        }
    }
}