package org.beatonma.gclocks.app.settings.clocks

import org.beatonma.gclocks.app.SettingsViewModel
import org.beatonma.gclocks.app.settings.ContextClockOptions
import org.beatonma.gclocks.compose.components.settings.RichSettingsGroup
import org.beatonma.gclocks.compose.components.settings.Settings
import org.beatonma.gclocks.core.util.debug
import org.beatonma.gclocks.io18.Io18LayoutOptions
import org.beatonma.gclocks.io18.Io18Options
import org.beatonma.gclocks.io18.Io18Paints


abstract class Io18SettingsViewModel(
    initial: ContextClockOptions<Io18Options>,
    onEditOptions: suspend (ContextClockOptions<Io18Options>) -> Unit,
) : SettingsViewModel<Io18Options>(initial, onEditOptions) {
    override fun buildClockSettings(clockOptions: Io18Options): List<Settings> {
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

private fun createAdapter(paints: Io18Paints, onUpdate: (Io18Paints) -> Unit): Settings =
    createColorsAdapter(paints) { newValue ->
        debug("${paints.colors.toList()} \n-> $newValue")
        onUpdate(paints.copy(colors = newValue.toTypedArray()))
    }


private fun createAdapter(
    layoutOptions: Io18LayoutOptions,
    onUpdate: (Io18LayoutOptions) -> Unit,
): Settings =
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
