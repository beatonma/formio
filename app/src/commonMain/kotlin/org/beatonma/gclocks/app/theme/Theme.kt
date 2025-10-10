package org.beatonma.gclocks.app.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

enum class Theme {
    Light,
    Dark,
    System,
    ;
}

@Composable
fun AppTheme(
    theme: Theme = Theme.Dark,
    content: @Composable () -> Unit,
) {
    MaterialTheme(colorScheme = getColorScheme(theme)) {
        Surface(
            Modifier.fillMaxSize(),
            color = colorScheme.background,
            contentColor = colorScheme.onBackground
        ) {
            content()
        }
    }
}

@Composable
fun getColorScheme(theme: Theme): ColorScheme {
    return when (theme) {
        Theme.Light -> lightColorScheme(
            scrim = Color(0, 0, 0, 80),
        )

        Theme.Dark -> darkColorScheme(
            background = Color(red = 9, green = 7, blue = 13),
            scrim = Color(0, 0, 0, 80)
        )

        Theme.System -> getColorScheme(
            if (isSystemInDarkTheme()) Theme.Dark
            else Theme.Light
        )
    }
}

@Composable
fun rememberContentColor(backgroundColor: Color, alpha: Float = 0.72f): Color {
    return remember(backgroundColor) { mutableStateOf(backgroundColor.getForegroundColor(alpha)) }.value
}

fun Color.getForegroundColor(alpha: Float = 0.72f): Color = when {
    luminance() > 0.5f -> Color.Black
    else -> Color.White
}.copy(alpha = alpha)


data class MarkdownTheme(
    val h1: SpanStyle,
    val h2: SpanStyle,
    val link: SpanStyle,
    val bold: SpanStyle = SpanStyle(fontWeight = FontWeight.Bold),
    val italic: SpanStyle = SpanStyle(fontStyle = FontStyle.Italic),
    val paragraph: ParagraphStyle = ParagraphStyle()
)

@Composable
fun ColorScheme.markdownColorScheme(): MarkdownTheme {
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

internal sealed interface ClockColorScheme {
    val backgroundColor: Color
    val clockColors: List<Color>
}

object FormColorScheme : ClockColorScheme {
    override val backgroundColor = Color(0xFF277CCC)
    override val clockColors = FormPaints.DefaultColors.map { it.toCompose() }
}

object Io16ColorScheme : ClockColorScheme {
    override val backgroundColor = Color(0xFF4C4C45)
    override val clockColors = Io16Paints.DefaultColors.map { it.toCompose() }
}

object Io18ColorScheme : ClockColorScheme {
    override val backgroundColor = Color(0xFF2D2D2D)
    override val clockColors = Io18Paints.DefaultColors.map { it.toCompose() }
}
