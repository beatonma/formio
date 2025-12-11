package org.beatonma.gclocks.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.unit.dp
import gclocks_multiplatform.app.generated.resources.Res
import gclocks_multiplatform.app.generated.resources.about_app_markdown
import gclocks_multiplatform.app.generated.resources.about_form_markdown
import gclocks_multiplatform.app.generated.resources.about_io16_markdown
import gclocks_multiplatform.app.generated.resources.about_io18_markdown
import gclocks_multiplatform.app.generated.resources.about_io_markdown
import gclocks_multiplatform.app.generated.resources.app_name
import org.beatonma.gclocks.app.theme.ClockColorScheme
import org.beatonma.gclocks.compose.VerticalBottomContentPadding
import org.beatonma.gclocks.compose.animation.AnimatedFade
import org.beatonma.gclocks.compose.components.Clock
import org.beatonma.gclocks.compose.components.MarkdownText
import org.beatonma.gclocks.compose.components.appBarVisibility
import org.beatonma.gclocks.compose.plus
import org.beatonma.gclocks.core.geometry.HorizontalAlignment
import org.beatonma.gclocks.core.geometry.VerticalAlignment
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.core.options.LayoutOptions
import org.beatonma.gclocks.core.options.TimeFormat
import org.beatonma.gclocks.form.FormOptions
import org.beatonma.gclocks.io16.Io16Options
import org.beatonma.gclocks.io18.Io18Options
import org.jetbrains.compose.resources.stringResource


private fun clockPreviewModifier(color: Color) =
    Modifier
        .composed { clip(shapes.medium) }
        .background(color)
        .padding(32.dp)

private val CardContentModifier = Modifier.padding(16.dp)

@Composable
fun AboutScreen(navigationIcon: @Composable () -> Unit) {
    val gridState = rememberLazyStaggeredGridState()
    val appBarVisibility = appBarVisibility(gridState)

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { AnimatedFade(!appBarVisibility.isTransparent) { Text(stringResource(Res.string.app_name)) } },
                navigationIcon = navigationIcon,
                colors = TopAppBarDefaults.topAppBarColors(containerColor = appBarVisibility.color),
            )
        }
    ) { insets ->
        val itemSpacing = 16.dp
        LazyVerticalStaggeredGrid(
            StaggeredGridCells.Adaptive(minSize = 300.dp),
            Modifier.consumeWindowInsets(insets).consumeWindowInsets(WindowInsets.safeDrawing),
            state = gridState,
            contentPadding = insets + PaddingValues(16.dp) + VerticalBottomContentPadding,
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
    OutlinedCard(modifier.widthIn(max = 600.dp)) {
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
    Card(modifier.widthIn(max = 600.dp), colors = colors) {
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
