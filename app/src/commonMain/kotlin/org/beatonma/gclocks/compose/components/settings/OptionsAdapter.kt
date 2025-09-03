package org.beatonma.gclocks.compose.components.settings

import org.beatonma.gclocks.app.LocalizedString
import org.beatonma.gclocks.core.options.Options
import kotlin.enums.EnumEntries
import org.beatonma.gclocks.core.graphics.Color as GraphicsColor


interface OptionsAdapter


data class RichSettingsGroup(
    val settings: List<OptionsAdapter>,
) : OptionsAdapter


/**
 * Wrapper for a specific [Options] field, with localized display test and
 * (optional) help text.
 */
sealed interface RichSetting<T : Any> : OptionsAdapter {
    val localized: LocalizedString
    val helpText: LocalizedString?
    val value: T
    val onValueChange: (T) -> Unit

    data class Color(
        override val localized: LocalizedString,
        override val helpText: LocalizedString? = null,
        override val value: GraphicsColor,
        override val onValueChange: (GraphicsColor) -> Unit,
    ) : RichSetting<GraphicsColor>

    data class Colors(
        override val localized: LocalizedString,
        override val helpText: LocalizedString? = null,
        override val value: List<GraphicsColor>,
        override val onValueChange: (List<GraphicsColor>) -> Unit,
    ) : RichSetting<List<GraphicsColor>>

    data class SingleSelect<E : Enum<E>>(
        override val localized: LocalizedString,
        override val helpText: LocalizedString? = null,
        override val value: E,
        val values: EnumEntries<E>,
        override val onValueChange: (E) -> Unit,
    ) : RichSetting<E>


    data class MultiSelect<E : Enum<E>>(
        override val localized: LocalizedString,
        override val helpText: LocalizedString? = null,
        override val value: Set<E>,
        val values: EnumEntries<E>,
        override val onValueChange: (Set<E>) -> Unit,
    ) : RichSetting<Set<E>>

    data class Int(
        override val localized: LocalizedString,
        override val helpText: LocalizedString? = null,
        override val value: kotlin.Int,
        val default: kotlin.Int,
        val min: kotlin.Int,
        val max: kotlin.Int,
        val stepSize: kotlin.Int = 1,
        override val onValueChange: (kotlin.Int) -> Unit,
    ) : RichSetting<kotlin.Int>

    data class Float(
        override val localized: LocalizedString,
        override val helpText: LocalizedString? = null,
        override val value: kotlin.Float,
        val default: kotlin.Float,
        val min: kotlin.Float,
        val max: kotlin.Float,
        val stepSize: kotlin.Float,
        override val onValueChange: (kotlin.Float) -> Unit,
    ) : RichSetting<kotlin.Float>

    data class Bool(
        override val localized: LocalizedString,
        override val helpText: LocalizedString? = null,
        override val value: Boolean,
        override val onValueChange: (Boolean) -> Unit,
    ) : RichSetting<Boolean>
}
