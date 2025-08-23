package org.beatonma.gclocks.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.beatonma.gclocks.core.ClockAnimator
import org.beatonma.gclocks.core.layout.ClockLayout
import org.beatonma.gclocks.core.ClockRenderer
import org.beatonma.gclocks.core.GlyphState
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
fun <Opts : Options<*>> Clock(
    options: Opts,
    modifier: Modifier = Modifier,
    getTickTime: () -> TimeOfDay = ::getTime,
    forcedState: GlyphState? = null,
) {
    val animator = rememberClockAnimator(options, forcedState)
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


@Composable
private fun <Opts : Options<*>> rememberClockAnimator(
    options: Opts,
    forcedState: GlyphState?,
): ClockAnimator<*, *> {
    return remember(forcedState) {
        when (options) {
            is Io16Options -> object : ClockAnimator<Io16Glyph, Io16Paints> {
                override val layout = ClockLayout(
                    font = Io16Font(
                        debugGetGlyphAt = if (forcedState == null) null else ({ glyph ->
                            glyph.apply { setState(forcedState, force = true) }
                        })
                    ),
                    options = options,
                )
                override val renderers: List<ClockRenderer<Io16Glyph, Io16Paints>> = listOf(
                    Io16ClockRenderer(
                        Io16GlyphRenderer(ComposePath(), options),
                        options.paints
                    ),
                )

                override fun scheduleNextFrame(delayMillis: Int) {
                    // TODO
                }
            }

            is FormOptions -> object : ClockAnimator<FormGlyph, FormPaints> {
                override val layout = ClockLayout(
                    font = FormFont(),
                    options = options,
                )
                override val renderers: List<ClockRenderer<FormGlyph, FormPaints>> = listOf(
                    FormClockRenderer(options.paints),
                )

                override fun scheduleNextFrame(delayMillis: Int) {
                    // TODO
                }
            }

            else -> {
                throw IllegalStateException("Unhandled Option type: ${options.javaClass.simpleName}")
            }
        }
    }
}
