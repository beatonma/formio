package org.beatonma.gclocks.app.data.settings

import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.form.FormOptions
import org.beatonma.gclocks.io16.Io16Options
import org.beatonma.gclocks.io18.Io18Options

enum class ClockType {
    Form,
    Io16,
    Io18,
    ;

    companion object {
        val Default get() = Form
    }
}


fun Options<*>.resolveClockType(): ClockType =
    when (this) {
        is FormOptions -> ClockType.Form
        is Io16Options -> ClockType.Io16
        is Io18Options -> ClockType.Io18
        else -> throw IllegalStateException("Unhandled options class ${this::class}")
    }
