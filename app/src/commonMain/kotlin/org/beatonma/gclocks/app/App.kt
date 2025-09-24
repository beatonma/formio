package org.beatonma.gclocks.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.window.core.layout.WindowWidthSizeClass
import gclocks_multiplatform.app.generated.resources.Res
import gclocks_multiplatform.app.generated.resources.ui_choose_clock_style
import gclocks_multiplatform.app.generated.resources.ui_fullscreen_close
import gclocks_multiplatform.app.generated.resources.ui_fullscreen_open
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.beatonma.gclocks.app.settings.AppSettings
import org.beatonma.gclocks.app.settings.ContextClockOptions
import org.beatonma.gclocks.app.settings.DisplayContext
import org.beatonma.gclocks.app.theme.rememberContentColor
import org.beatonma.gclocks.compose.*
import org.beatonma.gclocks.compose.animation.*
import org.beatonma.gclocks.compose.components.ButtonGroup
import org.beatonma.gclocks.compose.components.Clock
import org.beatonma.gclocks.compose.components.ClockLayout
import org.beatonma.gclocks.compose.components.IconToolbar
import org.beatonma.gclocks.compose.components.settings.RichSettings
import org.beatonma.gclocks.compose.components.settings.components.SettingLayout
import org.beatonma.gclocks.compose.components.settings.components.SettingName
import org.beatonma.gclocks.core.options.Options
import org.jetbrains.compose.resources.stringResource

internal interface Screen {
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


/**
 * Data needed to render a preview of a clock with current settings.
 */
data class ClockPreview(
    val options: Options<*>,
    val background: Color,
)

val LocalClockPreview: ProvidableCompositionLocal<ClockPreview?> = compositionLocalOf { null }

data class AppAdapter(
    val snackbarHostState: SnackbarHostState? = null,
    val onClickPreview: ((DisplayContext) -> Unit)? = null,
    val toolbar: (@Composable RowScope.(DisplayContext) -> Unit)? = null,
)


@Composable
fun App(
    viewModel: AppViewModel,
    snackbarHostState: SnackbarHostState? = null,
    toolbar: @Composable (RowScope.(DisplayContext) -> Unit)? = null,
) {
    var isClockFullscreen by rememberSaveable { mutableStateOf(false) }
    val settings by viewModel.appSettings.collectAsStateWithLifecycle()
    val options = settings.contextOptions

    if (options.displayOptions !is DisplayContext.Options.WithBackground) {
        return App(
            settings,
            viewModel,
            AppAdapter(
                snackbarHostState = snackbarHostState,
                onClickPreview = null,
                toolbar = toolbar
            )
        )
    }

    @OptIn(ExperimentalComposeUiApi::class)
    BackHandler(enabled = isClockFullscreen) {
        isClockFullscreen = false
    }

    AnimatedVisibility(
        !isClockFullscreen,
        enter = EnterImmediate,
        exit = fadeOut(tween(delayMillis = 100))
    ) {
        App(
            settings,
            viewModel,
            AppAdapter(
                snackbarHostState = snackbarHostState,
                onClickPreview = when (toolbar) {
                    null -> null
                    else -> {
                        { isClockFullscreen = true }
                    }
                },
                toolbar = toolbar ?: {
                    IconButton({ isClockFullscreen = true }) {
                        Icon(AppIcon.FullscreenOpen, stringResource(Res.string.ui_fullscreen_open))
                    }
                }
            )
        )
    }

    AnimatedVisibility(
        isClockFullscreen,
        enter = EnterVertical,
        exit = ExitFade
    ) {
        FullScreenClock(options, onClose = {
            @Suppress("AssignedValueIsNeverRead")
            isClockFullscreen = false
        })
    }
}


@Composable
private fun App(
    settings: AppSettings,
    viewModel: AppViewModel,
    appAdapter: AppAdapter?,
) {
    val hasUnsavedChanges by viewModel.hasUnsavedChanges.collectAsStateWithLifecycle()
    val content: @Composable () -> Unit = {
        ClockSettingsScaffold(
            settings,
            setClock = viewModel::setClock,
            updateClockOptions = viewModel::setClockOptions,
            updateDisplayOptions = viewModel::setDisplayOptions,
            hasUnsavedChanges = hasUnsavedChanges,
            onSave = viewModel::save,
            appAdapter = appAdapter,
        )
    }

    if (Screens.entries.size <= 1) {
        content()
    } else {
        NavigationSuiteApp(
            settings,
            setDisplayContext = viewModel::setDisplayContext,
            content = content,
        )
    }
}


@Composable
private fun NavigationSuiteApp(
    appSettings: AppSettings,
    setDisplayContext: (DisplayContext) -> Unit,
    content: @Composable () -> Unit,
) {
    val currentScreen =
        Screens.entries.find { it.displayContext == appSettings.state.displayContext }
            ?: Screens.entries.first()

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            Screens.entries.forEach {
                item(
                    icon = { Icon(it.icon, it.contentDescription.resolve()) },
                    label = { Text(it.label.resolve()) },
                    selected = it == currentScreen,
                    onClick = { setDisplayContext(it.displayContext) }
                )
            }
        }
    ) {
        content()
    }
}


@Composable
private fun ClockSettingsScaffold(
    appSettings: AppSettings,
    setClock: (AppSettings.Clock) -> Unit,
    updateClockOptions: (Options<*>) -> Unit,
    updateDisplayOptions: (DisplayContext.Options) -> Unit,
    hasUnsavedChanges: Boolean,
    onSave: () -> Unit,
    appAdapter: AppAdapter?,
) {
    val appState = appSettings.state
    val clockViewModel = clockSettingsViewModel(
        appSettings.contextOptions,
        updateClockOptions,
        updateDisplayOptions,
        key = "${appState.displayContext}_${appSettings.contextSettings.clock}",
    )
    val gridState = rememberLazyStaggeredGridState()
    val options by clockViewModel.contextOptions.collectAsStateWithLifecycle()
    val richSettings by clockViewModel.richSettings.collectAsStateWithLifecycle()

    val backgroundColor = resolveClockBackgroundColor(options.displayOptions)
    val foregroundColor = rememberContentColor(backgroundColor)

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    val clockPreviewShape = when (windowSizeClass.windowWidthSizeClass) {
        WindowWidthSizeClass.COMPACT -> RectangleShape
        else -> shapes.medium
    }

    LaunchedEffect(appState) {
        gridState.scrollToItem(0)
    }

    Scaffold(
        snackbarHost = { appAdapter?.snackbarHostState?.let { SnackbarHost(it) } },
        floatingActionButton = {
            AnimatedFade(hasUnsavedChanges) {
                ExtendedFloatingActionButton(onSave, Modifier.safeDrawingPadding()) {
                    Text("Save changes")
                }
            }
        },
    ) { contentPadding ->
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CompositionLocalProvider(LocalContentColor provides foregroundColor) {
                    ClockPreview(
                        options.clockOptions,
                        appAdapter?.toolbar?.let { toolbar -> { toolbar(appState.displayContext) } },
                        Modifier
                            .clip(clockPreviewShape)
                            .background(backgroundColor)
                            .animateContentSize(),
                        clockModifier = Modifier
                            .onlyIf(appAdapter?.onClickPreview) { onClick ->
                                clickable(onClick = { onClick(appState.displayContext) })
                            }
                            .sizeIn(maxWidth = 600.dp, maxHeight = 300.dp)
                            .padding(contentPadding)
                            .padding(64.dp)
                            .border(1.dp, foregroundColor.copy(alpha = 0.1f)),
                    )
                }

                CompositionLocalProvider(
                    LocalClockPreview provides ClockPreview(
                        options.clockOptions,
                        backgroundColor
                    )
                ) {
                    SettingsGrid(
                        appSettings.contextSettings.clock,
                        setClock,
                        richSettings ?: return@CompositionLocalProvider Loading(),
                        gridState = gridState,
                        contentPadding = VerticalBottomContentPadding,
                    )
                }
            }
        }
    }
}


@Composable
private fun ClockPreview(
    options: Options<*>,
    toolbar: @Composable (RowScope.() -> Unit)?,
    modifier: Modifier,
    clockModifier: Modifier,
) {
    Box(modifier) {
        Clock(options, clockModifier)
        toolbar?.let { toolbar ->
            IconToolbar(Modifier.align(Alignment.BottomEnd), content = toolbar)
        }
    }
}


@Composable
private fun SettingsGrid(
    clock: AppSettings.Clock,
    onChangeClock: (AppSettings.Clock) -> Unit,
    richSettings: RichSettings,
    modifier: Modifier = Modifier,
    gridState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    contentPadding: PaddingValues = PaddingValues(),
) {
    val groupModifier = Modifier.padding(vertical = 8.dp)
    val itemModifier = Modifier.horizontalMarginModifier().padding(vertical = 4.dp)

    LazyVerticalStaggeredGrid(
        StaggeredGridCells.Adaptive(minSize = 350.dp),
        modifier.widthIn(max = 800.dp),
        gridState,
        contentPadding = contentPadding,
        verticalItemSpacing = 0.dp,
        horizontalArrangement = Arrangement.spacedBy(
            HorizontalMargin,
            Alignment.CenterHorizontally
        )
    ) {
        item {
            ClockSelector(
                clock,
                onChangeClock,
                groupModifier.then(itemModifier).horizontalMarginModifier(),
            )
        }

        ClockSettingsItems(
            richSettings.all,
            groupModifier = groupModifier,
            itemModifier = itemModifier,
        )
    }
}


@Composable
private fun ClockSelector(
    selected: AppSettings.Clock,
    onSelect: (AppSettings.Clock) -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingLayout(modifier) {
        SettingName(stringResource(Res.string.ui_choose_clock_style))

        ButtonGroup(
            selected,
            { it.name },
            onSelect,
            AppSettings.Clock.entries,
        )
    }
}

@Composable
private fun FullScreenClock(
    options: ContextClockOptions<*>,
    onClose: () -> Unit,
) {
    var isOverlayVisible by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    var visibilityTimeoutJob: Job? by remember { mutableStateOf(null) }
    val systemBarsController = LocalSystemBars.current

    DisposableEffect(Unit) {
        systemBarsController?.onRequestHideSystemBars()
        onDispose { systemBarsController?.onRequestShowSystemBars() }
    }

    Box(
        Modifier.pointerInput(Unit) {
            while (true) {
                awaitPointerEventScope { awaitPointerEvent() }
                isOverlayVisible = true

                visibilityTimeoutJob?.cancel()
                visibilityTimeoutJob = scope.launch {
                    delay(1000L)
                    isOverlayVisible = false
                }
            }
        }
    ) {
        ClockLayout(
            options.displayOptions as DisplayContext.Options.WithBackground,
        ) {
            Clock(options.clockOptions)
        }

        AnimatedVisibility(
            isOverlayVisible,
            Modifier.align(Alignment.BottomEnd).safeDrawingPadding(),
            enter = EnterFade,
            exit = ExitFade
        ) {
            IconToolbar(Modifier.padding(8.dp).clip(shapes.small).background(colorScheme.scrim)) {
                IconButton(onClose) {
                    Icon(
                        AppIcon.FullscreenClose,
                        stringResource(Res.string.ui_fullscreen_close)
                    )
                }
            }
        }
    }
}


@Composable
private fun <O : Options<*>> clockSettingsViewModel(
    options: ContextClockOptions<O>,
    updateClockOptions: (O) -> Unit,
    updateDisplayOptions: (DisplayContext.Options) -> Unit,
    key: String,
): SettingsViewModel<O> {
    return viewModel(
        key = key,
        factory = remember(options, updateClockOptions, updateDisplayOptions) {
            SettingsViewModelFactory(
                options,
                updateClockOptions,
                updateDisplayOptions
            )
        }
    )
}


@Composable
private fun resolveClockBackgroundColor(displayOptions: DisplayContext.Options): Color {
    return when (displayOptions) {
        is DisplayContext.Options.WithBackground -> displayOptions.backgroundColor.toCompose()
        else -> null
    } ?: colorScheme.surface
}
