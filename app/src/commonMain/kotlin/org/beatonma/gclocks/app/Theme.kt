package  org.beatonma.gclocks.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

enum class Theme {
    Light,
    Dark,
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

private fun getColorScheme(theme: Theme): ColorScheme {
    return when (theme) {
        Theme.Light -> lightColorScheme()
        Theme.Dark -> darkColorScheme(
            background = Color(red = 9, green = 7, blue = 13)
        )
    }
}