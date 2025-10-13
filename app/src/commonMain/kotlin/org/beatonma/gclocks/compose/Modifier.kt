package org.beatonma.gclocks.compose

import androidx.compose.ui.Modifier


inline fun Modifier.onlyIf(condition: Boolean, block: Modifier.() -> Modifier): Modifier =
    when (condition) {
        true -> this.block()
        false -> this
    }

inline fun <T> Modifier.onlyIf(value: T?, block: Modifier.(T) -> Modifier): Modifier = when (value) {
    null -> this
    else -> this.block(value)
}
