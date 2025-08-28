package org.beatonma.gclocks.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.beatonma.gclocks.compose.rememberCanvas
import org.beatonma.gclocks.core.Glyph
import org.beatonma.gclocks.core.GlyphRenderer
import org.beatonma.gclocks.core.geometry.ConstrainedLayout
import org.beatonma.gclocks.core.geometry.MeasureConstraints
import org.beatonma.gclocks.core.geometry.NativeSize
import org.beatonma.gclocks.core.geometry.ScaledSize
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.util.getTime
import org.beatonma.gclocks.core.util.progress


private const val AnimationDurationSeconds = 2f

@Composable
fun <P : Paints, G : Glyph<P>> GlyphPreview(
    glyph: G,
    paints: P,
    modifier: Modifier = Modifier,
    renderer: GlyphRenderer<P, G>? = null,
    animPosition: Float? = null,
) {
    val preview = remember { GlyphPreview(glyph, renderer, paints) }
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

    Box(modifier) {
        ConstrainedCanvas(preview, Modifier.fillMaxSize()) {
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


private class GlyphPreview<P : Paints, G : Glyph<P>>(
    val glyph: G,
    val renderer: GlyphRenderer<P, G>?,
    private val paints: P,
) : ConstrainedLayout {
    private var measureScale: Float = 100f
    private var drawScale: Float = 100f

    override fun setConstraints(constraints: MeasureConstraints): ScaledSize {
        measureScale = constraints.measureScale(
            glyph.maxSize.toRect()
                .add(0f, 0f, paints.strokeWidth, paints.strokeWidth)
                .run {
                    NativeSize(width, height)
                })

        drawScale = constraints.measureScale(
            glyph.maxSize.toRect()
                .add(0f, 0f, paints.strokeWidth * 2, paints.strokeWidth * 2)
                .run {
                    NativeSize(width, height)
                })


        return glyph.maxSize * measureScale
    }

    fun draw(canvas: Canvas, glyphProgress: Float, paints: P) {
        canvas.withTranslationAndScale(
            paints.strokeWidth * drawScale / 2f,
            paints.strokeWidth * drawScale / 2f,
            this.measureScale
        ) {
            glyph.draw(canvas, glyphProgress, paints, renderer?.let { renderer ->
                { renderer.draw(glyph, canvas, paints) }
            })
        }
    }
}
