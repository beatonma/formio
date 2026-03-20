package org.beatonma.formio.app

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.beatonma.formio.core.Build

fun main() = application {
    val windowState = rememberWindowState(
        position = WindowPosition.Aligned(Alignment.TopEnd),
        size = DpSize(1600.dp, 1200.dp),
    )

    Window(
        state = windowState,
        onCloseRequest = ::exitApplication,
        title = Build.AppName,
        alwaysOnTop = true,
    ) {
        DebugApp()
    }
}
