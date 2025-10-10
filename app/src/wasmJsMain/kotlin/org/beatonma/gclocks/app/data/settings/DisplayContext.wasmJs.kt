package org.beatonma.gclocks.app.data.settings

import kotlinx.serialization.Serializable
import org.beatonma.gclocks.core.geometry.RectF
import org.beatonma.gclocks.core.graphics.Color


actual enum class DisplayContext {
    Default {
        override fun defaultOptions(): DisplayContextDefaults.WithBackground {
            return DisplayContextDefaults.WithBackground()
        }
    },
    ;

    actual abstract fun defaultOptions(): Options

    @Serializable
    actual sealed interface Options {
        actual sealed interface WithBackground : Options {
            actual val backgroundColor: Color
            actual val position: RectF
        }
    }
}
