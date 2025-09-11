package org.beatonma.gclocks.app.settings.clocks

import gclocks_multiplatform.app.generated.resources.Res
import gclocks_multiplatform.app.generated.resources.setting_stroke_width
import org.beatonma.gclocks.app.LocalizedString
import org.beatonma.gclocks.app.SettingsViewModel
import org.beatonma.gclocks.app.settings.ContextClockOptions
import org.beatonma.gclocks.compose.components.settings.Key
import org.beatonma.gclocks.compose.components.settings.RichSetting
import org.beatonma.gclocks.compose.components.settings.RichSettingsGroup
import org.beatonma.gclocks.compose.components.settings.Settings
import org.beatonma.gclocks.io16.Io16LayoutOptions
import org.beatonma.gclocks.io16.Io16Options
import org.beatonma.gclocks.io16.Io16Paints


abstract class Io16SettingsViewModel(
    initial: ContextClockOptions<Io16Options>,
    onEditOptions: suspend (ContextClockOptions<Io16Options>) -> Unit,
) : SettingsViewModel<Io16Options>(initial, onEditOptions) {
    override fun buildClockSettings(clockOptions: Io16Options): List<Settings> {
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

private val StrokeWidthKey = Key.FloatKey("stroke_width")
private fun createAdapter(paints: Io16Paints, onUpdate: (Io16Paints) -> Unit): Settings =
    RichSettingsGroup(
        settings = listOf(
            createColorsAdapter(paints) { colors ->
                onUpdate(paints.copy(colors = colors.toTypedArray()))
            },
            RichSetting.Float(
                key = StrokeWidthKey,
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
