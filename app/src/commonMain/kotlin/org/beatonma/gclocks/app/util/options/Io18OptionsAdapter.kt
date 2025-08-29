package org.beatonma.gclocks.app.util.options

import org.beatonma.gclocks.compose.components.settings.SettingsGroup
import org.beatonma.gclocks.core.util.debug
import org.beatonma.gclocks.io18.Io18LayoutOptions
import org.beatonma.gclocks.io18.Io18Options
import org.beatonma.gclocks.io18.Io18Paints

fun createIo18OptionsAdapter(options: Io18Options, onUpdate: (Io18Options) -> Unit): SettingsGroup {
    return SettingsGroup(
        name = "io18",
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

private fun createAdapter(paints: Io18Paints, onUpdate: (Io18Paints) -> Unit) =
    createColorsAdapter(paints) { newValue ->
        debug("${paints.colors.toList()} \n-> $newValue")
        onUpdate(paints.copy(colors = newValue.toTypedArray()))
    }


private fun createAdapter(
    layoutOptions: Io18LayoutOptions,
    onUpdate: (Io18LayoutOptions) -> Unit,
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
                default = 13,
                max = 64,
            ),
            chooseSecondScale(
                layoutOptions.secondsGlyphScale,
                { onUpdate(layoutOptions.copy(secondsGlyphScale = it)) }
            )
        )
    )
