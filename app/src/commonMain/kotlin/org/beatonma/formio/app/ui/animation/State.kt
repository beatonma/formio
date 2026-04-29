package org.beatonma.formio.app.ui.animation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import kotlin.time.Duration


@Composable
expect fun currentFrameDelta(): State<Duration>