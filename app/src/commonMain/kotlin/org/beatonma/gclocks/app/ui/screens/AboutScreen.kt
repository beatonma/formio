package org.beatonma.gclocks.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
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
import org.beatonma.gclocks.app.theme.FormColorScheme
import org.beatonma.gclocks.app.theme.Io16ColorScheme
import org.beatonma.gclocks.app.theme.Io18ColorScheme
import org.beatonma.gclocks.clocks.layoutOptions
import org.beatonma.gclocks.compose.components.Clock
import org.beatonma.gclocks.compose.components.MarkdownText
import org.beatonma.gclocks.core.geometry.HorizontalAlignment
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.core.options.LayoutOptions
import org.beatonma.gclocks.core.options.TimeFormat
import org.beatonma.gclocks.form.FormLayoutOptions
import org.beatonma.gclocks.form.FormOptions
import org.beatonma.gclocks.io16.Io16LayoutOptions
import org.beatonma.gclocks.io16.Io16Options
import org.beatonma.gclocks.io18.Io18LayoutOptions
import org.beatonma.gclocks.io18.Io18Options
import org.jetbrains.compose.resources.stringResource


private fun clockPreviewModifier(color: Color) =
    Modifier
        .composed { clip(shapes.medium) }
        .background(color)
        .padding(32.dp)

private val CardContentModifier = Modifier.padding(16.dp)

@Composable
fun AboutScreen() {
    Scaffold {
        val itemSpacing = 16.dp
        LazyVerticalStaggeredGrid(
            StaggeredGridCells.Adaptive(minSize = 300.dp),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(itemSpacing),
            verticalItemSpacing = itemSpacing
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
    Box(modifier.padding(top = 96.dp)) {
        MarkdownText(stringResource(Res.string.about_app_markdown), CardContentModifier)
    }
}

@Composable
private fun AboutForm(modifier: Modifier = Modifier) {
    AboutCard(stringResource(Res.string.about_form_markdown), modifier) {
        Clock(
            FormOptions(
                layout = previewLayoutOptions<FormLayoutOptions>()
            ),
            clockPreviewModifier(FormColorScheme.backgroundColor)
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
    AboutCard(stringResource(Res.string.about_io16_markdown), modifier) {
        Clock(
            Io16Options(
                layout = previewLayoutOptions<Io16LayoutOptions>()
            ),
            clockPreviewModifier(Io16ColorScheme.backgroundColor)
        )
    }
}

@Composable
private fun AboutIo18(modifier: Modifier = Modifier) {
    AboutCard(stringResource(Res.string.about_io18_markdown), modifier) {
        Clock(
            Io18Options(
                layout = previewLayoutOptions<Io18LayoutOptions>()
            ),
            clockPreviewModifier(Io18ColorScheme.backgroundColor)
        )
    }
}


@Composable
private fun AboutCard(
    rawMarkdown: String,
    modifier: Modifier = Modifier,
    header: (@Composable () -> Unit)? = null,
) {
    Card(modifier.widthIn(max = 600.dp)) {
        header?.invoke()
        MarkdownText(rawMarkdown, CardContentModifier)
    }
}


private inline fun <reified T : LayoutOptions> previewLayoutOptions() = layoutOptions<T>(
    Layout.Wrapped,
    TimeFormat.build(is24Hour = true, isZeroPadded = false, showSeconds = true),
    horizontalAlignment = HorizontalAlignment.End,
)
