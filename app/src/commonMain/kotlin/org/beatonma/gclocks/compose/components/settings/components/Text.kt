package org.beatonma.gclocks.compose.components.settings.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingName(name: String, modifier: Modifier = Modifier) {
    Text(name, modifier.padding(top = 16.dp, bottom = 8.dp), style = typography.labelLarge)
}