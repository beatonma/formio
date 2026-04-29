package org.beatonma.formio.app

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.beatonma.formio.app.data.DataStoreAppSettingsRepository
import org.beatonma.formio.app.data.createDataStore
import org.beatonma.formio.app.ui.App
import org.beatonma.formio.app.ui.providers.SystemBarsController
import org.beatonma.formio.app.ui.screens.settings.settingsEditorViewModel
import org.beatonma.formio.core.Build

fun main() = application {
    var isWindowFullscreen by remember { mutableStateOf(false) }
    val systemBarsController = remember {
        SystemBarsController(
            onRequestShowSystemBars = { isWindowFullscreen = false },
            onRequestHideSystemBars = { isWindowFullscreen = true }
        )
    }
    val windowState = rememberWindowState(
        position = WindowPosition.Aligned(Alignment.TopEnd),
        size = DpSize(400.dp, 800.dp),
        placement = WindowPlacement.Floating
    )

    LaunchedEffect(isWindowFullscreen) {
        windowState.placement = when (isWindowFullscreen) {
            true -> WindowPlacement.Fullscreen
            false -> WindowPlacement.Floating
        }
    }

    Window(
        state = windowState,
        onCloseRequest = ::exitApplication,
        title = Build.AppName,
    ) {
        val repository = remember { DataStoreAppSettingsRepository(createDataStore()) }
        val editorViewModel = settingsEditorViewModel(repository)

        App(editorViewModel, systemBarsController)
    }
}
