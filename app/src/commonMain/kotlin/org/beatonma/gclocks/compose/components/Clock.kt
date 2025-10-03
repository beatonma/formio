package org.beatonma.gclocks.compose.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import org.beatonma.gclocks.clocks.createAnimatorFromOptions
import org.beatonma.gclocks.compose.ComposePath
import org.beatonma.gclocks.compose.rememberCanvasHost
import org.beatonma.gclocks.core.ClockAnimator
import org.beatonma.gclocks.core.GlyphState
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.core.util.TimeOfDay
import org.beatonma.gclocks.core.util.getTime
import kotlin.time.Duration


@Composable
expect fun currentFrameDelta(): Duration


@Composable
fun <Opts : Options<*>> Clock(
    options: Opts,
    modifier: Modifier = Modifier,
    getTickTime: () -> TimeOfDay = ::getTime,
    allowVariance: Boolean = true,
    forcedState: GlyphState? = null,
) {
    val animator = rememberClockAnimator(options, allowVariance, forcedState)
    val frameDeltaMillis = currentFrameDelta()
    val canvasHost = rememberCanvasHost()

    ConstrainedCanvas(
        animator,
        modifier.pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent()

                    val pointer = event.changes.firstOrNull() ?: continue
                    val (x, y) = pointer.position

                    animator.getGlyphAt(x, y)?.setState(GlyphState.Active)
                }
            }
        }
    ) {
        frameDeltaMillis
        animator.tick(getTickTime())

        canvasHost.withScope(this) { canvas ->
            animator.render(canvas)
        }
    }
}

@Composable
private fun <Opts : Options<*>> rememberClockAnimator(
    options: Opts,
    allowVariance: Boolean,
    forcedState: GlyphState?,
): ClockAnimator<*, *> {
    val path = remember { ComposePath() }
    return remember(options, forcedState) {
        createAnimatorFromOptions(
            options,
            path,
            allowVariance = allowVariance,
            forcedState = forcedState
        ) {
            // TODO schedule next frame
        }
    }
}
