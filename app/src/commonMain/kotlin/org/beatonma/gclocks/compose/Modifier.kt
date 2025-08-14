package org.beatonma.gclocks.compose

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed


fun Modifier.onlyIf(condition: Boolean, block: Modifier.() -> Unit): Modifier = composed {
    apply {
        if (condition) {
            block()
        }
    }
}