package org.beatonma.gclocks.app.theme

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

object DesignSpec {
    // https://m3.material.io/components/extended-fab/specs#17471317-9cd9-450c-8fa8-708cfb29c73e
    fun Modifier.extendedFloatingActionButton(): Modifier =
        padding(16.dp).safeDrawingPadding()//.systemBarsPadding()

    // https://m3.material.io/components/floating-action-button/specs#7712fa7f-cd29-4852-86a9-fa2f4a01f6bc
    fun Modifier.floatingActionButton(): Modifier = padding(16.dp).safeDrawingPadding()

    // https://m3.material.io/foundations/designing/structure#1057f862-b8f1-42a1-9239-7077b8763a48
    val TouchTargetPadding = 8.dp
    val TouchTargetMinSize = 48.dp
    fun Modifier.touchTargetSize(): Modifier =
        sizeIn(
            minWidth = TouchTargetMinSize,
            minHeight = TouchTargetMinSize
        )
}
