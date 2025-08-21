package org.beatonma.gclocks.core

import org.beatonma.gclocks.core.geometry.Rect
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.GenericCanvas
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.graphics.Stroke
import org.beatonma.gclocks.core.layout.ClockLayout
import org.beatonma.gclocks.core.util.debug


/**
 * Draws a [ClockLayout] to a [org.beatonma.gclocks.core.graphics.Canvas].
 */
interface ClockRenderer<G : BaseClockGlyph, P : Paints> {
    val renderer: GlyphRenderer<G>
    var paints: P

    fun draw(canvas: GenericCanvas, layout: ClockLayout<G>) {
        if (!layout.isDrawable) {
            debug("layout is not drawable! $layout")
            return
        }

        layout.measureFrame { x, y, scale ->
            canvas.drawRect(Color.Cyan, nativeSize.toRect(), Stroke.Default)

            canvas.withTranslationAndScale(x, y, scale) {
                canvas.drawRect(Color.Blue, nativeSize.toRect(), Stroke.Default)
                layout.layoutPass { glyph, glyphAnimationProgress, rect ->
                    if (rect.isEmpty) return@layoutPass

                    debug {
                        drawGlyphBoundary(canvas, paints, rect)
                    }

                    canvas.withTranslationAndScale(rect.left, rect.top, glyph.scale) {
                        drawGlyph(glyph, canvas, glyphAnimationProgress, paints)
                    }
                }
            }
        }
    }

    fun drawGlyphBoundary(canvas: GenericCanvas, paints: Paints, boundary: Rect<Float>) {
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
        canvas: GenericCanvas,
        glyphAnimationProgress: Float,
        paints: Paints,
    ) {
        renderer.draw(glyph, canvas, glyphAnimationProgress, paints)
    }
}