package org.beatonma.gclocks.app.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
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
import org.beatonma.gclocks.app.data.settings.ClockSettingsAdapter
import org.beatonma.gclocks.app.data.settings.ClockType
import org.beatonma.gclocks.app.data.settings.ContextClockOptions
import org.beatonma.gclocks.app.data.settings.DisplayContext
import org.beatonma.gclocks.app.data.settings.DisplayContextDefaults
import org.beatonma.gclocks.app.data.settings.buildClockSettingsAdapter
import org.beatonma.gclocks.app.data.settings.clocks.SettingKey
import org.beatonma.gclocks.app.data.settings.clocks.chooseClockPosition
import org.beatonma.gclocks.app.data.settings.clocks.chooseClockType
import org.beatonma.gclocks.app.data.settings.clocks.chooseClockColors
import org.beatonma.gclocks.compose.components.settings.data.RichSetting
import org.beatonma.gclocks.compose.components.settings.data.RichSettings
import org.beatonma.gclocks.compose.components.settings.data.replace
import org.beatonma.gclocks.core.options.Options
import kotlin.reflect.KClass

private typealias OnSaveCallback = () -> Unit

class SettingsEditorViewModelFactory(
    private val repository: AppSettingsRepository,
    private var onSave: OnSaveCallback? = null,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: KClass<T>,
        extras: CreationExtras,
    ): T {
        @Suppress("UNCHECKED_CAST")
        return SettingsEditorViewModel(repository, onSave) as T
    }
}

class SettingsEditorViewModel(
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
    val displayContext: Flow<DisplayContext?> = appSettings.mapLatest { it?.state?.displayContext }

    @OptIn(ExperimentalCoroutinesApi::class)
    val richSettings: StateFlow<RichSettings?> = appSettings.mapLatest { settings ->
        when (settings) {
            null -> null
            else -> buildRichSettings(settings.contextSettings.clock, settings.contextOptions)
        }
    }.stateIn(viewModelScope, started = SharingStarted.Eagerly, initialValue = null)


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
            previous?.copyWithDisplayContext(context)
        }
    }

    fun setClock(clock: ClockType) {
        _appSettings.update { previous ->
            previous?.copyWithClock(clock)
        }
    }

    fun <O : Options<*>> setClockOptions(clockOptions: O) {
        _appSettings.update { previous ->
            previous?.copyWithOptions(clockOptions, null)
        }
    }

    fun setDisplayOptions(displayOptions: DisplayContext.Options) {
        _appSettings.update { previous ->
            previous?.copyWithOptions(null, displayOptions)
        }
    }

    fun save() {
        viewModelScope.launch {
            appSettings.value?.copy()?.let { currentSettings ->
                repository.save(currentSettings)
                _lastSavedAppSettings.update { currentSettings }
                onSave?.invoke()
            }
        }
    }

    fun restoreDefaultSettings() {
        viewModelScope.launch { repository.restoreDefaultSettings() }
    }

    private fun <O : Options<*>> buildRichSettings(
        clock: ClockType,
        options: ContextClockOptions<O>
    ): RichSettings {
        @Suppress("UNCHECKED_CAST")
        val adapter = buildClockSettingsAdapter(clock) as ClockSettingsAdapter<O>

        var settings = RichSettings.empty(
            listOf(
                chooseClockType(clock, ::setClock)
            )
        )
        settings = adapter.addClockSettings(settings, options.clockOptions, ::setClockOptions)
        settings = addDisplaySettings(settings, options.displayOptions, ::setDisplayOptions)
        settings = adapter.filterRichSettings(settings, options.clockOptions, options.displayContext)

        return settings
    }
}


expect fun addDisplaySettings(
    settings: RichSettings,
    options: DisplayContext.Options,
    update: (DisplayContext.Options) -> Unit,
): RichSettings


internal fun defaultAddDisplaySettings(
    settings: RichSettings,
    options: DisplayContext.Options,
    update: (DisplayContext.Options) -> Unit,
): RichSettings {
    return when (options) {
        is DisplayContextDefaults.WithBackground -> {
            settings.copy(
                colors = settings.colors.replace(
                    SettingKey.clockColors,
                    { previous ->
                        val previous = previous as RichSetting.ClockColors
                        chooseClockColors(
                            options.backgroundColor,
                            previous.value.colors,
                            onValueChange = {
                                it.background?.let { backgroundColor ->
                                    update(options.copy(backgroundColor = backgroundColor))
                                }
                                previous.onValueChange(it)
                            }
                        )
                    }
                ),
                layout = listOf(
                    chooseClockPosition(
                        value = options.position,
                        onUpdate = { update(options.copy(position = it)) },
                    ),
                ) + settings.layout,
            )
        }

        else -> throw IllegalStateException("Unhandled DisplayContext.Options: ${options::class}")
    }
}

@Composable
fun settingsEditorViewModel(
    repository: AppSettingsRepository,
    onSave: (() -> Unit)? = null
): SettingsEditorViewModel {
    val factory = remember {
        SettingsEditorViewModelFactory(repository = repository, onSave = onSave)
    }
    return viewModel<SettingsEditorViewModel>(factory = factory)
}
