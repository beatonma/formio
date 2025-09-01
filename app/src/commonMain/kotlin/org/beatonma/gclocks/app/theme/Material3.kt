package org.beatonma.gclocks.app.theme

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

object Material3 {
    // https://m3.material.io/components/extended-fab/specs#17471317-9cd9-450c-8fa8-708cfb29c73e
    fun Modifier.extendedFloatingActionButton(): Modifier =
        padding(16.dp).systemBarsPadding()
}