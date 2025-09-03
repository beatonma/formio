package org.beatonma.gclocks.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import org.beatonma.gclocks.app.settings.clocks.FormOptionsViewModel
import org.beatonma.gclocks.app.settings.clocks.Io16OptionsViewModel
import org.beatonma.gclocks.app.settings.clocks.Io18OptionsViewModel
import org.beatonma.gclocks.compose.components.settings.OptionsAdapter
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.form.FormOptions
import org.beatonma.gclocks.io16.Io16Options
import org.beatonma.gclocks.io18.Io18Options
import kotlin.reflect.KClass

class ClockSettingsViewModelFactory<O : Options<*>>(
    private val initialOptions: O,
    private val onEditOptions: suspend (O) -> Unit,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: KClass<T>,
        extras: CreationExtras,
    ): T {
        return when (initialOptions) {
            is FormOptions -> FormOptionsViewModel(
                initialOptions,
                onEditOptions as (suspend (FormOptions) -> Unit)
            )

            is Io16Options -> Io16OptionsViewModel(
                initialOptions,
                onEditOptions as (suspend (Io16Options) -> Unit)
            )

            is Io18Options -> Io18OptionsViewModel(
                initialOptions,
                onEditOptions as (suspend (Io18Options) -> Unit)
            )

            else -> throw NotImplementedError("Unhandled Options class: ${initialOptions::class}")
        } as T
    }
}

abstract class ClockSettingsViewModel<O : Options<*>>(
    initialOptions: O,
    private val onEditOptions: suspend (O) -> Unit,
) : ViewModel() {
    private val _options = MutableStateFlow(initialOptions)
    val options: StateFlow<O> = _options.asStateFlow()

    private val _richSettings = MutableStateFlow<List<OptionsAdapter>>(emptyList())
    val richSettings = _richSettings.asStateFlow()

    init {
        viewModelScope.launch {
            _options.mapLatest(::buildOptionsAdapter)
                .collect { rich -> _richSettings.value = rich }
        }
    }

    fun update(options: O) {
        viewModelScope.launch {
            _options.value = options
            onEditOptions(options)
        }
    }

    abstract fun buildOptionsAdapter(options: O): List<OptionsAdapter>
}