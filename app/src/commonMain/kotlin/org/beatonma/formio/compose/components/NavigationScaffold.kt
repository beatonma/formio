package org.beatonma.formio.compose.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.material3.WideNavigationRail
import androidx.compose.material3.WideNavigationRailItem
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldLayout
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.beatonma.formio.app.theme.tokens.NavigationTokens
import org.beatonma.formio.app.ui.NavigationMenu
import org.beatonma.formio.app.ui.NavigationMenuItem
import org.beatonma.formio.compose.isHeightAtLeastMedium
import org.beatonma.formio.compose.isWidthAtLeastExpanded
import org.beatonma.formio.compose.isWidthAtLeastMedium
import org.beatonma.formio.compose.onlyIf
import org.beatonma.formio.core.util.fastForEach
import org.jetbrains.compose.resources.stringResource

private typealias OnClickNavigationItem = (NavigationMenuItem) -> Unit
private typealias IsNavigationItemSelected = (NavigationMenuItem) -> Boolean

private val NavigationDrawerMaxWidth = NavigationTokens.Drawer.MaxWidth


private fun hasSecondaryNavigation(navigationType: NavigationSuiteType): Boolean =
    navigationType == NavigationSuiteType.NavigationBar

@Composable
fun NavigationScaffold(
    selected: NavigationMenuItem,
    onSelect: (NavigationMenuItem) -> Unit,
    menu: NavigationMenu,
    navigationType: NavigationSuiteType = getNavigationLayoutType(),
    scope: CoroutineScope = rememberCoroutineScope(),
    content: @Composable (navigationIcon: (@Composable () -> Unit)?) -> Unit,
) {
    /*
    * SecondaryNavigation wrapping PrimaryNavigation may be counterintuitive:
    * When primary navigation uses NavigationBar, any secondary navigation will
    * be placed in a modal drawer which, when visible, needs to fill the window
    * height and render above the bottom navigation bar.
    */
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val isSelected: IsNavigationItemSelected = { it == selected }

    SecondaryNavigation(menu, navigationType, selected, onSelect, drawerState, scope) {
        NavigationSuiteScaffoldLayout(
            navigationSuite = { PrimaryNavigation(menu, navigationType, isSelected, onSelect, scope) },
            layoutType = navigationType,
        ) {
            Box(
                Modifier.onlyIf(navigationType == NavigationSuiteType.NavigationBar) {
                    consumeWindowInsets(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
                }
            ) {
                content(
                    if (hasSecondaryNavigation(navigationType)) {
                        // Pass navigationIcon to child content so it can be displayed in a context-suitable way
                        {
                            HamburgerNavigationIcon(
                                { scope.launch { drawerState.open() } },
                                isOpen = drawerState.isOpen
                            )
                        }
                    } else null
                )
            }
        }
    }
}

@Composable
private fun PrimaryNavigation(
    menu: NavigationMenu,
    navigationType: NavigationSuiteType,
    isSelected: IsNavigationItemSelected,
    onClick: OnClickNavigationItem,
    scope: CoroutineScope,
) {
    when (navigationType) {
        NavigationSuiteType.NavigationBar, NavigationSuiteType.ShortNavigationBarMedium, NavigationSuiteType.ShortNavigationBarCompact -> {
            if (!menu.usesNavigationBar) return

            NavigationBar {
                BarItems(menu.primary, isSelected, onClick)
            }
        }

        NavigationSuiteType.NavigationDrawer -> PrimaryNavigationDrawer(menu, isSelected, onClick)

        else -> PrimaryNavigationWideRail(menu, navigationType, isSelected, onClick, scope)
    }
}

@Composable
private fun PrimaryNavigationWideRail(
    menu: NavigationMenu,
    navigationType: NavigationSuiteType,
    isSelected: IsNavigationItemSelected,
    onClick: OnClickNavigationItem,
    scope: CoroutineScope,
) {
    val railState = rememberWideNavigationRailState(
        when (navigationType) {
            NavigationSuiteType.WideNavigationRailExpanded, NavigationSuiteType.NavigationDrawer -> WideNavigationRailValue.Expanded
            else -> WideNavigationRailValue.Collapsed
        }
    )
    val isRailExpanded by remember {
        derivedStateOf {
            railState.currentValue == WideNavigationRailValue.Expanded || railState.targetValue == WideNavigationRailValue.Expanded
        }
    }

    LaunchedEffect(navigationType) {
        when (navigationType) {
            NavigationSuiteType.WideNavigationRailExpanded, NavigationSuiteType.NavigationDrawer -> scope.launch { railState.expand() }
            else -> scope.launch { railState.collapse() }
        }
    }

    WideNavigationRail(
        state = railState,
        header = {
            Box(Modifier.padding(start = NavigationTokens.WideRail.HeaderIconButtonStartPadding)) {
                HamburgerNavigationIcon(
                    { scope.launch { railState.toggle() } },
                    isOpen = isRailExpanded,
                )
            }
        },
    ) {
        WideRailItems(isRailExpanded, menu.primary, isSelected, onClick)

        if (menu.secondary.isNotEmpty()) {
            WideRailItems(isRailExpanded, menu.secondary, isSelected, onClick)
        }
    }
}


@Composable
private fun PrimaryNavigationDrawer(
    menu: NavigationMenu,
    isSelected: IsNavigationItemSelected,
    onClick: OnClickNavigationItem,
) {
    PermanentDrawerSheet(Modifier.widthIn(max = NavigationDrawerMaxWidth)) {
        DrawerItems(menu.primary, isSelected, onClick)

        if (menu.secondary.isNotEmpty()) {
            Separator()
            DrawerItems(menu.secondary, isSelected, onClick)
        }
    }
}


/*
 * When window size prefers use of NavigationSuiteType.NavigationBar, only
 * the primary navigation targets are displayed there. Secondary targets are
 * instead made available in a modal navigation drawer.
 *
 * The primary NavigationBar will not be shown at all if there is only one
 * primary navigation target. In that case, the primary target should also be
 * included in the modal navigation drawer.
 */
@Composable
private fun SecondaryNavigation(
    menu: NavigationMenu,
    navigationType: NavigationSuiteType,
    selected: NavigationMenuItem,
    onSelect: (NavigationMenuItem) -> Unit,
    state: DrawerState,
    scope: CoroutineScope = rememberCoroutineScope(),
    content: @Composable () -> Unit,
) {
    if (!hasSecondaryNavigation(navigationType)) {
        // Secondary navigation only needed if primary navigation uses NavigationBar.
        return content()
    }

    val isOpen = state.isOpen || state.targetValue == DrawerValue.Open
    val close: () -> Unit = { scope.launch { state.close() } }

    val isItemSelected: (NavigationMenuItem) -> Boolean = { it == selected }
    val onClickItem: (NavigationMenuItem) -> Unit = {
        onSelect(it)
        close()
    }

    ModalNavigationDrawer(
        drawerState = state,
        drawerContent = {
            ModalDrawerSheet(
                state,
                Modifier.widthIn(max = NavigationDrawerMaxWidth),
            ) {
                NavigationIconContainer {
                    HamburgerNavigationIcon(close, isOpen = isOpen)
                }

                if (!menu.usesNavigationBar) {
                    DrawerItems(menu.primary, isItemSelected, onClickItem)
                }

                if (menu.secondary.isNotEmpty()) {
                    Separator()
                    DrawerItems(menu.secondary, isItemSelected, onClickItem)
                }
            }
        }
    ) {
        content()
    }
}

@Composable
private fun RowScope.BarItems(
    items: List<NavigationMenuItem>,
    isSelected: (NavigationMenuItem) -> Boolean,
    onClick: (NavigationMenuItem) -> Unit,
) {
    items.fastForEach { item ->
        NavigationBarItem(
            label = { Text(stringResource(item.label)) },
            selected = isSelected(item),
            onClick = { onClick(item) },
            icon = { Icon(item.icon, stringResource(item.contentDescription)) },
        )
    }
}

@Composable
private fun WideRailItems(
    isRailExpanded: Boolean,
    items: List<NavigationMenuItem>,
    isSelected: (NavigationMenuItem) -> Boolean,
    onClick: (NavigationMenuItem) -> Unit,
) {
    items.fastForEach { item ->
        WideNavigationRailItem(
            selected = isSelected(item),
            onClick = { onClick(item) },
            icon = { Icon(item.icon, stringResource(item.contentDescription)) },
            label = { Text(stringResource(item.label)) },
            railExpanded = isRailExpanded,
        )
    }
}

@Composable
private fun DrawerItems(
    items: List<NavigationMenuItem>,
    isSelected: (NavigationMenuItem) -> Boolean,
    onClick: (NavigationMenuItem) -> Unit,
) {
    items.fastForEach { item ->
        NavigationDrawerItem(
            label = { Text(stringResource(item.label)) },
            selected = isSelected(item),
            onClick = { onClick(item) },
            icon = { Icon(item.icon, stringResource(item.contentDescription)) },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }
}

@Composable
private fun ColumnScope.Separator() {
    Spacer(Modifier.weight(1f))
    HorizontalDivider(Modifier.padding(vertical = NavigationTokens.Drawer.SeparatorVerticalPadding))
}


@Composable
private fun getNavigationLayoutType(): NavigationSuiteType {
    val windowInfo = currentWindowAdaptiveInfo()

    with(windowInfo) {
        return if (windowSizeClass.isHeightAtLeastMedium()) {
            when {
                windowSizeClass.isWidthAtLeastExpanded() -> NavigationSuiteType.WideNavigationRailExpanded
                windowSizeClass.isWidthAtLeastMedium() -> NavigationSuiteType.WideNavigationRailCollapsed
                else -> NavigationSuiteType.NavigationBar
            }
        } else {
            when {
                windowSizeClass.isWidthAtLeastExpanded() -> NavigationSuiteType.NavigationDrawer
                else -> NavigationSuiteType.NavigationBar
            }
        }
    }
}

private val NavigationMenu.usesNavigationBar: Boolean get() = primary.size > 1
