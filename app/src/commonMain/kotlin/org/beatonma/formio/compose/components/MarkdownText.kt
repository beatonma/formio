package org.beatonma.formio.compose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.beatonma.formio.app.theme.MarkdownTheme
import org.beatonma.formio.app.theme.markdownTheme
import org.beatonma.formio.core.util.fastForEachIndexed
import org.beatonma.formio.util.MarkdownBlockType
import org.beatonma.formio.util.parseMarkdown


@Composable
fun MarkdownText(
    raw: String,
    modifier: Modifier = Modifier,
    style: MarkdownTheme = markdownTheme(),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    blockModifier: Modifier = Modifier,
) {
    val markdown = rememberSaveable(raw, style) { parseMarkdown(raw.trim(), style) }
    val paragraphModifier = blockModifier.padding(bottom = style.paragraphSpacing)

    Column(
        modifier,
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = verticalArrangement
    ) {
        markdown.fastForEachIndexed { index, (annotatedString, type) ->
            val modifier = when (type) {
                MarkdownBlockType.Header -> blockModifier
                MarkdownBlockType.Paragraph -> if (index == markdown.size - 1) blockModifier else paragraphModifier
            }
            Text(annotatedString, modifier)
        }
    }
}
