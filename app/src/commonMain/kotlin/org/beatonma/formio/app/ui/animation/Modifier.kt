package org.beatonma.formio.app.ui.animation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import kotlinx.coroutines.delay


fun Modifier.fadeIn(delay: Long = 0L) = composed {
    var target by remember { mutableStateOf(0f) }
    val opacity by animateFloatAsState(target)

    LaunchedEffect(Unit) {
        delay(delay)
        target = 1f
    }

    alpha(opacity)
}

fun Modifier.scaleIn(popScale: Float, initialScale: Float = 0f, delay: Long = 0L, key: Any = Unit) = composed {
    var target by remember(key) { mutableStateOf(initialScale) }
    val animatedScale by animateFloatAsState(
        target,
        finishedListener = { value ->
            if (value != 1f) {
                target = 1f
            }
        }
    )

    LaunchedEffect(key) {
        delay(delay)
        target = popScale
    }

    scale(animatedScale)
}