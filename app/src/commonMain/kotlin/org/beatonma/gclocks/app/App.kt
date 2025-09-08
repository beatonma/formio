package org.beatonma.gclocks.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
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
import org.beatonma.gclocks.app.settings.AppState
import org.beatonma.gclocks.app.settings.ContextClockOptions
import org.beatonma.gclocks.app.theme.AppTheme
import org.beatonma.gclocks.app.theme.Material3.extendedFloatingActionButton
import org.beatonma.gclocks.compose.Loading
import org.beatonma.gclocks.compose.components.Clock
import org.beatonma.gclocks.compose.components.settings.components.ScrollingColumn
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
                    onEditSettings(appSettings.copyWithOptions(it.clock, it.display))
                },
                key = "${state.context}_${state.clock}",
            )

            Actions(
                state,
                onEditState = { onEditSettings(appSettings.copy(state = it)) },
                onSave = onSave,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}

@Composable
private fun <O : Options<*>> ClockSettings(
    options: ContextClockOptions<O>,
    onEditOptions: suspend (ContextClockOptions<O>) -> Unit,
    key: String,
    modifier: Modifier = Modifier,
) {
    val viewmodel: SettingsViewModel<O> = viewModel(
        key = key,
        factory = remember(options, onEditOptions) {
            SettingsViewModelFactory(
                options,
                onEditOptions = onEditOptions,
            )
        }
    )

    ClockSettings(viewmodel, modifier)
}

@Composable
private fun <O : Options<*>> ClockSettings(
    viewModel: SettingsViewModel<O>,
    modifier: Modifier = Modifier,
) {
    val options by viewModel.contextOptions.collectAsState()
    val richSettings by viewModel.richSettings.collectAsState()

    ScrollingColumn(modifier.fillMaxSize().padding(WindowInsets.safeContent.asPaddingValues())) {
        item {
            Clock(
                options.clock,
                Modifier.background(Color.DarkGray)
                    .heightIn(min = 200.dp, max = 400.dp)
            )
        }

        ClockSettingsItems(richSettings)
    }
}


//@Composable
//private fun <O : Options<*>> ClockSettings(
//    options: O,
//    onEditOptions: suspend (O) -> Unit,
//    key: String,
//    modifier: Modifier = Modifier,
//) {
//    val viewmodel: ClockSettingsViewModel<O> = viewModel(
//        key = key,
//        factory = remember(options, onEditOptions) {
//            ClockSettingsViewModelFactory(
//                options,
//                onEditOptions = onEditOptions,
//            )
//        }
//    )
//
//    ClockSettings(viewmodel, modifier)
//}
//@Composable
//private fun <O : Options<*>> ClockSettings(
//    viewModel: ClockSettingsViewModel<O>,
//    modifier: Modifier = Modifier,
//) {
//    val options by viewModel.options.collectAsState()
//    val richSettings by viewModel.richSettings.collectAsState()
//
//    ScrollingColumn(modifier.fillMaxSize().padding(WindowInsets.safeContent.asPaddingValues())) {
//        item {
//            Clock(
//                options,
//                Modifier.background(Color.DarkGray)
//                    .heightIn(min = 200.dp, max = 400.dp)
//            )
//        }
//
//        ClockSettingsItems(richSettings)
//    }
//}

@Composable
private fun Actions(
    state: AppState,
    onEditState: (AppState) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .extendedFloatingActionButton(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Dropdown(state.context, {
            onEditState(state.copy(context = it))
        })
        Dropdown(state.clock, {
            onEditState(state.copy(clock = it))
        })

        ExtendedFloatingActionButton(onSave) {
            Text("Save changes")
        }
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