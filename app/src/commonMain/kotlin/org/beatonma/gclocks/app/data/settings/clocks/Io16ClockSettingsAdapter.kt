package org.beatonma.gclocks.app.data.settings.clocks

import gclocks_multiplatform.app.generated.resources.Res
import gclocks_multiplatform.app.generated.resources.setting_stroke_width
import org.beatonma.gclocks.app.data.settings.ClockSettingsAdapter
import org.beatonma.gclocks.app.ui.LocalizedString
import org.beatonma.gclocks.compose.components.settings.data.Key
import org.beatonma.gclocks.compose.components.settings.data.RichSetting
import org.beatonma.gclocks.compose.components.settings.data.RichSettings
import org.beatonma.gclocks.compose.components.settings.data.Setting
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.io16.Io16LayoutOptions
import org.beatonma.gclocks.io16.Io16Options
import org.beatonma.gclocks.io16.Io16Paints


interface Io16ClockSettingsAdapter : ClockSettingsAdapter<Io16Options> {
    override fun addClockSettings(
        richSettings: RichSettings,
        options: Io16Options,
        updateOptions: (Io16Options) -> Unit
    ): RichSettings {
        val updateLayout: (Io16LayoutOptions) -> Unit = {
            updateOptions(options.copy(layout = it))
        }
        return richSettings.append(
            colors = buildColorSettings(options.paints) {
                updateOptions(options.copy(paints = it))
            },
            layout = buildLayoutSettings(options.layout, updateLayout),
            time = buildTimeSettings(options.layout, updateLayout),
            sizes = buildSizeSettings(options, updateOptions)
        )
    }
}


private fun buildColorSettings(paints: Io16Paints, onUpdate: (Io16Paints) -> Unit): List<Setting> =
    listOf(
        chooseClockColors(paints) { colors ->
            onUpdate(paints.copy(colors = colors.toTypedArray()))
        }
    )

private fun buildLayoutSettings(
    layoutOptions: Io16LayoutOptions,
    onUpdate: (Io16LayoutOptions) -> Unit,
): List<Setting> = listOf(
    chooseLayout(layoutOptions.layout) {
        onUpdate(layoutOptions.copy(layout = it))
    },
    chooseHorizontalAlignment(layoutOptions.horizontalAlignment) {
        onUpdate(layoutOptions.copy(horizontalAlignment = it))
    },
    chooseVerticalAlignment(layoutOptions.verticalAlignment) {
        onUpdate(layoutOptions.copy(verticalAlignment = it))
    },
)

private fun buildTimeSettings(
    layoutOptions: Io16LayoutOptions,
    onUpdate: (Io16LayoutOptions) -> Unit,
): List<Setting> = chooseTimeFormat(layoutOptions.format) { format ->
    // If seconds are not visible, revert Layout.Wrapped to Layout.Horizontal
    val layout = if (!format.showSeconds && layoutOptions.layout == Layout.Wrapped) {
        Layout.Horizontal
    } else layoutOptions.layout

    onUpdate(layoutOptions.copy(layout = layout, format = format))
}


private val StrokeWidthKey = Key.FloatKey("stroke_width")
private fun buildSizeSettings(
    options: Io16Options,
    updateOptions: (Io16Options) -> Unit,
): List<Setting> = listOf(
    chooseSpacing(
        options.layout.spacingPx,
        { updateOptions(options.copy(layout = options.layout.copy(spacingPx = it))) },
        default = 8,
        max = 64,
    ),
    chooseSecondScale(options.layout.secondsGlyphScale) {
        updateOptions(options.copy(layout = options.layout.copy(secondsGlyphScale = it)))
    },
    RichSetting.Float(
        key = StrokeWidthKey,
        localized = LocalizedString(Res.string.setting_stroke_width),
        value = options.paints.strokeWidth,
        default = 2f,
        min = 0f,
        max = 16f,
        stepSize = 1f,
        onValueChange = { updateOptions(options.copy(paints = options.paints.copy(strokeWidth = it))) }
    )
)
