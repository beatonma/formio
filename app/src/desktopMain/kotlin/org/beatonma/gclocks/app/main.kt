package org.beatonma.gclocks.app

import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.lifecycle.viewmodel.compose.viewModel
import gclocks_multiplatform.app.generated.resources.Res
import gclocks_multiplatform.app.generated.resources.app_name
import org.beatonma.gclocks.app.data.DataStoreAppSettingsRepository
import org.beatonma.gclocks.app.data.createDataStore
import org.beatonma.gclocks.app.ui.App
import org.beatonma.gclocks.app.ui.screens.SettingsEditorScreen
import org.jetbrains.compose.resources.stringResource

fun main() = application {
    val windowState = rememberWindowState(
        position = WindowPosition.Aligned(Alignment.TopEnd),
        size = DpSize(400.dp, 800.dp),
    )

    val factory = remember {
        AppViewModelFactory(
            repository = DataStoreAppSettingsRepository(createDataStore())
        )
    }

    Window(
        state = windowState,
        onCloseRequest = ::exitApplication,
        title = stringResource(Res.string.app_name),
    ) {
        val viewModel: AppViewModel = viewModel(factory = factory)

        App(viewModel) { navigation ->
            SettingsEditorScreen(viewModel, navigation)
        }
    }
}
