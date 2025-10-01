package org.beatonma.gclocks.app.data.settings

import kotlinx.serialization.Serializable
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.form.FormOptions
import org.beatonma.gclocks.io16.Io16Options
import org.beatonma.gclocks.io18.Io18Options


/** Per-context set of options for each clock type. */
@Serializable
data class ContextSettings(
    val context: DisplayContext,
    val clock: ClockType = ClockType.Default,

    /* Options for each type of clock. */
    val form: ContextClockOptions<FormOptions> = ContextClockOptions(
        context,
        FormOptions(),
        context.defaultOptions(),
    ),
    val io16: ContextClockOptions<Io16Options> = ContextClockOptions(
        context,
        Io16Options(),
        context.defaultOptions(),
    ),
    val io18: ContextClockOptions<Io18Options> = ContextClockOptions(
        context,
        Io18Options(),
        context.defaultOptions(),
    ),
) {
    fun get(clock: ClockType = this.clock): ContextClockOptions<*> =
        when (clock) {
            ClockType.Form -> this.form
            ClockType.Io16 -> this.io16
            ClockType.Io18 -> this.io18
        }
}

@Serializable
data class ContextClockOptions<O : Options<*>>(
    val displayContext: DisplayContext,
    val clockOptions: O,
    val displayOptions: DisplayContext.Options,
)

@Serializable
data class AppState(
    val displayContext: DisplayContext,
)


@Serializable
data class AppSettings(
    val state: AppState,
    val settings: Map<DisplayContext, ContextSettings>,
) {
    val contextSettings: ContextSettings get() = getContextSettings(state.displayContext)
    val contextOptions: ContextClockOptions<*> get() = getContextOptions(state.displayContext)

    fun getContextOptions(context: DisplayContext): ContextClockOptions<*> {
        return getContextSettings(context).get()
    }

    fun copyWithClock(clock: ClockType): AppSettings {
        val previous = settings[state.displayContext] ?: ContextSettings(state.displayContext)
        val updatedSettings = settings.toMutableMap().apply {
            set(state.displayContext, previous.copy(clock = clock))
        }.toMap()

        return copy(settings = updatedSettings)
    }

    fun copyWithOptions(
        clockOptions: Options<*>,
        displayOptions: DisplayContext.Options,
    ): AppSettings {
        return copyWithOptions(
            clockOptions.resolveClockType(),
            clockOptions,
            displayOptions
        )
    }

    fun copyWithOptions(
        clock: ClockType,
        clockOptions: Options<*>?,
        displayOptions: DisplayContext.Options?,
    ): AppSettings {
        val previous = settings[state.displayContext] ?: ContextSettings(state.displayContext)
        val updatedSettings = settings.toMutableMap().apply {
            set(
                state.displayContext,
                when (clock) {
                    ClockType.Form -> previous.copy(
                        clock = ClockType.Form,
                        form = previous.form.copy(
                            clockOptions = clockOptions as FormOptions? ?: previous.form.clockOptions,
                            displayOptions = displayOptions ?: previous.form.displayOptions
                        )
                    )

                    ClockType.Io16 -> previous.copy(
                        clock = ClockType.Io16,
                        io16 = previous.io16.copy(
                            clockOptions = clockOptions as Io16Options? ?: previous.io16.clockOptions,
                            displayOptions = displayOptions ?: previous.io16.displayOptions
                        )
                    )

                    ClockType.Io18 -> previous.copy(
                        clock = ClockType.Io18,
                        io18 = previous.io18.copy(
                            clockOptions = clockOptions as Io18Options? ?: previous.io18.clockOptions,
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
