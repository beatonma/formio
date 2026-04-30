package org.beatonma.formio.app

import android.app.WallpaperManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.app.AlarmManagerCompat.canScheduleExactAlarms
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import formio.app.generated.resources.Res
import formio.app.generated.resources.widget_alarm_permission_request_action
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.beatonma.formio.android.alarmManager
import org.beatonma.formio.android.appContext
import org.beatonma.formio.android.componentNameOf
import org.beatonma.formio.android.widgetManager
import org.beatonma.formio.app.data.save
import org.beatonma.formio.app.data.settings.DisplayContext
import org.beatonma.formio.app.data.settings.DisplayMetrics
import org.beatonma.formio.app.data.settingsRepository
import org.beatonma.formio.app.ui.App
import org.beatonma.formio.app.ui.components.AndroidIcon
import org.beatonma.formio.app.ui.providers.SystemBarsController
import org.beatonma.formio.app.ui.screens.settings.SettingsEditorScreen
import org.beatonma.formio.app.ui.screens.settings.SettingsEditorViewModel
import org.beatonma.formio.app.ui.screens.settings.components.LocalSettingActions
import org.beatonma.formio.app.ui.screens.settings.components.SettingAction
import org.beatonma.formio.app.ui.screens.settings.components.SettingActions
import org.beatonma.formio.app.ui.screens.settings.settingsEditorViewModel
import org.beatonma.formio.wallpaper.ClockWallpaperService
import org.beatonma.formio.widget.ClockWidgetProvider

class MainActivity : ComponentActivity() {
    private var shouldShowWidgetPermissionRequest by mutableStateOf(true)
    private val requestAlarmPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        shouldShowWidgetPermissionRequest = !canScheduleExactAlarms(appContext.alarmManager)
        ClockWidgetProvider.refreshWidgets(appContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setAppContent()

        shouldShowWidgetPermissionRequest = !canScheduleExactAlarms(appContext.alarmManager)
        lifecycleScope.launch(Dispatchers.io) { updateDisplayMetrics() }
    }

    private fun setAppContent() {
        setContent {
            val editorViewModel = settingsEditorViewModel(
                settingsRepository,
                onSave = { ClockWidgetProvider.refreshWidgets(appContext) }
            )
            val systemBarsController = rememberSystemBarsController()

            LaunchedEffect(shouldShowWidgetPermissionRequest) {
                editorViewModel.shouldShowWidgetPermissionRequest = shouldShowWidgetPermissionRequest
            }

            App(editorViewModel, systemBarsController) { navigation, navigationIcon ->
                CompositionLocalProvider(LocalSettingActions provides rememberSettingActions()) {
                    SettingsEditorScreen(
                        editorViewModel,
                        navigation,
                        snackbarHostState = null,
                        navigationIcon = navigationIcon,
                        toolbar = { ClockToolbar(it) }
                    )
                }
            }
        }
    }

    @Composable
    private fun rememberSettingActions(): SettingActions {
        return remember {
            mutableStateMapOf(
                SettingsEditorViewModel.WidgetAlarmPermission to SettingAction(
                    Res.string.widget_alarm_permission_request_action,
                    ::requestWidgetExactAlarmPermission
                )
            )
        }
    }

    private fun requestWidgetExactAlarmPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            error("requestWidgetExactAlarmPermission() should only be called on Android >= 31")
        }

        requestAlarmPermissionLauncher.launch(
            Intent(
                Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                "package:$packageName".toUri()
            )
        )
    }

    /**
     * Live wallpaper frame scheduling is better with knowledge of the device
     * frame rate, but that is not directly accessible from the LWP service context.
     * As a workaround, we retrieve it on app creation and store it in the settings
     * repository which is accessible from the LWP.
     */
    private suspend fun updateDisplayMetrics() {
        val refreshRate: Float = display.refreshRate

        settingsRepository.save(DisplayMetrics(refreshRate = refreshRate))
    }

    @Composable
    private fun rememberSystemBarsController(): SystemBarsController {
        val systemUiController =
            remember { WindowCompat.getInsetsController(window, window.decorView) }
        val systemBarsController = remember {
            SystemBarsController(
                onRequestHideSystemBars = { systemUiController.hide(WindowInsetsCompat.Type.systemBars()) },
                onRequestShowSystemBars = { systemUiController.show(WindowInsetsCompat.Type.systemBars()) }
            )
        }
        return systemBarsController
    }

    @Composable
    private fun ClockToolbar(context: DisplayContext) {
        when (context) {
            DisplayContext.LiveWallpaper -> {
                IconButton(::openWallpaperSelector) {
                    Icon(AndroidIcon.LiveWallpaper, null)
                }
            }

            DisplayContext.Screensaver -> {
                IconButton(::openDaydreamSelector) {
                    Icon(AndroidIcon.Screensaver, null)
                }
            }

            DisplayContext.Widget -> {
                IconButton(::openWidgetSelector) {
                    Icon(AndroidIcon.Widget, null)
                }
            }
        }
    }

    private fun openWallpaperSelector() {
        val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
            putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                appContext.componentNameOf(ClockWallpaperService::class)
            )
        }
        startActivity(intent)
    }

    private fun openDaydreamSelector() {
        val intent = Intent(Settings.ACTION_DREAM_SETTINGS)
        startActivity(intent)
    }

    private fun openWidgetSelector() {
        widgetManager.requestPinAppWidget(
            componentNameOf(ClockWidgetProvider::class),
            null,
            null
        )
    }
}
