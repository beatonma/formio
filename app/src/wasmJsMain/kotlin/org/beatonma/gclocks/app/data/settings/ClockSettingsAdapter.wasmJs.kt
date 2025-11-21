package org.beatonma.gclocks.app.data.settings

import org.beatonma.gclocks.core.options.AnyOptions

actual fun <O : AnyOptions> buildClockSettingsAdapter(clock: ClockType): ClockSettingsAdapter<O> {
    return defaultBuildClockSettingsAdapter(clock)
}
