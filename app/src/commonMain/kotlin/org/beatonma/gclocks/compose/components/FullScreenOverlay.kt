package org.beatonma.gclocks.compose.components

import androidx.compose.runtime.Composable

expect @Composable
fun FullScreenOverlay(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
)