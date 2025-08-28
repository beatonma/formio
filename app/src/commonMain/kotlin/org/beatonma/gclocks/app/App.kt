package org.beatonma.gclocks.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.beatonma.gclocks.app.screens.ClockSettingsScreen
import org.beatonma.gclocks.io18.Io18Options

@Composable
fun App() {
    var options by remember { mutableStateOf(Io18Options()) }

    MaterialTheme {
        ClockSettingsScreen(
            options,
            { options = it }
        )
    }
}