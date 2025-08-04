package org.beatonma.gclocks.app

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.beatonma.gclocks.compose.Clock
import org.beatonma.gclocks.compose.GlyphPreview
import org.beatonma.gclocks.compose.toCompose
import org.beatonma.gclocks.core.Glyph
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
            "1_2",
            "0_1",
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
        )
    }

    MaterialTheme {
        LazyVerticalGrid(
            GridCells.Adaptive(minSize = 160.dp),
            modifier = Modifier.fillMaxSize().safeDrawingPadding()
        ) {
            itemsIndexed(
                keys,
                key = { index, key -> key },
            ) { index, key ->
                Box(Modifier.fillMaxSize().border(2.dp, Color.Blue)) {
                    GlyphPreview(
                        FormGlyph(GlyphRole.Hour).apply {
                            this.key = key
                        },
                        paints,
                        Modifier.fillMaxSize()
                    )
                }
            }
        }

//        Column(Modifier.fillMaxSize().safeDrawingPadding()) {
//            Clock(Modifier.fillMaxSize())
//        }
    }
}