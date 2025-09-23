package org.beatonma.gclocks.app.settings

import kotlinx.serialization.Serializable
import org.beatonma.gclocks.app.settings.DisplayContext.Options
import org.beatonma.gclocks.core.geometry.MutableRectF
import org.beatonma.gclocks.core.geometry.RectF
import org.beatonma.gclocks.core.graphics.Color


/**
 * Context in which a clock can be used.
 *
 * Each context uses its own set of [Options] for each [AppSettings.Clock] type.
 */
expect enum class DisplayContext {
    ;

    abstract fun defaultOptions(): Options

    sealed interface Options {
        sealed interface WithBackground : Options {
            val backgroundColor: Color
            val position: RectF
        }
    }
}

object DisplayContextDefaults {
    @Serializable
    data class WithBackground(
        override val backgroundColor: Color = Color(0xff222222),
        override val position: RectF = MutableRectF(0f, 0f, 1f, 1f).inset(0.1f).toRect(),
    ) : DisplayContext.Options.WithBackground
}
