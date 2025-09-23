package org.beatonma.gclocks.app

import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.browser.document
import org.beatonma.gclocks.app.theme.AppTheme

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        val factory = remember {
            AppViewModelFactory(
                repository = WebSettingsRepository()
            )
        }

        val viewModel: AppViewModel = viewModel(factory = factory)

        AppTheme {
            App(viewModel)
        }
    }
}
