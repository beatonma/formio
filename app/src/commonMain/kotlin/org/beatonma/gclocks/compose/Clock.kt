package org.beatonma.gclocks.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.beatonma.gclocks.core.ClockAnimator
import org.beatonma.gclocks.core.ClockFont
import org.beatonma.gclocks.core.ClockGlyph
import org.beatonma.gclocks.core.layout.ClockLayout
import org.beatonma.gclocks.core.ClockRenderer
import org.beatonma.gclocks.core.GlyphState
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
import org.beatonma.gclocks.io18.Io18Font
import org.beatonma.gclocks.io18.Io18Glyph
import org.beatonma.gclocks.io18.Io18Options
import org.beatonma.gclocks.io18.Io18Paints
import org.beatonma.gclocks.io18.Io18Renderer
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
    val path = remember { ComposePath() }
    return remember(forcedState) {
        when (options) {
            is FormOptions -> createAnimator(
                options,
                FormFont(),
                FormClockRenderer(options.paints)
            )

            is Io16Options -> createAnimator(
                options,
                Io16Font(
                    debugGetGlyphAt = if (forcedState == null) null else ({ glyph ->
                        glyph.apply { setState(forcedState, force = true) }
                    })
                ),
                Io16ClockRenderer(
                    Io16GlyphRenderer(path, options),
                    options.paints
                ),
            )

            is Io18Options -> createAnimator(
                options,
                Io18Font(path),
                Io18Renderer(options.paints)
            )

            else -> throw IllegalStateException(
                "Unhandled Option type: ${options.javaClass.simpleName}"
            )
        }
    }
}


private fun <P : Paints, O : Options<P>, G : ClockGlyph<P>> createAnimator(
    options: O,
    font: ClockFont<P, G>,
    renderer: ClockRenderer<P, G>,
) = object : ClockAnimator<P, G> {
    override val layout = ClockLayout(font = font, options = options)
    override val renderers: List<ClockRenderer<P, G>> = listOf(renderer)

//    private var meanFrameTime: Duration = Duration.ZERO
//    override fun render(canvas: Canvas) {
//        val frameTime = measureTime {
//            super.render(canvas)
//        }
//        if (meanFrameTime == Duration.ZERO) {
//            meanFrameTime = frameTime
//        } else {
//            meanFrameTime = (frameTime + meanFrameTime) / 2
//        }
//        canvas.drawText("FPS: %.0f".format(1.seconds / meanFrameTime))
//    }

    override fun scheduleNextFrame(delayMillis: Int) {
        // TODO
    }
}