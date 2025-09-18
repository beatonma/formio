package org.beatonma.gclocks.compose.components.settings.components

import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun SettingName(name: String, modifier: Modifier = Modifier) {
    Text(name, modifier, style = typography.titleMedium)
}

@Composable
fun SettingValue(
    name: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    Text(
        name,
        modifier,
        color = color,
        style = typography.titleSmall,
        overflow = overflow,
        maxLines = 1
    )
}
