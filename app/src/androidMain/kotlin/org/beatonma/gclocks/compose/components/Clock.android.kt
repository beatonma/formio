package org.beatonma.gclocks.compose.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.Dispatchers
import org.beatonma.gclocks.compose.lifecycleAwareEffect
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Composable
actual fun currentFrameDelta(): Duration {
    var frameMillis by remember { mutableLongStateOf(0L) }

    lifecycleAwareEffect(
        Dispatchers.Main,
        rememberCoroutineScope(),
        LocalLifecycleOwner.current,
        onStop = {

        },
    ) {
        var previousFrameMillis = 0L
        while (true) {
            withFrameMillis { frameTimeMillis ->
                frameMillis = frameTimeMillis - previousFrameMillis
                previousFrameMillis = frameTimeMillis
            }
        }
    }

    return frameMillis.milliseconds
}