package org.beatonma.gclocks.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import org.beatonma.gclocks.compose.components.ConstrainedCanvas
import org.beatonma.gclocks.compose.rememberCanvasHost
import org.beatonma.gclocks.core.geometry.ConstrainedLayout
import org.beatonma.gclocks.core.geometry.MeasureConstraints
import org.beatonma.gclocks.core.geometry.NativeSize
import org.beatonma.gclocks.core.geometry.ScaledSize
import org.beatonma.gclocks.core.glyph.Glyph
import org.beatonma.gclocks.core.glyph.GlyphRenderer
import org.beatonma.gclocks.core.glyph.GlyphState
import org.beatonma.gclocks.core.graphics.Canvas
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.graphics.Stroke
import org.beatonma.gclocks.core.options.GlyphOptions
import org.beatonma.gclocks.core.util.currentTimeMillis
import org.beatonma.gclocks.core.util.debug
import org.beatonma.gclocks.core.util.getCurrentTimeMillis
import org.beatonma.gclocks.core.util.getInstant
import org.beatonma.gclocks.core.util.getTime
import org.beatonma.gclocks.core.util.progress
import kotlin.time.Instant
import androidx.compose.ui.graphics.Color as ComposeColor


private const val AnimationDurationSeconds = 2f

@Composable
fun <G : Glyph> GlyphPreview(
    glyph: G,
    paints: Paints,
    modifier: Modifier = Modifier,
    renderer: GlyphRenderer<G>? = null,
    animPosition: Float? = null,
) {
    val preview = remember(glyph, renderer, paints) { GlyphPreview(glyph, renderer, paints) }
    var animProgress: Float by remember(animPosition) {
        mutableFloatStateOf(
            when {
                (animPosition == null) -> 0f
                else -> progress(animPosition, 0f, 1f)
            }
        )
    }
    val canvasHost = rememberCanvasHost()
    var flag by remember { mutableStateOf(false) }

    Box(
        modifier
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()

                        val pointer = event.changes.firstOrNull() ?: continue

                        if (event.type == PointerEventType.Press) {
                            preview.glyph.setState(
                                GlyphState.Active,
                                currentTimeMillis = getCurrentTimeMillis()
                            )
                        }
                    }
                }
            }
    ) {
        ConstrainedCanvas(preview, Modifier.fillMaxSize()) {
            preview.tick()
            canvasHost.withScope(this) { canvas ->
                preview.draw(canvas, animProgress, paints)
            }
            if (animPosition == null) {
                animProgress = getTime().run {
                    (((second % AnimationDurationSeconds) * 1000) + millisecond) / (AnimationDurationSeconds * 1000)
                }
            }

            flag = !flag // re-render even when time is 'stopped' for io16 segment animation
        }

        Column(
            Modifier.align(Alignment.BottomEnd).background(colorScheme.scrim).padding(2.dp),
            horizontalAlignment = Alignment.End
        ) {
            CompositionLocalProvider(LocalContentColor provides ComposeColor.White) {
                Text("'${glyph.key}'")
                Text(glyph.visibility.name)
            }
        }
    }
}


private class GlyphPreview<G : Glyph>(
    val glyph: G,
    val renderer: GlyphRenderer<G>?,
    private val paints: Paints,
    private val options: GlyphOptions = object : GlyphOptions {
        override val activeStateDurationMillis: Int = 2000
        override val stateChangeDurationMillis: Int = 1200
        override val glyphMorphMillis: Int = 600
        override val visibilityChangeDurationMillis: Int = 600
    },
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

    fun tick(instant: Instant = getInstant()) {
        val millis = instant.currentTimeMillis
        glyph.tick(options, millis)
        renderer?.update(millis)
    }

    fun draw(canvas: Canvas, glyphProgress: Float, paints: Paints) {
        canvas.withTranslationAndScale(
            paints.strokeWidth * drawScale / 2f,
            paints.strokeWidth * drawScale / 2f,
            this.measureScale
        ) {
            glyph.draw(canvas, glyphProgress, paints, renderer?.let { renderer ->
                { renderer.draw(glyph, canvas, paints) }
            })

            debug(false) {
                drawRect(
                    Color.Red,
                    0f,
                    0f,
                    glyph.getWidthAtProgress(glyphProgress),
                    glyph.maxSize.height,
                    Stroke.Default
                )
            }
        }
    }
}
