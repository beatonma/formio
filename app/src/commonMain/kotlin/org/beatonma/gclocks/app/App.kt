package org.beatonma.gclocks.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.beatonma.gclocks.app.settings.AppSettings
import org.beatonma.gclocks.app.settings.CommonAppSettings
import org.beatonma.gclocks.app.settings.clocks.FormOptionsAdapter
import org.beatonma.gclocks.app.settings.clocks.Io16OptionsAdapter
import org.beatonma.gclocks.app.settings.clocks.Io18OptionsAdapter
import org.beatonma.gclocks.app.settings.loadAppSettings
import org.beatonma.gclocks.app.theme.AppTheme
import org.beatonma.gclocks.app.theme.Material3.extendedFloatingActionButton
import org.beatonma.gclocks.compose.components.Clock
import org.beatonma.gclocks.compose.components.settings.ClockSettings


@Composable
fun App(
    appSettings: AppSettings = rememberAppSettings(),
    contextSelector: @Composable (AppSettings) -> Unit,
) {
    val clockConfig = rememberClockSettings(appSettings)
        ?: return Text("Loading")

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
                        contextSelector(appSettings)
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
private fun rememberAppSettings(): AppSettings {
    return remember { loadAppSettings() }
}

@Composable
private fun rememberClockSettings(
    appSettings: AppSettings,
    scope: CoroutineScope = rememberCoroutineScope(),
): ClockSettings<*>? {
    var config: ClockSettings<*>? by remember { mutableStateOf(null) }

    LaunchedEffect(appSettings, appSettings.context, appSettings.clock) {
        scope.launch {
            val context = appSettings.context
            val clock = appSettings.clock
            val settings = appSettings.getSettings(context)

            config = when (clock) {
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
    }


    return config
}
