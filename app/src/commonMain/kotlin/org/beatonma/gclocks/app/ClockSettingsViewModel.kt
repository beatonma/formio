package org.beatonma.gclocks.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import org.beatonma.gclocks.app.settings.ContextClockOptions
import org.beatonma.gclocks.app.settings.DisplayContext
import org.beatonma.gclocks.compose.components.settings.Settings
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

    private val _richSettings = MutableStateFlow<List<Settings>>(emptyList())
    val richSettings = _richSettings.asStateFlow()

    init {
        viewModelScope.launch {
            _options.mapLatest { buildOptionsAdapter(it.display, it.clock) }
                .collect { rich -> _richSettings.value = rich }
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
    abstract fun buildClockSettings(clockOptions: O): List<Settings>

    /**
     * Build a list of settings for [DisplayContext]-dependent options.
     */
    abstract fun buildDisplaySettings(displayOptions: DisplayContext.Options): List<Settings>

    /**
     * Post-process the results of [buildClockSettings] and [buildDisplaySettings] to remove
     * or edit any settings that require some kind of special treatment for the current context.
     */
    abstract fun filterSettings(settings: List<Settings>): List<Settings>

    fun buildOptionsAdapter(
        displayOptions: DisplayContext.Options,
        clockOptions: O,
    ): List<Settings> {
        val settings = buildDisplaySettings(displayOptions) + buildClockSettings(clockOptions)
        return filterSettings(settings)
    }
}