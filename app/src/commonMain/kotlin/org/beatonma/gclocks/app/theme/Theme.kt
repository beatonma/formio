package org.beatonma.gclocks.app.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

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
        Theme.Light -> lightColorScheme()
        Theme.Dark -> darkColorScheme(
            background = Color(red = 9, green = 7, blue = 13)
        )

        Theme.System -> getColorScheme(
            if (isSystemInDarkTheme()) Theme.Dark
            else Theme.Light
        )
    }
}

@Composable
fun rememberContentColor(color: Color, alpha: Float = 0.72f): Color {
    return remember(color) { mutableStateOf(color.getForegroundColor(alpha)) }.value
}

fun Color.getForegroundColor(alpha: Float = 0.72f): Color = when {
    luminance() > 0.5f -> Color.Black
    else -> Color.White
}.copy(alpha = alpha)
