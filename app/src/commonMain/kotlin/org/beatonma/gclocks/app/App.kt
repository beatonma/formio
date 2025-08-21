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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import org.beatonma.gclocks.compose.FormConfig
import org.beatonma.gclocks.compose.GlyphPreview
import org.beatonma.gclocks.compose.Io16Config
import org.beatonma.gclocks.compose.Platform
import org.beatonma.gclocks.compose.platform
import org.beatonma.gclocks.core.GlyphRole
import org.beatonma.gclocks.core.util.TimeOfDay
import org.beatonma.gclocks.core.util.getTime
import org.beatonma.gclocks.core.util.interpolate
import org.beatonma.gclocks.core.util.timeOfDay
import org.beatonma.gclocks.form.FormGlyph
import org.beatonma.gclocks.form.FormOptions
import org.beatonma.gclocks.form.FormPaints
import org.beatonma.gclocks.io16.Io16Glyph
import org.beatonma.gclocks.io16.Io16GlyphRenderer
import org.beatonma.gclocks.io16.Io16Options
import org.beatonma.gclocks.io16.Io16Paints
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.roundToInt


private val ItemModifier = Modifier.border(1.dp, Color.Black.copy(alpha = 0.33f))


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
    var customTimeStr by remember { mutableStateOf("") }
    val timeFunc: () -> TimeOfDay by remember {
        mutableStateOf({
            parseTime(
                customTimeStr,
                interpolate(animationPosition ?: 0f, 0f, 1000f).roundToInt()
            )
        })
    }

    MaterialTheme {
        Box(Modifier.background(Color.LightGray)) {
            LazyVerticalGrid(
                GridCells.Adaptive(220.dp),
                modifier = Modifier.fillMaxSize()
                    .background(Color.DarkGray),
                contentPadding = when (platform) {
                    Platform.Android -> WindowInsets.systemBars.asPaddingValues() + PaddingValues(
                        bottom = 256.dp
                    )

                    else -> PaddingValues()
                }
            ) {
                item {
                    Clock(
                        FormConfig(
                            FormOptions(),
                            FormPaints()
                        ),
                        ItemModifier.fillMaxSize(),
                        getTickTime = timeFunc
                    )
                }

                item {
                    Clock(
                        Io16Config(
                            Io16Options(),
                            Io16Paints()
                        ),
                        ItemModifier.fillMaxSize(),
                        getTickTime = timeFunc
                    )
                }

                FormPreview(keys, animationPosition)
                Io16Preview(keys, animationPosition)
            }

            Column(
                Modifier.align(Alignment.BottomCenter)
                    .background(colorScheme.surface)
                    .padding(8.dp)
                    .systemBarsPadding()
            ) {
                TextField(customTimeStr, { customTimeStr = it })

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
        key = { index, key -> "form_$key" },
    ) { index, key ->
        GlyphPreview(
            FormGlyph(GlyphRole.Hour).apply {
                this.key = key
            },
            FormPaints(),
            ItemModifier.fillMaxSize(),
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
        key = { index, key -> "io16_$key" },
    ) { index, key ->
        GlyphPreview(
            Io16Glyph(GlyphRole.Hour).apply {
                this.key = key
            },
            Io16Paints(),
            Modifier.fillMaxSize()
                .border(1.dp, Color.Black.copy(alpha = 0.33f)),
            renderer = Io16GlyphRenderer(
                ComposePath(),
                Io16Options(),
                updateOnDraw = true
            ),
            animPosition = animationPosition,
        )
    }
}

private fun parseTime(str: String, millis: Int): TimeOfDay {
    Regex("(\\d{1,2}):?(\\d{2})?:?(\\d{2})?").find(str)?.let {
        val h = it.groups[1]?.value?.toInt() ?: 0
        val m = it.groups[2]?.value?.toInt() ?: 0
        val s = it.groups[3]?.value?.toInt() ?: 0

        return timeOfDay(
            hour = h,
            minute = m,
            second = s,
            millisecond = millis,
        )
    }
    return getTime()
}
