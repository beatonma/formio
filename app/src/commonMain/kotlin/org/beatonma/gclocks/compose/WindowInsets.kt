package org.beatonma.gclocks.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.mandatorySystemGestures
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeGestures
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemGestures
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsEndWidth
import androidx.compose.foundation.layout.windowInsetsStartWidth
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection


@Composable
fun WindowInsetsOverlay(
    color: Color = Color.Yellow,
    modifier: Modifier = Modifier
) {
    val values =
        mapOf(
            "safeDrawing" to WindowInsets.safeDrawing,
            "systemBars" to WindowInsets.systemBars,
            "displayCutout" to WindowInsets.displayCutout,
            "mandatorySystemGestures" to WindowInsets.mandatorySystemGestures,
            "systemGestures" to WindowInsets.systemGestures,
            "safeGestures" to WindowInsets.safeGestures,
            "safeContent" to WindowInsets.safeContent,
//            "ime" to WindowInsets.ime,
//            "waterfall" to WindowInsets.waterfall,
//            "captionBar" to WindowInsets.captionBar,
        )
    var index by rememberSaveable { mutableIntStateOf(0) }
    val (name, insets) = values.entries.toList()[index]

    WindowInsetsOverlay(insets, color, modifier) {
        OutlinedButton(
            { index = (index + 1) % values.size },
            Modifier.align(Alignment.Center),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = colorScheme.scrim)
        ) {
            Text(name, style = typography.bodyLarge)
        }
    }
}

@Composable
fun WindowInsetsOverlay(
    insets: WindowInsets,
    color: Color = Color.Yellow.copy(alpha = 0.5f),
    modifier: Modifier = Modifier,
    content: (@Composable BoxScope.() -> Unit)? = null,
) {
    val spacerModifier = Modifier.background(color)
    Box(modifier.fillMaxSize()/*.background(colorScheme.scrim)*/) {
        Spacer(
            spacerModifier.windowInsetsStartWidth(insets).align(Alignment.TopStart).fillMaxHeight()
        )
        Spacer(
            spacerModifier.windowInsetsTopHeight(insets).align(Alignment.TopStart).fillMaxWidth()
        )
        Spacer(spacerModifier.windowInsetsEndWidth(insets).align(Alignment.TopEnd).fillMaxHeight())
        Spacer(
            spacerModifier.windowInsetsBottomHeight(insets).align(Alignment.BottomStart)
                .fillMaxWidth()
        )
        content?.invoke(this)
    }
}


@Composable
fun PaddingOverlay(
    padding: PaddingValues,
    color: Color = Color.Cyan.copy(alpha = 0.5f),
    modifier: Modifier = Modifier,
    content: (@Composable BoxScope.() -> Unit)? = null,
) {
    val spacerModifier = Modifier.background(color)
    val layoutDirection = LocalLayoutDirection.current
    Box(modifier.fillMaxSize()) {
        Spacer(
            spacerModifier.width(padding.calculateStartPadding(layoutDirection))
                .align(Alignment.TopStart).fillMaxHeight()
        )
        Spacer(
            spacerModifier.height(padding.calculateTopPadding()).align(Alignment.TopStart)
                .fillMaxWidth()
        )
        Spacer(
            spacerModifier.width(padding.calculateEndPadding(layoutDirection))
                .align(Alignment.TopEnd).fillMaxHeight()
        )
        Spacer(
            spacerModifier.height(padding.calculateBottomPadding()).align(Alignment.BottomStart)
                .fillMaxWidth()
        )
        content?.invoke(this)
    }
}
