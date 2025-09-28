package org.beatonma.gclocks.app.data.settings.clocks

import org.beatonma.gclocks.app.data.settings.ClockSettingsAdapter
import org.beatonma.gclocks.compose.components.settings.data.RichSettings
import org.beatonma.gclocks.compose.components.settings.data.Setting
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.form.FormLayoutOptions
import org.beatonma.gclocks.form.FormOptions
import org.beatonma.gclocks.form.FormPaints

interface FormClockSettingsAdapter : ClockSettingsAdapter<FormOptions> {
    override fun addClockSettings(
        richSettings: RichSettings,
        options: FormOptions,
        updateOptions: (FormOptions) -> Unit
    ): RichSettings {
        val updateLayout: (FormLayoutOptions) -> Unit = {
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
    paints: FormPaints,
    onUpdate: (FormPaints) -> Unit,
): List<Setting> =
    listOf(
        createColorsAdapter(paints) { colors ->
            onUpdate(paints.copy(colors = colors.toTypedArray()))
        }
    )

private fun buildLayoutSettings(
    layoutOptions: FormLayoutOptions,
    onUpdate: (FormLayoutOptions) -> Unit,
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
    layoutOptions: FormLayoutOptions,
    onUpdate: (FormLayoutOptions) -> Unit,
): List<Setting> = chooseTimeFormat(layoutOptions.format) { format ->
    // If seconds are not visible, revert Layout.Wrapped to Layout.Horizontal
    val layout = if (!format.showSeconds && layoutOptions.layout == Layout.Wrapped) {
        Layout.Horizontal
    } else layoutOptions.layout

    onUpdate(layoutOptions.copy(layout = layout, format = format))
}

private fun buildSizeSettings(
    layoutOptions: FormLayoutOptions,
    onUpdate: (FormLayoutOptions) -> Unit,
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
