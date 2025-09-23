package org.beatonma.gclocks.app

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf


val LocalSystemBars: ProvidableCompositionLocal<SystemBarsController?> = compositionLocalOf { null }

data class SystemBarsController(
    val onRequestHideSystemBars: () -> Unit,
    val onRequestShowSystemBars: () -> Unit,
)
