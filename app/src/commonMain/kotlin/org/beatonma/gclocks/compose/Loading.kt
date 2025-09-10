package org.beatonma.gclocks.compose

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color


@Composable
fun Loading(modifier: Modifier = Modifier) {
    Text("Loading...", modifier, color = Color.Yellow)
}