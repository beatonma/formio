package org.beatonma.gclocks.compose.components.settings.components

import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SettingName(name: String, modifier: Modifier = Modifier) {
    Text(name, modifier, style = typography.titleMedium)
}

@Composable
fun SettingValue(name: String, modifier: Modifier = Modifier) {
    Text(name, modifier, style = typography.labelLarge)
}
