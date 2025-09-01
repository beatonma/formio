package org.beatonma.gclocks.app.settings.clocks

import org.beatonma.gclocks.compose.components.settings.ClockSettings
import org.beatonma.gclocks.compose.components.settings.OptionsAdapter
import org.beatonma.gclocks.compose.components.settings.RichSettingsGroup
import org.beatonma.gclocks.core.util.debug
import org.beatonma.gclocks.io18.Io18LayoutOptions
import org.beatonma.gclocks.io18.Io18Options
import org.beatonma.gclocks.io18.Io18Paints


class Io18OptionsAdapter(
    options: Io18Options,
    onSave: (Io18Options) -> Unit,
) :
    ClockSettings<Io18Options>(options, onSave) {

    override fun buildOptionsAdapter(options: Io18Options): List<OptionsAdapter> {
        return listOf(
            createAdapter(options.paints) {
                this.options = options.copy(paints = it)
            },
            createAdapter(options.layout) {
                this.options = options.copy(layout = it)
            },
        )
    }
}

private fun createAdapter(paints: Io18Paints, onUpdate: (Io18Paints) -> Unit): OptionsAdapter =
    createColorsAdapter(paints) { newValue ->
        debug("${paints.colors.toList()} \n-> $newValue")
        onUpdate(paints.copy(colors = newValue.toTypedArray()))
    }


private fun createAdapter(
    layoutOptions: Io18LayoutOptions,
    onUpdate: (Io18LayoutOptions) -> Unit,
): OptionsAdapter =
    RichSettingsGroup(
        settings = listOf(
            chooseLayout(layoutOptions.layout, { onUpdate(layoutOptions.copy(layout = it)) }),
            chooseHorizontalAlignment(
                layoutOptions.horizontalAlignment,
                { onUpdate(layoutOptions.copy(horizontalAlignment = it)) }
            ),
            chooseVerticalAlignment(
                layoutOptions.verticalAlignment,
                { onUpdate(layoutOptions.copy(verticalAlignment = it)) }
            ),
            chooseTimeFormat(
                layoutOptions.format,
                { onUpdate(layoutOptions.copy(format = it)) }
            ),
            chooseSpacing(
                layoutOptions.spacingPx,
                { onUpdate(layoutOptions.copy(spacingPx = it)) },
                default = 13,
                max = 64,
            ),
            chooseSecondScale(
                layoutOptions.secondsGlyphScale,
                { onUpdate(layoutOptions.copy(secondsGlyphScale = it)) }
            )
        )
    )
