package org.beatonma.formio.app.data.settings

import kotlinx.serialization.Serializable
import org.beatonma.formio.app.data.settings.DisplayContext.Options
import org.beatonma.formio.core.geometry.MutableRectF
import org.beatonma.formio.core.geometry.RectF
import org.beatonma.formio.core.graphics.Color


/**
 * Context in which a clock can be used.
 *
 * Each context uses its own set of [Options] for each [ClockType] type.
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
    val DefaultBackgroundColor: Color = Color(0xff222222)
    val DefaultPosition: RectF = MutableRectF(0f, 0f, 1f, 1f).inset(0.1f).toRect()

    @Serializable
    data class WithBackground(
        override val backgroundColor: Color = DefaultBackgroundColor,
        override val position: RectF = DefaultPosition,
    ) : Options.WithBackground
}
