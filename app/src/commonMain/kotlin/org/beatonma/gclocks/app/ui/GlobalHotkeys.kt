package org.beatonma.gclocks.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type

interface HotkeyRegistry {
    fun register(handler: KeyAction)
    fun unregister(handler: KeyAction)
}

private typealias KeyAction = (KeyEvent) -> Boolean

private class _HotkeyRegistry : HotkeyRegistry {
    private val handlers = mutableListOf<KeyAction>()

    override fun register(handler: KeyAction) {
        if (handler in handlers) {
            return
        }
        handlers.add(0, handler)
    }

    override fun unregister(handler: KeyAction) {
        handlers.remove(handler)
    }

    fun onKeyEvent(event: KeyEvent): Boolean {
        for (handler in handlers) {
            if (handler(event)) return true
        }
        return false
    }
}

val LocalHotkeyRegistry: ProvidableCompositionLocal<HotkeyRegistry> =
    staticCompositionLocalOf { error("LocalHotkeyRegistry has not been initialized") }

/**
 * Allow hotkeys to be registered via `globalHotkey` modifier. Hotkeys registered in this way do not require
 * any particular component to have focus and will be usable as long as the modified component is in the composition.
 *
 * Hotkeys must use at least one of the Control or Alt modifier keys, to avoid interfering with text input components.
 */
@Composable
fun HotkeyRegistryProvider(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val registry = remember { _HotkeyRegistry() }

    CompositionLocalProvider(LocalHotkeyRegistry provides registry) {
        Box(modifier.onPreviewKeyEvent { event ->
            if (event.key == Key.Back) {
                // Ignore Android system `back` navigation.
                return@onPreviewKeyEvent false
            }
            if (event.type != KeyEventType.KeyDown) {
                return@onPreviewKeyEvent false
            }

            if (event.isCtrlPressed || event.isAltPressed) {
                // When modifier is pressed, pass KeyDown event to registered handlers
                return@onPreviewKeyEvent registry.onKeyEvent(event)
            }

            false
        }) {
            content()
        }
    }
}


fun Modifier.globalHotkey(onKeyDown: (Key) -> Boolean): Modifier = composed {
    val handler = LocalHotkeyRegistry.current

    DisposableEffect(onKeyDown) {
        val action: KeyAction = { event -> onKeyDown(event.key) }
        handler.register(action)

        onDispose {
            handler.unregister(action)
        }
    }

    this
}