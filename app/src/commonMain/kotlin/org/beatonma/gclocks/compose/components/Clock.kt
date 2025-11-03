package org.beatonma.gclocks.compose.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.pointer.pointerInput
import org.beatonma.gclocks.clocks.createAnimatorFromOptions
import org.beatonma.gclocks.compose.ComposePath
import org.beatonma.gclocks.compose.debugKeyEvent
import org.beatonma.gclocks.compose.rememberCanvasHost
import org.beatonma.gclocks.core.ClockAnimator
import org.beatonma.gclocks.core.GlyphState
import org.beatonma.gclocks.core.GlyphVisibility
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.core.util.getInstant
import kotlin.time.Duration
import kotlin.time.Instant


@Composable
expect fun currentFrameDelta(): Duration


@Composable
fun <Opts : Options<*>> Clock(
    options: Opts,
    modifier: Modifier = Modifier,
    getInstant: () -> Instant = ::getInstant,
    allowVariance: Boolean = true,
    forcedState: GlyphState? = null,
) {
    val animator = rememberClockAnimator(options, allowVariance, forcedState)
    val frameDeltaMillis = currentFrameDelta()
    val canvasHost = rememberCanvasHost()

    ConstrainedCanvas(
        animator,
        modifier
            .debugKeyEvent { event ->
                if (event.isCtrlPressed) {
                    return@debugKeyEvent when (event.key) {
                        Key.One -> {
                            animator.setState(GlyphState.Inactive, false)
                            true
                        }

                        Key.Two -> {
                            animator.setState(GlyphState.Active, false)
                            true
                        }

                        Key.Three -> {
                            animator.setState(GlyphVisibility.Hidden, false)
                            true
                        }

                        Key.Four -> {
                            animator.setState(GlyphVisibility.Visible, false)
                            true
                        }

                        else -> false
                    }
                }
                false
            }
            .pointerInput(Unit) {
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
        animator.tick(getInstant())

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
