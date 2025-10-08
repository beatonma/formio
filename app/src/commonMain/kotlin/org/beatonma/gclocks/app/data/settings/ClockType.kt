package org.beatonma.gclocks.app.data.settings

import org.beatonma.gclocks.clocks.whenOptions
import org.beatonma.gclocks.core.options.Options

enum class ClockType {
    Form,
    Io16,
    Io18,
    ;

    companion object {
        val Default get() = Form
    }
}


fun Options<*>.resolveClockType(): ClockType = whenOptions(
    this,
    form = { ClockType.Form },
    io16 = { ClockType.Io16 },
    io18 = { ClockType.Io18 },
)
