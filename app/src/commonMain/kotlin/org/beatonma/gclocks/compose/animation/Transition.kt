package org.beatonma.gclocks.compose.animation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

private const val Scale: Float = 0.95f
private const val Duration: Int = 300
private const val DurationShort: Int = (Duration * 1f / 3f).toInt()
private const val DurationHalf: Int = (Duration * 1f / 2f).toInt()
private const val DurationMedium: Int = (Duration * 2f / 3f).toInt()
private val EaseInOut = CubicBezierEasing(0.4f, 0f, 0.8f, 1f)

val EnterImmediate: EnterTransition = slideInVertically(animationSpec = tween(0)) { 0 }
val ExitImmediate: ExitTransition = slideOutVertically(animationSpec = tween(0)) { 0 }


val EnterFade: EnterTransition = fadeIn(
    tween(
        durationMillis = DurationHalf,
        easing = FastOutSlowInEasing,
    )
)

val ExitFade: ExitTransition = fadeOut(
    tween(durationMillis = DurationShort, easing = FastOutLinearInEasing)
)

val EnterVertical: EnterTransition =
    fadeIn(
        tween(
            durationMillis = DurationHalf,
            delayMillis = DurationHalf,
            easing = FastOutSlowInEasing,
        )
    ) + scaleIn(
        tween(
            durationMillis = DurationMedium,
            delayMillis = DurationShort,
            easing = LinearEasing,
        ),
        initialScale = Scale,
    ) + expandVertically(
        tween(
            durationMillis = Duration,
            easing = FastOutSlowInEasing,
        )
    )


val ExitVertical: ExitTransition =
    fadeOut(
        tween(durationMillis = DurationShort, easing = FastOutLinearInEasing)
    ) + scaleOut(
        tween(durationMillis = Duration, easing = LinearEasing),
        targetScale = Scale,
    ) + shrinkVertically(
        tween(
            durationMillis = DurationHalf,
            delayMillis = DurationHalf,
            easing = EaseInOut
        )
    )

val EnterScale: EnterTransition = fadeIn(
    tween(
        durationMillis = DurationHalf,
        delayMillis = DurationHalf,
        easing = FastOutSlowInEasing,
    )
) + scaleIn(
    tween(
        durationMillis = DurationMedium,
        delayMillis = DurationShort,
        easing = LinearEasing,
    ),
    initialScale = Scale,
)

val ExitScale: ExitTransition = fadeOut(
    tween(durationMillis = Duration, easing = FastOutLinearInEasing)
) + scaleOut(
    tween(durationMillis = Duration, easing = LinearEasing),
    targetScale = Scale,
)


@Composable
fun AnimatedFade(
    visible: Boolean,
    modifier: Modifier = Modifier,
    label: String = "AnimatedFade",
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(visible, modifier, EnterFade, ExitFade, label, content)
}

@Composable
fun AnimatedVertical(
    visible: Boolean,
    modifier: Modifier = Modifier,
    label: String = "AnimatedVertical",
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(visible, modifier, EnterVertical, ExitVertical, label, content)
}
