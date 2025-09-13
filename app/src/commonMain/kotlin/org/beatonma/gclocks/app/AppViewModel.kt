package org.beatonma.gclocks.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import org.beatonma.gclocks.app.settings.AppSettings
import org.beatonma.gclocks.app.settings.AppState
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
        return AppViewModel(repository, onSave) as T
    }
}

class AppViewModel(
    private val repository: AppSettingsRepository,
    private var onSave: OnSaveCallback? = null,
) : ViewModel() {
    private val _appSettings: MutableStateFlow<AppSettings?> = MutableStateFlow(null)
    val appSettings: StateFlow<AppSettings?> = _appSettings.asStateFlow()

    val currentState: Flow<AppState?> = appSettings.mapLatest { it?.state }

    init {
        viewModelScope.launch {
            repository.load().collectLatest { _appSettings.value = it }
        }
    }

    fun updateSettingsWithoutSave(settings: AppSettings) {
        this._appSettings.value = settings
    }

    fun save() {
        appSettings.value?.let { settings ->
            viewModelScope.launch {
                repository.save(settings)
                onSave?.invoke()
            }
        }
    }
}
