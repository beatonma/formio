package org.beatonma.gclocks.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beatonma.gclocks.app.data.AppSettingsRepository
import org.beatonma.gclocks.app.data.settings.AppSettings
import org.beatonma.gclocks.app.data.settings.AppState
import org.beatonma.gclocks.app.data.settings.ClockType
import org.beatonma.gclocks.app.data.settings.DisplayContext
import org.beatonma.gclocks.core.options.Options
import kotlin.reflect.KClass

private typealias OnSaveCallback = () -> Unit

class AppViewModelFactory(
    private val repository: AppSettingsRepository,
    private var onSave: OnSaveCallback? = null,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: KClass<T>,
        extras: CreationExtras,
    ): T {
        @Suppress("UNCHECKED_CAST")
        return AppViewModel(repository, onSave) as T
    }
}

class AppViewModel(
    private val repository: AppSettingsRepository,
    private var onSave: OnSaveCallback? = null,
) : ViewModel() {
    private val _appSettings: MutableStateFlow<AppSettings?> = MutableStateFlow(null)
    val appSettings: StateFlow<AppSettings?> = _appSettings.asStateFlow()

    private val _lastSavedAppSettings: MutableStateFlow<AppSettings?> = MutableStateFlow(null)
    val hasUnsavedChanges: StateFlow<Boolean> = combine(_appSettings, _lastSavedAppSettings) { current, saved ->
        current?.settings != saved?.settings
    }.stateIn(viewModelScope, started = SharingStarted.Eagerly, initialValue = false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentState: Flow<AppState?> = appSettings.mapLatest { it?.state }


    init {
        viewModelScope.launch {
            repository.loadAppSettings().collectLatest {
                _appSettings.value = it
                _lastSavedAppSettings.value = it
            }
        }
    }

    fun setDisplayContext(context: DisplayContext) {
        _appSettings.update { previous ->
            previous?.copy(state = previous.state.copy(displayContext = context))
        }
    }

    fun setClock(clock: ClockType) {
        _appSettings.update { previous ->
            previous?.copyWithClock(clock)
        }
    }

    fun <O : Options<*>> setClockOptions(clockOptions: O) {
        _appSettings.update { previous ->
            previous?.copyWithOptions(clockOptions, previous.contextOptions.displayOptions)
        }
    }

    fun setDisplayOptions(displayOptions: DisplayContext.Options) {
        _appSettings.update { previous ->
            previous?.copyWithOptions(previous.contextOptions.clockOptions, displayOptions)
        }
    }

    fun save() {
        viewModelScope.launch {
            appSettings.value?.copy()?.let { currentSettings ->
                repository.save(currentSettings)
                _lastSavedAppSettings.update { currentSettings.copy() }
                onSave?.invoke()
            }
        }
    }
}
