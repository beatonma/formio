package org.beatonma.formio.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import kotlinx.browser.window
import org.beatonma.formio.app.data.ClockSearchParams
import org.beatonma.formio.app.data.WebSettingsRepository
import org.beatonma.formio.app.data.mergeSettings
import org.beatonma.formio.app.data.settings.AnyContextClockOptions
import org.beatonma.formio.app.data.settings.ClockType
import org.beatonma.formio.app.data.settings.DefaultAppSettings
import org.beatonma.formio.app.ui.App
import org.beatonma.formio.app.ui.screens.FullSizeClock
import org.beatonma.formio.app.ui.screens.SettingsEditorScreen
import org.beatonma.formio.app.ui.screens.settingsEditorViewModel

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        when {
            window.self !== window.top -> {
                // Window is included via iframe -> show non-editable clock.
                val params = ClockSearchParams.fromString(window.location.search)

                ReadOnly(params)
            }

            else -> Editable()
        }
    }
}


@Composable
private fun Editable() {
    val repository = remember { WebSettingsRepository() }
    val viewModel = settingsEditorViewModel(repository)

    App(viewModel) { navigation, navigationIcon ->
        SettingsEditorScreen(viewModel, navigation, navigationIcon = navigationIcon)
    }
}


@Composable
private fun ReadOnly(custom: ClockSearchParams?) {
    val options: AnyContextClockOptions = remember {
        mergeSettings(
            DefaultAppSettings.copyWithClock(custom?.clock ?: ClockType.entries.random()),
            custom
        ).contextOptions
    }

    FullSizeClock(options, Modifier.fillMaxSize().padding(16.dp))
}
