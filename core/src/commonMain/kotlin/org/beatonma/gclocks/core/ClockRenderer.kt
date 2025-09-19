package org.beatonma.gclocks.core

import org.beatonma.gclocks.core.geometry.MutableRectF
import org.beatonma.gclocks.core.geometry.Rect
import org.beatonma.gclocks.core.geometry.Size
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.graphics.Stroke
import org.beatonma.gclocks.core.graphics.drawDebugRect
import org.beatonma.gclocks.core.layout.ClockLayout
import org.beatonma.gclocks.core.util.debug


/**
 * Draws a [ClockLayout] to a [org.beatonma.gclocks.core.graphics.Canvas].
 */
interface ClockRenderer<P : Paints, G : ClockGlyph<P>> {
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

                    withTranslationAndScale(rect.left, rect.top, glyph.scale) {
                        drawGlyph(glyph, canvas, glyphAnimationProgress, paints)
                    }

                    debug(false) {
                        canvas.debugDrawGlyphBoundary(rect)
                    }
                }
            }

            debug(false) {
                // Show bounds at native scale
                canvas.debugDrawBounds(drawBounds, nativeSize)
                layout.layoutPass { glyph, glyphAnimationProgress, rect ->
                    canvas.debugDrawGlyphBoundary(rect)
                }
            }
        }
    }

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
            // Render a skeleton view of any active paths
            canvas.drawPath(Color.Black, Stroke.Default)
        }
    }

    private fun Canvas.debugDrawBounds(drawBounds: Rect<Float>, nativeSize: Size<Float>) {
        drawDebugRect(Color.Green, drawBounds)
        drawDebugRect(Color.Yellow, nativeSize.toRect())
    }

    private fun Canvas.debugDrawGlyphBoundary(boundary: Rect<Float>) {
        drawDebugRect(Color.Magenta, MutableRectF(boundary).extrude(paints.strokeWidth / 2f))
    }
}
