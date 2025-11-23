package org.beatonma.gclocks.app.data.settings

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import org.beatonma.gclocks.core.graphics.Color
import org.beatonma.gclocks.core.options.AnyOptions
import org.beatonma.gclocks.core.options.GlyphOptions
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.form.FormGlyphOptions
import org.beatonma.gclocks.form.FormOptions
import org.beatonma.gclocks.form.FormPaints
import org.beatonma.gclocks.io16.Io16GlyphOptions
import org.beatonma.gclocks.io16.Io16Options
import org.beatonma.gclocks.io16.Io16Paints
import org.beatonma.gclocks.io18.Io18GlyphOptions
import org.beatonma.gclocks.io18.Io18Options
import org.beatonma.gclocks.io18.Io18Paints


/** Per-context set of options for each clock type. */
@Serializable
@Immutable
data class ContextSettings(
    val context: DisplayContext,
    val clock: ClockType = ClockType.Default,

    /* Options for each type of clock. */
    val form: FormContextClockOptions = ContextClockOptions(
        context,
        FormOptions(),
        context.defaultOptions(),
    ),
    val io16: Io16ContextClockOptions = ContextClockOptions(
        context,
        Io16Options(),
        context.defaultOptions(),
    ),
    val io18: Io18ContextClockOptions = ContextClockOptions(
        context,
        Io18Options(),
        context.defaultOptions(),
    ),
) {
    fun getContextOptions(clock: ClockType = this.clock): ContextClockOptions<*, *> =
        when (clock) {
            ClockType.Form -> this.form
            ClockType.Io16 -> this.io16
            ClockType.Io18 -> this.io18
        }
}

@Serializable
@Immutable
data class ContextClockOptions<O : Options<G>, G : GlyphOptions>(
    val displayContext: DisplayContext,
    val clockOptions: O,
    val displayOptions: DisplayContext.Options,
)
typealias FormContextClockOptions = ContextClockOptions<FormOptions, FormGlyphOptions>
typealias Io16ContextClockOptions = ContextClockOptions<Io16Options, Io16GlyphOptions>
typealias Io18ContextClockOptions = ContextClockOptions<Io18Options, Io18GlyphOptions>
typealias ContextClockOptionsOf<O> = ContextClockOptions<O, *>

@Serializable
@Immutable
data class AppState(
    val displayContext: DisplayContext,
)

@Serializable
@Immutable
data class ClockColors(
    val background: Color?,
    val colors: List<Color>,
) {
    val allColors: List<Color>
        get() = when (background) {
            null -> colors
            else -> listOf(background) + colors
        }

    fun update(newColors: List<Color>): ClockColors =
        when (background) {
            null -> ClockColors(background = null, colors = newColors)
            else -> ClockColors(
                background = newColors[0],
                colors = newColors.subList(1, newColors.size)
            )
        }
}

@Serializable
@Immutable
data class GlobalOptions(
    val colorPalettes: List<ClockColors> = listOf(
        ClockColors(
            background = DisplayContextDefaults.DefaultBackgroundColor,
            colors = FormPaints.DefaultColors
        ),
        ClockColors(
            background = DisplayContextDefaults.DefaultBackgroundColor,
            colors = Io16Paints.DefaultColors
        ),
        ClockColors(
            background = DisplayContextDefaults.DefaultBackgroundColor,
            colors = Io18Paints.DefaultColors
        ),
    )
)


@Serializable
@Immutable
data class AppSettings(
    val state: AppState,
    val settings: Map<DisplayContext, ContextSettings>,
    val globalOptions: GlobalOptions
) {
    val contextSettings: ContextSettings get() = getContextSettings(state.displayContext)
    val contextOptions: ContextClockOptions<*, *> get() = getContextOptions(state.displayContext)

    fun getContextOptions(context: DisplayContext): ContextClockOptions<*, *> {
        return getContextSettings(context).getContextOptions()
    }

    fun copyWithDisplayContext(context: DisplayContext): AppSettings {
        return copy(state = state.copy(displayContext = context))
    }

    fun copyWithClock(clock: ClockType): AppSettings {
        val previous = settings[state.displayContext] ?: ContextSettings(state.displayContext)
        val updatedSettings = settings.toMutableMap().apply {
            set(state.displayContext, previous.copy(clock = clock))
        }.toMap()

        return copy(settings = updatedSettings)
    }

    fun copyWithOptions(
        clockOptions: AnyOptions?,
        displayOptions: DisplayContext.Options?
    ): AppSettings {
        return copyWithOptions(
            clockOptions?.resolveClockType() ?: contextSettings.clock,
            clockOptions,
            displayOptions
        )
    }

    fun copyWithOptions(
        clock: ClockType,
        clockOptions: AnyOptions?,
        displayOptions: DisplayContext.Options?,
    ): AppSettings {
        val previous = settings[state.displayContext] ?: ContextSettings(state.displayContext)

        @Suppress("UNCHECKED_CAST")
        val updatedSettings = settings.toMutableMap().apply {
            set(
                state.displayContext,
                when (clock) {
                    ClockType.Form -> previous.copy(
                        clock = ClockType.Form,
                        form = previous.form.copy(
                            clockOptions = clockOptions as FormOptions?
                                ?: previous.form.clockOptions,
                            displayOptions = displayOptions ?: previous.form.displayOptions
                        )
                    )

                    ClockType.Io16 -> previous.copy(
                        clock = ClockType.Io16,
                        io16 = previous.io16.copy(
                            clockOptions = clockOptions as Io16Options?
                                ?: previous.io16.clockOptions,
                            displayOptions = displayOptions ?: previous.io16.displayOptions
                        )
                    )

                    ClockType.Io18 -> previous.copy(
                        clock = ClockType.Io18,
                        io18 = previous.io18.copy(
                            clockOptions = clockOptions as Io18Options?
                                ?: previous.io18.clockOptions,
                            displayOptions = displayOptions ?: previous.io18.displayOptions
                        )
                    )
                }
            )
        }

        return copy(settings = updatedSettings)
    }

    private fun getContextSettings(context: DisplayContext): ContextSettings =
        settings[context] ?: ContextSettings(context)

    companion object {
        /**
         * Naive mapping of default [ContextSettings] for each [DisplayContext].
         * This can be used as a base for [DefaultAppSettings], but may
         * need to be altered for different [DisplayContext]s on the platform.
         */
        val DefaultSettings: Map<DisplayContext, ContextSettings>
            get() = DisplayContext.entries.associateWith { ContextSettings(it) }
    }
}

/**
 * Nuanced per-platform settings.
 *
 * This may use [AppSettings.DefaultSettings] as a base, but should apply
 * alterations for each [DisplayContext] available on the platform as needed.
 */
expect val DefaultAppSettings: AppSettings
