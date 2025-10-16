package org.beatonma.gclocks.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
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
import kotlinx.serialization.StringFormat
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.beatonma.gclocks.app.data.settings.ContextClockOptions
import org.beatonma.gclocks.app.data.settings.DisplayContext
import org.beatonma.gclocks.app.ui.screens.AboutScreen
import org.beatonma.gclocks.app.ui.screens.FullSizeClock
import org.beatonma.gclocks.app.ui.screens.SettingsEditorViewModel
import org.beatonma.gclocks.clocks.whenOptions
import org.beatonma.gclocks.compose.LoadingSpinner
import org.beatonma.gclocks.compose.components.NavigationScaffold
import org.beatonma.gclocks.core.options.Options
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
    sealed interface ClockPreview<T : Options<*>> : NavigationTarget {
        val options: ContextClockOptions<T>
    }

    @Serializable
    data class FormClockPreview(override val options: ContextClockOptions<FormOptions>) : ClockPreview<FormOptions>

    @Serializable
    data class Io16ClockPreview(override val options: ContextClockOptions<Io16Options>) : ClockPreview<Io16Options>

    @Serializable
    data class Io18ClockPreview(override val options: ContextClockOptions<Io18Options>) : ClockPreview<Io18Options>
}

interface Pane : NavigationTarget {
    @Serializable
    object SettingsEditor : Pane

    @Serializable
    object About : Pane
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
    val onNavigateClockPreview: (ContextClockOptions<*>) -> Unit,
)


@Composable
fun AppNavigation(
    viewModel: SettingsEditorViewModel,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    settingsEditor: @Composable (AppNavigation) -> Unit,
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
                onNavigateClockPreview = dropUnlessResumed { preview -> fullscreenNavController.navigate(preview) },
                modifier = modifier,
                contentAlignment = contentAlignment,
            )
        }

        composable<FullScreen.FormClockPreview>(
            typeMap = mapOf(typing<ContextClockOptions<FormOptions>>())
        ) { backStackEntry ->
            val route = backStackEntry.toRoute<FullScreen.FormClockPreview>()
            FullSizeClock(route.options, onClose = { fullscreenNavController.popBackStack() })
        }
        composable<FullScreen.Io16ClockPreview>(
            typeMap = mapOf(typing<ContextClockOptions<Io16Options>>())
        ) { backStackEntry ->
            val route = backStackEntry.toRoute<FullScreen.Io16ClockPreview>()
            FullSizeClock(route.options, onClose = { fullscreenNavController.popBackStack() })
        }
        composable<FullScreen.Io18ClockPreview>(
            typeMap = mapOf(typing<ContextClockOptions<Io18Options>>())
        ) { backStackEntry ->
            val route = backStackEntry.toRoute<FullScreen.Io18ClockPreview>()
            FullSizeClock(route.options, onClose = { fullscreenNavController.popBackStack() })
        }
    }
}

@Composable
private fun NavigationUI(
    settingsEditor: @Composable (AppNavigation) -> Unit,
    displayContext: DisplayContext,
    setDisplayContext: (DisplayContext) -> Unit,
    onNavigateClockPreview: (FullScreen.ClockPreview<*>) -> Unit,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    startDestination: Pane = Pane.SettingsEditor,
) {
    // Navigation destinations for this controller fill only the content slot.of navigation UI.
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val route = navBackStackEntry?.destination?.route ?: Pane.SettingsEditor::class.qualifiedName

    val currentPane: NavigationMenuItem = when (route) {
        Pane.About::class.qualifiedName -> NavigationMenuItem.About
        Pane.SettingsEditor::class.qualifiedName -> NavigationMenuItem.entries.find { it.name == displayContext.name }
        else -> null
    } ?: throw IllegalStateException("Unhandled route $route")

    fun navigateTo(item: NavigationMenuItem) {
        when (item) {
            NavigationMenuItem.About -> navController.navigate(Pane.About) {
                launchSingleTop = true
            }

            else -> {
                DisplayContext.entries.find { it.name == item.name }?.let(setDisplayContext)
                navController.navigate(Pane.SettingsEditor) {
                    launchSingleTop = true
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
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
        onSelect = { navigateTo(it) },
        menu = menu,
    ) {
        NavHost(
            navController,
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

                settingsEditor(appNavigation)
            }

            composable<Pane.About> {
                AboutScreen()
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
    formatter: StringFormat = Json.Default
): NavType<T> {
    return object : NavType<T>(isNullableAllowed) {
        override fun put(bundle: SavedState, key: String, value: T) {
            bundle.write { putString(key, formatter.encodeToString(value)) }
        }

        override fun get(bundle: SavedState, key: String): T? {
            return formatter.decodeFromString(bundle.read { getString(key) })
        }

        override fun parseValue(value: String): T {
            return formatter.decodeFromString(value)
        }

        override fun serializeAsValue(value: T): String {
            return formatter.encodeToString(value)
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
