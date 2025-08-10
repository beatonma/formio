package org.beatonma.gclocks.app

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.beatonma.gclocks.compose.Clock
import org.beatonma.gclocks.compose.ComposePath
import org.beatonma.gclocks.compose.GlyphPreview
import org.beatonma.gclocks.core.GlyphRole
import org.beatonma.gclocks.core.util.debug
import org.beatonma.gclocks.form.FormGlyph
import org.beatonma.gclocks.form.FormPaints
import org.beatonma.gclocks.io16.Io16Glyph
import org.beatonma.gclocks.io16.Io16GlyphRenderer
import org.beatonma.gclocks.io16.Io16Options
import org.beatonma.gclocks.io16.Io16Paints
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    val keys = remember {
        listOf(
            "0_1",
            "1_2",
            "2_3",
            "3_4",
            "4_5",
            "5_6",
            "6_7",
            "7_8",
            "8_9",
            "9_0",
            "1_0",
            "2_0",
            "3_0",
            "5_0",
            "1_ ",
            "2_ ",
            " _1",
            ":",
            " ",
            "_",
            "#",
        )
    }
    var animationPosition by remember { mutableStateOf<Float?>(null) }

    MaterialTheme {
        Box {
            LazyVerticalGrid(
                GridCells.Adaptive(220.dp),
//                GridCells.Fixed(1),
                modifier = Modifier.fillMaxSize()
                    .background(Color.DarkGray),
                contentPadding = WindowInsets.systemBars.asPaddingValues() + PaddingValues(bottom = 64.dp)
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Clock(Modifier.fillMaxWidth().wrapContentHeight().aspectRatio(16f / 9f))
                }

//                FormPreview(keys, animationPosition)
                Io16Preview(keys, animationPosition)
            }

            Column(
                Modifier.align(Alignment.BottomCenter)
                    .background(colorScheme.surface)
                    .padding(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = animationPosition == null,
                        onCheckedChange = { animationPosition = if (it) null else 0f }
                    )
                    Text(
                        when (animationPosition) {
                            null -> ""
                            else -> "%.2f".format(animationPosition)
                        }
                    )
                }
                Slider(
                    animationPosition ?: 0f,
                    { animationPosition = it },
                    valueRange = 0f..1f,
                    steps = 100,
                )
            }
        }
    }
}


private fun LazyGridScope.FormPreview(
    keys: List<String>,
    animationPosition: Float?,
) {
    itemsIndexed(
        keys,
        key = { index, key -> key },
    ) { index, key ->
        GlyphPreview(
            FormGlyph(GlyphRole.Hour).apply {
                this.key = key
            },
            FormPaints(),
            Modifier.fillMaxSize()
                .border(1.dp, Color.Black.copy(alpha = 0.33f)),
            animPosition = animationPosition,
        )
    }
}

private fun LazyGridScope.Io16Preview(
    keys: List<String>,
    animationPosition: Float?,
) {
    itemsIndexed(
        keys,
        key = { index, key -> key },
    ) { index, key ->
        debug("GlyphPreview($key)")
        GlyphPreview(
            Io16Glyph(GlyphRole.Hour).apply {
                this.key = key
                debug("Io16Glyph")
            },
            Io16Paints().also { debug("Io16Paints") },
            Modifier.fillMaxSize()
                .border(1.dp, Color.Black.copy(alpha = 0.33f)),
            renderer = Io16GlyphRenderer(
                ComposePath(),
                Io16Options().also { debug("Io16Options") },
                updateOnDraw = true
            ),
            animPosition = animationPosition,
        )
    }
}