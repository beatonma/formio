package org.beatonma.formio.app.theme.tokens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal object WindowTokens {
    /**
     * Text should be at least this far from the edge of the window.
     */
    val ContentPadding: Dp = 16.dp
    val ContentPaddingValues: PaddingValues = PaddingValues(ContentPadding)
}