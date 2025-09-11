package org.beatonma.gclocks.app.settings.clocks

import org.beatonma.gclocks.app.SettingsViewModel
import org.beatonma.gclocks.app.settings.ContextClockOptions
import org.beatonma.gclocks.compose.components.settings.RichSettingsGroup
import org.beatonma.gclocks.compose.components.settings.Settings
import org.beatonma.gclocks.form.FormLayoutOptions
import org.beatonma.gclocks.form.FormOptions
import org.beatonma.gclocks.form.FormPaints


abstract class FormSettingsViewModel(
    initial: ContextClockOptions<FormOptions>,
    onEditOptions: suspend (ContextClockOptions<FormOptions>) -> Unit,
) : SettingsViewModel<FormOptions>(initial, onEditOptions) {
    override fun buildClockSettings(clockOptions: FormOptions): List<Settings> {
        return listOf(
            createAdapter(clockOptions.paints) {
                update(this.contextOptions.value.clock.copy(paints = it))
            },
            createAdapter(clockOptions.layout) {
                update(this.contextOptions.value.clock.copy(layout = it))
            },
        )
    }
}

private fun createAdapter(paints: FormPaints, onUpdate: (FormPaints) -> Unit): Settings =
    createColorsAdapter(paints) { colors ->
        onUpdate(paints.copy(colors = colors.toTypedArray()))
    }

private fun createAdapter(
    layoutOptions: FormLayoutOptions,
    onUpdate: (FormLayoutOptions) -> Unit,
): Settings = RichSettingsGroup(
    listOf(
        RichSettingsGroup(
            listOf(
                chooseLayout(layoutOptions.layout, {
                    onUpdate(layoutOptions.copy(layout = it))
                }),
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
                    { onUpdate(layoutOptions.copy(format = it)) },
                ),
            )
        ),
        RichSettingsGroup(
            listOf(
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
    )
)