package org.beatonma.gclocks.app.util.options

import gclocks_multiplatform.app.generated.resources.Res
import gclocks_multiplatform.app.generated.resources.setting_stroke_width
import org.beatonma.gclocks.app.LocalizedString
import org.beatonma.gclocks.compose.components.settings.Setting
import org.beatonma.gclocks.compose.components.settings.SettingsGroup
import org.beatonma.gclocks.io16.Io16LayoutOptions
import org.beatonma.gclocks.io16.Io16Options
import org.beatonma.gclocks.io16.Io16Paints

fun createIo16OptionsAdapter(options: Io16Options, onUpdate: (Io16Options) -> Unit): SettingsGroup =
    SettingsGroup(
        name = "io16",
        settings = listOf(
            createAdapter(options.paints) { paints ->
                onUpdate(options.copy(paints = paints))
            },
            createAdapter(options.layout) { layout ->
                onUpdate(options.copy(layout = layout))
            },
        )
    )

private fun createAdapter(paints: Io16Paints, onUpdate: (Io16Paints) -> Unit): SettingsGroup =
    SettingsGroup(
        name = "Paints",
        settings = listOf(
            createColorsAdapter(paints) { colors ->
                onUpdate(paints.copy(colors = colors.toTypedArray()))
            },
            Setting.Float(
                key = "stroke_width",
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
