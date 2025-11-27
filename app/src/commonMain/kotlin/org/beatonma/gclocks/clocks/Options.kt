package org.beatonma.gclocks.clocks

import org.beatonma.gclocks.app.data.settings.ContextClockOptions
import org.beatonma.gclocks.app.data.settings.FormContextClockOptions
import org.beatonma.gclocks.app.data.settings.Io16ContextClockOptions
import org.beatonma.gclocks.app.data.settings.Io18ContextClockOptions
import org.beatonma.gclocks.core.options.AnyOptions
import org.beatonma.gclocks.form.FormClock
import org.beatonma.gclocks.form.FormOptions
import org.beatonma.gclocks.io16.Io16Clock
import org.beatonma.gclocks.io16.Io16Options
import org.beatonma.gclocks.io18.Io18Clock
import org.beatonma.gclocks.io18.Io18Options


inline fun <O : AnyOptions, R> whenOptions(
    options: O,
    form: (formOptions: FormOptions) -> R,
    io16: (io16Options: Io16Options) -> R,
    io18: (io18Options: Io18Options) -> R,
): R {
    @Suppress("UNCHECKED_CAST")
    return when (options.clock) {
        FormClock -> form(options as FormOptions)
        Io16Clock -> io16(options as Io16Options)
        Io18Clock -> io18(options as Io18Options)
        else -> throw IllegalArgumentException("Unhandled Options: $options")
    }
}


inline fun <O : AnyOptions, R> whenOptions(
    options: ContextClockOptions<O>,
    form: (FormContextClockOptions) -> R,
    io16: (Io16ContextClockOptions) -> R,
    io18: (Io18ContextClockOptions) -> R,
): R {
    @Suppress("UNCHECKED_CAST")
    return when (options.clockOptions.clock) {
        FormClock -> form(options as FormContextClockOptions)
        Io16Clock -> io16(options as Io16ContextClockOptions)
        Io18Clock -> io18(options as Io18ContextClockOptions)
        else -> throw IllegalArgumentException("Unhandled ContextClockOptions class ${options::class}")
    }
}
