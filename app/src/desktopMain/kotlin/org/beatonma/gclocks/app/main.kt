package org.beatonma.gclocks.app

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
import androidx.lifecycle.viewmodel.compose.viewModel
import gclocks_multiplatform.app.generated.resources.Res
import gclocks_multiplatform.app.generated.resources.app_name
import org.beatonma.gclocks.app.data.DataStoreAppSettingsRepository
import org.beatonma.gclocks.app.data.createDataStore
import org.beatonma.gclocks.app.ui.App
import org.beatonma.gclocks.app.ui.SystemBarsController
import org.beatonma.gclocks.app.ui.screens.SettingsEditorScreen
import org.beatonma.gclocks.app.ui.screens.SettingsEditorViewModel
import org.beatonma.gclocks.app.ui.screens.SettingsEditorViewModelFactory
import org.jetbrains.compose.resources.stringResource

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

    val factory = remember {
        SettingsEditorViewModelFactory(
            repository = DataStoreAppSettingsRepository(createDataStore())
        )
    }

    LaunchedEffect(isWindowFullscreen) {
        windowState.placement = when (isWindowFullscreen) {
            true -> WindowPlacement.Fullscreen
            false -> WindowPlacement.Floating
        }
    }

    Window(
        state = windowState,
        onCloseRequest = ::exitApplication,
        title = stringResource(Res.string.app_name),
    ) {
        val viewModel: SettingsEditorViewModel = viewModel(factory = factory)

        App(viewModel, systemBarsController) { navigation ->
            SettingsEditorScreen(viewModel, navigation)
        }
    }
}
