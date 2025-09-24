package org.beatonma.gclocks.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.beatonma.gclocks.app.settings.AppSettings
import org.beatonma.gclocks.app.settings.AppState
import org.beatonma.gclocks.app.settings.DefaultAppSettings
import org.beatonma.gclocks.app.settings.DisplayContext
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
    private val _appSettings: MutableStateFlow<AppSettings> = MutableStateFlow(DefaultAppSettings)
    val appSettings: StateFlow<AppSettings> = _appSettings.asStateFlow()

    private val _lastSavedAppSettings: MutableStateFlow<AppSettings> = MutableStateFlow(DefaultAppSettings)
    val hasUnsavedChanges: StateFlow<Boolean> = combine(_appSettings, _lastSavedAppSettings) { current, saved ->
        current.settings != saved.settings
    }.stateIn(viewModelScope, started = SharingStarted.Eagerly, initialValue = false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentState: Flow<AppState?> = appSettings.mapLatest { it.state }


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
            previous.copy(state = previous.state.copy(displayContext = context))
        }
    }

    fun setClock(clock: AppSettings.Clock) {
        _appSettings.update { previous ->
            previous.copyWithClock(clock)
        }
    }

    fun <O : Options<*>> setClockOptions(clockOptions: O) {
        _appSettings.update { previous ->
            previous.copyWithOptions(clockOptions, previous.contextOptions.displayOptions)
        }
    }

    fun setDisplayOptions(displayOptions: DisplayContext.Options) {
        _appSettings.update { previous ->
            previous.copyWithOptions(previous.contextOptions.clockOptions, displayOptions)
        }
    }

    fun save() {
        viewModelScope.launch {
            val currentSettings = appSettings.value.copy()
            repository.save(currentSettings)
            _lastSavedAppSettings.update { currentSettings.copy() }
            onSave?.invoke()
        }
    }
}
