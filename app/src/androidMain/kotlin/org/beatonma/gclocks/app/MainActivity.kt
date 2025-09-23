package org.beatonma.gclocks.app

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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.app.AlarmManagerCompat.canScheduleExactAlarms
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import org.beatonma.R
import org.beatonma.gclocks.android.alarmManager
import org.beatonma.gclocks.android.appContext
import org.beatonma.gclocks.android.componentNameOf
import org.beatonma.gclocks.android.widgetManager
import org.beatonma.gclocks.app.settings.DisplayContext
import org.beatonma.gclocks.app.settings.DisplayMetrics
import org.beatonma.gclocks.app.theme.AppTheme
import org.beatonma.gclocks.compose.AndroidIcon
import org.beatonma.gclocks.wallpaper.ClockWallpaperService
import org.beatonma.gclocks.widget.ClockWidgetProvider

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

        shouldShowWidgetPermissionRequest = !canScheduleExactAlarms(appContext.alarmManager)
        lifecycleScope.launch { updateDisplayMetrics() }

        setAppContent()
    }

    private fun setAppContent() {
        val factory = AppViewModelFactory(settingsRepository, onSave = {
            ClockWidgetProvider.refreshWidgets(appContext)
        })

        setContent {
            val viewModel: AppViewModel = viewModel(factory = factory)
            val currentState by viewModel.currentState.collectAsState(null)
            val snackbarHostState = remember { SnackbarHostState() }
            val systemBarsController = rememberSystemBarsController()

            LaunchedEffect(currentState, shouldShowWidgetPermissionRequest) {
                if (currentState?.context == DisplayContext.Widget && shouldShowWidgetPermissionRequest) {
                    showWidgetPermissionRequest(snackbarHostState)
                }
            }

            AppTheme {
                CompositionLocalProvider(LocalSystemBars provides systemBarsController) {
                    App(
                        viewModel,
                        snackbarHostState = snackbarHostState,
                        toolbar = { ClockToolbar(it) }
                    )
                }
            }
        }
    }

    private suspend fun showWidgetPermissionRequest(snackbarHostState: SnackbarHostState) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            throw IllegalStateException("")
        }
        val result = snackbarHostState.showSnackbar(
            resources.getString(R.string.permission_widget_alarms),
            actionLabel = resources.getString(R.string.permission_widget_alarms_action_label),
            duration = SnackbarDuration.Indefinite,
        )

        when (result) {
            SnackbarResult.ActionPerformed -> {
                requestAlarmPermissionLauncher.launch(
                    Intent(
                        Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                        "package:$packageName".toUri()
                    )
                )
            }

            SnackbarResult.Dismissed -> {
                shouldShowWidgetPermissionRequest = false
            }
        }
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
    private fun rememberAppAdapter(snackbarHostState: SnackbarHostState): AppAdapter {
        return remember {
            AppAdapter(
                snackbarHostState = snackbarHostState,
                toolbar = { ClockToolbar(it) }
            )
        }
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
