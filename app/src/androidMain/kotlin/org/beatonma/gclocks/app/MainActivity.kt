package org.beatonma.gclocks.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import org.beatonma.gclocks.app.settings.dataStore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val factory = AppViewModelFactory(
            DataStoreAppSettingsRepository(
                this@MainActivity.dataStore
            )
        )

        setContent {
            val viewModel: AppViewModel = viewModel(factory = factory)
            App(viewModel)
        }
    }
}
