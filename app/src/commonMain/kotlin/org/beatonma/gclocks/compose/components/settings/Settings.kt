package org.beatonma.gclocks.compose.components.settings

import org.beatonma.gclocks.app.LocalizedString
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.core.graphics.Color as GraphicsColor


interface Settings


data class RichSettingsGroup(
    val settings: List<Settings>,
) : Settings


/**
 * Wrapper for a specific [Options] field, with localized display test and
 * (optional) help text.
 */
sealed interface RichSetting<T : Any> : Settings {
    val key: Key
    val localized: LocalizedString
    val helpText: LocalizedString?
    val value: T
    val onValueChange: (T) -> Unit

    data class Color(
        override val key: Key.ColorKey,
        override val localized: LocalizedString,
        override val helpText: LocalizedString? = null,
        override val value: GraphicsColor,
        override val onValueChange: (GraphicsColor) -> Unit,
    ) : RichSetting<GraphicsColor>

    data class Colors(
        override val key: Key.ColorsKey,
        override val localized: LocalizedString,
        override val helpText: LocalizedString? = null,
        override val value: List<GraphicsColor>,
        override val onValueChange: (List<GraphicsColor>) -> Unit,
    ) : RichSetting<List<GraphicsColor>>

    data class SingleSelect<E : Enum<E>>(
        override val key: Key.EnumKey<E>,
        override val localized: LocalizedString,
        override val helpText: LocalizedString? = null,
        override val value: E,
        override val onValueChange: (E) -> Unit,
        val values: Set<E>,
    ) : RichSetting<E>

    data class MultiSelect<E : Enum<E>>(
        override val key: Key.EnumKey<E>,
        override val localized: LocalizedString,
        override val helpText: LocalizedString? = null,
        override val value: Set<E>,
        override val onValueChange: (Set<E>) -> Unit,
        val values: Set<E>,
    ) : RichSetting<Set<E>>

    data class Int(
        override val key: Key.IntKey,
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
        override val key: Key.FloatKey,
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
        override val key: Key.BoolKey,
        override val localized: LocalizedString,
        override val helpText: LocalizedString? = null,
        override val value: Boolean,
        override val onValueChange: (Boolean) -> Unit,
    ) : RichSetting<Boolean>
}


sealed interface Key {
    val value: String

    @JvmInline
    value class BoolKey(override val value: String) : Key

    @JvmInline
    value class IntKey(override val value: String) : Key

    @JvmInline
    value class FloatKey(override val value: String) : Key

    @JvmInline
    value class ColorKey(override val value: String) : Key

    @JvmInline
    value class ColorsKey(override val value: String) : Key

    @JvmInline
    value class EnumKey<E : Enum<E>>(override val value: String) : Key
}


fun List<Settings>.forEachSetting(block: (RichSetting<*>) -> Unit) {
    for (setting in this) {
        when (setting) {
            is RichSettingsGroup -> setting.settings.forEachSetting(block)
            is RichSetting<*> -> block(setting)
        }
    }
}