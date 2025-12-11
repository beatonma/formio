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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import gclocks_multiplatform.app.generated.resources.Res
import gclocks_multiplatform.app.generated.resources.cd_fullscreen_open
import gclocks_multiplatform.app.generated.resources.setting_save_changes_fab
import org.beatonma.gclocks.app.data.settings.AnyContextClockOptions
import org.beatonma.gclocks.app.data.settings.DisplayContext
import org.beatonma.gclocks.app.data.settings.DisplayContextDefaults
import org.beatonma.gclocks.app.theme.DesignSpec.hamburgerPadding
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
import org.beatonma.gclocks.compose.copy
import org.beatonma.gclocks.compose.debugKeyEvent
import org.beatonma.gclocks.compose.onlyIf
import org.beatonma.gclocks.compose.plus
import org.beatonma.gclocks.compose.toCompose
import org.beatonma.gclocks.core.options.AnyOptions
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.graphics.Color as ComposeColor


private val ColumnPreferredWidth = 350.dp
private val ColumnMaxWidth = 450.dp

private val ColumnSpacing = 16.dp
private val ColumnTopPadding = 8.dp


/**
 * Data needed to render a preview of a clock with current settings.
 */
@Immutable
data class ClockPreview(
    val options: AnyOptions,
    val background: ComposeColor,
)

val LocalClockPreview: ProvidableCompositionLocal<ClockPreview?> = compositionLocalOf { null }

data class SettingsEditorAdapter(
    val snackbarHostState: SnackbarHostState? = null,
    val onClickPreview: ((DisplayContext) -> Unit)? = null,
    val navigationIcon: (@Composable () -> Unit)? = null,
    val toolbar: (@Composable RowScope.() -> Unit)? = null,
)


@Composable
fun SettingsEditorScreen(
    viewModel: SettingsEditorViewModel,
    navigation: AppNavigation,
    snackbarHostState: SnackbarHostState? = null,
    navigationIcon: (@Composable () -> Unit)? = null,
    toolbar: @Composable (RowScope.(DisplayContext) -> Unit)? = null,
) {
    val _settings by viewModel.appSettings.collectAsStateWithLifecycle()
    val _richSettings by viewModel.richSettings.collectAsStateWithLifecycle()

    val hasUnsavedChanges by viewModel.hasUnsavedChanges.collectAsStateWithLifecycle()

    val settings = _settings ?: return LoadingSpinner(Modifier.fillMaxSize())
    val onClickPreview: () -> Unit =
        remember(navigation.onNavigateClockPreview, settings.contextOptions) {
            { navigation.onNavigateClockPreview(settings.contextOptions) }
        }

    val adapter = remember(navigation, snackbarHostState, navigationIcon, toolbar, onClickPreview) {
        SettingsEditorAdapter(
            snackbarHostState = snackbarHostState,
            onClickPreview = toolbar?.let { { onClickPreview() } },
            navigationIcon = navigationIcon,
            toolbar = toolbar?.let { toolbar ->
                {
                    toolbar(
                        this,
                        settings.contextOptions.displayContext
                    )
                }
            } ?: {
                IconButton(onClickPreview) {
                    Icon(AppIcon.FullscreenOpen, stringResource(Res.string.cd_fullscreen_open))
                }
            }
        )
    }

    val richSettings = _richSettings
        ?: return LoadingSpinner(Modifier.fillMaxSize(), settings.contextSettings.clock)

    ClockSettingsScaffold(
        Modifier.debugKeyEvents(viewModel::restoreDefaultSettings),
        key = "${settings.state.displayContext}_${settings.contextSettings.clock}",
        options = settings.contextOptions,
        richSettings = richSettings,
        hasUnsavedChanges = hasUnsavedChanges,
        onSave = viewModel::save,
        settingsEditorAdapter = adapter,
    )
}


@Composable
private fun ClockSettingsScaffold(
    modifier: Modifier = Modifier,
    key: Any,
    options: AnyContextClockOptions,
    richSettings: RichSettings,
    hasUnsavedChanges: Boolean,
    onSave: () -> Unit,
    settingsEditorAdapter: SettingsEditorAdapter?,
) {
    val backgroundColor = resolveClockBackgroundColor(options.displayOptions)
    val foregroundColor = rememberContentColor(backgroundColor)

    Scaffold(
        modifier,
        snackbarHost = { settingsEditorAdapter?.snackbarHostState?.let { SnackbarHost(it) } },
        floatingActionButton = {
            AnimatedFade(hasUnsavedChanges) {
                ExtendedFloatingActionButton(onSave) {
                    Text(stringResource(Res.string.setting_save_changes_fab))
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
                contentPadding,
                Modifier.fillMaxWidth(),
                clockPreview = { modifier ->
                    CompositionLocalProvider(LocalContentColor provides foregroundColor) {
                        ClockPreview(
                            options.clockOptions,
                            settingsEditorAdapter?.toolbar,
                            modifier
                                .background(backgroundColor)
                                .onlyIf(settingsEditorAdapter?.onClickPreview) { onClick ->
                                    clickable(onClick = { onClick(options.displayContext) })
                                }
                                .animateContentSize(),
                            clockModifier = Modifier
                                .sizeIn(maxWidth = 600.dp, maxHeight = 300.dp)
                                .padding(contentPadding)
                                .consumeWindowInsets(contentPadding)
                                .padding(64.dp)
                                .fillMaxWidth(),
                        )
                    }
                }
            )
        }

        Box(Modifier.hamburgerPadding()) {
            CompositionLocalProvider(LocalContentColor provides foregroundColor) {
                settingsEditorAdapter?.navigationIcon?.invoke()
            }
        }
    }
}


@Composable
private fun ClockPreview(
    options: AnyOptions,
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
    columnModifier: Modifier,
    itemModifier: Modifier,
    groupModifier: Modifier,
    contentPadding: PaddingValues,
    clockPreview: (@Composable (Modifier) -> Unit),
) {
    val (left, right) = richSettings.groups(2)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        clockPreview(Modifier)

        Row(
            horizontalArrangement = Arrangement.spacedBy(
                ColumnSpacing,
                Alignment.CenterHorizontally
            )
        ) {
            val columnModifier = columnModifier.weight(1f, fill = false)
            ClockSettingsColumn(
                key,
                left,
                contentPadding.copy(end = 0.dp),
                modifier = columnModifier,
                itemModifier = itemModifier,
                groupModifier = groupModifier
            )

            ClockSettingsColumn(
                key,
                right,
                contentPadding.copy(start = 0.dp),
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
    columnModifier: Modifier,
    itemModifier: Modifier,
    groupModifier: Modifier,
    contentPadding: PaddingValues,
    clockPreview: (@Composable (Modifier) -> Unit),
) {
    val (settings) = richSettings.groups(1)

    ClockSettingsColumn(
        key,
        settings,
        contentPadding.copy(top = 0.dp),
        modifier = columnModifier,
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
    columnModifier: Modifier,
    itemModifier: Modifier,
    groupModifier: Modifier,
    contentPadding: PaddingValues,
    clockPreview: (@Composable (Modifier) -> Unit),
) {
    val (settings) = richSettings.groups(1)

    Row(
        Modifier.fillMaxWidth(),
    ) {
        val columnModifier = columnModifier.weight(1f, fill = true)
        clockPreview(columnModifier.fillMaxHeight())

        Box(columnModifier) {
            ClockSettingsColumn(
                key,
                settings,
                contentPadding.copy(start = 0.dp),
                modifier = columnModifier,
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
    columnModifier: Modifier,
    itemModifier: Modifier,
    groupModifier: Modifier,
    contentPadding: PaddingValues,
    clockPreview: @Composable (Modifier) -> Unit,
) {
    val (settings) = richSettings.groups(1)

    ClockSettingsColumn(
        key,
        settings,
        contentPadding,
        modifier = columnModifier,
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
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    clockPreview: (@Composable (Modifier) -> Unit),
) {
    val columnModifier = Modifier
    val groupModifier = Modifier.padding(vertical = 8.dp)
    val itemModifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)

    BoxWithConstraints(modifier, contentAlignment = Alignment.TopCenter) {
        val isWide: Boolean
        val isTall: Boolean

        with(LocalDensity.current) {
            isWide = constraints.maxWidth.toDp() > ((ColumnPreferredWidth * 2) + ColumnSpacing)
            isTall = constraints.maxHeight.toDp() > WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND.dp
        }

        when {
            isWide && isTall -> WideAndTall(
                key,
                richSettings,
                columnModifier,
                itemModifier,
                groupModifier,
                contentPadding,
                clockPreview
            )

            isWide -> WideAndShort(
                key,
                richSettings,
                columnModifier,
                itemModifier,
                groupModifier,
                contentPadding,
                clockPreview
            )

            isTall -> NarrowAndTall(
                key,
                richSettings,
                columnModifier,
                itemModifier,
                groupModifier,
                contentPadding,
                clockPreview
            )

            else -> NarrowAndShort(
                key,
                richSettings,
                columnModifier,
                itemModifier,
                groupModifier,
                contentPadding,
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
        contentPadding = contentPadding + VerticalBottomContentPadding,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val headerItemOffset = header()

        item { Spacer(Modifier.height(ColumnTopPadding)) }

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
                        Setting(item, itemModifier)
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
private fun resolveClockBackgroundColor(displayOptions: DisplayContext.Options): ComposeColor {
    return when (displayOptions) {
        is DisplayContext.Options.WithBackground -> displayOptions.backgroundColor
        else -> DisplayContextDefaults.DefaultBackgroundColor
    }.toCompose()
}


private fun Modifier.debugKeyEvents(onRestoreDefaultSettings: () -> Unit) = debugKeyEvent { event ->
    if (event.type == KeyEventType.KeyDown) {
        when (event.key) {
            Key.R -> {
                if (event.isCtrlPressed) {
                    onRestoreDefaultSettings()
                    return@debugKeyEvent true
                }
            }
        }
    }
    false
}
