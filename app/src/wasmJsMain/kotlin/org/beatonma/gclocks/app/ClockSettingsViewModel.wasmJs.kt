package org.beatonma.gclocks.app

import org.beatonma.gclocks.app.settings.ContextClockOptions
import org.beatonma.gclocks.core.options.Options

actual fun <O : Options<*>> buildSettingsViewModel(
    initial: ContextClockOptions<O>,
    onEditOptions: suspend (ContextClockOptions<O>) -> Unit,
): SettingsViewModel<O> = buildDefaultSettingsViewModel(initial, onEditOptions)
