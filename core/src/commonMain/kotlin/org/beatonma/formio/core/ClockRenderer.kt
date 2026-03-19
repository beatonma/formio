package org.beatonma.formio.core

import org.beatonma.formio.core.geometry.MutableRectF
import org.beatonma.formio.core.geometry.Rect
import org.beatonma.formio.core.geometry.Size
import org.beatonma.formio.core.glyph.ClockGlyph
import org.beatonma.formio.core.glyph.GlyphRenderer
import org.beatonma.formio.core.glyph.GlyphState
import org.beatonma.formio.core.glyph.GlyphVisibility
import org.beatonma.formio.core.graphics.Canvas
import org.beatonma.formio.core.graphics.Color
import org.beatonma.formio.core.graphics.Paints
import org.beatonma.formio.core.graphics.Stroke
import org.beatonma.formio.core.graphics.drawDebugRect
import org.beatonma.formio.core.layout.ClockLayout
import org.beatonma.formio.core.util.debug


/**
 * Draws a [ClockLayout] to a [org.beatonma.formio.core.graphics.Canvas].
 */
interface ClockRenderer<G : ClockGlyph> {
    val renderer: GlyphRenderer<G>?
    val paints: Paints

    fun update(currentTimeMillis: Long) {}

    fun draw(canvas: Canvas, layout: ClockLayout<G>) {
        if (!layout.isDrawable) {
            debug("layout is not drawable! $layout")
            return
        }

        layout.measureFrame { x, y, scale ->
            canvas.withTranslationAndScale(x, y, scale) {
                layout.layoutPass { glyph, glyphAnimationProgress, rect ->
                    if (rect.isEmpty) return@layoutPass
                    if (glyph.visibility == GlyphVisibility.Hidden) return@layoutPass

                    withTranslationAndScale(rect.left, rect.top, glyph.scale) {
                        drawGlyph(glyph, canvas, glyphAnimationProgress, paints)

                        debug(false) {
                            canvas.drawText(glyph.key)
                        }
                    }

                    debug(false) {
                        canvas.debugDrawGlyphBoundary(glyph, rect)
                    }
                }
            }

            debug(false) {
                // Show bounds at native scale
                canvas.debugDrawBounds(drawBounds, nativeSize)
                layout.layoutPass { glyph, glyphAnimationProgress, rect ->
                    canvas.debugDrawGlyphBoundary(glyph, rect)
                }
            }
        }
    }

    fun drawGlyph(
        glyph: G,
        canvas: Canvas,
        glyphAnimationProgress: Float,
        paints: Paints,
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

    private fun Canvas.debugDrawGlyphBoundary(glyph: G, boundary: Rect<Float>) {
        val visibilityColor = when (glyph.visibility) {
            GlyphVisibility.Visible -> Color.Green
            GlyphVisibility.Appearing -> Color.Yellow
            GlyphVisibility.Disappearing -> Color.Orange
            GlyphVisibility.Hidden -> Color.Red
        }
        val stateColor = when (glyph.state) {
            GlyphState.Active -> Color.Green
            GlyphState.Activating -> Color.Yellow
            GlyphState.Deactivating -> Color.Orange
            GlyphState.Inactive -> Color.Red
        }

        drawDebugRect(stateColor, MutableRectF(boundary).extrude(paints.strokeWidth / 2f))
        drawDebugRect(visibilityColor, boundary)
    }
}
