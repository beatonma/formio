package org.beatonma.gclocks.app.data.settings.clocks

import org.beatonma.gclocks.app.SettingsViewModel
import org.beatonma.gclocks.app.data.settings.ContextClockOptions
import org.beatonma.gclocks.app.data.settings.DisplayContext
import org.beatonma.gclocks.compose.components.settings.data.RichSettings
import org.beatonma.gclocks.compose.components.settings.data.Setting
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.io18.Io18LayoutOptions
import org.beatonma.gclocks.io18.Io18Options
import org.beatonma.gclocks.io18.Io18Paints


abstract class Io18SettingsViewModel(
    initial: ContextClockOptions<Io18Options>,
    onEditClockOptions: (Io18Options) -> Unit,
    onEditDisplayOptions: (DisplayContext.Options) -> Unit,
) : SettingsViewModel<Io18Options>(initial, onEditClockOptions, onEditDisplayOptions) {
    override fun buildClockSettings(
        settings: RichSettings,
        clockOptions: Io18Options,
    ): RichSettings {
        val updateLayout: (Io18LayoutOptions) -> Unit = {
            update(contextOptions.value.clockOptions.copy(layout = it))
        }
        return settings.append(
            colors = buildPaintsSettings(clockOptions.paints) {
                update(contextOptions.value.clockOptions.copy(paints = it))
            },
            layout = buildLayoutSettings(clockOptions.layout, updateLayout),
            time = buildTimeSettings(clockOptions.layout, updateLayout),
            sizes = buildSizeSettings(clockOptions.layout, updateLayout),
        )
    }
}

private fun buildPaintsSettings(
    paints: Io18Paints,
    onUpdate: (Io18Paints) -> Unit,
): List<Setting> =
    listOf(
        createColorsAdapter(paints) { newValue ->
            onUpdate(paints.copy(colors = newValue.toTypedArray()))
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
