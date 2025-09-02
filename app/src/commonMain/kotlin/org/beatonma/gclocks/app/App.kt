package org.beatonma.gclocks.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.beatonma.gclocks.app.settings.AppSettings
import org.beatonma.gclocks.app.settings.CommonAppSettings
import org.beatonma.gclocks.app.settings.SettingsContext
import org.beatonma.gclocks.app.settings.clocks.FormOptionsAdapter
import org.beatonma.gclocks.app.settings.clocks.Io16OptionsAdapter
import org.beatonma.gclocks.app.settings.clocks.Io18OptionsAdapter
import org.beatonma.gclocks.app.theme.AppTheme
import org.beatonma.gclocks.app.theme.Material3.extendedFloatingActionButton
import org.beatonma.gclocks.compose.Loading
import org.beatonma.gclocks.compose.components.Clock
import org.beatonma.gclocks.compose.components.settings.ClockSettings


@Composable
fun App(appSettings: Flow<AppSettings>) {
    val appSettings: AppSettings? by appSettings.collectAsState(initial = null)

    appSettings?.let { App(it) } ?: Loading()
}

@Composable
fun App(appSettings: AppSettings) {
    val clockConfig = rememberClockSettings(appSettings) ?: return Loading()

    AppTheme {
        Box(Modifier.fillMaxSize()) {
            LazyVerticalStaggeredGrid(
                StaggeredGridCells.Adaptive(minSize = 300.dp),
                Modifier
                    .fillMaxHeight()
                    .widthIn(max = 800.dp)
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                contentPadding = WindowInsets.systemBars.asPaddingValues()
            ) {
                item {
                    Column {
                        Row {
                            ContextDropdown(appSettings.context, { appSettings.context = it })
                            ClockDropdown(appSettings.clock, { appSettings.clock = it })
                        }
                        Clock(
                            clockConfig.options,
                            Modifier.background(Color.DarkGray)
                                .heightIn(min = 200.dp, max = 400.dp)
                                .fillMaxWidth()
                        )
                    }
                }

                ClockSettings(clockConfig)
            }

            ExtendedFloatingActionButton(
                { clockConfig.save() },
                Modifier
                    .align(Alignment.BottomEnd)
                    .extendedFloatingActionButton()
            ) {
                Text("Save changes")
            }
        }
    }
}


@Composable
private fun rememberClockSettings(
    appSettings: AppSettings,
    scope: CoroutineScope = rememberCoroutineScope(),
): ClockSettings<*>? {
    val config: ClockSettings<*>? by produceState(
        initialValue = null,
        appSettings.context, appSettings.clock
    ) {
        val context = appSettings.context
        val settings = appSettings.getSettings(context)
        val clock = appSettings.clock

        value = when (clock) {
            CommonAppSettings.Companion.Clock.Form -> FormOptionsAdapter(settings.form) {
                scope.launch { appSettings.saveSettings(context, it) }
            }

            CommonAppSettings.Companion.Clock.Io16 -> Io16OptionsAdapter(settings.io16) {
                scope.launch { appSettings.saveSettings(context, it) }
            }

            CommonAppSettings.Companion.Clock.Io18 -> Io18OptionsAdapter(settings.io18) {
                scope.launch { appSettings.saveSettings(context, it) }
            }
        }
    }

    return config
}


@Composable
private fun ContextDropdown(
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