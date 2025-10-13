package org.beatonma.gclocks.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.onKeyEvent
import org.beatonma.gclocks.core.util.debug


@Suppress("NOTHING_TO_INLINE", "ComposableNaming")
@Composable
inline fun log(content: Any?) {
    SideEffect {
        debug(content)
    }
}


fun Modifier.debugKeyEvent(onKeyEvent: (KeyEvent) -> Boolean): Modifier {
    debug {
        return this.onKeyEvent(onKeyEvent)
    }
    return this
}
