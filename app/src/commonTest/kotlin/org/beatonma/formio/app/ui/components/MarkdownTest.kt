package org.beatonma.formio.app.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import org.beatonma.formio.app.ui.theme.MarkdownTheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private val theme = MarkdownTheme(
    h1 = SpanStyle(fontSize = 28.sp),
    h2 = SpanStyle(fontSize = 24.sp),
    link = SpanStyle(color = Color.Red),
    bold = SpanStyle(fontWeight = FontWeight.Bold),
    italic = SpanStyle(fontStyle = FontStyle.Italic),
)


private fun build(block: AnnotatedString.Builder.() -> Unit) =
    AnnotatedString.Builder().apply {
        block()
    }.toAnnotatedString()

private fun buildParagraph(block: AnnotatedString.Builder.() -> Unit) =
    AnnotatedString.Builder().apply {
        withStyle(theme.paragraph) { block() }
    }.toAnnotatedString()


@Suppress("VisibleForTests")
class MarkdownTest {
    private infix fun List<MarkdownBlock>.shouldbe(other: List<AnnotatedString>) {
        zip(other).forEach { (self, other) ->
            assertEquals(
                other.text,
                self.annotatedString.text,
                message = "Text is not equal: ${self.annotatedString.text} != ${other.text}"
            )
            assertTrue(
                self.annotatedString.hasEqualAnnotations(other),
                message = "Annotations are not equal $self vs $other"
            )
        }
    }

    @Test
    fun `parseMarkdown is correct`() {
        parseMarkdown("Hello", theme) shouldbe listOf(buildParagraph { append("Hello") })
        parseMarkdown("*Hello*", theme) shouldbe listOf(buildParagraph {
            withStyle(theme.italic) {
                append("Hello")
            }
        })
        parseMarkdown("**Hello**", theme) shouldbe listOf(buildParagraph {
            withStyle(theme.bold) {
                append("Hello")
            }
        })

        parseMarkdown("# Hello", theme) shouldbe listOf(build {
            withStyle(theme.h1) {
                append("Hello")
            }
        })
        parseMarkdown("## Hello", theme) shouldbe listOf(build {
            withStyle(theme.h2) {
                append("Hello")
            }
        })

        parseMarkdown(
            """# Heading
                |
            |## This is a subheading
            |
            |This is a paragraph with *italic* text.
            |
            |This is a paragraph with **bold** text and a [link](https://beatonma.org).
        """.trimMargin(),
            theme
        ) shouldbe listOf(
            build {
                withStyle(theme.h1) {
                    append("Heading")
                }
            },
            build {
                withStyle(theme.h2) {
                    append("This is a subheading")
                }
            },
            buildParagraph {
                append("This is a paragraph with ")
                withStyle(theme.italic) {
                    append("italic")
                }
                append(" text.")
            },
            buildParagraph {
                append("This is a paragraph with ")
                withStyle(theme.bold) {
                    append("bold")
                }
                append(" text and a ")
                withStyledLink("https://beatonma.org", theme.link) {
                    append("link")
                }
                append(".")
            })
    }
}