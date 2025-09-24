package org.beatonma.gclocks.app.settings.clocks

import org.beatonma.gclocks.app.SettingsViewModel
import org.beatonma.gclocks.app.settings.ContextClockOptions
import org.beatonma.gclocks.app.settings.DisplayContext
import org.beatonma.gclocks.compose.components.settings.RichSettings
import org.beatonma.gclocks.compose.components.settings.Setting
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.form.FormLayoutOptions
import org.beatonma.gclocks.form.FormOptions
import org.beatonma.gclocks.form.FormPaints


abstract class FormSettingsViewModel(
    initial: ContextClockOptions<FormOptions>,
    onEditClockOptions: (FormOptions) -> Unit,
    onEditDisplayOptions: (DisplayContext.Options) -> Unit,
) : SettingsViewModel<FormOptions>(initial, onEditClockOptions, onEditDisplayOptions) {
    override fun buildClockSettings(
        settings: RichSettings,
        clockOptions: FormOptions,
    ): RichSettings {
        val updateLayout: (FormLayoutOptions) -> Unit = {
            update(this.contextOptions.value.clockOptions.copy(layout = it))
        }
        return settings.append(
            colors = buildPaintsSettings(clockOptions.paints) {
                update(this.contextOptions.value.clockOptions.copy(paints = it))
            },
            layout = buildLayoutSettings(clockOptions.layout, updateLayout),
            time = buildTimeSettings(clockOptions.layout, updateLayout),
            sizes = buildSizeSettings(clockOptions.layout, updateLayout)
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
