package org.beatonma.gclocks.core

import org.beatonma.gclocks.core.geometry.Rect
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.graphics.Stroke
import org.beatonma.gclocks.core.util.warn

interface ClockRenderer<G : BaseClockGlyph> {
    var paints: Paints

    fun draw(canvas: Canvas<*>, layout: ClockLayout<*>) {
        if (!layout.isDrawable) {
            warn("layout is not drawable! ${layout}")
            return
        }
        val paints = this.paints

        layout.onDraw { x, y, scale ->
            canvas.withTranslationAndScale(x, y, scale) {
                layout.layoutPass { glyph, glyphAnimationProgress, rect ->
                    drawGlyphBoundary(canvas, paints, rect)

                    canvas.withTranslationAndScale(rect.left, rect.top, glyph.scale) {
                        drawGlyph(glyph, canvas, glyphAnimationProgress, paints)
                    }
                }
            }
        }
    }

    fun drawGlyphBoundary(canvas: Canvas<*>, paints: Paints, boundary: Rect<Float>) {
        canvas.drawRect(paints.colors.first(), boundary, Stroke())
        canvas.drawLine(
            paints.colors.first(),
            boundary.left, boundary.top,
            boundary.right, boundary.bottom,
            Stroke()
        )
        canvas.drawLine(
            paints.colors.first(),
            boundary.right, boundary.top,
            boundary.left, boundary.bottom,
            Stroke()
        )
    };

    fun drawGlyph(
        glyph: Glyph,
        canvas: Canvas<*>,
        glyphAnimationProgress: Float,
        paints: Paints,
    ) {
        glyph.draw(canvas, glyphAnimationProgress, paints)
    }
}