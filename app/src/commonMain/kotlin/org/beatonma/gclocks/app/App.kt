package org.beatonma.gclocks.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.beatonma.gclocks.app.settings.AppSettings
import org.beatonma.gclocks.app.theme.AppTheme
import org.beatonma.gclocks.app.theme.Material3.extendedFloatingActionButton
import org.beatonma.gclocks.compose.Loading
import org.beatonma.gclocks.compose.components.Clock
import org.beatonma.gclocks.core.options.Options
import kotlin.enums.enumEntries


@Composable
fun App(viewModel: AppViewModel) {
    val settings by viewModel.appSettings.collectAsState()

    App(
        settings ?: return Loading(),
        onEditSettings = {
            viewModel.updateSettingsWithoutSave(it)
        },
        onSave = {
            viewModel.save()
        }
    )
}

@Composable
fun App(
    appSettings: AppSettings,
    onEditSettings: (AppSettings) -> Unit,
    onSave: () -> Unit,
) {
    val state = appSettings.state
    AppTheme {
        Box(Modifier.fillMaxSize()) {
            ClockSettings(
                appSettings.options,
                onEditOptions = {
                    onEditSettings(appSettings.copyWithOptions(it))
                },
                key = "${state.context}_${state.clock}"
            )

            Row(
                Modifier
                    .align(Alignment.BottomEnd)
                    .extendedFloatingActionButton(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Dropdown(state.context, {
                    onEditSettings(appSettings.copy(state = appSettings.state.copy(context = it)))
                })
                Dropdown(state.clock, {
                    onEditSettings(appSettings.copy(state = appSettings.state.copy(clock = it)))
                })

                ExtendedFloatingActionButton(onSave) {
                    Text("Save changes")
                }
            }
        }
    }
}

@Composable
private fun <O : Options<*>> ClockSettings(
    options: O,
    onEditOptions: suspend (O) -> Unit,
    key: String,
    modifier: Modifier = Modifier,
) {
    val viewmodel: ClockSettingsViewModel<O> = viewModel(
        key = key,
        factory = remember(options, onEditOptions) {
            ClockSettingsViewModelFactory(
                options,
                onEditOptions = onEditOptions,
            )
        }
    )

    ClockSettings(viewmodel, modifier)
}

@Composable
private fun <O : Options<*>> ClockSettings(
    viewModel: ClockSettingsViewModel<O>,
    modifier: Modifier = Modifier,
) {
    val options by viewModel.options.collectAsState()
    val richSettings by viewModel.richSettings.collectAsState()

    LazyVerticalStaggeredGrid(
        StaggeredGridCells.Adaptive(minSize = 300.dp),
        modifier
            .fillMaxHeight()
            .widthIn(max = 800.dp)
            .fillMaxWidth(),
        contentPadding = WindowInsets.systemBars.asPaddingValues()
    ) {
        item {
            Clock(
                options,
                Modifier.background(Color.DarkGray)
                    .heightIn(min = 200.dp, max = 400.dp)
            )
        }

        ClockSettingsItems(richSettings)
    }
}


@Composable
private inline fun <reified E : Enum<E>> Dropdown(
    value: E,
    crossinline onChange: (E) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(modifier) {
        ElevatedButton({ isExpanded = !isExpanded }) {
            Text("$value")
        }
        DropdownMenu(
            expanded = isExpanded,
            { isExpanded = false }
        ) {
            enumEntries<E>().forEach { item ->
                DropdownMenuItem(
                    { Text(item.name) },
                    {
                        isExpanded = false
                        onChange(item)
                    }
                )
            }
        }

    }
}