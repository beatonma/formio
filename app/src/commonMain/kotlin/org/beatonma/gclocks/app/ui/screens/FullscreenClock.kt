package org.beatonma.gclocks.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import gclocks_multiplatform.app.generated.resources.Res
import gclocks_multiplatform.app.generated.resources.cd_fullscreen_close
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.beatonma.gclocks.app.data.settings.ContextClockOptions
import org.beatonma.gclocks.app.data.settings.DisplayContext
import org.beatonma.gclocks.app.data.settings.DisplayContextDefaults
import org.beatonma.gclocks.app.theme.DesignSpec.floatingActionButton
import org.beatonma.gclocks.app.ui.LocalSystemBars
import org.beatonma.gclocks.app.ui.SystemBarsController
import org.beatonma.gclocks.compose.AppIcon
import org.beatonma.gclocks.compose.animation.EnterFade
import org.beatonma.gclocks.compose.animation.ExitFade
import org.beatonma.gclocks.compose.components.Clock
import org.beatonma.gclocks.compose.components.ClockLayout
import org.jetbrains.compose.resources.stringResource


@Composable
fun FullSizeClock(
    options: ContextClockOptions<*>,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    systemBarsController: SystemBarsController? = LocalSystemBars.current,
    onClose: () -> Unit,
) {
    var isOverlayVisible by remember { mutableStateOf(true) }
    var visibilityTimeoutJob: Job? by remember { mutableStateOf(null) }

    val displayOptions = when (options.displayOptions) {
        is DisplayContext.Options.WithBackground -> options.displayOptions
        else -> remember { DisplayContextDefaults.WithBackground() }
    }

    DisposableEffect(Unit) {
        systemBarsController?.onRequestHideSystemBars()
        onDispose { systemBarsController?.onRequestShowSystemBars() }
    }

    Box(
        Modifier.pointerInput(Unit) {
            while (true) {
                awaitPointerEventScope { awaitPointerEvent() }
                isOverlayVisible = true

                visibilityTimeoutJob?.cancel()
                visibilityTimeoutJob = coroutineScope.launch {
                    delay(1000L)
                    isOverlayVisible = false
                }
            }
        }
    ) {
        ClockLayout(displayOptions) {
            Clock(options.clockOptions)
        }

        AnimatedVisibility(
            isOverlayVisible,
            Modifier.align(Alignment.BottomEnd).safeDrawingPadding(),
            enter = EnterFade,
            exit = ExitFade
        ) {
            FloatingActionButton(onClose, Modifier.floatingActionButton()) {
                Icon(
                    AppIcon.FullscreenClose,
                    stringResource(Res.string.cd_fullscreen_close)
                )
            }
        }
    }
}
