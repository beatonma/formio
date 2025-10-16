package org.beatonma.gclocks.app.data.settings.clocks

import org.beatonma.gclocks.app.data.settings.ClockSettingsAdapter
import org.beatonma.gclocks.compose.components.settings.data.RichSettings
import org.beatonma.gclocks.compose.components.settings.data.Setting
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.io18.Io18LayoutOptions
import org.beatonma.gclocks.io18.Io18Options
import org.beatonma.gclocks.io18.Io18Paints


interface Io18ClockSettingsAdapter : ClockSettingsAdapter<Io18Options> {
    override fun addClockSettings(
        richSettings: RichSettings,
        options: Io18Options,
        updateOptions: (Io18Options) -> Unit
    ): RichSettings {
        val updateLayout: (Io18LayoutOptions) -> Unit = {
            updateOptions(options.copy(layout = it))
        }
        return richSettings.append(
            colors = buildPaintsSettings(options.paints) {
                updateOptions(options.copy(paints = it))
            },
            layout = buildLayoutSettings(options.layout, updateLayout),
            time = buildTimeSettings(options.layout, updateLayout),
            sizes = buildSizeSettings(options.layout, updateLayout)
        )
    }
}

private fun buildPaintsSettings(
    paints: Io18Paints,
    onUpdate: (Io18Paints) -> Unit,
): List<Setting> =
    listOf(
        chooseClockColors(paints) { colors ->
            onUpdate(paints.copy(colors = colors.toTypedArray()))
        }
    )


private fun buildLayoutSettings(
    layoutOptions: Io18LayoutOptions,
    onUpdate: (Io18LayoutOptions) -> Unit,
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

private fun buildSizeSettings(
    layoutOptions: Io18LayoutOptions,
    onUpdate: (Io18LayoutOptions) -> Unit,
): List<Setting> = listOf(
    chooseSpacing(
        layoutOptions.spacingPx,
        { onUpdate(layoutOptions.copy(spacingPx = it)) },
        default = 8,
        max = 64,
    ),
    chooseSecondScale(layoutOptions.secondsGlyphScale) {
        onUpdate(layoutOptions.copy(secondsGlyphScale = it))
    }
)


private fun buildTimeSettings(
    layoutOptions: Io18LayoutOptions,
    onUpdate: (Io18LayoutOptions) -> Unit,
): List<Setting> = chooseTimeFormat(layoutOptions.format) { format ->
    // If seconds are not visible, revert Layout.Wrapped to Layout.Horizontal
    val layout = if (!format.showSeconds && layoutOptions.layout == Layout.Wrapped) {
        Layout.Horizontal
    } else layoutOptions.layout

    onUpdate(layoutOptions.copy(layout = layout, format = format))
}
