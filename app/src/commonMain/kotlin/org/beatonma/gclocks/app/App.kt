package org.beatonma.gclocks.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.beatonma.gclocks.compose.GlyphPreview
import org.beatonma.gclocks.core.GlyphRole
import org.beatonma.gclocks.form.FormGlyph
import org.beatonma.gclocks.form.FormPaints
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val paints = FormPaints()
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
        )
    }
    val colors = remember {
        listOf(
            Color.DarkGray,
            Color.LightGray,
            Color.LightGray,
            Color.DarkGray,
        )
    }
    MaterialTheme {
//        Column(Modifier.fillMaxSize().safeDrawingPadding()) {
//            Clock(Modifier.fillMaxSize())
//        }
        LazyVerticalGrid(
            GridCells.Adaptive(minSize = 160.dp),
            modifier = Modifier.fillMaxSize()
                .background(Color.DarkGray),
            contentPadding = WindowInsets.systemBars.asPaddingValues()
        ) {
//            item(span = { GridItemSpan(maxLineSpan) }) {
//                Clock(Modifier.fillMaxWidth().aspectRatio(16f / 9f))
//            }

            itemsIndexed(
                keys,
                key = { index, key -> key },
            ) { index, key ->
                GlyphPreview(
                    FormGlyph(GlyphRole.Hour).apply {
                        this.key = key
                    },
                    paints,
                    Modifier
                        .background(colors[index % colors.size])
                        .fillMaxSize()
                )
            }
        }
    }
}