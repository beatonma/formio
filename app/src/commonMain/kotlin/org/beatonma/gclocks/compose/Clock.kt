package org.beatonma.gclocks.compose

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import org.beatonma.gclocks.core.ClockAnimator
import org.beatonma.gclocks.core.ClockLayout
import org.beatonma.gclocks.core.ClockRenderer
import org.beatonma.gclocks.core.MeasureStrategy
import org.beatonma.gclocks.core.geometry.FloatSize
import org.beatonma.gclocks.core.geometry.Size
import org.beatonma.gclocks.core.util.TimeOfDay
import org.beatonma.gclocks.core.util.getTime
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
fun Clock(
    type: ClockType,
    modifier: Modifier = Modifier,
    measureStrategy: MeasureStrategy = MeasureStrategy.FillWidth,
    getTickTime: () -> TimeOfDay = ::getTime,
) {
    val animator = rememberClockAnimator(type, measureStrategy)
    val frameDeltaMillis = currentFrameDelta()
    val canvas = rememberCanvas()
    var measuredSize: Size<Float> by remember { mutableStateOf(FloatSize()) }

    Canvas(
        modifier
            .onSizeChanged { size ->
                measuredSize =
                    animator.setAvailableSize(
                        FloatSize(
                            size.width.toFloat(),
                            size.height.toFloat()
                        )
                    )
            }
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                layout(placeable.width, measuredSize.y.toInt()) {
                    placeable.place(0, 0)
                }
            }
    ) {
        frameDeltaMillis
        animator.tick(getTickTime())

        canvas.withScope(this) {
            animator.render(canvas)
        }
    }
}


@Composable
expect fun currentFrameDelta(): Duration


enum class ClockType {
    Form {
        override fun animator(measureStrategy: MeasureStrategy) =
            object : ClockAnimator<FormGlyph> {
                override val layout = ClockLayout(
                    font = FormFont(),
                    options = FormOptions(),
                    measureStrategy = measureStrategy
                )
                override val renderers: List<ClockRenderer<FormGlyph>> = listOf(
                    FormClockRenderer(),
                )

                override fun scheduleNextFrame(delayMillis: Int) {

                }
            }
    },
    Io16 {
        override fun animator(measureStrategy: MeasureStrategy) =
            object : ClockAnimator<Io16Glyph> {
                override val layout = ClockLayout(
                    font = Io16Font(),
                    options = Io16Options(),
                    measureStrategy = measureStrategy
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

    abstract fun animator(measureStrategy: MeasureStrategy): ClockAnimator<*>
}

@Composable
private fun rememberClockAnimator(
    type: ClockType,
    measureStrategy: MeasureStrategy,
): ClockAnimator<*> {
    return remember(type, measureStrategy) { type.animator(measureStrategy) }
}