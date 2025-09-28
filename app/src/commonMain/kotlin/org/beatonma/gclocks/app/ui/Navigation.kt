package org.beatonma.gclocks.app.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.savedstate.SavedState
import androidx.savedstate.read
import androidx.savedstate.write
import kotlinx.serialization.Serializable
import kotlinx.serialization.StringFormat
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.beatonma.gclocks.app.AppViewModel
import org.beatonma.gclocks.app.ui.screens.AboutScreen
import org.beatonma.gclocks.app.ui.screens.FullSizeClock
import org.beatonma.gclocks.compose.LoadingSpinner
import kotlin.reflect.KType
import kotlin.reflect.typeOf

@Serializable
sealed interface NavigationTarget

@Serializable
object SettingsEditor : NavigationTarget

@Serializable
object About : NavigationTarget

@Serializable
object ClockPreview : NavigationTarget

data class AppNavigation(
    val onNavigateAbout: () -> Unit,
    val onNavigateClockPreview: () -> Unit,
)


@Composable
fun AppNavigation(
    viewModel: AppViewModel,
    settingsEditor: @Composable (AppNavigation) -> Unit,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    navController: NavHostController = rememberNavController(),
    startDestination: NavigationTarget = SettingsEditor
) {
    NavHost(
        navController,
        startDestination = startDestination,
        modifier = modifier,
        contentAlignment = contentAlignment,
    ) {
        composable<SettingsEditor> {
            settingsEditor(
                AppNavigation(
                    { navController.navigate(About) },
                    { navController.navigate(ClockPreview) }
                )
            )
        }

        composable<About> {
            AboutScreen(onClose = { navController.popBackStack() })
        }

        composable<ClockPreview> {
            val appSettings by viewModel.appSettings.collectAsStateWithLifecycle()
            val options = appSettings?.contextOptions ?: return@composable LoadingSpinner(Modifier.fillMaxSize())

            FullSizeClock(options, onClose = { navController.popBackStack() })
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
