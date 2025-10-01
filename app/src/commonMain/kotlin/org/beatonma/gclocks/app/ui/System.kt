package org.beatonma.gclocks.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier


val LocalSystemBars: ProvidableCompositionLocal<SystemBarsController?> = compositionLocalOf { null }

data class SystemBarsController(
    val onRequestHideSystemBars: () -> Unit,
    val onRequestShowSystemBars: () -> Unit,
)


@Composable
fun EdgeToEdge(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    systemBarsController: SystemBarsController? = LocalSystemBars.current,
    content: @Composable BoxScope.() -> Unit,
) {
    DisposableEffect(Unit) {
        systemBarsController?.onRequestHideSystemBars()
        onDispose { systemBarsController?.onRequestShowSystemBars() }
    }

    Box(modifier, contentAlignment = contentAlignment, content = content)
}
