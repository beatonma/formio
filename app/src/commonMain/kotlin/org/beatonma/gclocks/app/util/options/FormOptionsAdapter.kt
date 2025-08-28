package org.beatonma.gclocks.app.util.options

import org.beatonma.gclocks.compose.components.settings.SettingsGroup
import org.beatonma.gclocks.form.FormLayoutOptions
import org.beatonma.gclocks.form.FormOptions
import org.beatonma.gclocks.form.FormPaints

fun createFormOptionsAdapter(options: FormOptions, onUpdate: (FormOptions) -> Unit): SettingsGroup {
    return SettingsGroup(
        name = "FORM",
        settings = listOf(
            createAdapter(options.paints) {
                onUpdate(options.copy(paints = it))
            },
            createAdapter(options.layout) {
                onUpdate(options.copy(layout = it))
            },
        )
    )
}

private fun createAdapter(paints: FormPaints, onUpdate: (FormPaints) -> Unit): SettingsGroup =
    createColorsAdapter(paints) { index, value ->
        onUpdate(paints.copy(colors = paints.colors.apply {
            this[index] = value
        }))
    }

private fun createAdapter(
    layoutOptions: FormLayoutOptions,
    onUpdate: (FormLayoutOptions) -> Unit,
): SettingsGroup =
    SettingsGroup(
        name = "Layout",
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
