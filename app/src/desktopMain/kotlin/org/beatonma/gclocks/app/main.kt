package org.beatonma.gclocks.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.beatonma.gclocks.app.settings.CommonAppSettings
import org.beatonma.gclocks.app.settings.SettingsContext

fun main() = application {
    val windowState = rememberWindowState(
        position = WindowPosition.Aligned(Alignment.TopEnd),
        size = DpSize(400.dp, 800.dp),
    )

    Window(
        state = windowState,
        onCloseRequest = ::exitApplication,
        title = "gclocks-multiplatform",
    ) {
        App { appSettings ->
            Row {
                AppContextDropdown(
                    appSettings.context,
                    { appSettings.context = it },
                )
                ClockDropdown(
                    appSettings.clock,
                    { appSettings.clock = it },
                )
            }
        }
    }
}


@Composable
private fun AppContextDropdown(
    context: SettingsContext,
    onChange: (SettingsContext) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }
    Box(modifier) {
        OutlinedButton({ isExpanded = !isExpanded }) {
            Text("$context")
        }
        DropdownMenu(
            expanded = isExpanded,
            { isExpanded = false }
        ) {
            SettingsContext.entries.forEach { ctx ->
                DropdownMenuItem(
                    { Text("$ctx") },
                    {
                        isExpanded = false
                        onChange(ctx)
                    },
                )
            }
        }
    }
}

@Composable
private fun ClockDropdown(
    context: CommonAppSettings.Companion.Clock,
    onChange: (CommonAppSettings.Companion.Clock) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }
    Box(modifier) {
        OutlinedButton({ isExpanded = !isExpanded }) {
            Text("$context")
        }
        DropdownMenu(
            expanded = isExpanded,
            { isExpanded = false }
        ) {
            CommonAppSettings.Companion.Clock.entries.forEach { ctx ->
                DropdownMenuItem(
                    { Text("$ctx") },
                    {
                        isExpanded = false
                        onChange(ctx)
                    },
                )
            }
        }
    }
}