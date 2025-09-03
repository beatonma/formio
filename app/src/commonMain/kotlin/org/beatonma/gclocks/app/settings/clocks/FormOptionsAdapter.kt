package org.beatonma.gclocks.app.settings.clocks

import org.beatonma.gclocks.app.ClockSettingsViewModel
import org.beatonma.gclocks.compose.components.settings.OptionsAdapter
import org.beatonma.gclocks.compose.components.settings.RichSettingsGroup
import org.beatonma.gclocks.form.FormLayoutOptions
import org.beatonma.gclocks.form.FormOptions
import org.beatonma.gclocks.form.FormPaints


class FormOptionsViewModel(
    options: FormOptions,
    onChangeOptions: suspend (FormOptions) -> Unit,
) : ClockSettingsViewModel<FormOptions>(options, onChangeOptions) {
    override fun buildOptionsAdapter(options: FormOptions): List<OptionsAdapter> {
        return listOf(
            createAdapter(options.paints) {
                update(this.options.value.copy(paints = it))
            },
            createAdapter(options.layout) {
                update(this.options.value.copy(layout = it))
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
