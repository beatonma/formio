package org.beatonma.gclocks.compose.components.settings.data

import androidx.compose.runtime.Immutable
import org.beatonma.gclocks.app.ui.LocalizedString
import org.beatonma.gclocks.core.geometry.RectF
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.core.util.warn
import kotlin.jvm.JvmInline
import org.beatonma.gclocks.app.data.settings.ClockType as ClockTypeData
import org.beatonma.gclocks.compose.components.settings.ClockColors as ClockColorsData

sealed interface Setting


data class RichSettings(
    val core: List<Setting>,
    val colors: List<Setting>,
    val layout: List<Setting>,
    val time: List<Setting>,
    val sizes: List<Setting>,
) {

    companion object {
        fun empty(core: List<Setting>): RichSettings = RichSettings(core, listOf(), listOf(), listOf(), listOf())
    }

    val all: List<Setting> get() = core + colors + layout + time + sizes

    fun append(
        colors: List<Setting>,
        layout: List<Setting>,
        time: List<Setting>,
        sizes: List<Setting>,
    ): RichSettings =
        copy(
            colors = this.colors + colors,
            layout = this.layout + layout,
            time = this.time + time,
            sizes = this.sizes + sizes,
        )

    fun groups(groupCount: Int): List<List<Setting>> {
        return when (groupCount) {
            1 -> listOf(all)
            2 -> listOf(
                core + colors + layout,
                time + sizes,
            )

            else -> throw IllegalArgumentException("RichSettings not configured to split into $groupCount groups")
        }
    }

    fun filter(block: (RichSetting<*>) -> RichSetting<*>?): RichSettings {
        return copy(
            colors = colors.filterSettings(block),
            layout = layout.filterSettings(block),
            time = time.filterSettings(block),
            sizes = sizes.filterSettings(block),
        )
    }
}


data class RichSettingsGroup(
    val settings: List<Setting>,
) : Setting


/**
 * Wrapper for a specific [Options] field, with localized display test and
 * (optional) help text.
 */
sealed interface RichSetting<T : Any> : Setting {
    val key: Key
    val localized: LocalizedString
    val helpText: LocalizedString?
    val value: T
    val onValueChange: (T) -> Unit

    @Immutable
    data class ClockColors(
        override val key: Key.ClockColors,
        override val localized: LocalizedString,
        override val helpText: LocalizedString? = null,
        override val value: ClockColorsData,
        override val onValueChange: (ClockColorsData) -> Unit,
    ) : RichSetting<ClockColorsData>

    @Immutable
    data class SingleSelect<E : Enum<E>>(
        override val key: Key.Enum<E>,
        override val localized: LocalizedString,
        override val helpText: LocalizedString? = null,
        override val value: E,
        override val onValueChange: (E) -> Unit,
        val values: Set<E>,
    ) : RichSetting<E>

    @Immutable
    data class MultiSelect<E : Enum<E>>(
        override val key: Key.Enum<E>,
        override val localized: LocalizedString,
        override val helpText: LocalizedString? = null,
        override val value: Set<E>,
        override val onValueChange: (Set<E>) -> Unit,
        val values: Set<E>,
    ) : RichSetting<Set<E>>

    @Immutable
    data class Int(
        override val key: Key.Int,
        override val localized: LocalizedString,
        override val helpText: LocalizedString? = null,
        override val value: kotlin.Int,
        val default: kotlin.Int,
        val min: kotlin.Int,
        val max: kotlin.Int,
        val stepSize: kotlin.Int = 1,
        override val onValueChange: (kotlin.Int) -> Unit,
    ) : RichSetting<kotlin.Int>

    @Immutable
    data class Float(
        override val key: Key.Float,
        override val localized: LocalizedString,
        override val helpText: LocalizedString? = null,
        override val value: kotlin.Float,
        val default: kotlin.Float,
        val min: kotlin.Float,
        val max: kotlin.Float,
        val stepSize: kotlin.Float,
        override val onValueChange: (kotlin.Float) -> Unit,
    ) : RichSetting<kotlin.Float>

    @Immutable
    data class Bool(
        override val key: Key.Bool,
        override val localized: LocalizedString,
        override val helpText: LocalizedString? = null,
        override val value: Boolean,
        override val onValueChange: (Boolean) -> Unit,
    ) : RichSetting<Boolean>

    @Immutable
    data class ClockPosition(
        override val key: Key.RectF,
        override val localized: LocalizedString,
        override val helpText: LocalizedString? = null,
        override val value: RectF,
        override val onValueChange: (RectF) -> Unit,
    ) : RichSetting<RectF>

    @Immutable
    data class ClockType(
        override val key: Key.Enum<ClockTypeData>,
        override val localized: LocalizedString,
        override val helpText: LocalizedString? = null,
        override val value: ClockTypeData,
        override val onValueChange: (ClockTypeData) -> Unit,
    ) : RichSetting<ClockTypeData>

    data class IntList(
        override val key: Key.IntList,
        override val localized: LocalizedString,
        override val helpText: LocalizedString? = null,
        override val value: List<kotlin.Int>,
        override val onValueChange: (List<kotlin.Int>) -> Unit,
        val validator: SettingValidator<kotlin.Int>,
        val placeholder: LocalizedString? = null
    ) : RichSetting<List<kotlin.Int>>
}


sealed interface Key {
    val value: String

    @JvmInline
    value class Bool(override val value: String) : Key

    @JvmInline
    value class Int(override val value: String) : Key

    @JvmInline
    value class IntList(override val value: String) : Key

//    @JvmInline
//    value class StringKey(override val value: String) : Key

    @JvmInline
    value class Float(override val value: String) : Key

    @JvmInline
    value class ClockColors(override val value: String) : Key

    @JvmInline
    value class Enum<E : kotlin.Enum<E>>(override val value: String) : Key

    @JvmInline
    value class RectF(override val value: String) : Key
}

fun interface SettingValidator<T> {
    /**
     * @throws ValidationFailed
     */
    fun validate(value: T)
}

class ValidationFailed(message: String?) : Exception(message)


private fun List<Setting>.filterSettings(block: (RichSetting<*>) -> RichSetting<*>?): List<Setting> {
    val out = mutableListOf<Setting>()

    for (setting in this) {
        when (setting) {
            is RichSettingsGroup -> {
                val result = setting.settings.filterSettings(block)
                if (result.isNotEmpty()) {
                    out.add(RichSettingsGroup(result))
                }
            }

            is RichSetting<*> -> {
                block(setting)?.let { out.add(it) }
            }
        }
    }

    return out
}

fun List<Setting>.forEachSetting(block: (RichSetting<*>) -> Unit) {
    for (setting in this) {
        when (setting) {
            is RichSettingsGroup -> setting.settings.forEachSetting(block)
            is RichSetting<*> -> block(setting)
        }
    }
}

fun List<Setting>.insertBefore(key: Key, setting: Setting): List<Setting> {
    val index = indexOfFirst { it is RichSetting<*> && it.key == key }

    return toMutableList().apply {
        if (index >= 0) {
            add(index, setting)
        } else {
            warn("insertBefore: Setting with key $key not found.")
        }
    }.toList()
}

fun List<Setting>.insertAfter(key: Key, setting: Setting): List<Setting> {
    val index = indexOfFirst { it is RichSetting<*> && it.key == key }

    return toMutableList().apply {
        if (index >= 0) {
            add(index + 1, setting)
        } else {
            warn("insertAfter: Setting with key $key not found.")
        }
    }.toList()
}

fun List<Setting>.replace(key: Key, block: (Setting) -> Setting): List<Setting> {
    val index = indexOfFirst { it is RichSetting<*> && it.key == key }

    return toMutableList().apply {
        if (index >= 0) {
            this[index] = block(this[index])
        } else {
            warn("replace: Setting with key $key not found.")
        }
    }.toList()
}
