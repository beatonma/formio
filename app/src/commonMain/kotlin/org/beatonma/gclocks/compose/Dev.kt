package org.beatonma.gclocks.compose

import androidx.compose.foundation.focusable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
        return composed {
            val focusRequester = remember { FocusRequester() }
            LaunchedEffect(Unit) { focusRequester.requestFocus() }

            focusable()
                .focusRequester(focusRequester)
                .onKeyEvent(onKeyEvent)
        }
    }
    return this
}
