package org.beatonma.gclocks.compose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import org.beatonma.gclocks.app.theme.MarkdownTheme
import org.beatonma.gclocks.app.theme.markdownTheme
import org.beatonma.gclocks.util.parseMarkdown


@Composable
fun MarkdownText(
    raw: String,
    modifier: Modifier = Modifier,
    style: MarkdownTheme = markdownTheme(),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(style.paragraphSpacing, Alignment.Top),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    text: @Composable ColumnScope.(AnnotatedString) -> Unit = { Text(it) },
) {
    val markdown = rememberSaveable(raw, style) { parseMarkdown(raw.trim(), style) }

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
