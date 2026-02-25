package org.beatonma.gclocks.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import org.beatonma.gclocks.app.theme.AppTheme
import org.beatonma.gclocks.app.theme.Theme
import org.beatonma.gclocks.app.ui.screens.SettingsEditorScreen
import org.beatonma.gclocks.app.ui.screens.SettingsEditorViewModel
import org.beatonma.gclocks.compose.debugHotkey

@Composable
fun App(
    viewModel: SettingsEditorViewModel,
    systemBarsController: SystemBarsController? = null,
    settingsEditor: SettingsEditorUI? = null,
) {
    var theme: Theme by remember { mutableStateOf(Theme.System) }

    HotkeyRegistryProvider {
        AppTheme(
            theme = theme,
            modifier = Modifier.debugHotkey { key ->
                when (key) {
                    Key.T -> {
                        theme = when (theme) {
                            Theme.System -> Theme.Light
                            Theme.Light -> Theme.Dark
                            Theme.Dark -> Theme.System
                        }
                        true
                    }

                    Key.R -> {
                        viewModel.restoreDefaultSettings()
                        true
                    }

                    else -> false
                }
            }
        ) {
            CompositionLocalProvider(LocalSystemBars provides systemBarsController) {
                AppNavigation(
                    viewModel,
                    settingsEditor = settingsEditor ?: { navigation, navigationIcon ->
                        SettingsEditorScreen(viewModel, navigation, navigationIcon = navigationIcon)
                    }
                )
            }
        }
    }
}
