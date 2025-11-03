package org.beatonma.gclocks.app

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.beatonma.gclocks.compose.ComposePath
import org.beatonma.gclocks.compose.VerticalBottomContentPadding
import org.beatonma.gclocks.compose.components.Clock
import org.beatonma.gclocks.compose.plus
import org.beatonma.gclocks.core.ClockGlyph
import org.beatonma.gclocks.core.GlyphRole
import org.beatonma.gclocks.core.GlyphState
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.core.types.ProgressFloat
import org.beatonma.gclocks.core.util.TimeOfDay
import org.beatonma.gclocks.core.util.getInstant
import org.beatonma.gclocks.core.util.interpolate
import org.beatonma.gclocks.core.util.nextSecond
import org.beatonma.gclocks.core.util.timeOfDay
import org.beatonma.gclocks.core.util.withTimeOfDay
import org.beatonma.gclocks.form.FormGlyph
import org.beatonma.gclocks.form.FormPaints
import org.beatonma.gclocks.io16.Io16Glyph
import org.beatonma.gclocks.io16.Io16GlyphRenderer
import org.beatonma.gclocks.io16.Io16Options
import org.beatonma.gclocks.io16.Io16Paints
import org.beatonma.gclocks.io18.GlyphAnimations
import org.beatonma.gclocks.io18.Io18Glyph
import org.beatonma.gclocks.io18.Io18Paints
import kotlin.math.roundToInt
import kotlin.time.Instant


private val ItemModifier = Modifier.border(1.dp, Color.Black.copy(alpha = 0.33f))


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugApp() {
    val keys = remember { ClockGlyph.Key.entries.map { it.key } }
    var animationPosition by remember { mutableStateOf<Float?>(null) }
    var customTimeStr by remember { mutableStateOf("") }
    val timeFunc: () -> Instant by remember {
        mutableStateOf({
            parseTime(
                customTimeStr,
                interpolate(animationPosition ?: 0f, 0f, 1000f).roundToInt()
            )
        })
    }
    var state: GlyphState? by remember { mutableStateOf(null) }

    MaterialTheme {
        Box(Modifier.background(Color.LightGray)) {
            LazyVerticalGrid(
                GridCells.Adaptive(220.dp),
                modifier = Modifier.fillMaxSize()
                    .background(Color.DarkGray),
                contentPadding = WindowInsets.systemBars.asPaddingValues() + VerticalBottomContentPadding
            ) {
//                clockPreview(
//                    FormOptions(
//                        layout = FormLayoutOptions(
//                            format = TimeFormat.hh_MM_SS_12,
//                            verticalAlignment = VerticalAlignment.Bottom
//                        )
//                    ), timeFunc, state
//                )
//                clockPreview(
//                    Io16Options(
//                        layout = Io16LayoutOptions(
//                            format = TimeFormat.hh_MM_SS_12
//                        )
//                    ), timeFunc, state
//                )
//                clockPreview(
//                    Io18Options(
//                        layout = Io18LayoutOptions(
//                            format = TimeFormat.hh_MM_SS_12
//                        )
//                    ), timeFunc, state
//                )

                FormGlyphs(keys, animationPosition)
                Io16Glyphs(keys, animationPosition)
                Io18Glyphs(keys, animationPosition)
            }

            Column(
                Modifier.align(Alignment.BottomCenter)
                    .background(colorScheme.surface)
                    .padding(8.dp)
                    .systemBarsPadding()
            ) {
                // Dev tools
                StateDropdown(state, { state = it })

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        customTimeStr,
                        { customTimeStr = it },
                        placeholder = { Text("Custom Time") }
                    )

                    FastForward {
                        customTimeStr =
                            parseTime(
                                customTimeStr,
                                interpolate(animationPosition ?: 0f, 0f, 1000f).roundToInt()
                            ).timeOfDay.nextSecond().toString()
                    }
                }

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


private fun <O : Options<*>> LazyGridScope.clockPreview(
    options: O,
    timeFunc: () -> Instant,
    forcedState: GlyphState? = null,
) {
    item(span = { GridItemSpan(maxLineSpan) }) {
        Clock(
            options,
            ItemModifier.fillMaxSize(),
            getInstant = timeFunc,
            forcedState = forcedState,
        )
    }
}


private fun LazyGridScope.FormGlyphs(
    keys: List<String>,
    animationPosition: Float?,
    state: GlyphState?,
) {
    items(
        keys,
        key = { key -> "form_$key" },
    ) { key ->
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

private fun LazyGridScope.Io16Glyphs(
    keys: List<String>,
    animationPosition: Float?,
) {
    items(
        keys,
        key = { key -> "io16_$key" },
    ) { key ->
        GlyphPreview(
            remember {
                Io16Glyph(GlyphRole.Hour, animationOffset = ProgressFloat.Zero).apply {
                    this.key = key
                }
            },
            remember { Io16Paints() },
            ItemModifier.fillMaxSize(),
            renderer = remember {
                Io16GlyphRenderer(
                    ComposePath(),
                    Io16Options(),
                )
            },
            animPosition = animationPosition,
        )
    }
}


@Composable
private fun rememberIo18Animations() = remember { GlyphAnimations(ComposePath()) }
private fun LazyGridScope.Io18Glyphs(
    keys: List<String>,
    animationPosition: Float?,
) {
    items(
        keys,
        key = { key -> "io18_$key" },
    ) { key ->
        val io18Animations = rememberIo18Animations()
        GlyphPreview(
            remember {
                Io18Glyph(io18Animations, GlyphRole.Default).apply {
                    this.key = key
                }
            },
            remember { Io18Paints() },
            ItemModifier.fillMaxSize(),
            animPosition = animationPosition,
        )
    }
}

private fun parseTime(str: String, millis: Int): Instant {
    val instant = getInstant()
    try {
        Regex("(\\d{1,2}):?(\\d{2})?:?(\\d{2})?").find(str)?.let {
            val h = it.groups[1]?.value?.toInt() ?: 0
            val m = it.groups[2]?.value?.toInt() ?: 0
            val s = it.groups[3]?.value?.toInt() ?: 0

            if (millis < 1000) {
                return instant.withTimeOfDay(
                    TimeOfDay(
                        hour = h,
                        minute = m,
                        second = s,
                        millisecond = millis,
                    )
                )
            }
            return instant.withTimeOfDay(TimeOfDay(h, m, s).nextSecond())
        }
    } catch (e: IllegalArgumentException) {
        // Invalid time string
    }
    return instant
}


@Composable
private fun StateDropdown(
    selected: GlyphState?,
    onSelect: (GlyphState?) -> Unit,
) {
    var isDropdownExtended by remember { mutableStateOf(false) }

    Box {
        TextButton(onClick = { isDropdownExtended = !isDropdownExtended }) {
            Text(selected?.name ?: "State override")
        }
        DropdownMenu(isDropdownExtended, { isDropdownExtended = false }) {
            DropdownMenuItem({ Text("Reset") }, onClick = { onSelect(null) })
            GlyphState.entries.forEach {
                DropdownMenuItem({ Text(it.name) }, onClick = { onSelect(it) })
            }
        }
    }
}

@Composable
private fun FastForward(block: () -> Unit) {
    val scope = rememberCoroutineScope()
    var enabled by remember { mutableStateOf(false) }
    var job: Job? = remember { null }

    DisposableEffect(enabled) {
        if (enabled) {
            job = scope.launch {
                while (true) {
                    withFrameMillis {
                        block()
                    }
                }
            }
        } else {
            job?.cancel()
        }

        onDispose {
            job?.cancel()
        }
    }

    Button(
        { enabled = !enabled },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) colorScheme.primary else colorScheme.surfaceVariant,
            contentColor = if (enabled) colorScheme.onPrimary else colorScheme.onSurfaceVariant
        )
    ) {
        Icon(Icons.Default.FastForward, "")
    }
}
