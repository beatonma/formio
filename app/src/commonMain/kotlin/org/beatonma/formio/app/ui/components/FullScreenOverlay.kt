package org.beatonma.formio.app.ui.components

import androidx.compose.runtime.Composable

@Composable
expect fun FullScreenOverlay(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
)
