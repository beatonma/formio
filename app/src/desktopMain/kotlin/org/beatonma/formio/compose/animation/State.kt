package org.beatonma.formio.compose.animation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameMillis
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Composable
actual fun currentFrameDelta(): State<Duration> {
    val frameDelta = remember { mutableStateOf(0.milliseconds) }

    LaunchedEffect(Unit) {
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