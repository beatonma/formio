package org.beatonma.gclocks.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.beatonma.gclocks.app.theme.AppTheme
import org.beatonma.gclocks.compose.ComposePath
import org.beatonma.gclocks.compose.VerticalBottomContentPadding
import org.beatonma.gclocks.compose.animation.EnterVertical
import org.beatonma.gclocks.compose.animation.ExitVertical
import org.beatonma.gclocks.compose.components.Clock
import org.beatonma.gclocks.compose.plus
import org.beatonma.gclocks.core.glyph.ClockGlyph
import org.beatonma.gclocks.core.glyph.GlyphRole
import org.beatonma.gclocks.core.glyph.GlyphState
import org.beatonma.gclocks.core.glyph.GlyphVisibility
import org.beatonma.gclocks.core.options.AnyOptions
import org.beatonma.gclocks.core.types.ProgressFloat
import org.beatonma.gclocks.core.util.TimeOfDay
import org.beatonma.gclocks.core.util.getCurrentTimeMillis
import org.beatonma.gclocks.core.util.getInstant
import org.beatonma.gclocks.core.util.interpolate
import org.beatonma.gclocks.core.util.nextSecond
import org.beatonma.gclocks.core.util.timeOfDay
import org.beatonma.gclocks.core.util.withTimeOfDay
import org.beatonma.gclocks.form.FormGlyph
import org.beatonma.gclocks.form.FormOptions
import org.beatonma.gclocks.form.FormPaints
import org.beatonma.gclocks.io16.Io16Glyph
import org.beatonma.gclocks.io16.Io16GlyphRenderer
import org.beatonma.gclocks.io16.Io16Options
import org.beatonma.gclocks.io16.Io16Paints
import org.beatonma.gclocks.io18.GlyphAnimations
import org.beatonma.gclocks.io18.Io18Glyph
import org.beatonma.gclocks.io18.Io18Options
import org.beatonma.gclocks.io18.Io18Paints
import kotlin.enums.enumEntries
import kotlin.math.roundToInt
import kotlin.time.Instant
import androidx.compose.ui.graphics.Color as ComposeColor


private val ItemModifier = Modifier.background(ComposeColor.Black)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugApp() {
    val keys = remember { ClockGlyph.Key.entries.map { it.key } }.filter { it.length > 1 }
    var animationPosition by remember { mutableStateOf<Float?>(null) }
    var timeFunc: () -> Instant by remember { mutableStateOf(::getInstant) }
    var state: GlyphState? by remember { mutableStateOf(null) }
    var visibility: GlyphVisibility? by remember { mutableStateOf(GlyphVisibility.Visible) }

    AppTheme {
        Box(Modifier.fillMaxSize()) {
            LazyVerticalGrid(
                GridCells.Adaptive(220.dp),
                modifier = Modifier.background(ComposeColor.DarkGray),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = WindowInsets.systemBars.asPaddingValues() + VerticalBottomContentPadding,
            ) {
                clockPreview(FormOptions(), timeFunc, state, visibility)
                clockPreview(Io16Options(), timeFunc, state, visibility)
                clockPreview(Io18Options(), timeFunc, state, visibility)

                lineBreak()

                FormGlyphs(keys, animationPosition, state, visibility)
//                Io16Glyphs(keys, animationPosition, state)
//                Io18Glyphs(keys, animationPosition, state)
            }

            Controls(
                state,
                { state = it },
                visibility,
                { visibility = it },
                animationPosition,
                { animationPosition = it },
                { timeFunc = it },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .widthIn(max = 400.dp)
            )
        }

    }
}

@Composable
private fun Controls(
    state: GlyphState?,
    setState: (GlyphState?) -> Unit,
    visibility: GlyphVisibility?,
    setVisibility: (GlyphVisibility?) -> Unit,
    animationPosition: Float?,
    setAnimationPosition: (Float?) -> Unit,
    setTimeFunc: (() -> Instant) -> Unit,
    modifier: Modifier = Modifier,
) {
    var controlsVisible by remember { mutableStateOf(true) }
    var customTimeStr by remember { mutableStateOf("") }

    LaunchedEffect(customTimeStr, animationPosition) {
        setTimeFunc(
            {
                val instant = parseTime(
                    customTimeStr,
                    interpolate(animationPosition ?: 0f, 0f, 1000f).roundToInt()
                )

                animationPosition?.let {
                    customTimeStr = instant.timeOfDay.toString()
                }

                instant
            }
        )
    }

    Surface(modifier) {
        Column(
            Modifier
                .padding(16.dp)
                .systemBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextButton(
                { controlsVisible = !controlsVisible },
                Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Controls")
            }

            AnimatedVisibility(controlsVisible, enter = EnterVertical, exit = ExitVertical) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row {
                        Dropdown<GlyphState>("State", state, setState)
                        Dropdown<GlyphVisibility>("Visibility", visibility, setVisibility)
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                            onCheckedChange = { setAnimationPosition(if (it) null else 0f) }
                        )
                        Text(
                            when (animationPosition) {
                                null -> "Play"
                                else -> "%.2f".format(animationPosition)
                            }
                        )
                    }
                    Slider(
                        animationPosition ?: 0f,
                        setAnimationPosition,
                        valueRange = 0f..0.999f,
                        steps = 999,
                    )
                }
            }
        }
    }
}


private fun <O : AnyOptions> LazyGridScope.clockPreview(
    options: O,
    timeFunc: () -> Instant,
    forcedState: GlyphState?,
    forcedVisibility: GlyphVisibility?,
) {
    item(span = { GridItemSpan(2) }) {
        Clock(
            options,
            ItemModifier.fillMaxSize(),
            getInstant = timeFunc,
            forcedState = forcedState,
            visibility = forcedVisibility,
        )
    }
}


private fun LazyGridScope.FormGlyphs(
    keys: List<String>,
    animationPosition: Float?,
    state: GlyphState?,
    visibility: GlyphVisibility?
) {
    items(
        keys,
        key = { key -> "form_$key" },
    ) { key ->
        GlyphPreview(
            remember(state, visibility) {
                FormGlyph(GlyphRole.Hour, lock = state).apply {
                    setKey(key, force = true)
                    visibility?.let {
                        setState(
                            it,
                            force = true,
                            currentTimeMillis = getCurrentTimeMillis()
                        )
                    }
                        ?: setState(
                            GlyphVisibility.Visible,
                            currentTimeMillis = getCurrentTimeMillis()
                        )
                }
            },
            remember { FormPaints() },
            ItemModifier.fillMaxSize(),
            animPosition = animationPosition,
        )
    }
}

private fun LazyGridScope.Io16Glyphs(
    keys: List<String>,
    animationPosition: Float?,
    state: GlyphState?,
    visibility: GlyphVisibility?
) {
    items(
        keys,
        key = { key -> "io16_$key" },
    ) { key ->
        GlyphPreview(
            remember(state) {
                Io16Glyph(
                    GlyphRole.Hour,
                    animationOffset = ProgressFloat.Zero,
                    lock = state
                ).apply {
                    setKey(key, force = true)
                    visibility?.let {
                        setState(
                            it,
                            force = true,
                            currentTimeMillis = getCurrentTimeMillis()
                        )
                    }
                        ?: setState(
                            GlyphVisibility.Visible,
                            currentTimeMillis = getCurrentTimeMillis()
                        )
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
    state: GlyphState?,
    visibility: GlyphVisibility?
) {
    items(
        keys,
        key = { key -> "io18_$key" },
    ) { key ->
        val io18Animations = rememberIo18Animations()
        GlyphPreview(
            remember(state) {
                Io18Glyph(io18Animations, GlyphRole.Default, lock = state).apply {
                    setKey(key, force = true)
                    visibility?.let {
                        setState(
                            it,
                            force = true,
                            currentTimeMillis = getCurrentTimeMillis()
                        )
                    }
                        ?: setState(
                            GlyphVisibility.Visible,
                            currentTimeMillis = getCurrentTimeMillis()
                        )
                }
            },
            remember { Io18Paints() },
            ItemModifier.fillMaxSize(),
            animPosition = animationPosition,
        )
    }
}

private fun parseTime(str: String, millis: Int?): Instant {
    val instant = getInstant()
    try {
        Regex("(\\d{1,2}):?(\\d{2})?:?(\\d{2})?").find(str)?.let {
            val h = it.groups[1]?.value?.toInt() ?: 0
            val m = it.groups[2]?.value?.toInt() ?: 0
            val s = it.groups[3]?.value?.toInt() ?: 0

            millis?.let { millis ->
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
            }
            return instant.withTimeOfDay(TimeOfDay(h, m, s).nextSecond())
        }
    } catch (e: IllegalArgumentException) {
        // Invalid time string
    }
    return instant
}

@Composable
private inline fun <reified E : Enum<E>> Dropdown(
    defaultText: String,
    selected: E?,
    crossinline onSelect: (E?) -> Unit,
) {
    var isDropdownExtended by remember { mutableStateOf(false) }

    Box {
        TextButton(onClick = { isDropdownExtended = !isDropdownExtended }) {
            Text(selected?.name ?: defaultText)
        }
        DropdownMenu(isDropdownExtended, { isDropdownExtended = false }) {
            DropdownMenuItem({ Text("Reset") }, onClick = { onSelect(null) })
            enumEntries<E>().forEach { entry ->
                DropdownMenuItem({ Text(entry.name) }, onClick = { onSelect(entry) })
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


private fun LazyGridScope.lineBreak() {
    item(span = { GridItemSpan(maxLineSpan) }) {

    }
}
