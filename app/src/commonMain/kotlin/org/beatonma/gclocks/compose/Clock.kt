package org.beatonma.gclocks.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import org.beatonma.gclocks.core.BaseClockGlyph
import org.beatonma.gclocks.core.ClockAnimator
import org.beatonma.gclocks.core.ClockLayout
import org.beatonma.gclocks.core.ClockRenderer
import org.beatonma.gclocks.core.MeasureStrategy
import org.beatonma.gclocks.core.geometry.FloatSize
import org.beatonma.gclocks.form.FormFont
import org.beatonma.gclocks.form.FormGlyph
import org.beatonma.gclocks.form.FormOptions
import org.beatonma.gclocks.form.FormClockRenderer
import org.beatonma.gclocks.io16.Io16ClockRenderer
import org.beatonma.gclocks.io16.Io16Font
import org.beatonma.gclocks.io16.Io16Glyph
import org.beatonma.gclocks.io16.Io16GlyphRenderer
import org.beatonma.gclocks.io16.Io16Options
import kotlin.time.Duration


@Composable
fun Clock(modifier: Modifier = Modifier) {
    val animator = rememberClockAnimator(ClockType.Io16)
    val frameDeltaMillis = currentFrameDelta()
    val canvas = remember(::ComposeCanvas)

    Canvas(
        modifier.background(Color(0xccccff))
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


enum class ClockType() {
    Form {
        override fun animator() = object : ClockAnimator<FormGlyph> {
            override val layout = ClockLayout(
                font = FormFont(),
                options = FormOptions(),
                measureStrategy = MeasureStrategy.FillWidth
            )
            override val renderers: List<ClockRenderer<FormGlyph>> = listOf(
                FormClockRenderer(),
            )

            override fun scheduleNextFrame(delayMillis: Int) {

            }
        }
    },
    Io16 {
        override fun animator() = object : ClockAnimator<Io16Glyph> {
            override val layout = ClockLayout(
                font = Io16Font(),
                options = Io16Options(),
                measureStrategy = MeasureStrategy.FillWidth
            )
            override val renderers: List<ClockRenderer<Io16Glyph>> = listOf(
                Io16ClockRenderer(
                    Io16GlyphRenderer(ComposePath())
                ),
            )

            override fun scheduleNextFrame(delayMillis: Int) {

            }
        }
    }
    ;

    abstract fun animator(): ClockAnimator<*>
}

@Composable
private fun rememberClockAnimator(type: ClockType): ClockAnimator<*> {
    val animator = remember(type) { type.animator() }

    return animator
}