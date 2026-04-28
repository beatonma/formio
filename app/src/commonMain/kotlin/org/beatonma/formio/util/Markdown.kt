package org.beatonma.formio.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import org.beatonma.formio.app.theme.MarkdownTheme

/*
 * Adapted from: https://gist.github.com/binrebin/f3dad29956eb8dcb760a38ce86a9553b
 */

fun parseMarkdown(rawText: String, theme: MarkdownTheme): List<MarkdownBlock> =
    rawText.trim().split("""[\r\n]{2,}""".toRegex()).map { rawParagraph ->
        parseBlock(rawParagraph, theme)
    }


private fun parseBlock(rawText: String, theme: MarkdownTheme): MarkdownBlock {
    val tokens = parseTokens(rawText)

    if (tokens.isEmpty()) return MarkdownBlock(
        buildAnnotatedString { withStyle(theme.paragraph) { append(rawText) } },
        MarkdownBlockType.Paragraph
    )

    val isHeader = tokens.any { it.type.isHeader }

    if (isHeader) {
        return MarkdownBlock(
            buildAnnotatedString { buildBlock(rawText, tokens, theme) },
            MarkdownBlockType.Header
        )
    } else return MarkdownBlock(
        buildAnnotatedString {
            withStyle(theme.paragraph) {
                buildBlock(rawText, tokens, theme)
            }
        },
        MarkdownBlockType.Paragraph
    )
}

private fun parseTokens(rawText: String): List<MarkdownToken> {
    val tokens = mutableListOf<MarkdownToken>()
    TokenType.entries.forEach { type ->
        type.pattern.findAll(rawText).forEach { match ->
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
    return tokens.toList()
}

private fun AnnotatedString.Builder.buildBlock(
    rawText: String,
    tokens: List<MarkdownToken>,
    theme: MarkdownTheme,
) {
    var rawIndex = 0

    fun appendRaw(until: Int) {
        if (rawIndex < until) {
            append(rawText.substring(rawIndex, until))
            rawIndex = until
        }
    }

    for (token in tokens) {
        if (token.start < rawIndex) continue
        appendRaw(token.start)

        when (token.type) {
            TokenType.H1 -> {
                val (_, text) = token.groups
                withStyle(theme.h1) {
                    append(text)
                }
            }

            TokenType.H2 -> {
                val (_, text) = token.groups
                withStyle(theme.h2) {
                    append(text)
                }
            }

            TokenType.Italic -> {
                val (_, text) = token.groups
                withStyle(theme.italic) {
                    append(text)
                }
            }

            TokenType.Bold -> {
                val (_, text) = token.groups
                withStyle(theme.bold) {
                    append(text)
                }
            }

            TokenType.Link -> {
                val (_, text, url) = token.groups
                withStyledLink(url, theme.link) {
                    append(text)
                }
            }
        }
        rawIndex = token.end
    }

    appendRaw(rawText.length)
}

fun AnnotatedString.Builder.withStyledLink(url: String, style: SpanStyle, block: AnnotatedString.Builder.() -> Unit) =
    withStyle(style) {
        withLink(LinkAnnotation.Url(url), block)
    }

data class MarkdownBlock(val annotatedString: AnnotatedString, val type: MarkdownBlockType)
enum class MarkdownBlockType {
    Header,
    Paragraph,
    ;
}

private enum class TokenType(val pattern: Regex) {
    H1("""^# +(.*)$""".toRegex(RegexOption.MULTILINE)),
    H2("""^## +(.*)$""".toRegex(RegexOption.MULTILINE)),
    Bold("""\*\*(.*?)\*\*""".toRegex()),
    Italic("""(?<!\*)\*(.*?)\*""".toRegex()),
    Link("""\[(?<display>.*?)]\((?<url>.*?)\)""".toRegex()),
    ;

    val isHeader get() = this == H1 || this == H2
}

private data class MarkdownToken(
    val type: TokenType,
    val start: Int,
    val end: Int,
    val groups: List<String>,
)
