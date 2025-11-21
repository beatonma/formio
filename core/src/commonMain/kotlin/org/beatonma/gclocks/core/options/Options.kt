package org.beatonma.gclocks.core.options

import kotlinx.serialization.Serializable
import org.beatonma.gclocks.core.Clock
import org.beatonma.gclocks.core.geometry.HorizontalAlignment
import org.beatonma.gclocks.core.geometry.VerticalAlignment
import org.beatonma.gclocks.core.graphics.Paints


typealias AnyOptions = Options<*>

@Serializable
data class Options<G : GlyphOptions>(
    val clock: Clock,
    val paints: Paints,
    val layout: LayoutOptions,
    val glyph: G
) {
    companion object {
        const val DefaultSecondsGlyphScale = 0.5f
    }
}


@Serializable
data class LayoutOptions(
    val layout: Layout,
    val format: TimeFormat,
    val spacingPx: Int,
    val horizontalAlignment: HorizontalAlignment,
    val verticalAlignment: VerticalAlignment,
    /* Relative scale of glyphs with GlyphRole.Second */
    val secondsGlyphScale: Float,
) {
    /* Alignment of the ClockLayout within the available space given to it */
    val outerHorizontalAlignment: HorizontalAlignment get() = horizontalAlignment

    /* Alignment of the ClockLayout within the available space given to it */
    val outerVerticalAlignment: VerticalAlignment get() = verticalAlignment

    /* Alignment of glyphs within the ClockLayout */
    val innerHorizontalAlignment: HorizontalAlignment get() = horizontalAlignment

    /* Alignment of glyphs within the ClockLayout */
    val innerVerticalAlignment: VerticalAlignment get() = verticalAlignment
}

interface GlyphOptions {
    /** How long a glyph remains in the active state */
    val activeStateDurationMillis: Int

    /** How long the transition between [org.beatonma.gclocks.core.GlyphState.Active]/[org.beatonma.gclocks.core.GlyphState.Inactive] takes */
    val stateChangeDurationMillis: Int

    /** How long the transition between [org.beatonma.gclocks.core.GlyphVisibility.Visible]/[org.beatonma.gclocks.core.GlyphVisibility.Hidden] takes */
    val visibilityChangeDurationMillis: Int

    /** How long it takes to animate from one glyph to the next */
    val glyphMorphMillis: Int
}
