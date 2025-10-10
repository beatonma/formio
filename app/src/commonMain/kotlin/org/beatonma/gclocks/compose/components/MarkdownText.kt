package org.beatonma.gclocks.compose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import org.beatonma.gclocks.app.theme.markdownColorScheme
import org.beatonma.gclocks.util.parseMarkdown


@Composable
fun MarkdownText(
    raw: String,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp, Alignment.Top),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    text: @Composable ColumnScope.(AnnotatedString) -> Unit = { Text(it) },
) {
    val markdown = rememberMarkdown(raw)

    Column(
        modifier,
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = verticalArrangement
    ) {
        markdown.forEach {
            text(it)
        }
    }
}

@Composable
private fun rememberMarkdown(raw: String): List<AnnotatedString> {
    val markdownColorScheme = colorScheme.markdownColorScheme()
    return rememberSaveable(raw, markdownColorScheme) {
        parseMarkdown(raw.trim(), markdownColorScheme)
    }
}
