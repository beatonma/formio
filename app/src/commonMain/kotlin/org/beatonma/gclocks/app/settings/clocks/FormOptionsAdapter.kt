package org.beatonma.gclocks.app.settings.clocks

import org.beatonma.gclocks.app.SettingsViewModel
import org.beatonma.gclocks.app.settings.ContextClockOptions
import org.beatonma.gclocks.compose.components.settings.RichSettings
import org.beatonma.gclocks.compose.components.settings.Setting
import org.beatonma.gclocks.form.FormLayoutOptions
import org.beatonma.gclocks.form.FormOptions
import org.beatonma.gclocks.form.FormPaints


abstract class FormSettingsViewModel(
    initial: ContextClockOptions<FormOptions>,
    onEditOptions: suspend (ContextClockOptions<FormOptions>) -> Unit,
) : SettingsViewModel<FormOptions>(initial, onEditOptions) {
    override fun buildClockSettings(
        settings: RichSettings,
        clockOptions: FormOptions,
    ): RichSettings {
        return settings.append(
            colors = buildPaintsSettings(clockOptions.paints) {
                update(this.contextOptions.value.clock.copy(paints = it))
            },
            layout = buildLayoutSettings(clockOptions.layout) {
                update(this.contextOptions.value.clock.copy(layout = it))
            },
            sizes = buildSizeSettings(clockOptions.layout) {
                update(this.contextOptions.value.clock.copy(layout = it))
            }
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
    chooseSecondScale(
        layoutOptions.secondsGlyphScale,
        { onUpdate(layoutOptions.copy(secondsGlyphScale = it)) }
    )
)
