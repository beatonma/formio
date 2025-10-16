package org.beatonma.gclocks.app.theme

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

object DesignSpec {
    // https://m3.material.io/components/floating-action-button/specs
    val FloatingActionButtonSize = 56.dp
    private val FloatingActionButtonPadding = 16.dp

    // https://m3.material.io/components/floating-action-button/specs
    fun Modifier.floatingActionButton(): Modifier = padding(FloatingActionButtonPadding).safeDrawingPadding()

    // https://m3.material.io/components/extended-fab/specs
    fun Modifier.extendedFloatingActionButton(): Modifier =
        padding(16.dp).safeDrawingPadding()

    // https://m3.material.io/foundations/designing/structure
    val TouchTargetPadding = 8.dp
    val TouchTargetMinSize = 48.dp
    fun Modifier.touchTargetSize(): Modifier =
        sizeIn(
            minWidth = TouchTargetMinSize,
            minHeight = TouchTargetMinSize
        )
}
