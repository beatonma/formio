package org.beatonma.gclocks.app.settings.clocks

import gclocks_multiplatform.app.generated.resources.Res
import gclocks_multiplatform.app.generated.resources.setting_stroke_width
import org.beatonma.gclocks.app.LocalizedString
import org.beatonma.gclocks.app.SettingsViewModel
import org.beatonma.gclocks.app.settings.ContextClockOptions
import org.beatonma.gclocks.compose.components.settings.Key
import org.beatonma.gclocks.compose.components.settings.RichSetting
import org.beatonma.gclocks.compose.components.settings.RichSettings
import org.beatonma.gclocks.compose.components.settings.Setting
import org.beatonma.gclocks.io16.Io16LayoutOptions
import org.beatonma.gclocks.io16.Io16Options
import org.beatonma.gclocks.io16.Io16Paints


abstract class Io16SettingsViewModel(
    initial: ContextClockOptions<Io16Options>,
    onEditOptions: suspend (ContextClockOptions<Io16Options>) -> Unit,
) : SettingsViewModel<Io16Options>(initial, onEditOptions) {
    override fun buildClockSettings(
        settings: RichSettings,
        clockOptions: Io16Options,
    ): RichSettings {
        val onUpdatePaints: (Io16Paints) -> Unit =
            { update(this.contextOptions.value.clock.copy(paints = it)) }

        val onUpdateLayout: (Io16LayoutOptions) -> Unit = {
            update(this.contextOptions.value.clock.copy(layout = it))
        }

        return settings.append(
            colors = buildColorSettings(clockOptions.paints, onUpdatePaints),
            layout = buildLayoutSettings(clockOptions.layout, onUpdateLayout),
            sizes = listOf(
                chooseSpacing(
                    clockOptions.layout.spacingPx,
                    { onUpdateLayout(clockOptions.layout.copy(spacingPx = it)) },
                    default = 8,
                    max = 64,
                ),
                chooseSecondScale(
                    clockOptions.layout.secondsGlyphScale,
                    { onUpdateLayout(clockOptions.layout.copy(secondsGlyphScale = it)) }
                ),
                RichSetting.Float(
                    key = StrokeWidthKey,
                    localized = LocalizedString(Res.string.setting_stroke_width),
                    value = clockOptions.paints.strokeWidth,
                    default = 2f,
                    min = 0f,
                    max = 16f,
                    stepSize = 1f,
                    onValueChange = { onUpdatePaints(clockOptions.paints.copy(strokeWidth = it)) }
                )
            )
        )
    }
}

private val StrokeWidthKey = Key.FloatKey("stroke_width")
private fun buildColorSettings(paints: Io16Paints, onUpdate: (Io16Paints) -> Unit): List<Setting> =
    listOf(
        createColorsAdapter(paints) { colors ->
            onUpdate(paints.copy(colors = colors.toTypedArray()))
        }
    )

private fun buildLayoutSettings(
    layoutOptions: Io16LayoutOptions,
    onUpdate: (Io16LayoutOptions) -> Unit,
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
