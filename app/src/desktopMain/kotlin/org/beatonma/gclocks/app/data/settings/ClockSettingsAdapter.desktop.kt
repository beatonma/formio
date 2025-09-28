package org.beatonma.gclocks.app.data.settings

actual fun buildClockSettingsAdapter(clock: ClockType): ClockSettingsAdapter<*> {
    return defaultBuildClockSettingsAdapter(clock)
}
