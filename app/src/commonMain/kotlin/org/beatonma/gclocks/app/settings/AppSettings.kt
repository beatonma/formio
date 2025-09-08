package org.beatonma.gclocks.app.settings

import kotlinx.serialization.Serializable
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.form.FormOptions
import org.beatonma.gclocks.io16.Io16Options
import org.beatonma.gclocks.io18.Io18Options


/** Per-context set of options for each clock type. */
@Serializable
data class ContextSettings(
    val context: DisplayContext,
    val clock: AppSettings.Clock = AppSettings.Clock.Default,

    /* Options for each type of clock. */
    val form: ContextClockOptions<FormOptions> = ContextClockOptions(
        context,
        context.defaultOptions(),
        FormOptions()
    ),
    val io16: ContextClockOptions<Io16Options> = ContextClockOptions(
        context,
        context.defaultOptions(),
        Io16Options()
    ),
    val io18: ContextClockOptions<Io18Options> = ContextClockOptions(
        context,
        context.defaultOptions(),
        Io18Options()
    ),
) {
    fun get(clock: AppSettings.Clock = this.clock): ContextClockOptions<*> = when (clock) {
        AppSettings.Clock.Form -> this.form
        AppSettings.Clock.Io16 -> this.io16
        AppSettings.Clock.Io18 -> this.io18
    }
}

@Serializable
data class ContextClockOptions<O : Options<*>>(
    val context: DisplayContext,
    val display: DisplayContext.Options,
    val clock: O,
)

@Serializable
data class AppState(
    val context: DisplayContext,
    val clock: AppSettings.Clock,
)


@Serializable
data class AppSettings(
    val state: AppState,
    val settings: Map<DisplayContext, ContextSettings>,
) {
    val options: ContextClockOptions<*> get() = getContextSettings(state.context).get(state.clock)

    fun getOptions(context: DisplayContext): ContextClockOptions<*> {
        return getContextSettings(context).get()
    }

    fun copyWithOptions(
        clockOptions: Options<*>,
        displayOptions: DisplayContext.Options,
    ): AppSettings {
        val updated = settings.toMutableMap().apply {
            val previous = this[state.context] ?: ContextSettings(state.context)

            this[state.context] = when (clockOptions) {
                is FormOptions -> previous.copy(
                    clock = Clock.Form,
                    form = previous.form.copy(clock = clockOptions, display = displayOptions)
                )

                is Io16Options -> previous.copy(
                    clock = Clock.Io16,
                    io16 = previous.io16.copy(clock = clockOptions, display = displayOptions)
                )

                is Io18Options -> previous.copy(
                    clock = Clock.Io18,
                    io18 = previous.io18.copy(clock = clockOptions, display = displayOptions)
                )

                else -> throw IllegalStateException("Unhandled options class ${clockOptions::class}")
            }
        }

        return copy(settings = updated.toMap())
    }

    private fun getContextSettings(context: DisplayContext): ContextSettings =
        settings[context] ?: ContextSettings(context)

    enum class Clock {
        Form,
        Io16,
        Io18,
        ;

        companion object {
            val Default get() = Form
        }
    }

    companion object {
        val DefaultSettings get() = DisplayContext.entries.associateWith { ContextSettings(it) }
    }
}


expect val DefaultAppSettings: AppSettings
