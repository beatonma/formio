package org.beatonma.formio.app.ui.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.beatonma.formio.app.ui.animation.AnimatedFade
import org.beatonma.formio.app.ui.theme.tokens.FloatingActionButtonTokens


fun Modifier.fabPadding() =
    safeDrawingPadding()
        .padding(
            bottom = FloatingActionButtonTokens.Padding,
            end = FloatingActionButtonTokens.Padding,
        )

@Composable
fun BoxScope.FloatingActionButton(
    isVisible: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    AnimatedFade(
        isVisible,
        Modifier.align(Alignment.BottomEnd)
    ) {
        FloatingActionButton(onClick, Modifier.fabPadding(), content = content)
    }
}