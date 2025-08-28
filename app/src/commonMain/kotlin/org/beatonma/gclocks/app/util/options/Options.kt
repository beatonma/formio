package org.beatonma.gclocks.app.util.options

import org.beatonma.gclocks.compose.components.settings.SettingsGroup
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.form.FormOptions
import org.beatonma.gclocks.io16.Io16Options
import org.beatonma.gclocks.io18.Io18Options

fun <O : Options<*>> getOptionsAdapter(options: O, onUpdate: (O) -> Unit): SettingsGroup {
    @Suppress("UNCHECKED_CAST")
    return when (options) {
        is FormOptions -> createFormOptionsAdapter(options, onUpdate as (FormOptions) -> Unit)
        is Io16Options -> createIo16OptionsAdapter(options, onUpdate as (Io16Options) -> Unit)
        is Io18Options -> createIo18OptionsAdapter(options, onUpdate as (Io18Options) -> Unit)
        else -> throw IllegalStateException("Unhandled options class $options")
    }
}