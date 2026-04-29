package org.beatonma.formio.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import formio.app.generated.resources.Res
import formio.app.generated.resources.about_app_markdown
import formio.app.generated.resources.about_form_markdown
import formio.app.generated.resources.about_io16_markdown
import formio.app.generated.resources.about_io18_markdown
import formio.app.generated.resources.about_io_markdown
import org.beatonma.formio.app.ui.animation.AnimatedFade
import org.beatonma.formio.app.ui.components.Clock
import org.beatonma.formio.app.ui.components.MarkdownText
import org.beatonma.formio.app.ui.components.appBarVisibility
import org.beatonma.formio.app.ui.theme.ClockColorScheme
import org.beatonma.formio.app.ui.theme.tokens.CardTokens
import org.beatonma.formio.app.ui.theme.tokens.ColumnTokens
import org.beatonma.formio.app.ui.theme.tokens.WindowTokens
import org.beatonma.formio.app.ui.util.VerticalBottomContentPadding
import org.beatonma.formio.app.ui.util.plus
import org.beatonma.formio.core.Build
import org.beatonma.formio.core.geometry.HorizontalAlignment
import org.beatonma.formio.core.geometry.VerticalAlignment
import org.beatonma.formio.core.options.Layout
import org.beatonma.formio.core.options.LayoutOptions
import org.beatonma.formio.core.options.TimeFormat
import org.beatonma.formio.form.FormOptions
import org.beatonma.formio.io16.Io16Options
import org.beatonma.formio.io18.Io18Options
import org.jetbrains.compose.resources.stringResource


private fun clockPreviewModifier(color: Color) =
    Modifier
        .composed { clip(shapes.medium) }
        .background(color)
        .padding(WindowTokens.ContentPadding * 2)

private val CardContentModifier = Modifier.padding(CardTokens.ContentPadding)

@Composable
fun AboutScreen(navigationIcon: @Composable (() -> Unit)?) {
    val gridState = rememberLazyStaggeredGridState()
    val appBarVisibility = appBarVisibility(gridState)

    Scaffold(
        topBar = {
            navigationIcon?.let { navigationIcon ->
                @OptIn(ExperimentalMaterial3Api::class)
                TopAppBar(
                    title = { AnimatedFade(!appBarVisibility.isTransparent) { Text(Build.AppName) } },
                    navigationIcon = navigationIcon,
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = appBarVisibility.color),
                )
            }
        }
    ) { insets ->
        val itemSpacing = CardTokens.BetweenCardsPadding
        LazyVerticalStaggeredGrid(
            StaggeredGridCells.Adaptive(minSize = ColumnTokens.PreferredMinWidth),
            Modifier.consumeWindowInsets(insets).consumeWindowInsets(WindowInsets.safeDrawing),
            state = gridState,
            contentPadding = insets + WindowTokens.ContentPaddingValues + VerticalBottomContentPadding,
            horizontalArrangement = Arrangement.spacedBy(itemSpacing),
            verticalItemSpacing = itemSpacing,
        ) {
            item { AboutApp() }
            item { AboutForm() }
            item { AboutIo() }
            item { AboutIo16() }
            item { AboutIo18() }
        }
    }
}

@Composable
private fun AboutApp(modifier: Modifier = Modifier) {
    Box(modifier) {
        MarkdownText(stringResource(Res.string.about_app_markdown))
    }
}

@Composable
private fun AboutForm(modifier: Modifier = Modifier) {
    AboutCard(
        stringResource(Res.string.about_form_markdown),
        modifier,
        colors = ClockColorScheme.Form.cardColors
    ) {
        Clock(
            FormOptions(
                layout = previewLayoutOptions()
            ),
            clockPreviewModifier(ClockColorScheme.Form.backgroundColor)
        )
    }
}

@Composable
private fun AboutIo(modifier: Modifier = Modifier) {
    OutlinedCard(modifier) {
        MarkdownText(stringResource(Res.string.about_io_markdown), CardContentModifier)
    }
}

@Composable
private fun AboutIo16(modifier: Modifier = Modifier) {
    AboutCard(
        stringResource(Res.string.about_io16_markdown),
        modifier,
        colors = ClockColorScheme.Io16.cardColors
    ) {
        Clock(
            Io16Options(
                layout = previewLayoutOptions()
            ),
            clockPreviewModifier(ClockColorScheme.Io16.backgroundColor)
        )
    }
}

@Composable
private fun AboutIo18(modifier: Modifier = Modifier) {
    AboutCard(
        stringResource(Res.string.about_io18_markdown),
        modifier,
        colors = ClockColorScheme.Io18.cardColors
    ) {
        Clock(
            Io18Options(
                layout = previewLayoutOptions()
            ),
            clockPreviewModifier(ClockColorScheme.Io18.backgroundColor)
        )
    }
}


@Composable
private fun AboutCard(
    rawMarkdown: String,
    modifier: Modifier = Modifier,
    colors: CardColors = CardDefaults.cardColors(),
    header: (@Composable () -> Unit)? = null,
) {
    Card(modifier, colors = colors) {
        header?.invoke()
        MarkdownText(rawMarkdown, CardContentModifier)
    }
}


private fun previewLayoutOptions() = LayoutOptions(
    Layout.Wrapped,
    TimeFormat.build(is24Hour = true, isZeroPadded = false, showSeconds = true),
    horizontalAlignment = HorizontalAlignment.End,
    verticalAlignment = VerticalAlignment.Top,
    spacingPx = 8,
    secondsGlyphScale = 0.5f,
)
