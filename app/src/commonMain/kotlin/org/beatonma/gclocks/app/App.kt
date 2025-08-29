package org.beatonma.gclocks.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.beatonma.gclocks.app.screens.ClockSettingsScreen
import org.beatonma.gclocks.compose.components.Clock
import org.beatonma.gclocks.core.util.debug
import org.beatonma.gclocks.io18.Io18Options

@Composable
fun App() {
    var options by remember { mutableStateOf(Io18Options()) }

    AppTheme {
        Column {
            Clock(options, Modifier.fillMaxWidth().aspectRatio(16f / 9f))
            ClockSettingsScreen(
                options,
                {
                    debug("Options updated")
                    options = it
                }
            )
        }
    }
}