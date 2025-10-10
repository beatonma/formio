package org.beatonma.gclocks.app.data.settings

import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable
import org.beatonma.gclocks.app.data.settings.DisplayContext.Options
import org.beatonma.gclocks.app.ui.LocalizedString
import org.beatonma.gclocks.core.geometry.MutableRectF
import org.beatonma.gclocks.core.geometry.RectF
import org.beatonma.gclocks.core.graphics.Color


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

internal interface DisplayContextScreen {
    val displayContext: DisplayContext
    val label: LocalizedString
    val contentDescription: LocalizedString
    val icon: ImageVector
}
