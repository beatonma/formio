package org.beatonma.gclocks.io16

import org.beatonma.gclocks.core.options.HorizontalAlignment
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.core.options.TimeFormat
import org.beatonma.gclocks.core.options.VerticalAlignment

data class Io16Options(
    override val horizontalAlignment: HorizontalAlignment = HorizontalAlignment.Default,
    override val verticalAlignment: VerticalAlignment = VerticalAlignment.Default,
    override val layout: Layout = Layout.Horizontal,
    override val format: TimeFormat = TimeFormat.HH_MM_SS_24,
    override val spacingPx: Int = 8,
    override val glyphMorphMillis: Int = 600,
    override val secondsGlyphScale: Float = Options.Companion.DefaultSecondsGlyphScale,

    // How long a path segment takes to complete a circuit
    val colorCycleDurationMillis: Int = 5000,

    // How long a glyph remains in the active state
    val activeStateDurationMillis: Int = 5000,

    // How long the transition between active/inactive takes
    val stateChangeDurationMillis: Int = 1200,

    val strokeWidth: Float = 2f,
) : Options