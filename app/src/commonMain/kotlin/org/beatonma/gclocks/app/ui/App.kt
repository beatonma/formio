package org.beatonma.gclocks.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import org.beatonma.gclocks.app.AppViewModel
import org.beatonma.gclocks.app.theme.AppTheme

@Composable
fun App(
    viewModel: AppViewModel,
    systemBarsController: SystemBarsController? = null,
    settingsEditor: @Composable (AppNavigation) -> Unit,
) {
    AppTheme {
        CompositionLocalProvider(LocalSystemBars provides systemBarsController) {
            AppNavigation(
                viewModel,
                settingsEditor = settingsEditor
            )
        }
    }
}
