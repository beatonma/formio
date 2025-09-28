package org.beatonma.gclocks.app.ui.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import gclocks_multiplatform.app.generated.resources.Res
import gclocks_multiplatform.app.generated.resources.cd_navigate_back
import org.beatonma.gclocks.compose.AppIcon
import org.jetbrains.compose.resources.stringResource


@Composable
fun AboutScreen(onClose: () -> Unit) {
    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                { Text("TODO: About screen") },
                navigationIcon = {
                    IconButton(onClose) {
                        Icon(AppIcon.Back, stringResource(Res.string.cd_navigate_back))
                    }
                }
            )
        }
    ) {
        Text("AboutScreen")
    }
}
