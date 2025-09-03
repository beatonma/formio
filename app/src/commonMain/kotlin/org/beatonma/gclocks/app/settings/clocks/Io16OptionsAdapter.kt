package org.beatonma.gclocks.app.settings.clocks

import gclocks_multiplatform.app.generated.resources.Res
import gclocks_multiplatform.app.generated.resources.setting_stroke_width
import org.beatonma.gclocks.app.ClockSettingsViewModel
import org.beatonma.gclocks.app.LocalizedString
import org.beatonma.gclocks.compose.components.settings.OptionsAdapter
import org.beatonma.gclocks.compose.components.settings.RichSetting
import org.beatonma.gclocks.compose.components.settings.RichSettingsGroup
import org.beatonma.gclocks.io16.Io16LayoutOptions
import org.beatonma.gclocks.io16.Io16Options
import org.beatonma.gclocks.io16.Io16Paints


class Io16OptionsViewModel(
    options: Io16Options,
    onChangeOptions: suspend (Io16Options) -> Unit,
) : ClockSettingsViewModel<Io16Options>(options, onChangeOptions) {
    override fun buildOptionsAdapter(options: Io16Options): List<OptionsAdapter> {
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

private fun createAdapter(paints: Io16Paints, onUpdate: (Io16Paints) -> Unit): OptionsAdapter =
    RichSettingsGroup(
        settings = listOf(
            createColorsAdapter(paints) { colors ->
                onUpdate(paints.copy(colors = colors.toTypedArray()))
            },
            RichSetting.Float(
                localized = LocalizedString(Res.string.setting_stroke_width),
                value = paints.strokeWidth,
                default = 2f,
                min = 0f,
                max = 32f,
                stepSize = 1f,
                onValueChange = { onUpdate(paints.copy(strokeWidth = it)) }
            )
        )

    )

private fun createAdapter(
    layoutOptions: Io16LayoutOptions,
    onUpdate: (Io16LayoutOptions) -> Unit,
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
