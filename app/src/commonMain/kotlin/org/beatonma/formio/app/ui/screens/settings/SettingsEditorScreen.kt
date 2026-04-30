package org.beatonma.formio.app.ui.screens.settings

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme.shapes
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import formio.app.generated.resources.Res
import formio.app.generated.resources.cd_fullscreen_open
import formio.app.generated.resources.setting_save_changes_fab
import org.beatonma.formio.app.data.settings.AnyContextClockOptions
import org.beatonma.formio.app.data.settings.DisplayContext
import org.beatonma.formio.app.data.settings.DisplayContextDefaults
import org.beatonma.formio.app.data.settings.RichSetting
import org.beatonma.formio.app.data.settings.RichSettings
import org.beatonma.formio.app.data.settings.RichSettingsGroup
import org.beatonma.formio.app.data.settings.Setting
import org.beatonma.formio.app.ui.AppNavigation
import org.beatonma.formio.app.ui.animation.AnimatedFade
import org.beatonma.formio.app.ui.animation.fadeIn
import org.beatonma.formio.app.ui.components.AppIcon
import org.beatonma.formio.app.ui.components.Clock
import org.beatonma.formio.app.ui.components.Column
import org.beatonma.formio.app.ui.components.IconToolbar
import org.beatonma.formio.app.ui.components.LoadingSpinner
import org.beatonma.formio.app.ui.components.NavigationIconContainer
import org.beatonma.formio.app.ui.components.toCompose
import org.beatonma.formio.app.ui.screens.settings.components.Setting
import org.beatonma.formio.app.ui.screens.settings.components.SettingTokens
import org.beatonma.formio.app.ui.theme.rememberContentColor
import org.beatonma.formio.app.ui.theme.tokens.ColumnTokens
import org.beatonma.formio.app.ui.theme.tokens.NavigationTokens
import org.beatonma.formio.app.ui.theme.tokens.RowTokens
import org.beatonma.formio.app.ui.theme.tokens.WindowTokens
import org.beatonma.formio.app.ui.util.VerticalBottomContentPadding
import org.beatonma.formio.app.ui.util.copy
import org.beatonma.formio.app.ui.util.onlyIf
import org.beatonma.formio.app.ui.util.plus
import org.beatonma.formio.core.options.AnyOptions
import org.beatonma.formio.core.util.fastForEachIndexed
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.graphics.Color as ComposeColor

private val MaxClockWidth = 400.dp
private val MaxClockHeight = 200.dp
private val ClockPadding = PaddingValues(
    horizontal = WindowTokens.ContentPadding * 2,
    vertical = WindowTokens.ContentPadding
)

private val ColumnPreferredWidth = ColumnTokens.PreferredMinWidth
private val ColumnMaxWidth = ColumnTokens.PreferredMaxWidth
private val SpaceBetweenColumns = RowTokens.LargeSpacing
private val ColumnContentPadding = SettingTokens.SettingsContainerContentPadding

/**
 * Data needed to render a preview of a clock with current settings.
 */
@Immutable
data class ClockPreview(
    val options: AnyOptions,
    val background: ComposeColor,
)

val LocalClockPreview: ProvidableCompositionLocal<ClockPreview?> = compositionLocalOf { null }


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

    val settings = _settings ?: return
    val onClickPreview: () -> Unit =
        remember(navigation.onNavigateClockPreview, settings.contextOptions) {
            { navigation.onNavigateClockPreview(settings.contextOptions) }
        }

    val richSettings = _richSettings ?: return

    ClockSettingsScaffold(
        key = "${settings.state.displayContext}_${settings.contextSettings.clock}",
        options = settings.contextOptions,
        richSettings = richSettings,
        hasUnsavedChanges = hasUnsavedChanges,
        snackbarHostState = snackbarHostState,
        onClickPreview = { onClickPreview() },
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
        },
        onSave = viewModel::save,
    )
}


@Composable
private fun ClockSettingsScaffold(
    modifier: Modifier = Modifier,
    key: Any,
    options: AnyContextClockOptions?,
    richSettings: RichSettings?,
    hasUnsavedChanges: Boolean,
    snackbarHostState: SnackbarHostState?,
    onClickPreview: ((DisplayContext) -> Unit)?,
    navigationIcon: (@Composable () -> Unit)?,
    toolbar: (@Composable RowScope.() -> Unit)?,
    onSave: () -> Unit,
) {
    val backgroundColor = rememberClockBackgroundColor(options?.displayOptions)
    val foregroundColor = rememberContentColor(backgroundColor)

    Scaffold(
        modifier,
        snackbarHost = { snackbarHostState?.let { SnackbarHost(it) } },
        floatingActionButton = {
            AnimatedFade(hasUnsavedChanges) {
                ExtendedFloatingActionButton(onSave) {
                    Text(stringResource(Res.string.setting_save_changes_fab))
                }
            }
        },
    ) { contentPadding ->
        if (options == null || richSettings == null) return@Scaffold LoadingSpinner(Modifier.fillMaxSize())

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
                Modifier.fillMaxWidth().fadeIn(),
                clockPreview = { backgroundModifier, clockModifier ->
                    CompositionLocalProvider(LocalContentColor provides foregroundColor) {
                        ClockPreview(
                            options.clockOptions,
                            navigationIcon?.let {
                                {
                                    NavigationIconContainer(foregroundColor) {
                                        navigationIcon()
                                    }
                                }
                            },
                            toolbar,
                            backgroundModifier
                                .background(backgroundColor)
                                .onlyIf(onClickPreview) { onClick ->
                                    clickable(onClick = { onClick(options.displayContext) })
                                }
                                .animateContentSize(),
                            clockModifier = clockModifier,
                        )
                    }
                }
            )
        }
    }
}


@Composable
private fun ClockPreview(
    options: AnyOptions,
    navigationIcon: @Composable (() -> Unit)?,
    toolbar: @Composable (RowScope.() -> Unit)?,
    modifier: Modifier,
    clockModifier: Modifier,
) {
    Column(modifier) {
        Column(Modifier.windowInsetsPadding(NavigationTokens.Drawer.windowInsets), Column.MediumSpacingArrangement) {
            navigationIcon?.invoke()
            Clock(
                options,
                clockModifier
                    .padding(ClockPadding)
                    .sizeIn(maxWidth = MaxClockWidth, maxHeight = MaxClockHeight)
                    .align(Alignment.CenterHorizontally),
                allowVariance = true
            )
            toolbar?.let { toolbar ->
                IconToolbar(Modifier.align(Alignment.End), content = toolbar)
            }
        }
    }
}


@Composable
private fun WideAndTall(
    key: Any,
    richSettings: RichSettings,
    columnModifier: Modifier,
    contentPadding: PaddingValues,
    clockPreview: (@Composable (backgroundModifier: Modifier, clockModifier: Modifier) -> Unit),
) {
    val (left, right) = richSettings.groups(2)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Column.LargeSpacingArrangement
    ) {
        clockPreview(
            Modifier.padding(WindowTokens.ContentPadding).clip(shapes.medium),
            Modifier.padding(contentPadding.copy(bottom = 0.dp))
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(
                SpaceBetweenColumns,
                Alignment.CenterHorizontally
            )
        ) {
            val columnModifier = columnModifier.weight(1f, fill = false)
            ClockSettingsColumn(
                key,
                left,
                contentPadding.copy(end = 0.dp),
                modifier = columnModifier,
            )

            ClockSettingsColumn(
                key,
                right,
                contentPadding.copy(start = 0.dp),
                modifier = columnModifier,
            )
        }
    }
}

@Composable
private fun NarrowAndTall(
    key: Any,
    richSettings: RichSettings,
    columnModifier: Modifier,
    contentPadding: PaddingValues,
    clockPreview: (@Composable (backgroundModifier: Modifier, clockModifier: Modifier) -> Unit),
) {
    val (settings) = richSettings.groups(1)

    ClockSettingsColumn(
        key,
        settings,
        contentPadding.copy(top = 0.dp) + VerticalBottomContentPadding,
        modifier = columnModifier,
    ) {
        stickyHeader {
            clockPreview(
                Modifier,
                Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun WideAndShort(
    key: Any,
    richSettings: RichSettings,
    columnModifier: Modifier,
    contentPadding: PaddingValues,
    clockPreview: (@Composable (backgroundModifier: Modifier, clockModifier: Modifier) -> Unit),
) {
    val (settings) = richSettings.groups(1)

    Row(Modifier.fillMaxWidth()) {
        clockPreview(
            columnModifier.weight(1f).fillMaxHeight(),
            Modifier.weight(1f)
        )

        ClockSettingsColumn(
            key,
            settings,
            contentPadding.copy(start = 0.dp) + VerticalBottomContentPadding,
            modifier = columnModifier,
        )
    }
}

@Composable
private fun NarrowAndShort(
    key: Any,
    richSettings: RichSettings,
    columnModifier: Modifier,
    contentPadding: PaddingValues,
    clockPreview: (@Composable (backgroundModifier: Modifier, clockModifier: Modifier) -> Unit),
) {
    val (settings) = richSettings.groups(1)

    ClockSettingsColumn(
        key,
        settings,
        contentPadding.copy(top = 0.dp) + VerticalBottomContentPadding,
        modifier = columnModifier,
    ) {
        item {
            // On very small display, allow preview to scroll offscreen.
            clockPreview(
                Modifier,
                Modifier.padding(contentPadding.copy(bottom = 0.dp))
            )
        }
    }
}


@Composable
private fun SettingsUi(
    key: Any,
    richSettings: RichSettings,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    clockPreview: (@Composable (backgroundModifier: Modifier, clockModifier: Modifier) -> Unit),
) {
    val columnModifier = Modifier

    BoxWithConstraints(modifier, contentAlignment = Alignment.TopCenter) {
        val isWide: Boolean
        val isTall: Boolean

        with(LocalDensity.current) {
            isWide = constraints.maxWidth.toDp() > ((ColumnPreferredWidth * 2) + SpaceBetweenColumns)
            isTall = constraints.maxHeight.toDp() > WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND.dp
        }

        when {
            isWide && isTall -> WideAndTall(key, richSettings, columnModifier, contentPadding, clockPreview)
            isWide -> WideAndShort(key, richSettings, columnModifier, contentPadding, clockPreview)
            isTall -> NarrowAndTall(key, richSettings, columnModifier, contentPadding, clockPreview)
            else -> NarrowAndShort(key, richSettings, columnModifier, contentPadding, clockPreview)
        }
    }
}

@Composable
private fun ClockSettingsColumn(
    key: Any,
    settings: List<Setting>,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    header: (LazyListScope.() -> Unit)? = null,
) {
    val state = rememberLazyListState()

    val groupModifier = Modifier
    val itemModifier = Modifier.padding(horizontal = ColumnContentPadding)

    LaunchedEffect(key) {
        state.scrollToItem(0)
    }

    LazyColumn(
        modifier.widthIn(max = ColumnMaxWidth),
        state = state,
        contentPadding = contentPadding,
        verticalArrangement = Column.SmallSpacingArrangement,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        header?.invoke(this)?.also {
            item {
                Spacer(Modifier.height(ColumnContentPadding))
            }
        }

        fun addItems(_settings: List<Setting>) {
            _settings.fastForEachIndexed { index, item ->
                when (item) {
                    is RichSettingsGroup -> {
                        addItems(item.settings)
                    }

                    is RichSetting -> item(
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

        addItems(settings)

        if (header != null) {
            item {
                Spacer(Modifier.height(ColumnContentPadding))
            }
        }
    }
}


@Composable
private fun GroupSeparator(modifier: Modifier, content: @Composable BoxScope.() -> Unit = {}) {
    Box(modifier, contentAlignment = Alignment.Center, content = content)
}

@Composable
private fun rememberClockBackgroundColor(displayOptions: DisplayContext.Options?): ComposeColor {
    return remember(displayOptions) {
        when (displayOptions) {
            is DisplayContext.Options.WithBackground -> displayOptions.backgroundColor
            else -> DisplayContextDefaults.DefaultBackgroundColor
        }.toCompose()
    }
}