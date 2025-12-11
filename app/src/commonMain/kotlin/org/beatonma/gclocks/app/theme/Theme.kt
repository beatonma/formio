package org.beatonma.gclocks.app.theme

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class Theme {
    Light,
    Dark,
    System,
    ;
}


@Composable
fun AppTheme(
    modifier: Modifier = Modifier,
    theme: Theme = Theme.System,
    content: @Composable () -> Unit,
) {
    MaterialTheme(colorScheme = getColorScheme(theme)) {
        Surface(
            modifier.fillMaxSize(),
            color = colorScheme.background,
            contentColor = colorScheme.onBackground
        ) {
            content()
        }
    }
}


@Immutable
data class MarkdownTheme(
    val h1: SpanStyle,
    val h2: SpanStyle,
    val link: SpanStyle,
    val bold: SpanStyle = SpanStyle(fontWeight = FontWeight.Bold),
    val italic: SpanStyle = SpanStyle(fontStyle = FontStyle.Italic),
    val paragraph: ParagraphStyle = ParagraphStyle(),
    val paragraphSpacing: Dp = 8.dp,
)

@Composable
fun markdownTheme(): MarkdownTheme {
    val h1Font = typography.displayMedium
    val h2Font = typography.headlineMedium
    val linkColor = colorScheme.primary

    return remember(h1Font, h2Font, linkColor) {
        MarkdownTheme(
            h1 = h1Font.toSpanStyle(),
            h2 = h2Font.toSpanStyle(),
            link = SpanStyle(color = linkColor)
        )
    }
}
