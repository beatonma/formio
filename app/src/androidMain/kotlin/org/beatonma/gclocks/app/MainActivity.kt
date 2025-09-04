package org.beatonma.gclocks.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import org.beatonma.gclocks.app.settings.settingsRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val factory = AppViewModelFactory(this@MainActivity.settingsRepository)

        setContent {
            val viewModel: AppViewModel = viewModel(factory = factory)
            App(viewModel)
        }
    }
}
