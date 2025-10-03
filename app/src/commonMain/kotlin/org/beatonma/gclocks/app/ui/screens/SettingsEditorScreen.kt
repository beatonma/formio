package org.beatonma.gclocks.app.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.waterfall
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import gclocks_multiplatform.app.generated.resources.Res
import gclocks_multiplatform.app.generated.resources.cd_fullscreen_open
import kotlinx.coroutines.CoroutineScope
import org.beatonma.gclocks.app.data.settings.AppSettings
import org.beatonma.gclocks.app.data.settings.ContextClockOptions
import org.beatonma.gclocks.app.data.settings.DisplayContext
import org.beatonma.gclocks.app.data.settings.DisplayContextScreens
import org.beatonma.gclocks.app.theme.rememberContentColor
import org.beatonma.gclocks.app.ui.AppNavigation
import org.beatonma.gclocks.compose.AppIcon
import org.beatonma.gclocks.compose.LoadingSpinner
import org.beatonma.gclocks.compose.VerticalBottomContentPadding
import org.beatonma.gclocks.compose.animation.AnimatedFade
import org.beatonma.gclocks.compose.components.Clock
import org.beatonma.gclocks.compose.components.IconToolbar
import org.beatonma.gclocks.compose.components.settings.Setting
import org.beatonma.gclocks.compose.components.settings.data.RichSetting
import org.beatonma.gclocks.compose.components.settings.data.RichSettings
import org.beatonma.gclocks.compose.components.settings.data.RichSettingsGroup
import org.beatonma.gclocks.compose.components.settings.data.Setting
import org.beatonma.gclocks.compose.horizontal
import org.beatonma.gclocks.compose.onlyIf
import org.beatonma.gclocks.compose.plus
import org.beatonma.gclocks.compose.toCompose
import org.beatonma.gclocks.core.options.Options
import org.jetbrains.compose.resources.stringResource


private val ColumnPreferredWidth = 350.dp
private val ColumnMaxWidth = 400.dp

private val ColumnSpacing = 32.dp
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

data class SettingsEditorAdapter(
    val snackbarHostState: SnackbarHostState? = null,
    val onClickPreview: ((DisplayContext) -> Unit)? = null,
    val toolbar: (@Composable RowScope.(DisplayContext) -> Unit)? = null,
)


@Composable
fun SettingsEditorScreen(
    viewModel: SettingsEditorViewModel,
    navigation: AppNavigation,
    snackbarHostState: SnackbarHostState? = null,
    toolbar: @Composable (RowScope.(DisplayContext) -> Unit)? = null,
) {
    SettingsEditorScreen(
        viewModel,
        remember(navigation, snackbarHostState, toolbar) {
            SettingsEditorAdapter(
                snackbarHostState = snackbarHostState,
                onClickPreview = when (toolbar) {
                    null -> null
                    else -> {
                        { navigation.onNavigateClockPreview() }
                    }
                },
                toolbar = toolbar ?: {
                    IconButton(navigation.onNavigateClockPreview) {
                        Icon(AppIcon.FullscreenOpen, stringResource(Res.string.cd_fullscreen_open))
                    }
                }
            )
        }
    )
}


@Composable
private fun SettingsEditorScreen(
    viewModel: SettingsEditorViewModel,
    settingsEditorAdapter: SettingsEditorAdapter?,
) {
    val _settings by viewModel.appSettings.collectAsStateWithLifecycle()
    val _richSettings by viewModel.richSettings.collectAsStateWithLifecycle()
    val settings = _settings ?: return LoadingSpinner(Modifier.fillMaxSize())
    val richSettings = _richSettings
        ?: return LoadingSpinner(Modifier.fillMaxSize(), settings.contextSettings.clock)

    val hasUnsavedChanges by viewModel.hasUnsavedChanges.collectAsStateWithLifecycle()
    val content: @Composable () -> Unit = {
        ClockSettingsScaffold(
            key = "${settings.state.displayContext}_${settings.contextSettings.clock}",
            options = settings.contextOptions,
            richSettings = richSettings,
            hasUnsavedChanges = hasUnsavedChanges,
            onSave = viewModel::save,
            settingsEditorAdapter = settingsEditorAdapter,
        )
    }

    if (DisplayContextScreens.entries.size <= 1) {
        content()
    } else {
        SettingsEditorScreenWithNavigation(
            settings,
            setDisplayContext = viewModel::setDisplayContext,
            content = content,
        )
    }
}


@Composable
private fun SettingsEditorScreenWithNavigation(
    appSettings: AppSettings,
    setDisplayContext: (DisplayContext) -> Unit,
    content: @Composable () -> Unit,
) {
    val currentScreen =
        DisplayContextScreens.entries.find { it.displayContext == appSettings.state.displayContext }
            ?: DisplayContextScreens.entries.first()

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            DisplayContextScreens.entries.forEach {
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
    key: Any,
    options: ContextClockOptions<*>,
    richSettings: RichSettings,
    hasUnsavedChanges: Boolean,
    onSave: () -> Unit,
    settingsEditorAdapter: SettingsEditorAdapter?,
) {
    val backgroundColor = resolveClockBackgroundColor(options.displayOptions)
    val foregroundColor = rememberContentColor(backgroundColor)

    Scaffold(
        snackbarHost = { settingsEditorAdapter?.snackbarHostState?.let { SnackbarHost(it) } },
        floatingActionButton = {
            AnimatedFade(hasUnsavedChanges) {
                ExtendedFloatingActionButton(onSave, Modifier.safeDrawingPadding()) {
                    Text("Save changes")
                }
            }
        },
    ) { contentPadding ->
        CompositionLocalProvider(
            LocalClockPreview provides ClockPreview(
                options.clockOptions,
                backgroundColor
            )
        ) {
            SettingsUi(
                key,
                richSettings,
                Modifier.fillMaxWidth(),
                contentPadding = VerticalBottomContentPadding + contentPadding.horizontal(),
                clockPreview = { modifier ->
                    CompositionLocalProvider(LocalContentColor provides foregroundColor) {
                        ClockPreview(
                            options.clockOptions,
                            settingsEditorAdapter?.toolbar?.let { toolbar -> { toolbar(options.displayContext) } },
                            modifier
                                .background(backgroundColor)
                                .onlyIf(settingsEditorAdapter?.onClickPreview) { onClick ->
                                    clickable(onClick = { onClick(options.displayContext) })
                                }
                                .animateContentSize(),
                            clockModifier = Modifier
                                .sizeIn(maxWidth = 600.dp, maxHeight = 300.dp)
                                .padding(64.dp),
                        )
                    }
                }
            )
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
        Clock(options, clockModifier.align(Alignment.Center), allowVariance = true)
        toolbar?.let { toolbar ->
            IconToolbar(Modifier.align(Alignment.BottomEnd), content = toolbar)
        }
    }
}


@Composable
private fun WideAndTall(
    key: Any,
    richSettings: RichSettings,
    itemModifier: Modifier,
    groupModifier: Modifier,
    contentPadding: PaddingValues,
    coroutineScope: CoroutineScope,
    clockPreview: (@Composable (Modifier) -> Unit),
) {
    val (left, right) = richSettings.groups(2)

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        clockPreview(Modifier)

        Row(horizontalArrangement = Arrangement.spacedBy(ColumnSpacing, Alignment.CenterHorizontally)) {
            val columnModifier = Modifier.widthIn(max = ColumnMaxWidth).weight(1f, fill = false)
            ClockSettingsColumn(
                key,
                left,
                contentPadding,
                coroutineScope,
                modifier = columnModifier,
                itemModifier = itemModifier,
                groupModifier = groupModifier
            )

            ClockSettingsColumn(
                key,
                right,
                contentPadding,
                coroutineScope,
                modifier = columnModifier,
                itemModifier = itemModifier,
                groupModifier = groupModifier
            )
        }
    }
}

@Composable
private fun NarrowAndTall(
    key: Any,
    richSettings: RichSettings,
    itemModifier: Modifier,
    groupModifier: Modifier,
    contentPadding: PaddingValues,
    coroutineScope: CoroutineScope,
    clockPreview: (@Composable (Modifier) -> Unit),
) {
    val (settings) = richSettings.groups(1)

    ClockSettingsColumn(
        key,
        settings,
        contentPadding,
        coroutineScope,
        itemModifier = itemModifier,
        groupModifier = groupModifier
    ) {
        stickyHeader {
            clockPreview(Modifier)
        }
        return@ClockSettingsColumn 1
    }
}

@Composable
private fun WideAndShort(
    key: Any,
    richSettings: RichSettings,
    itemModifier: Modifier,
    groupModifier: Modifier,
    contentPadding: PaddingValues,
    coroutineScope: CoroutineScope,
    clockPreview: (@Composable (Modifier) -> Unit),
) {
    val (settings) = richSettings.groups(1)

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ColumnSpacing, Alignment.CenterHorizontally)
    ) {
        val columnModifier = Modifier.weight(1f, fill = false)
        clockPreview(columnModifier.fillMaxHeight())

        Box(columnModifier) {
            ClockSettingsColumn(
                key,
                settings,
                contentPadding,
                coroutineScope,
                modifier = Modifier.widthIn(max = ColumnMaxWidth),
                itemModifier = itemModifier,
                groupModifier = groupModifier
            )
        }
    }
}

@Composable
private fun NarrowAndShort(
    key: Any,
    richSettings: RichSettings,
    itemModifier: Modifier,
    groupModifier: Modifier,
    contentPadding: PaddingValues,
    coroutineScope: CoroutineScope,
    clockPreview: @Composable (Modifier) -> Unit,
) {
    val (settings) = richSettings.groups(1)

    ClockSettingsColumn(
        key,
        settings,
        contentPadding,
        coroutineScope,
        itemModifier = itemModifier,
        groupModifier = groupModifier
    ) {
        item {
            // On very small display, allow preview to scroll offscreen.
            clockPreview(Modifier)
        }
        return@ClockSettingsColumn 1
    }
}


@Composable
private fun SettingsUi(
    key: Any,
    richSettings: RichSettings,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    clockPreview: (@Composable (Modifier) -> Unit),
) {
    val groupModifier = Modifier.padding(vertical = 8.dp)
    val itemModifier = Modifier
        .horizontalMarginModifier()
        .padding(vertical = 4.dp)

    BoxWithConstraints(modifier, contentAlignment = Alignment.TopCenter) {
        val isWide: Boolean
        val isTall: Boolean

        with(LocalDensity.current) {
            isWide = constraints.maxWidth.toDp() > ((ColumnPreferredWidth * 2) + ColumnSpacing)
            isTall = constraints.maxHeight.toDp() > WindowSizeClass.HEIGHT_DP_EXPANDED_LOWER_BOUND.dp
        }

        when {
            isWide && isTall -> WideAndTall(
                key,
                richSettings,
                itemModifier,
                groupModifier,
                contentPadding,
                coroutineScope,
                clockPreview
            )

            isWide -> WideAndShort(
                key,
                richSettings,
                itemModifier,
                groupModifier,
                contentPadding,
                coroutineScope,
                clockPreview
            )

            isTall -> NarrowAndTall(
                key,
                richSettings,
                itemModifier,
                groupModifier,
                contentPadding,
                coroutineScope,
                clockPreview
            )

            else -> NarrowAndShort(
                key,
                richSettings,
                itemModifier,
                groupModifier,
                contentPadding,
                coroutineScope,
                clockPreview
            )
        }
    }
}

@Composable
private fun ClockSettingsColumn(
    key: Any,
    settings: List<Setting>,
    contentPadding: PaddingValues,
    scope: CoroutineScope,
    itemModifier: Modifier,
    groupModifier: Modifier,
    modifier: Modifier = Modifier,
    maxWidth: Dp = ColumnMaxWidth,
    state: LazyListState = rememberLazyListState(),
    header: LazyListScope.() -> Int = { 0 },
) {
    LaunchedEffect(key) {
        state.scrollToItem(0)
    }

    LazyColumn(
        modifier.widthIn(max = maxWidth),
        state = state,
        contentPadding = contentPadding,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val headerItemOffset = header()
//        val onFocus: (index: Int) -> Unit = { index ->
//            scope.launch {
//                state.scrollToItem(index + headerItemOffset)
//            }
//        }

        fun addItems(_settings: List<Setting>, indexOffset: Int) {
            _settings.forEachIndexed { index, item ->
                val index = index + indexOffset
                when (item) {
                    is RichSettingsGroup -> {
                        addItems(item.settings, index)
                    }

                    is RichSetting<*> -> item(
                        key = item.key.value,
                        contentType = item.key
                    ) {
                        Setting(item, itemModifier, onFocus = null)
                        if (index == _settings.size - 1) {
                            GroupSeparator(groupModifier)
                        }
                    }
                }
            }
        }

        addItems(settings, headerItemOffset)
    }
}


@Composable
private fun GroupSeparator(modifier: Modifier, content: @Composable BoxScope.() -> Unit = {}) {
    Box(modifier, contentAlignment = Alignment.Center, content = content)
}


@Composable
private fun resolveClockBackgroundColor(displayOptions: DisplayContext.Options): Color {
    return when (displayOptions) {
        is DisplayContext.Options.WithBackground -> displayOptions.backgroundColor.toCompose()
        else -> null
    } ?: colorScheme.surface
}
