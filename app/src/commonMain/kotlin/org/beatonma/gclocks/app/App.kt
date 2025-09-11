package org.beatonma.gclocks.app

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.waterfall
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import gclocks_multiplatform.app.generated.resources.Res
import gclocks_multiplatform.app.generated.resources.ui_choose_clock_style
import org.beatonma.gclocks.app.settings.AppSettings
import org.beatonma.gclocks.app.settings.ContextClockOptions
import org.beatonma.gclocks.app.settings.DisplayContext
import org.beatonma.gclocks.app.settings.clocks.CommonKeys
import org.beatonma.gclocks.app.theme.AppTheme
import org.beatonma.gclocks.compose.Loading
import org.beatonma.gclocks.compose.VerticalBottomContentPadding
import org.beatonma.gclocks.compose.components.ButtonGroup
import org.beatonma.gclocks.compose.components.Clock
import org.beatonma.gclocks.compose.components.settings.RichSetting
import org.beatonma.gclocks.compose.components.settings.components.SettingName
import org.beatonma.gclocks.compose.plus
import org.beatonma.gclocks.compose.toCompose
import org.beatonma.gclocks.core.options.Options
import org.jetbrains.compose.resources.stringResource

interface Screen {
    val displayContext: DisplayContext
    val label: LocalizedString
    val contentDescription: LocalizedString
    val icon: ImageVector
}

expect enum class Screens : Screen

private val HorizontalMargin = 8.dp

private fun Modifier.horizontalMarginModifier() = composed {
    padding(horizontal = HorizontalMargin)
        .padding(WindowInsets.waterfall.asPaddingValues())
}


@Composable
fun App(
    viewModel: AppViewModel,
    snackbarHostState: SnackbarHostState? = null,
) {
    val settings by viewModel.appSettings.collectAsState()

    NavigationSuiteApp(
        settings ?: return Loading(),
        onEditSettings = {
            viewModel.updateSettingsWithoutSave(it)
        },
        onSave = {
            viewModel.save()
        },
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun NavigationSuiteApp(
    appSettings: AppSettings,
    onEditSettings: (AppSettings) -> Unit,
    onSave: () -> Unit,
    snackbarHostState: SnackbarHostState? = null,
) {
    val currentScreen =
        Screens.entries.find { it.displayContext == appSettings.state.context }
            ?: Screens.entries.first()

    AppTheme {
        NavigationSuiteScaffold(
            navigationSuiteItems = {
                Screens.entries.forEach {
                    item(
                        icon = { Icon(it.icon, it.contentDescription.resolve()) },
                        label = { Text(it.label.resolve()) },
                        selected = it == currentScreen,
                        onClick = {
                            onEditSettings(appSettings.copy(state = appSettings.state.copy(context = it.displayContext)))
                        },
                    )
                }
            }
        ) {
            ClockSettingsScaffold(appSettings, onEditSettings, onSave, snackbarHostState)
        }
    }
}


@Composable
private fun ClockSettingsScaffold(
    appSettings: AppSettings,
    onEditSettings: (AppSettings) -> Unit,
    onSave: () -> Unit,
    snackbarHostState: SnackbarHostState?,
) {
    val state = appSettings.state
    val clockViewModel = clockSettingsViewModel(
        appSettings.options,
        onEditOptions = { onEditSettings(appSettings.copyWithOptions(it.clock, it.display)) },
        key = "${state.context}_${state.clock}",
    )
    val options by clockViewModel.contextOptions.collectAsState()
    val richSettings by clockViewModel.richSettings.collectAsState()
    val gridState = rememberLazyStaggeredGridState()

    LaunchedEffect(state) {
        gridState.scrollToItem(0)
    }

    Scaffold(
        snackbarHost = { snackbarHostState?.let { SnackbarHost(it) } },
        floatingActionButton = {
            ExtendedFloatingActionButton(onSave) {
                Text("Save changes")
            }
        },
    ) { contentPadding ->
        val backgroundColor =
            richSettings.firstOrNull { it is RichSetting<*> && it.key == CommonKeys.backgroundColor }
                ?.let { it as? RichSetting.Color }?.value?.toCompose() ?: colorScheme.surface

        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            LazyVerticalStaggeredGrid(
                StaggeredGridCells.Adaptive(minSize = 300.dp),
                Modifier.widthIn(max = 700.dp),
                gridState,
                contentPadding = contentPadding + VerticalBottomContentPadding,
                verticalItemSpacing = 24.dp,
                horizontalArrangement = Arrangement.spacedBy(
                    HorizontalMargin,
                    Alignment.CenterHorizontally
                )
            ) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    ClockSelector(
                        appSettings.state.clock,
                        Modifier.horizontalMarginModifier(),
                    ) { selected ->
                        onEditSettings(
                            appSettings.copy(
                                state = appSettings.state.copy(clock = selected)
                            )
                        )
                    }
                }

                item(span = StaggeredGridItemSpan.FullLine) {
                    Clock(
                        options.clock,
                        Modifier
                            .background(backgroundColor)
                            .padding(64.dp)
                            .animateContentSize()
                    )
                }

                ClockSettingsItems(
                    richSettings,
                    groupModifier = Modifier.padding(vertical = 8.dp),
                    itemModifier = Modifier.horizontalMarginModifier().padding(vertical = 4.dp)
                )
            }
        }
    }
}


@Composable
private fun ClockSelector(
    selected: AppSettings.Clock,
    modifier: Modifier = Modifier,
    onSelect: (AppSettings.Clock) -> Unit,
) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        SettingName(stringResource(Res.string.ui_choose_clock_style))

        ButtonGroup(
            selected,
            { it.name },
            onSelect,
            AppSettings.Clock.entries.toList(),
        )
    }
}


@Composable
private fun <O : Options<*>> clockSettingsViewModel(
    options: ContextClockOptions<O>,
    onEditOptions: suspend (ContextClockOptions<O>) -> Unit,
    key: String,
): SettingsViewModel<O> {
    return viewModel(
        key = key,
        factory = remember(options, onEditOptions) {
            SettingsViewModelFactory(
                options,
                onEditOptions = onEditOptions,
            )
        }
    )
}
