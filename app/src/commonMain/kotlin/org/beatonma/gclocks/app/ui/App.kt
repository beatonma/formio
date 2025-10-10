package org.beatonma.gclocks.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import org.beatonma.gclocks.app.theme.AppTheme
import org.beatonma.gclocks.app.ui.screens.SettingsEditorScreen
import org.beatonma.gclocks.app.ui.screens.SettingsEditorViewModel

@Composable
fun App(
    viewModel: SettingsEditorViewModel,
    systemBarsController: SystemBarsController? = null,
    settingsEditor: (@Composable (AppNavigation) -> Unit)? = null,
) {
    AppTheme {
        CompositionLocalProvider(LocalSystemBars provides systemBarsController) {
            AppNavigation(
                viewModel,
                settingsEditor = settingsEditor ?: { navigation ->
                    SettingsEditorScreen(viewModel, navigation)
                }
            )
        }
    }
}
