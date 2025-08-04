package org.beatonma.gclocks.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import org.beatonma.gclocks.core.BaseClockGlyph
import org.beatonma.gclocks.core.ClockAnimator
import org.beatonma.gclocks.core.ClockLayout
import org.beatonma.gclocks.core.ClockRenderer
import org.beatonma.gclocks.core.MeasureStrategy
import org.beatonma.gclocks.core.geometry.FloatSize
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.dev.DevFont
import org.beatonma.gclocks.dev.DevGlyph
import org.beatonma.gclocks.dev.DevOptions
import org.beatonma.gclocks.form.FormFont
import org.beatonma.gclocks.form.FormGlyph
import org.beatonma.gclocks.form.FormOptions
import org.beatonma.gclocks.form.FormRenderer
import kotlin.time.Duration


@Composable
fun Clock(modifier: Modifier = Modifier) {
    val animator = remember {
        object : ClockAnimator<FormGlyph> {
            override val layout = ClockLayout(
                font = FormFont(),
                options = FormOptions(),
                measureStrategy = MeasureStrategy.FillWidth
            )
            override val renderers: List<ClockRenderer<FormGlyph>> = listOf(
                FormRenderer()
            )

            override fun scheduleNextFrame(delayMillis: Int) {
//                debug("scheduleNextFrame($delayMillis)")
            }
        }
    }
    val canvas = remember { ComposeCanvas() }
    val frameDeltaMillis = currentFrameDelta()

    Canvas(
        modifier.background(Color(0xccccff).toCompose())
            .heightIn(min = 100.dp)
            .widthIn(min = 300.dp)
            .onSizeChanged { size ->
                animator.setAvailableSize(FloatSize(size.width.toFloat(), size.height.toFloat()))
            }
    ) {
        frameDeltaMillis
        animator.tick()
        canvas.withScope(this) {
            animator.render(canvas)
        }
    }
}


@Composable
expect fun currentFrameDelta(): Duration
