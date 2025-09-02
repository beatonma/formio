package org.beatonma.gclocks.app

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.beatonma.gclocks.app.settings.dataStore
import org.beatonma.gclocks.app.settings.loadAppSettings

fun main() = application {
    val windowState = rememberWindowState(
        position = WindowPosition.Aligned(Alignment.TopEnd),
        size = DpSize(400.dp, 800.dp),
    )

    Window(
        state = windowState,
        onCloseRequest = ::exitApplication,
        title = "gclocks-multiplatform",
    ) {
        App(dataStore.loadAppSettings())
    }
}
