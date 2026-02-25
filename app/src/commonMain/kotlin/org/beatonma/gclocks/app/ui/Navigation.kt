package org.beatonma.gclocks.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.savedstate.SavedState
import androidx.savedstate.read
import androidx.savedstate.write
import kotlinx.serialization.Serializable
import org.beatonma.gclocks.app.data.deserialize
import org.beatonma.gclocks.app.data.serialize
import org.beatonma.gclocks.app.data.settings.AnyContextClockOptions
import org.beatonma.gclocks.app.data.settings.ContextClockOptions
import org.beatonma.gclocks.app.data.settings.DisplayContext
import org.beatonma.gclocks.app.data.settings.FormContextClockOptions
import org.beatonma.gclocks.app.data.settings.Io16ContextClockOptions
import org.beatonma.gclocks.app.data.settings.Io18ContextClockOptions
import org.beatonma.gclocks.app.ui.screens.AboutScreen
import org.beatonma.gclocks.app.ui.screens.FullSizeClock
import org.beatonma.gclocks.app.ui.screens.SettingsEditorViewModel
import org.beatonma.gclocks.clocks.whenOptions
import org.beatonma.gclocks.compose.LoadingSpinner
import org.beatonma.gclocks.compose.components.NavigationScaffold
import org.beatonma.gclocks.core.options.AnyOptions
import org.beatonma.gclocks.form.FormOptions
import org.beatonma.gclocks.io16.Io16Options
import org.beatonma.gclocks.io18.Io18Options
import org.jetbrains.compose.resources.StringResource
import kotlin.reflect.KType
import kotlin.reflect.typeOf

@Serializable
sealed interface NavigationTarget

@Serializable
sealed interface FullScreen : NavigationTarget {
    @Serializable
    object AppMain : FullScreen

    @Serializable
    sealed interface ClockPreview<T : AnyOptions> : NavigationTarget {
        val options: ContextClockOptions<T>
    }

    @Serializable
    data class FormClockPreview(override val options: FormContextClockOptions) :
        ClockPreview<FormOptions>

    @Serializable
    data class Io16ClockPreview(override val options: Io16ContextClockOptions) :
        ClockPreview<Io16Options>

    @Serializable
    data class Io18ClockPreview(override val options: Io18ContextClockOptions) :
        ClockPreview<Io18Options>
}

interface Pane : NavigationTarget {
    val route: String

    @Serializable
    object SettingsEditor : Pane {
        override val route: String = "SettingsEditor"
    }

    @Serializable
    object About : Pane {
        override val route: String = "About"
    }
}


enum class NavigationMenuItemType {
    DisplayContext,
    Overflow,
    ;
}

@Serializable
expect enum class NavigationMenuItem {
    About,
    ;

    val label: StringResource
    val contentDescription: StringResource
    val icon: ImageVector
    val type: NavigationMenuItemType
}

@Serializable
@Immutable
data class NavigationMenu(
    val primary: List<NavigationMenuItem>,
    val secondary: List<NavigationMenuItem>,
)


@Immutable
data class AppNavigation(
    val onNavigateAbout: () -> Unit,
    val onNavigateClockPreview: (AnyContextClockOptions) -> Unit,
)


typealias SettingsEditorUI = @Composable (navigation: AppNavigation, navigationIcon: @Composable () -> Unit) -> Unit

@Composable
fun AppNavigation(
    viewModel: SettingsEditorViewModel,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    settingsEditor: SettingsEditorUI,
) {
    // Navigation destinations for this controller fill the entire window.
    val fullscreenNavController = rememberNavController()

    NavHost(
        fullscreenNavController,
        startDestination = FullScreen.AppMain,
        modifier = modifier,
        contentAlignment = contentAlignment,
    ) {
        composable<FullScreen.AppMain> {
            val displayContext by viewModel.displayContext.collectAsStateWithLifecycle(null)
            NavigationUI(
                settingsEditor,
                displayContext ?: return@composable LoadingSpinner(),
                viewModel::setDisplayContext,
                onNavigateClockPreview = dropUnlessResumed { preview ->
                    fullscreenNavController.navigate(
                        preview
                    )
                },
                modifier = modifier,
                contentAlignment = contentAlignment,
            )
        }

        composable<FullScreen.FormClockPreview>(
            typeMap = mapOf(typing<FormContextClockOptions>())
        ) { backStackEntry ->
            val route = backStackEntry.toRoute<FullScreen.FormClockPreview>()
            FullSizeClock(route.options, onClose = { fullscreenNavController.popBackStack() })
        }
        composable<FullScreen.Io16ClockPreview>(
            typeMap = mapOf(typing<Io16ContextClockOptions>())
        ) { backStackEntry ->
            val route = backStackEntry.toRoute<FullScreen.Io16ClockPreview>()
            FullSizeClock(route.options, onClose = { fullscreenNavController.popBackStack() })
        }
        composable<FullScreen.Io18ClockPreview>(
            typeMap = mapOf(typing<Io18ContextClockOptions>())
        ) { backStackEntry ->
            val route = backStackEntry.toRoute<FullScreen.Io18ClockPreview>()
            FullSizeClock(route.options, onClose = { fullscreenNavController.popBackStack() })
        }
    }
}


@Composable
private fun NavigationUI(
    settingsEditor: SettingsEditorUI,
    displayContext: DisplayContext,
    setDisplayContext: (DisplayContext) -> Unit,
    onNavigateClockPreview: (FullScreen.ClockPreview<*>) -> Unit,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    startDestination: Pane = Pane.SettingsEditor,
) {
    // Navigation destinations for this controller fill only the content slot of navigation UI.
    val panelNavController = rememberNavController()
    val navBackStackEntry by panelNavController.currentBackStackEntryAsState()

    val currentPane: NavigationMenuItem by derivedStateOf {
        val route = navBackStackEntry?.destination?.route ?: Pane.SettingsEditor.route
        when {
            route.endsWith(Pane.About.route) -> NavigationMenuItem.About
            route.endsWith(Pane.SettingsEditor.route) -> NavigationMenuItem.entries.find { it.name == displayContext.name }
            else -> null
        } ?: throw IllegalStateException("Unhandled route $route")
    }

    fun navigateTo(item: NavigationMenuItem) {
        when (item) {
            NavigationMenuItem.About -> panelNavController.navigate(Pane.About) {
                launchSingleTop = true
            }

            else -> {
                DisplayContext.entries.find { it.name == item.name }?.let(setDisplayContext)
                panelNavController.navigate(Pane.SettingsEditor) {
                    launchSingleTop = true
                    popUpTo(panelNavController.graph.findStartDestination().id)
                }
            }
        }
    }

    val menu = remember {
        NavigationMenu(
            NavigationMenuItem.entries.filter { it.type == NavigationMenuItemType.DisplayContext },
            NavigationMenuItem.entries.filter { it.type == NavigationMenuItemType.Overflow }
        )
    }

    NavigationScaffold(
        selected = currentPane,
        onSelect = ::navigateTo,
        menu = menu,
    ) { navigationIcon ->
        NavHost(
            panelNavController,
            startDestination = startDestination,
            modifier = modifier,
            contentAlignment = contentAlignment,
        ) {
            composable<Pane.SettingsEditor> {
                val appNavigation = AppNavigation(
                    onNavigateAbout = dropUnlessResumed { navigateTo(NavigationMenuItem.About) },
                    onNavigateClockPreview = { contextClockOptions ->
                        val preview = whenOptions(
                            contextClockOptions,
                            form = FullScreen::FormClockPreview,
                            io16 = FullScreen::Io16ClockPreview,
                            io18 = FullScreen::Io18ClockPreview,
                        )
                        onNavigateClockPreview(preview)
                    },
                )

                settingsEditor(appNavigation, navigationIcon)
            }

            composable<Pane.About> {
                AboutScreen(navigationIcon)
            }
        }
    }
}

/**
 * Enums work fine on Android, but not on desktop (and perhaps other platforms?).
 * Not clear if this is intentional so we may be able to remove this in the
 * future, but for now it is required to make enum navigation targets work.
 */
private inline fun <reified T> serializable(
    isNullableAllowed: Boolean = false,
): NavType<T> {
    return object : NavType<T>(isNullableAllowed) {
        override fun put(bundle: SavedState, key: String, value: T) {
            bundle.write { putString(key, serialize(value)) }
        }

        override fun get(bundle: SavedState, key: String): T? {
            return deserialize(bundle.read { getString(key) })
        }

        override fun parseValue(value: String): T {
            return deserialize(value)
        }

        override fun serializeAsValue(value: T): String {
            return serialize(value)
        }
    }
}

private inline fun <reified T : Any> NavGraphBuilder.typing(): Pair<KType, NavType<T>> =
    typeOf<T>() to serializable<T>()

/** Typed version of [androidx.lifecycle.compose.dropUnlessResumed]. */
@Composable
private fun <T> dropUnlessResumed(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    block: (T) -> Unit,
): ((T) -> Unit) {
    return { value ->
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(State.RESUMED)) {
            block(value)
        }
    }
}
