package org.beatonma.gclocks.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import org.beatonma.gclocks.core.Glyph
import org.beatonma.gclocks.core.MeasureStrategy
import org.beatonma.gclocks.core.geometry.FloatSize
import org.beatonma.gclocks.core.geometry.Size
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.graphics.Stroke
import org.beatonma.gclocks.core.util.getTime


@Composable
fun GlyphPreview(glyph: Glyph, paints: Paints, modifier: Modifier = Modifier) {
    val preview = remember { GlyphPreview(glyph) }
    val canvas = remember { ComposeCanvas() }
    var animProgress by remember { mutableFloatStateOf(0f) }

    Canvas(
        modifier
            .aspectRatio(glyph.companion.aspectRatio)
            .onSizeChanged { size ->
                preview.setAvailableSize(
                    FloatSize(
                        size.width.toFloat(),
                        size.height.toFloat()
                    )
                )
            }) {
        canvas.withScope(this) {
            preview.draw(this, animProgress, paints)
        }
        animProgress = getTime().millisecond.toFloat() / 1000f
    }
}


private class GlyphPreview(
    val glyph: Glyph,
) {
    private var measuredSize: Size<Float> = FloatSize()

    fun setAvailableSize(available: Size<Float>) {
        val scale = MeasureStrategy.Fit.measureScale(glyph.maxSize, available)
        glyph.scale = scale
        measuredSize = glyph.maxSize.scaledBy(scale)
    }

    fun draw(canvas: Canvas<*>, glyphProgress: Float, paints: Paints) {
        canvas.withScale(glyph.scale) {
            glyph.draw(canvas, glyphProgress, paints)
        }
    }
}