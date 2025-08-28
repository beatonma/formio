package org.beatonma.gclocks.compose.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Composable
actual fun currentFrameDelta(): Duration {
    var frameMillis by remember { mutableLongStateOf(0L) }

    LaunchedEffect(Unit) {
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