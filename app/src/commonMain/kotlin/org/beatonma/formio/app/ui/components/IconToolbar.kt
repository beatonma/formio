package org.beatonma.formio.app.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier


@Composable
fun IconToolbar(
    modifier: Modifier = Modifier,
    alignment: Alignment.Horizontal = Alignment.End,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier,
        horizontalArrangement = Row.mediumSpacingArrangement(alignment),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}
