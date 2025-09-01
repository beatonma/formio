package org.beatonma.gclocks.app.settings.clocks

import org.beatonma.gclocks.compose.components.settings.ClockSettings
import org.beatonma.gclocks.compose.components.settings.OptionsAdapter
import org.beatonma.gclocks.compose.components.settings.RichSettingsGroup
import org.beatonma.gclocks.form.FormLayoutOptions
import org.beatonma.gclocks.form.FormOptions
import org.beatonma.gclocks.form.FormPaints


class FormOptionsAdapter(
    options: FormOptions,
    onSave: (FormOptions) -> Unit,
) : ClockSettings<FormOptions>(options, onSave) {
    override fun buildOptionsAdapter(options: FormOptions): List<OptionsAdapter> {
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


private fun createAdapter(paints: FormPaints, onUpdate: (FormPaints) -> Unit): OptionsAdapter =
    createColorsAdapter(paints) { colors ->
        onUpdate(paints.copy(colors = colors.toTypedArray()))
    }

private fun createAdapter(
    layoutOptions: FormLayoutOptions,
    onUpdate: (FormLayoutOptions) -> Unit,
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
                default = 8,
                max = 64,
            ),
            chooseSecondScale(
                layoutOptions.secondsGlyphScale,
                { onUpdate(layoutOptions.copy(secondsGlyphScale = it)) }
            )
        )
    )
