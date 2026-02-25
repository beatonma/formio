package org.beatonma.gclocks.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import org.beatonma.gclocks.app.ui.globalHotkey
import org.beatonma.gclocks.core.util.debug


@Suppress("NOTHING_TO_INLINE", "ComposableNaming")
@Composable
inline fun log(content: Any?) {
    SideEffect {
        debug(content)
    }
}


fun Modifier.debugHotkey(onKeyDown: (Key) -> Boolean): Modifier {
    debug {
        return globalHotkey(onKeyDown)
    }
    return this
}
