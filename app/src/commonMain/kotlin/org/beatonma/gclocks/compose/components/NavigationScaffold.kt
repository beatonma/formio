package org.beatonma.gclocks.compose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldLayout
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import gclocks_multiplatform.app.generated.resources.Res
import gclocks_multiplatform.app.generated.resources.navigation_cd_modal_close
import gclocks_multiplatform.app.generated.resources.navigation_cd_modal_open
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.beatonma.gclocks.app.theme.DesignSpec
import org.beatonma.gclocks.app.ui.NavigationMenu
import org.beatonma.gclocks.app.ui.NavigationMenuItem
import org.beatonma.gclocks.compose.AppIcon
import org.beatonma.gclocks.compose.isHeightAtLeastMedium
import org.beatonma.gclocks.compose.isWidthAtLeastExpanded
import org.beatonma.gclocks.compose.isWidthAtLeastMedium
import org.jetbrains.compose.resources.stringResource

private val NavigationDrawerMaxWidth = 240.dp
private val HamburgerPadding = DesignSpec.HamburgerPadding
private val PermanentDrawerInset = HamburgerPadding / 2
private val DrawerContentPadding = PaddingValues(HamburgerPadding / 2)
private val DrawerItemSpacing = 4.dp
private val RailContentPadding = PaddingValues(horizontal = 8.dp, vertical = HamburgerPadding)
private val RailItemSpacing = 4.dp


private fun hasSecondaryNavigation(navigationType: NavigationSuiteType): Boolean =
    navigationType == NavigationSuiteType.NavigationBar

@Composable
fun NavigationScaffold(
    selected: NavigationMenuItem,
    onSelect: (NavigationMenuItem) -> Unit,
    menu: NavigationMenu,
    navigationType: NavigationSuiteType = getNavigationLayoutType(),
    scope: CoroutineScope = rememberCoroutineScope(),
    content: @Composable (navigationIcon: @Composable () -> Unit) -> Unit,
) {
    /*
    * SecondaryNavigation wrapping PrimaryNavigation may be counterintuitive:
    * When primary navigation uses NavigationBar, any secondary navigation will
    * be placed in a modal drawer which, when visible, needs to fill the window
    * height and render above the bottom navigation bar.
    */
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    SecondaryNavigation(menu, navigationType, selected, onSelect, drawerState, scope = scope) {
        NavigationSuiteScaffoldLayout(
            navigationSuite = { PrimaryNavigation(menu, navigationType, selected, onSelect) },
            layoutType = navigationType,
        ) {
            Box {
                content {
                    // Pass navigationIcon to child content so it can be displayed in a context-suitable way
                    if (hasSecondaryNavigation(navigationType)) {
                        Hamburger(isOpen = false, onClick = { scope.launch { drawerState.open() } })
                    }
                }
            }
        }
    }
}

@Composable
private fun PrimaryNavigation(
    menu: NavigationMenu,
    navigationType: NavigationSuiteType = getNavigationLayoutType(),
    selected: NavigationMenuItem,
    onSelect: (NavigationMenuItem) -> Unit,
) {
    when (navigationType) {
        NavigationSuiteType.NavigationBar -> {
            if (!menu.usesNavigationBar) return

            NavigationBar {
                menu.primary.forEach { menuItem ->
                    BarItem(menuItem, menuItem == selected, onSelect)
                }
            }
        }

        NavigationSuiteType.NavigationRail -> {
            NavigationRail(Modifier.width(IntrinsicSize.Max)) {
                NavigationColumn(RailContentPadding, RailItemSpacing) {
                    menu.primary.forEach { menuItem ->
                        RailItem(menuItem, menuItem == selected, onSelect)
                    }

                    if (menu.secondary.isNotEmpty()) {
                        Separator()

                        menu.secondary.forEach { menuItem ->
                            RailItem(menuItem, menuItem == selected, onSelect)
                        }
                    }
                }
            }
        }

        NavigationSuiteType.NavigationDrawer -> {
            PermanentDrawerSheet(
                Modifier.widthIn(max = NavigationDrawerMaxWidth).padding(PermanentDrawerInset),
                drawerShape = shapes.large,
            ) {
                NavigationColumn(DrawerContentPadding, DrawerItemSpacing) {
                    menu.primary.forEach { menuItem ->
                        DrawerItem(menuItem, menuItem == selected, onSelect)
                    }

                    if (menu.secondary.isNotEmpty()) {
                        Separator()

                        menu.secondary.forEach { menuItem ->
                            DrawerItem(menuItem, menuItem == selected, onSelect)
                        }
                    }
                }
            }
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
    drawerState: DrawerState,
    scope: CoroutineScope = rememberCoroutineScope(),
    content: @Composable () -> Unit,
) {
    if (!hasSecondaryNavigation(navigationType)) {
        // Secondary navigation only needed if primary navigation uses NavigationBar.
        return content()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerState, Modifier.widthIn(max = NavigationDrawerMaxWidth)) {
                NavigationColumn(DrawerContentPadding, DrawerItemSpacing) {
                    Hamburger(
                        isOpen = true,
                        onClick = { scope.launch { drawerState.close() } },
                        modifier = Modifier.padding(4.dp)
                    )
                    Spacer(Modifier.height(0.dp))

                    if (!menu.usesNavigationBar) {
                        menu.primary.forEach { menuItem ->
                            DrawerItem(menuItem, menuItem == selected) {
                                onSelect(it)
                                scope.launch { drawerState.close() }
                            }
                        }
                    }
                    Separator()

                    menu.secondary.forEach { menuItem ->
                        DrawerItem(menuItem, menuItem == selected) {
                            onSelect(it)
                            scope.launch { drawerState.close() }
                        }
                    }
                }
            }
        }
    ) {
        content()
    }
}


@Composable
private fun getNavigationLayoutType(): NavigationSuiteType {
    return with(currentWindowAdaptiveInfo()) {
        if (windowSizeClass.isHeightAtLeastMedium()) {
            when {
                windowSizeClass.isWidthAtLeastExpanded() -> NavigationSuiteType.NavigationDrawer
                windowSizeClass.isWidthAtLeastMedium() -> NavigationSuiteType.NavigationRail
                else -> NavigationSuiteType.NavigationBar
            }
        } else {
            when {
                windowPosture.isTabletop -> NavigationSuiteType.NavigationBar
                else -> NavigationSuiteType.NavigationBar
            }
        }
    }
}

@Composable
private fun RowScope.BarItem(
    item: NavigationMenuItem,
    isSelected: Boolean,
    onSelect: (NavigationMenuItem) -> Unit
) {
    NavigationBarItem(
        label = { Text(stringResource(item.label)) },
        selected = isSelected,
        onClick = { onSelect(item) },
        icon = { Icon(item.icon, stringResource(item.contentDescription)) },
    )
}

@Composable
private fun ColumnScope.RailItem(
    item: NavigationMenuItem,
    isSelected: Boolean,
    onSelect: (NavigationMenuItem) -> Unit
) {
    RailItem(
        label = stringResource(item.label),
        isSelected = isSelected,
        onClick = { onSelect(item) },
        icon = { Icon(item.icon, stringResource(item.contentDescription)) },
    )
}

@Composable
private fun ColumnScope.RailItem(
    label: String?,
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    NavigationRailItem(
        label = { label?.let { Text(it) } },
        selected = isSelected,
        onClick = onClick,
        icon = icon,
    )
}

@Composable
private fun ColumnScope.DrawerItem(
    item: NavigationMenuItem,
    isSelected: Boolean,
    onSelect: (NavigationMenuItem) -> Unit
) {
    DrawerItem(
        label = stringResource(item.label),
        isSelected = isSelected,
        onClick = { onSelect(item) },
        icon = { Icon(item.icon, stringResource(item.contentDescription)) },
    )
}

@Composable
private fun ColumnScope.DrawerItem(
    label: String?,
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    NavigationDrawerItem(
        label = { label?.let { Text(it) } },
        selected = isSelected,
        onClick = onClick,
        icon = icon,
    )
}

@Composable
private fun ColumnScope.Separator() {
    Spacer(Modifier.weight(1f))
    HorizontalDivider(Modifier.padding(vertical = 8.dp))
}

@Composable
private fun NavigationColumn(
    contentPadding: PaddingValues,
    itemSpacing: Dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        Modifier.fillMaxHeight().padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(itemSpacing),
        content = content,
    )
}

private val NavigationMenu.usesNavigationBar: Boolean get() = primary.size > 1


@Composable
private fun Hamburger(
    isOpen: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier
    ) {
        if (isOpen) {
            Icon(
                AppIcon.HamburgerClose,
                stringResource(Res.string.navigation_cd_modal_close)
            )
        } else {
            Icon(
                AppIcon.Hamburger,
                stringResource(Res.string.navigation_cd_modal_open)
            )
        }
    }
}
