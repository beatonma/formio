package org.beatonma.gclocks.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import org.beatonma.gclocks.app.theme.MarkdownTheme

/*
 * Adapted from: https://gist.github.com/binrebin/f3dad29956eb8dcb760a38ce86a9553b
 */


fun parseMarkdown(raw: String, theme: MarkdownTheme): List<AnnotatedString> =
    raw.trim().split("""[\r\n]{2,}""".toRegex()).map { rawParagraph ->
        buildAnnotatedString {
            withStyle(theme.paragraph) {
                parseParagraph(this@buildAnnotatedString, rawParagraph, theme)
            }
        }
    }


private fun parseParagraph(builder: AnnotatedString.Builder, raw: String, theme: MarkdownTheme) {
    val tokens = mutableListOf<MarkdownToken>()
    TokenType.entries.forEach { type ->
        type.pattern.findAll(raw).forEach { match ->
            tokens.add(
                MarkdownToken(
                    type,
                    match.range.first,
                    match.range.last + 1,
                    match.groupValues
                )
            )
        }
    }
    tokens.sortBy { it.start }

    var rawIndex = 0

    fun appendRaw(until: Int) {
        if (rawIndex < until) {
            builder.append(raw.substring(rawIndex, until))
            rawIndex = until
        }
    }

    for (token in tokens) {
        if (token.start < rawIndex) continue
        appendRaw(token.start)

        when (token.type) {
            TokenType.H1 -> {
                val (_, text) = token.groups
                builder.withStyle(theme.h1) {
                    append(text)
                }
            }

            TokenType.H2 -> {
                val (_, text) = token.groups
                builder.withStyle(theme.h2) {
                    append(text)
                }
            }

            TokenType.Italic -> {
                val (_, text) = token.groups
                builder.withStyle(theme.italic) {
                    append(text)
                }
            }

            TokenType.Bold -> {
                val (_, text) = token.groups
                builder.withStyle(theme.bold) {
                    append(text)
                }
            }

            TokenType.Link -> {
                val (_, text, url) = token.groups
                builder.withStyledLink(url, theme.link) {
                    append(text)
                }
            }
        }
        rawIndex = token.end
    }

    appendRaw(raw.length)
}


fun AnnotatedString.Builder.withStyledLink(url: String, style: SpanStyle, block: AnnotatedString.Builder.() -> Unit) =
    withStyle(style) {
        withLink(LinkAnnotation.Url(url), block)
    }


private enum class TokenType(val pattern: Regex) {
    H1("""^# +(.*)$""".toRegex(RegexOption.MULTILINE)),
    H2("""^## +(.*)$""".toRegex(RegexOption.MULTILINE)),
    Bold("""\*\*(.*?)\*\*""".toRegex()),
    Italic("""(?<!\*)\*(.*?)\*""".toRegex()),
    Link("""\[(?<display>.*?)]\((?<url>.*?)\)""".toRegex()),
    ;
}

private data class MarkdownToken(
    val type: TokenType,
    val start: Int,
    val end: Int,
    val groups: List<String>,
)
