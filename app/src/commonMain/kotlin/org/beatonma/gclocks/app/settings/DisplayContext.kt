package org.beatonma.gclocks.app.settings

import org.beatonma.gclocks.app.settings.DisplayContext.Options


/**
 * Context in which a clock can be used.
 *
 * Each context uses its own set of [Options] for each [AppSettings.Clock] type.
 */
expect enum class DisplayContext {
    ;

    abstract fun defaultOptions(): Options

    sealed interface Options {

    }
}


