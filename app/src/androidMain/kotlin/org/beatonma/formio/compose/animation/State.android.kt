package org.beatonma.formio.compose.animation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.withFrameMillis
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.Dispatchers
import org.beatonma.formio.compose.lifecycleAwareEffect
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Composable
actual fun currentFrameDelta(): State<Duration> {
    val frameDelta = remember { mutableStateOf(0.milliseconds) }

    lifecycleAwareEffect(
        Dispatchers.Main,
        rememberCoroutineScope(),
        LocalLifecycleOwner.current,
        onStop = null,
    ) {
        var previousFrameMillis = 0L
        while (true) {
            withFrameMillis { frameTimeMillis ->
                frameDelta.value = (frameTimeMillis - previousFrameMillis).milliseconds
                previousFrameMillis = frameTimeMillis
            }
        }
    }

    return frameDelta
}