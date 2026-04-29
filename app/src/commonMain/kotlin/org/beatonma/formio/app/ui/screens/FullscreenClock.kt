package org.beatonma.formio.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import formio.app.generated.resources.Res
import formio.app.generated.resources.cd_fullscreen_close
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.beatonma.formio.app.data.settings.AnyContextClockOptions
import org.beatonma.formio.app.data.settings.DisplayContext
import org.beatonma.formio.app.data.settings.DisplayContextDefaults
import org.beatonma.formio.app.ui.components.AppIcon
import org.beatonma.formio.app.ui.components.Clock
import org.beatonma.formio.app.ui.components.FloatingActionButton
import org.beatonma.formio.app.ui.components.toCompose
import org.beatonma.formio.app.ui.providers.EdgeToEdge
import org.beatonma.formio.core.options.AnyOptions
import org.jetbrains.compose.resources.stringResource


@Composable
fun FullSizeClock(
    options: AnyContextClockOptions,
    modifier: Modifier = Modifier,
) {
    EdgeToEdgeClock(options, modifier) {
        ClockWithBackground(options)
    }
}


@Composable
fun FullSizeClock(
    options: AnyContextClockOptions,
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    onClose: () -> Unit,
) {
    var isOverlayVisible by remember { mutableStateOf(true) }
    var visibilityTimeoutJob: Job? by remember { mutableStateOf(null) }

    EdgeToEdgeClock(
        options,
        modifier.pointerInput(Unit) {
            while (true) {
                awaitPointerEventScope { awaitPointerEvent() }
                isOverlayVisible = true

                visibilityTimeoutJob?.cancel()
                visibilityTimeoutJob = coroutineScope.launch {
                    delay(1000L)
                    isOverlayVisible = false
                }
            }
        },
    ) {
        FloatingActionButton(
            isOverlayVisible,
            onClick = onClose,
        ) {
            Icon(
                AppIcon.FullscreenClose,
                stringResource(Res.string.cd_fullscreen_close)
            )
        }
    }
}

@Composable
private fun EdgeToEdgeClock(
    options: AnyContextClockOptions,
    modifier: Modifier,
    overlay: @Composable (BoxScope.() -> Unit)? = null,
) {
    val background = rememberBackground(options.displayOptions)
    EdgeToEdge(Modifier.background(background.backgroundColor.toCompose()).then(modifier)) {
        ClockWithBackground(options)

        overlay?.invoke(this)
    }
}

@Composable
private fun ClockWithBackground(options: AnyContextClockOptions) {
    val displayOptions = when (options.displayOptions) {
        is DisplayContext.Options.WithBackground -> options.displayOptions
        else -> remember { DisplayContextDefaults.WithBackground() }
    }

    ClockWithBackground(options.clockOptions, displayOptions)
}


/**
 * Wrapper for [Clock] which applies background and positioning from [displayOptions].
 */
@Composable
private fun ClockWithBackground(
    clockOptions: AnyOptions,
    displayOptions: DisplayContext.Options.WithBackground,
    modifier: Modifier = Modifier,
) {
    Layout(
        modifier = modifier,
        content = { Clock(clockOptions, allowVariance = true) },
    ) { measurables, constraints ->
        val position = displayOptions.position
        val left = position.left * constraints.maxWidth
        val top = position.top * constraints.maxHeight
        val width = position.width * constraints.maxWidth
        val height = position.height * constraints.maxHeight

        val placeable = measurables[0].measure(
            Constraints(
                maxWidth = width.toInt(),
                maxHeight = height.toInt()
            )
        )

        val offsetX = left + clockOptions.layout.outerHorizontalAlignment.apply(
            placeable.width,
            width.toInt()
        )
        val offsetY =
            top + clockOptions.layout.outerVerticalAlignment.apply(placeable.height, height.toInt())

        layout(constraints.maxWidth, constraints.maxHeight) {
            placeable.place(offsetX.toInt(), offsetY.toInt())
        }
    }
}

@Composable
private fun rememberBackground(displayOptions: DisplayContext.Options): DisplayContext.Options.WithBackground {
    return when (displayOptions) {
        is DisplayContext.Options.WithBackground -> displayOptions
        else -> remember { DisplayContextDefaults.WithBackground() }
    }
}
