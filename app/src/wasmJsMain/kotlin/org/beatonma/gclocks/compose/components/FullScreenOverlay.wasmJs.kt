package org.beatonma.gclocks.compose.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
actual fun FullScreenOverlay(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismiss,
        DialogProperties(
            usePlatformDefaultWidth = false,
        ),
        content
    )
}
