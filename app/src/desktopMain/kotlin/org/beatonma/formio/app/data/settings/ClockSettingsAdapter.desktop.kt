package org.beatonma.formio.app.data.settings

import org.beatonma.formio.core.options.AnyOptions

actual fun <O : AnyOptions> buildClockSettingsAdapter(clock: ClockType): ClockSettingsAdapter<O> {
    return defaultBuildClockSettingsAdapter(clock)
}
