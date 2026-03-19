package org.beatonma.formio.app.ui

import androidx.compose.runtime.Composable
import formio.app.generated.resources.Res
import formio.app.generated.resources.setting_alignment_horizontal_center
import formio.app.generated.resources.setting_alignment_horizontal_end
import formio.app.generated.resources.setting_alignment_horizontal_start
import formio.app.generated.resources.setting_alignment_vertical_center
import formio.app.generated.resources.setting_alignment_vertical_end
import formio.app.generated.resources.setting_alignment_vertical_start
import formio.app.generated.resources.setting_clock_layout_horizontal
import formio.app.generated.resources.setting_clock_layout_vertical
import formio.app.generated.resources.setting_clock_layout_wrapped
import formio.app.generated.resources.setting_color_label_hex
import formio.app.generated.resources.setting_color_label_hsl
import formio.app.generated.resources.setting_color_label_rgb
import formio.app.generated.resources.setting_color_label_samples
import formio.app.generated.resources.setting_help_time_format_HH_MM_12
import formio.app.generated.resources.setting_help_time_format_HH_MM_24
import formio.app.generated.resources.setting_help_time_format_HH_MM_SS_12
import formio.app.generated.resources.setting_help_time_format_HH_MM_SS_24
import formio.app.generated.resources.setting_help_time_format_hh_MM_12
import formio.app.generated.resources.setting_help_time_format_hh_MM_24
import formio.app.generated.resources.setting_help_time_format_hh_MM_SS_12
import formio.app.generated.resources.setting_help_time_format_hh_MM_SS_24
import org.beatonma.formio.compose.components.settings.ColorEditorMode
import org.beatonma.formio.core.geometry.HorizontalAlignment
import org.beatonma.formio.core.geometry.VerticalAlignment
import org.beatonma.formio.core.options.Layout
import org.beatonma.formio.core.options.TimeFormat
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.reflect.KClass


@Composable
fun StringResource.resolve() = stringResource(this)

internal object Localization {
    /**
     * Retrieve a mapping of Enum values to translatable string resources.
     */
    @Suppress("UNCHECKED_CAST")
    val <E : Enum<E>> KClass<out E>.stringResourceMap: Map<E, StringResource>
        get() = when (this) {
            Layout::class -> layoutStrings
            HorizontalAlignment::class -> horizontalAlignmentStrings
            VerticalAlignment::class -> verticalAlignmentStrings
            ColorEditorMode::class -> colorEditorModeStrings

            else -> throw IllegalArgumentException("Unhandled class $this")
        } as Map<E, StringResource>

    @Suppress("UNCHECKED_CAST")
    val <E : Enum<E>> KClass<out E>.helpStringResourceMap: Map<E, StringResource>?
        get() = when (this) {
            TimeFormat::class -> timeFormatHelp
            else -> null
        } as? Map<E, StringResource>

    private val layoutStrings = mapOf(
        Layout.Horizontal to Res.string.setting_clock_layout_horizontal,
        Layout.Vertical to Res.string.setting_clock_layout_vertical,
        Layout.Wrapped to Res.string.setting_clock_layout_wrapped,
    )

    private val horizontalAlignmentStrings = mapOf(
        HorizontalAlignment.Start to Res.string.setting_alignment_horizontal_start,
        HorizontalAlignment.Center to Res.string.setting_alignment_horizontal_center,
        HorizontalAlignment.End to Res.string.setting_alignment_horizontal_end,
    )
    private val verticalAlignmentStrings = mapOf(
        VerticalAlignment.Top to Res.string.setting_alignment_vertical_start,
        VerticalAlignment.Center to Res.string.setting_alignment_vertical_center,
        VerticalAlignment.Bottom to Res.string.setting_alignment_vertical_end,
    )

    private val timeFormatHelp = mapOf(
        TimeFormat.HH_MM_SS_24 to Res.string.setting_help_time_format_HH_MM_SS_24,
        TimeFormat.hh_MM_SS_24 to Res.string.setting_help_time_format_hh_MM_SS_24,
        TimeFormat.HH_MM_24 to Res.string.setting_help_time_format_HH_MM_24,
        TimeFormat.hh_MM_24 to Res.string.setting_help_time_format_hh_MM_24,
        TimeFormat.HH_MM_SS_12 to Res.string.setting_help_time_format_HH_MM_SS_12,
        TimeFormat.hh_MM_SS_12 to Res.string.setting_help_time_format_hh_MM_SS_12,
        TimeFormat.HH_MM_12 to Res.string.setting_help_time_format_HH_MM_12,
        TimeFormat.hh_MM_12 to Res.string.setting_help_time_format_hh_MM_12,
    )

    private val colorEditorModeStrings = mapOf(
        ColorEditorMode.Samples to Res.string.setting_color_label_samples,
        ColorEditorMode.HSL to Res.string.setting_color_label_hsl,
        ColorEditorMode.RGB to Res.string.setting_color_label_rgb,
        ColorEditorMode.HEX to Res.string.setting_color_label_hex,
    )
}
