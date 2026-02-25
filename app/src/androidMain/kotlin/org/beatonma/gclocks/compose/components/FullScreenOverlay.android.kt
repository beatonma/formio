package org.beatonma.gclocks.compose.components

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider


@Composable
actual fun FullScreenOverlay(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
    if (isOpen) {
        Dialog(
            onDismiss,
            DialogProperties(
                dismissOnBackPress = true,
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            val window = (LocalView.current.parent as DialogWindowProvider).window
            LaunchedEffect(Unit) {
                @Suppress("DEPRECATION")
                window.apply {
                    statusBarColor = Color.TRANSPARENT
                    navigationBarColor = Color.TRANSPARENT
                }
            }
            content()
        }
    }
}
