package org.beatonma.gclocks.compose

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import org.beatonma.gclocks.core.ClockAnimator
import org.beatonma.gclocks.core.layout.ClockLayout
import org.beatonma.gclocks.core.ClockRenderer
import org.beatonma.gclocks.core.MeasureStrategy
import org.beatonma.gclocks.core.geometry.FloatSize
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.core.util.TimeOfDay
import org.beatonma.gclocks.core.util.getTime
import org.beatonma.gclocks.form.FormFont
import org.beatonma.gclocks.form.FormGlyph
import org.beatonma.gclocks.form.FormOptions
import org.beatonma.gclocks.form.FormClockRenderer
import org.beatonma.gclocks.form.FormPaints
import org.beatonma.gclocks.io16.Io16ClockRenderer
import org.beatonma.gclocks.io16.Io16Font
import org.beatonma.gclocks.io16.Io16Glyph
import org.beatonma.gclocks.io16.Io16GlyphRenderer
import org.beatonma.gclocks.io16.Io16Options
import org.beatonma.gclocks.io16.Io16Paints
import kotlin.time.Duration


@Composable
fun <Config : ClockConfig<*, *>> Clock(
    config: Config,
    modifier: Modifier = Modifier,
    measureStrategy: MeasureStrategy = MeasureStrategy.FillWidth,
    getTickTime: () -> TimeOfDay = ::getTime,
) {
    val animator = rememberClockAnimator(config, measureStrategy)
    val frameDeltaMillis = currentFrameDelta()
    val canvas = rememberCanvas()

    Layout(
        modifier = modifier,
        content = {
            Canvas(Modifier) {
                frameDeltaMillis
                animator.tick(getTickTime())
                canvas.withScope(this) {
                    animator.render(canvas)
                }
            }
        }
    ) { measurables, constraints ->
        val clockSize = animator.setAvailableSize(
            FloatSize(
                constraints.maxWidth.toFloat(),
                constraints.maxHeight.toFloat()
            )
        )

        val placeable = measurables.first().measure(
            Constraints.fixed(
                clockSize.width.toInt(),
                clockSize.height.toInt(),
            )
        )

        layout(constraints.maxWidth, constraints.maxHeight) {
            placeable.placeRelative(0, 0)
        }
    }
}


@Composable
expect fun currentFrameDelta(): Duration

sealed interface ClockConfig<O : Options, P : Paints> {
    val options: O
    val paints: P
}

data class Io16Config(
    override val options: Io16Options,
    override val paints: Io16Paints,
) : ClockConfig<Io16Options, Io16Paints>

data class FormConfig(
    override val options: FormOptions,
    override val paints: FormPaints,
) : ClockConfig<FormOptions, FormPaints>


@Composable
private fun <Config : ClockConfig<*, *>> rememberClockAnimator(
    config: Config,
    measureStrategy: MeasureStrategy,
): ClockAnimator<*, *> {
    return remember(measureStrategy) {
        when (config) {
            is Io16Config -> object : ClockAnimator<Io16Glyph, Io16Paints> {
                override val layout = ClockLayout(
                    font = Io16Font(),
                    options = config.options,
                    measureStrategy = measureStrategy
                )
                override val renderers: List<ClockRenderer<Io16Glyph, Io16Paints>> = listOf(
                    Io16ClockRenderer(
                        Io16GlyphRenderer(ComposePath(), config.options),
                        config.paints
                    ),
                )

                override fun scheduleNextFrame(delayMillis: Int) {
                    // TODO
                }
            }

            is FormConfig -> object : ClockAnimator<FormGlyph, FormPaints> {
                override val layout = ClockLayout(
                    font = FormFont(),
                    options = config.options,
                    measureStrategy = measureStrategy
                )
                override val renderers: List<ClockRenderer<FormGlyph, FormPaints>> = listOf(
                    FormClockRenderer(config.paints),
                )

                override fun scheduleNextFrame(delayMillis: Int) {
                    // TODO
                }
            }
        }
    }
}