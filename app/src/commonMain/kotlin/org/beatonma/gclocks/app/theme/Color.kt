package org.beatonma.gclocks.app.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

object Opacity {
    const val TextPrimary = 0.72f
    const val TextSecondary = 0.54f
}


@Composable
fun getColorScheme(theme: Theme): ColorScheme {
    return when (theme) {
        Theme.Light -> lightScheme
        Theme.Dark -> darkScheme

        Theme.System -> getColorScheme(
            if (isSystemInDarkTheme()) Theme.Dark else Theme.Light
        )
    }
}

@Composable
fun rememberContentColor(backgroundColor: Color, alpha: Float = Opacity.TextPrimary): Color {
    return remember(backgroundColor) { mutableStateOf(backgroundColor.getForegroundColor(alpha)) }.value
}

fun Color.getForegroundColor(alpha: Float = Opacity.TextPrimary): Color = when {
    luminance() > 0.5f -> Color.Black
    else -> Color.White
}.copy(alpha = alpha)


/*
 * Colors below generated via https://material-foundation.github.io/material-theme-builder/
 */

val primaryLight = Color(0xFF515B92)
val onPrimaryLight = Color(0xFFFFFFFF)
val primaryContainerLight = Color(0xFFDEE0FF)
val onPrimaryContainerLight = Color(0xFF394379)
val secondaryLight = Color(0xFF5B5D72)
val onSecondaryLight = Color(0xFFFFFFFF)
val secondaryContainerLight = Color(0xFFDFE1F9)
val onSecondaryContainerLight = Color(0xFF434659)
val tertiaryLight = Color(0xFF765A0B)
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFFFFDF99)
val onTertiaryContainerLight = Color(0xFF5A4300)
val errorLight = Color(0xFFBA1A1A)
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFFFDAD6)
val onErrorContainerLight = Color(0xFF93000A)
val backgroundLight = Color(0xFFEEEAF3)
val onBackgroundLight = Color(0xFF1B1B21)
val surfaceLight = Color(0xFFFBF8FF)
val onSurfaceLight = Color(0xFF1B1B21)
val surfaceVariantLight = Color(0xFFE3E1EC)
val onSurfaceVariantLight = Color(0xFF46464F)
val outlineLight = Color(0xFF767680)
val outlineVariantLight = Color(0xFFC6C5D0)
val scrimLight = Color(0xFF000000)
val inverseSurfaceLight = Color(0xFF303036)
val inverseOnSurfaceLight = Color(0xFFF2EFF7)
val inversePrimaryLight = Color(0xFFBAC3FF)
val surfaceDimLight = Color(0xFFDBD9E0)
val surfaceBrightLight = Color(0xFFFBF8FF)
val surfaceContainerLowestLight = Color(0xFFFFFFFF)
val surfaceContainerLowLight = Color(0xFFF5F2FA)
val surfaceContainerLight = Color(0xFFEFEDF4)
val surfaceContainerHighLight = Color(0xFFE9E7EF)
val surfaceContainerHighestLight = Color(0xFFE4E1E9)

val primaryDark = Color(0xFFBAC3FF)
val onPrimaryDark = Color(0xFF222C61)
val primaryContainerDark = Color(0xFF394379)
val onPrimaryContainerDark = Color(0xFFDEE0FF)
val secondaryDark = Color(0xFFC3C5DD)
val onSecondaryDark = Color(0xFF2C2F42)
val secondaryContainerDark = Color(0xFF434659)
val onSecondaryContainerDark = Color(0xFFDFE1F9)
val tertiaryDark = Color(0xFFE7C26C)
val onTertiaryDark = Color(0xFF3F2E00)
val tertiaryContainerDark = Color(0xFF5A4300)
val onTertiaryContainerDark = Color(0xFFFFDF99)
val errorDark = Color(0xFFFFB4AB)
val onErrorDark = Color(0xFF690005)
val errorContainerDark = Color(0xFF93000A)
val onErrorContainerDark = Color(0xFFFFDAD6)
val backgroundDark = Color(0xFF131318)
val onBackgroundDark = Color(0xFFE4E1E9)
val surfaceDark = Color(0xFF16181F)
val onSurfaceDark = Color(0xFFE4E1E9)
val surfaceVariantDark = Color(0xFF46464F)
val onSurfaceVariantDark = Color(0xFFC6C5D0)
val outlineDark = Color(0xFF90909A)
val outlineVariantDark = Color(0xFF46464F)
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = Color(0xFFE4E1E9)
val inverseOnSurfaceDark = Color(0xFF303036)
val inversePrimaryDark = Color(0xFF515B92)
val surfaceDimDark = Color(0xFF121318)
val surfaceBrightDark = Color(0xFF39393F)
val surfaceContainerLowestDark = Color(0xFF0D0E13)
val surfaceContainerLowDark = Color(0xFF1B1B21)
val surfaceContainerDark = Color(0xFF1F1F25)
val surfaceContainerHighDark = Color(0xFF29292F)
val surfaceContainerHighestDark = Color(0xFF34343A)


private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)
