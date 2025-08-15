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
    override val strokeWidth: Float = 2f,
    override val activeStateDurationMillis: Int = 5000,
    override val stateChangeDurationMillis: Int = 1200,

    // How long a path segment takes to complete a circuit
    val colorCycleDurationMillis: Int = 5000,

    ) : Options