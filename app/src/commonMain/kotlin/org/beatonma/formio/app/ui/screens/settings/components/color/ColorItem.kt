package org.beatonma.formio.app.ui.screens.settings.components.color

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import org.beatonma.formio.app.ui.components.toCompose
import org.beatonma.formio.app.ui.theme.rememberContentColor
import org.beatonma.formio.app.ui.theme.tokens.FoundationTokens
import org.beatonma.formio.core.graphics.Color
import androidx.compose.ui.graphics.Color as ComposeColor


internal object ColorItemTokens {
    val DefaultPatchSize = FoundationTokens.TouchTarget.MinSize
    val EditablePatchSize = DefaultPatchSize * 1.5f
    val DefaultPreviewSize = DefaultPatchSize * 0.5f
    val LargePreviewSize = DefaultPatchSize * 0.75f
}


/**
 * Small, read-only preview of a color.
 */
@Composable
internal fun ColorPreview(color: Color, size: Dp = ColorItemTokens.DefaultPreviewSize, modifier: Modifier = Modifier) {
    Spacer(
        modifier
            .size(size)
            .border(
                Dp.Hairline,
                LocalContentColor.current.copy(alpha = 0.2f),
                shapes.small
            )
            .background(color.toCompose(), shapes.small)
    )
}

/**
 * Clickable color preview.
 */
@Composable
internal fun ColorPatch(
    color: ComposeColor,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentColor: ComposeColor = rememberContentColor(color),
    size: Dp = ColorItemTokens.DefaultPatchSize,
    content: (@Composable () -> Unit)? = null,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.size(size),
        color = color,
        contentColor = contentColor,
        shape = shapes.extraSmall,
        border = BorderStroke(Dp.Hairline, colorScheme.onBackground.copy(alpha = 0.3f))
    ) {
        content?.let {
            Box(contentAlignment = Alignment.Center) {
                content.invoke()
            }
        }
    }
}
