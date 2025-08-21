package org.beatonma.gclocks.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.beatonma.gclocks.core.ClockAnimator
import org.beatonma.gclocks.core.layout.ClockLayout
import org.beatonma.gclocks.core.ClockRenderer
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
    getTickTime: () -> TimeOfDay = ::getTime,
) {
    val animator = rememberClockAnimator(config)
    val frameDeltaMillis = currentFrameDelta()
    val canvas = rememberCanvas()

    ConstrainedCanvas(animator, modifier) {
        frameDeltaMillis
        animator.tick(getTickTime())
        canvas.withScope(this) {
            animator.render(canvas)
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
private fun <Config : ClockConfig<*, *>> rememberClockAnimator(config: Config): ClockAnimator<*, *> {
    return remember {
        when (config) {
            is Io16Config -> object : ClockAnimator<Io16Glyph, Io16Paints> {
                override val layout = ClockLayout(
                    font = Io16Font(),
                    options = config.options,
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
