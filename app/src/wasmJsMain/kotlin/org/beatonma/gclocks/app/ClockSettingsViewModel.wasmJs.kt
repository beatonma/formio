package org.beatonma.gclocks.app

import org.beatonma.gclocks.app.settings.ContextClockOptions
import org.beatonma.gclocks.app.settings.DisplayContext
import org.beatonma.gclocks.core.options.Options

actual fun <O : Options<*>> buildSettingsViewModel(
    initial: ContextClockOptions<O>,
    onEditClockOptions: (O) -> Unit,
    onEditDisplayOptions: (DisplayContext.Options) -> Unit,
): SettingsViewModel<O> =
    buildDefaultSettingsViewModel(initial, onEditClockOptions, onEditDisplayOptions)
