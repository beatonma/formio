package org.beatonma.gclocks.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import org.beatonma.gclocks.app.data.settings.ContextClockOptions
import org.beatonma.gclocks.app.data.settings.DisplayContext
import org.beatonma.gclocks.app.data.settings.DisplayContextDefaults
import org.beatonma.gclocks.app.data.settings.clocks.FormSettingsViewModel
import org.beatonma.gclocks.app.data.settings.clocks.Io16SettingsViewModel
import org.beatonma.gclocks.app.data.settings.clocks.Io18SettingsViewModel
import org.beatonma.gclocks.app.data.settings.clocks.SettingKey
import org.beatonma.gclocks.app.data.settings.clocks.chooseBackgroundColor
import org.beatonma.gclocks.app.data.settings.clocks.chooseClockPosition
import org.beatonma.gclocks.compose.components.settings.data.RichSettings
import org.beatonma.gclocks.compose.components.settings.data.insertBefore
import org.beatonma.gclocks.core.options.Layout
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.form.FormOptions
import org.beatonma.gclocks.io16.Io16Options
import org.beatonma.gclocks.io18.Io18Options
import kotlin.reflect.KClass


class SettingsViewModelFactory<O : Options<*>>(
    private val initial: ContextClockOptions<O>,
    private val onEditClockOptions: (O) -> Unit,
    private val onEditDisplayOptions: (DisplayContext.Options) -> Unit,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: KClass<T>,
        extras: CreationExtras,
    ): T {
        @Suppress("UNCHECKED_CAST")
        return buildSettingsViewModel(initial, onEditClockOptions, onEditDisplayOptions) as T
    }
}

/**
 * Build [SettingsViewModel] implementation using per-platform [DisplayContext.Options].
 */
expect fun <O : Options<*>> buildSettingsViewModel(
    initial: ContextClockOptions<O>,
    onEditClockOptions: (O) -> Unit,
    onEditDisplayOptions: (DisplayContext.Options) -> Unit,
): SettingsViewModel<O>


abstract class SettingsViewModel<O : Options<*>>(
    initial: ContextClockOptions<O>,
    private val onEditClockOptions: (O) -> Unit,
    private val onEditDisplayOptions: (DisplayContext.Options) -> Unit,
) : ViewModel() {
    protected val context: DisplayContext = initial.displayContext
    private val _options = MutableStateFlow(initial)
    val contextOptions = _options.asStateFlow()

    private val _richSettings = MutableStateFlow<RichSettings?>(null)
    val richSettings = _richSettings.asStateFlow()

    init {
        viewModelScope.launch {
            @OptIn(ExperimentalCoroutinesApi::class)
            _options.mapLatest { buildOptionsAdapter(it.displayOptions, it.clockOptions) }
                .collectLatest { rich -> _richSettings.value = rich }
        }
    }

    fun update(clockOptions: O) {
        val newValue =
            _options.updateAndGet { previous -> previous.copy(clockOptions = clockOptions) }
        onEditClockOptions(newValue.clockOptions)
    }

    fun update(displayOptions: DisplayContext.Options) {
        val newValue =
            _options.updateAndGet { previous -> previous.copy(displayOptions = displayOptions) }
        onEditDisplayOptions(newValue.displayOptions)
    }

    /**
     * Build a list of settings to allow editing the values of [clockOptions].
     */
    abstract fun buildClockSettings(settings: RichSettings, clockOptions: O): RichSettings

    /**
     * Build a list of settings for [DisplayContext]-dependent options.
     */
    abstract fun buildDisplaySettings(
        settings: RichSettings,
        displayOptions: DisplayContext.Options,
    ): RichSettings

    /**
     * Post-process the results of [buildClockSettings] and [buildDisplaySettings] to remove
     * or edit any settings that require some kind of special treatment for the current context.
     */
    open fun filterSettings(
        settings: RichSettings,
        clockOptions: O,
        displayOptions: DisplayContext.Options,
    ): RichSettings {
        return settings.filter { setting ->
            when (setting.key) {
                SettingKey.clockVerticalAlignment -> {
                    // Vertical alignment only affects Layout.Horizontal
                    when (clockOptions.layout.layout) {
                        Layout.Horizontal -> setting
                        else -> null
                    }
                }

                SettingKey.clockSecondsScale -> {
                    // No need to edit second scale if they are not visible
                    when (clockOptions.layout.format.showSeconds) {
                        true -> setting
                        false -> null
                    }
                }

                else -> setting
            }
        }
    }

    fun buildOptionsAdapter(
        displayOptions: DisplayContext.Options,
        clockOptions: O,
    ): RichSettings {
        var settings = RichSettings.Empty

        settings = buildClockSettings(settings, clockOptions)
        settings = buildDisplaySettings(settings, displayOptions)
        settings = filterSettings(settings, clockOptions, displayOptions)

        return settings.applyGroups()
    }
}


internal fun <O : Options<*>> buildDefaultSettingsViewModel(
    initial: ContextClockOptions<O>,
    onEditClockOptions: (O) -> Unit,
    onEditDisplayOptions: (DisplayContext.Options) -> Unit,
): SettingsViewModel<O> {
    @Suppress("UNCHECKED_CAST")
    return when (initial.clockOptions) {
        is FormOptions -> {
            object : FormSettingsViewModel(
                initial as ContextClockOptions<FormOptions>,
                onEditClockOptions as (FormOptions) -> Unit,
                onEditDisplayOptions
            ) {
                override fun buildDisplaySettings(
                    settings: RichSettings,
                    displayOptions: DisplayContext.Options,
                ): RichSettings {
                    return buildDefaultDisplaySettings(settings, displayOptions)
                }
            }
        }

        is Io16Options -> {
            object : Io16SettingsViewModel(
                initial as ContextClockOptions<Io16Options>,
                onEditClockOptions as (Io16Options) -> Unit,
                onEditDisplayOptions
            ) {
                override fun buildDisplaySettings(
                    settings: RichSettings,
                    displayOptions: DisplayContext.Options,
                ): RichSettings {
                    return buildDefaultDisplaySettings(settings, displayOptions)
                }
            }
        }

        is Io18Options -> {
            object : Io18SettingsViewModel(
                initial as ContextClockOptions<Io18Options>,
                onEditClockOptions as (Io18Options) -> Unit,
                onEditDisplayOptions
            ) {
                override fun buildDisplaySettings(
                    settings: RichSettings,
                    displayOptions: DisplayContext.Options,
                ): RichSettings {
                    return buildDefaultDisplaySettings(settings, displayOptions)
                }
            }
        }

        else -> throw NotImplementedError("Unhandled Options class ${initial.clockOptions::class}")
    } as SettingsViewModel<O>
}


private fun <O : Options<*>> SettingsViewModel<O>.buildDefaultDisplaySettings(
    settings: RichSettings,
    displayOptions: DisplayContext.Options,
): RichSettings {
    return when (displayOptions) {
        is DisplayContextDefaults.WithBackground -> {
            settings.copy(
                colors = settings.colors.insertBefore(
                    SettingKey.clockColors,
                    chooseBackgroundColor(
                        value = displayOptions.backgroundColor,
                        onUpdate = { update(displayOptions.copy(backgroundColor = it)) },
                    ),
                ),
                layout = listOf(
                    chooseClockPosition(
                        value = displayOptions.position,
                        onUpdate = { update(displayOptions.copy(position = it)) },
                    ),
                ) + settings.layout,
            )
        }

        else -> {
            throw IllegalStateException("Unhandled DisplayContext.Options: ${displayOptions::class}")
        }
    }
}
