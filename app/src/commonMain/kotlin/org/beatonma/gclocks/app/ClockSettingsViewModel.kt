package org.beatonma.gclocks.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import org.beatonma.gclocks.app.settings.ContextClockOptions
import org.beatonma.gclocks.app.settings.DisplayContext
import org.beatonma.gclocks.compose.components.settings.RichSettings
import org.beatonma.gclocks.core.options.Options
import kotlin.reflect.KClass


class SettingsViewModelFactory<O : Options<*>>(
    private val initial: ContextClockOptions<O>,
    private val onEditOptions: suspend (ContextClockOptions<O>) -> Unit,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: KClass<T>,
        extras: CreationExtras,
    ): T {
        @Suppress("UNCHECKED_CAST")
        return buildSettingsViewModel(initial, onEditOptions) as T
    }
}

/**
 * Build [SettingsViewModel] implementation using per-platform [DisplayContext.Options].
 */
expect fun <O : Options<*>> buildSettingsViewModel(
    initial: ContextClockOptions<O>,
    onEditOptions: suspend (ContextClockOptions<O>) -> Unit,
): SettingsViewModel<O>


abstract class SettingsViewModel<O : Options<*>>(
    initial: ContextClockOptions<O>,
    private val onEditOptions: suspend (ContextClockOptions<O>) -> Unit,
) : ViewModel() {
    protected val context: DisplayContext = initial.context
    private val _options = MutableStateFlow(initial)
    val contextOptions = _options.asStateFlow()

    private val _richSettings = MutableStateFlow<RichSettings?>(null)
    val richSettings = _richSettings.asStateFlow()

    init {
        viewModelScope.launch {
            @Suppress("OPT_IN_USAGE")
            _options.mapLatest { buildOptionsAdapter(it.display, it.clock) }
                .collectLatest { rich -> _richSettings.value = rich }
        }
    }

    fun update(clockOptions: O) {
        val newValue = _options.value.copy(clock = clockOptions)
        _options.value = newValue
        viewModelScope.launch {
            onEditOptions(newValue)
        }
    }

    fun update(displayOptions: DisplayContext.Options) {
        val newValue = _options.value.copy(display = displayOptions)
        _options.value = newValue
        viewModelScope.launch {
            onEditOptions(newValue)
        }
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
    abstract fun filterSettings(settings: RichSettings): RichSettings

    fun buildOptionsAdapter(
        displayOptions: DisplayContext.Options,
        clockOptions: O,
    ): RichSettings {
        var settings = RichSettings.Empty

        settings = buildClockSettings(settings, clockOptions)
        settings = buildDisplaySettings(settings, displayOptions)
        settings = filterSettings(settings)

        return settings.applyGroups()
    }
}
