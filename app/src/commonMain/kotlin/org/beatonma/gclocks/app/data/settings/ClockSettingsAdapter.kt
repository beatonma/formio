package org.beatonma.gclocks.app.data.settings


import gclocks_multiplatform.app.generated.resources.Res
import gclocks_multiplatform.app.generated.resources.setting_stroke_width
import org.beatonma.gclocks.compose.components.settings.data.RichSetting
import org.beatonma.gclocks.compose.components.settings.data.RichSettings
import org.beatonma.gclocks.compose.components.settings.data.Setting
import org.beatonma.gclocks.core.graphics.Paints
import org.beatonma.gclocks.core.options.AnyOptions
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.core.options.LayoutOptions
import org.beatonma.gclocks.form.FormOptions
import org.beatonma.gclocks.io16.Io16Options
import org.beatonma.gclocks.io18.Io18Options

interface ClockSettingsAdapter<O : Options<*>> {
    fun addClockSettings(richSettings: RichSettings, options: O, updateOptions: (O) -> Unit): RichSettings
interface ClockSettingsAdapter<O : Options<G>, G : GlyphOptions> {
    fun addClockSettings(
        richSettings: RichSettings,
        options: O,
        updateOptions: (O) -> Unit,
    ): RichSettings {
        val updateLayout: (LayoutOptions) -> Unit = {
            @Suppress("UNCHECKED_CAST")
            updateOptions(options.copy(layout = it) as O)
        }
        return richSettings.append(
            colors = buildColorSettings(
                options.paints,
                { updateOptions(options.copy(paints = it) as O) },
            ),
            layout = buildLayoutSettings(options.layout, updateLayout),
            time = buildTimeSettings(options.layout, updateLayout),
            sizes = buildSizeSettings(
                options.layout,
                updateLayout,
                options.paints,
                {
                    @Suppress("UNCHECKED_CAST")
                    updateOptions(options.copy(paints = it) as O)
                }
            )
        )
    }

    fun filterRichSettings(
        richSettings: RichSettings,
        options: O,
        displayContext: DisplayContext
    ): RichSettings {
        return richSettings.filter { setting ->
            when (setting.key) {
                SettingKey.clockVerticalAlignment -> {
                    // Vertical alignment only affects Layout.Horizontal
                    when (options.layout.layout) {
                        Layout.Horizontal -> setting
                        else -> null
                    }
                }

                SettingKey.clockSecondsScale -> {
                    // No need to edit second scale if they are not visible
                    when (options.layout.format.showSeconds) {
                        true -> setting
                        false -> null
                    }
                }

                else -> setting
            }
        }
    }
}

    fun buildColorSettings(
        paints: Paints,
        onUpdatePaints: (Paints) -> Unit,
    ): List<Setting> {
        return listOf(
            chooseClockColors(
                paints,
                { colors -> onUpdatePaints(paints.copy(colors = colors)) },
            )
        )
    }

    fun buildLayoutSettings(
        layoutOptions: LayoutOptions,
        onUpdate: (LayoutOptions) -> Unit,
    ): List<Setting> = listOf(
        chooseLayout(layoutOptions.layout) {
            onUpdate(layoutOptions.copy(layout = it))
        },
        chooseHorizontalAlignment(layoutOptions.horizontalAlignment) {
            onUpdate(layoutOptions.copy(horizontalAlignment = it))
        },
        chooseVerticalAlignment(layoutOptions.verticalAlignment) {
            onUpdate(layoutOptions.copy(verticalAlignment = it))
        },
    )

internal fun defaultBuildClockSettingsAdapter(clock: ClockType): ClockSettingsAdapter<*> {
    fun buildTimeSettings(
        layoutOptions: LayoutOptions,
        onUpdate: (LayoutOptions) -> Unit,
    ): List<Setting> = chooseTimeFormat(layoutOptions.format) { format ->
        // If seconds are not visible, revert Layout.Wrapped to Layout.Horizontal
        val layout = if (!format.showSeconds && layoutOptions.layout == Layout.Wrapped) {
            Layout.Horizontal
        } else layoutOptions.layout

        onUpdate(layoutOptions.copy(layout = layout, format = format))
    }

    fun buildSizeSettings(
        layoutOptions: LayoutOptions,
        updateLayoutOptions: (LayoutOptions) -> Unit,
        paints: Paints,
        updatePaintOptions: (Paints) -> Unit,
    ): List<Setting> = listOf(
        chooseSpacing(
            layoutOptions.spacingPx,
            { updateLayoutOptions(layoutOptions.copy(spacingPx = it)) },
            default = 8,
            max = 64,
        ),
        chooseSecondScale(layoutOptions.secondsGlyphScale) {
            updateLayoutOptions(layoutOptions.copy(secondsGlyphScale = it))
        }
    )

}

expect fun <O : AnyOptions> buildClockSettingsAdapter(clock: ClockType): ClockSettingsAdapter<O>

internal fun <O : AnyOptions> defaultBuildClockSettingsAdapter(clock: ClockType): ClockSettingsAdapter<O> {
    @Suppress("UNCHECKED_CAST")
    return when (clock) {
        ClockType.Form -> object : FormClockSettingsAdapter {}
        ClockType.Io16 -> object : Io16ClockSettingsAdapter {}
        ClockType.Io18 -> object : Io18ClockSettingsAdapter {}
    } as ClockSettingsAdapter<O>
}

interface FormClockSettingsAdapter : ClockSettingsAdapter<FormOptions>
interface Io18ClockSettingsAdapter : ClockSettingsAdapter<Io18Options>
interface Io16ClockSettingsAdapter : ClockSettingsAdapter<Io16Options> {
    override fun buildSizeSettings(
        layoutOptions: LayoutOptions,
        updateLayoutOptions: (LayoutOptions) -> Unit,
        paints: Paints,
        updatePaintOptions: (Paints) -> Unit,
    ): List<Setting> {
        return super.buildSizeSettings(
            layoutOptions,
            updateLayoutOptions,
            paints,
            updatePaintOptions
        ) + listOf(
            RichSetting.Float(
                key = SettingKey.clockStrokeWidthKey,
                localized = Res.string.setting_stroke_width,
                value = paints.strokeWidth,
                default = 2f,
                min = 0f,
                max = 16f,
                stepSize = 1f,
                onValueChange = { updatePaintOptions(paints.copy(strokeWidth = it)) }
            )
        )
    }
}
