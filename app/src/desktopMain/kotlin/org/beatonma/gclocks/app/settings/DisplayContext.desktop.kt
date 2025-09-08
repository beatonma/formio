package org.beatonma.gclocks.app.settings

import kotlinx.serialization.Serializable


actual enum class DisplayContext {
    Default {
        override fun defaultOptions(): Options.Empty {
            return Options.Empty
        }
    },
    Alternative {
        override fun defaultOptions(): Options.Empty {
            return Options.Empty
        }
    },
    ;

    actual abstract fun defaultOptions(): Options

    @Serializable
    actual sealed interface Options {
        object Empty : Options
    }
}