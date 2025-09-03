package org.beatonma.gclocks.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.beatonma.gclocks.app.settings.AppSettings
import kotlin.reflect.KClass


class AppViewModelFactory(
    private val repository: AppSettingsRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: KClass<T>,
        extras: CreationExtras,
    ): T {
        return AppViewModel(repository) as T
    }
}

class AppViewModel(
    private val repository: AppSettingsRepository,
) : ViewModel() {
    private val _appSettings: MutableStateFlow<AppSettings?> = MutableStateFlow(null)
    val appSettings: StateFlow<AppSettings?> = _appSettings.asStateFlow()

    init {
        viewModelScope.launch {
            repository.load().collect { _appSettings.value = it }
        }
    }

    fun updateSettingsWithoutSave(settings: AppSettings) {
        this._appSettings.value = settings
    }

    fun save() {
        appSettings.value?.let { settings ->
            viewModelScope.launch {
                repository.save(settings)
            }
        }
    }
}
