package org.beatonma.gclocks.compose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.beatonma.gclocks.app.theme.rememberContentColor


@Composable
fun IconToolbar(
    modifier: Modifier = Modifier,
    alignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp, alignment),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}


@Composable
fun FloatingIconToolbar(
    modifier: Modifier = Modifier,
    backgroundColor: Color = colorScheme.scrim,
    alignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    content: @Composable RowScope.() -> Unit,
) {
    Surface(
        modifier,
        color = backgroundColor,
        contentColor = rememberContentColor(backgroundColor),
        shape = shapes.small
    ) {
        IconToolbar(Modifier.padding(8.dp), alignment, content)
    }
}
