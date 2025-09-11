package org.beatonma.gclocks.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.core.app.AlarmManagerCompat.canScheduleExactAlarms
import androidx.lifecycle.viewmodel.compose.viewModel
import org.beatonma.R
import org.beatonma.gclocks.android.alarmManager
import org.beatonma.gclocks.android.appContext
import org.beatonma.gclocks.app.settings.DisplayContext
import org.beatonma.gclocks.app.settings.settingsRepository
import org.beatonma.gclocks.core.util.debug
import org.beatonma.gclocks.widget.ClockWidgetProvider

class MainActivity : ComponentActivity() {
    private var shouldShowWidgetPermissionRequest = true
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

        setAppContent()
    }

    private fun setAppContent() {
        val factory = AppViewModelFactory(settingsRepository, onSave = {
            ClockWidgetProvider.refreshWidgets(appContext)
        })

        setContent {
            val viewModel: AppViewModel = viewModel(factory = factory)
            val currentState = viewModel.currentState.collectAsState(null)
            val snackbarHostState = remember { SnackbarHostState() }

            LaunchedEffect(currentState.value, shouldShowWidgetPermissionRequest) {
                if (currentState.value?.context == DisplayContext.Widget && shouldShowWidgetPermissionRequest) {
                    showWidgetPermissionRequest(snackbarHostState)
                }
            }

            App(
                viewModel,
                snackbarHostState = snackbarHostState,
            )
        }
    }

    private suspend fun showWidgetPermissionRequest(snackbarHostState: SnackbarHostState) {
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
                        Uri.parse("package:$packageName")
                    )
                )

                debug("Launched permission request..?")
            }

            SnackbarResult.Dismissed -> {
                shouldShowWidgetPermissionRequest = false
            }
        }
    }
}
