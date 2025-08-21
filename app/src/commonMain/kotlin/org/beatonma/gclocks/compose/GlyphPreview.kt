package org.beatonma.gclocks.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import org.beatonma.gclocks.core.Glyph
import org.beatonma.gclocks.core.GlyphRenderer
import org.beatonma.gclocks.core.MeasureStrategy
import org.beatonma.gclocks.core.geometry.FloatSize
import org.beatonma.gclocks.core.geometry.ScaledSize
import org.beatonma.gclocks.core.geometry.Size
import org.beatonma.gclocks.core.graphics.GenericCanvas
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.util.getTime
import org.beatonma.gclocks.core.util.progress


private const val AnimationDurationSeconds = 2f

@Composable
fun <G : Glyph> GlyphPreview(
    glyph: G,
    paints: Paints,
    modifier: Modifier = Modifier,
    renderer: GlyphRenderer<G> = GlyphRenderer.Default(),
    animPosition: Float? = null,
) {
    val preview = remember { GlyphPreview(glyph, renderer) }
    var animProgress: Float by remember(animPosition) {
        mutableFloatStateOf(
            when {
                (animPosition == null) -> 0f
                else -> progress(animPosition, 0f, 1f)
            }
        )
    }
    val canvas = rememberCanvas()
    var flag by remember { mutableStateOf(false) }

    Box {
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
                }
        ) {
            canvas.withScope(this) {
                preview.draw(this, animProgress, paints)
            }
            if (animPosition == null) {
                animProgress = getTime().run {
                    (((second % AnimationDurationSeconds) * 1000) + millisecond) / (AnimationDurationSeconds * 1000)
                }
            }

            flag = !flag // re-render even when time is 'stopped' for io16 segment animation
        }

        Text(
            glyph.key,
            Modifier.align(Alignment.BottomEnd).background(colorScheme.scrim).padding(2.dp),
            color = Color.White
        )
    }
}


private class GlyphPreview<G : Glyph>(
    val glyph: G,
    val renderer: GlyphRenderer<G>,
) {
    private var measuredSize: ScaledSize = ScaledSize.Init

    fun setAvailableSize(available: Size<Float>) {
        val scale = MeasureStrategy.Fit.measureScale(glyph.maxSize, available)
        glyph.scale = scale
        measuredSize = glyph.maxSize * scale
    }

    fun draw(canvas: GenericCanvas, glyphProgress: Float, paints: Paints) {
        canvas.withScale(glyph.scale) {
            renderer.draw(glyph, canvas, glyphProgress, paints)
        }
    }
}

private fun Modifier.gridlines(): Modifier = composed {
    val color = Color.Black.copy(alpha = 0.4f)
    this.drawWithContent {
        drawContent()

        val (w, h) = size
        for (i in 0 until 10) {
            val x = i * w / 10f
            drawLine(color, Offset(x, 0f), Offset(x, h))
        }
        for (i in 0 until 10) {
            val y = i * h / 10f
            drawLine(color, Offset(0f, y), Offset(w, y))
        }
    }
}