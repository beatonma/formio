package org.beatonma.formio.app.ui.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
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
import org.beatonma.formio.app.data.AppSettingsRepository
import org.beatonma.formio.app.data.settings.AppSettings
import org.beatonma.formio.app.data.settings.ClockType
import org.beatonma.formio.app.data.settings.ContextClockOptions
import org.beatonma.formio.app.data.settings.DisplayContext
import org.beatonma.formio.app.data.settings.DisplayContextDefaults
import org.beatonma.formio.app.data.settings.GlobalOptions
import org.beatonma.formio.app.data.settings.RichSetting
import org.beatonma.formio.app.data.settings.RichSettings
import org.beatonma.formio.app.data.settings.SettingKey
import org.beatonma.formio.app.data.settings.buildClockSettingsAdapter
import org.beatonma.formio.app.data.settings.chooseClockColors
import org.beatonma.formio.app.data.settings.chooseClockPosition
import org.beatonma.formio.app.data.settings.chooseClockType
import org.beatonma.formio.app.data.settings.replace
import org.beatonma.formio.app.io
import org.beatonma.formio.core.options.AnyOptions
import kotlin.reflect.KClass

private typealias OnSaveCallback = () -> Unit

class SettingsEditorViewModelFactory(
    private val repository: AppSettingsRepository,
    private val onSave: OnSaveCallback? = null,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: KClass<T>,
        extras: CreationExtras,
    ): T {
        @Suppress("UNCHECKED_CAST")
        return SettingsEditorViewModel(repository, onSave) as T
    }
}

abstract class AbstractSettingsEditorViewModel(
    private val repository: AppSettingsRepository,
    private val onSave: OnSaveCallback? = null,
) : ViewModel() {
    private val _appSettings: MutableStateFlow<AppSettings?> = MutableStateFlow(null)
    val appSettings: StateFlow<AppSettings?> = _appSettings.asStateFlow()

    private val _lastSavedAppSettings: MutableStateFlow<AppSettings?> = MutableStateFlow(null)
    val hasUnsavedChanges: StateFlow<Boolean> =
        combine(_appSettings, _lastSavedAppSettings) { current, saved ->
            current?.settings != saved?.settings
        }.stateIn(viewModelScope, started = SharingStarted.Eagerly, initialValue = false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val displayContext: Flow<DisplayContext?> = appSettings.mapLatest { it?.state?.displayContext }

    /**
     * When value is changed (via [refreshRichSettings]), causes [richSettings] to rebuild and emit a new value.
     */
    private val refreshRichSettingsFlag = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val richSettings: StateFlow<RichSettings?> =
        combine(refreshRichSettingsFlag, appSettings) { _, settings -> settings }.mapLatest { settings ->
            when (settings) {
                null -> null
                else -> buildRichSettings(
                    settings.contextSettings.clock,
                    settings.contextOptions,
                    settings.globalOptions
                )
            }
        }.stateIn(viewModelScope, started = SharingStarted.Eagerly, initialValue = null)

    init {
        viewModelScope.launch(Dispatchers.io) {
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

    fun <O : AnyOptions> setClockOptions(clockOptions: O) {
        _appSettings.update { previous ->
            previous?.copyWithOptions(clockOptions, null)
        }
    }

    fun setDisplayOptions(displayOptions: DisplayContext.Options) {
        _appSettings.update { previous ->
            previous?.copyWithOptions(null, displayOptions)
        }
    }

    fun setGlobalOptions(globalOptions: GlobalOptions) {
        _appSettings.update { previous ->
            previous?.copy(globalOptions = globalOptions)
        }
    }

    fun save() {
        viewModelScope.launch(Dispatchers.io) {
            appSettings.value?.copy()?.let { currentSettings ->
                repository.save(currentSettings)
                _lastSavedAppSettings.update { currentSettings }
                onSave?.invoke()
            }
        }
    }

    /**
     * Force [richSettings] to rebuild and emit a new value.
     *
     * Primary use case: update the display state of any [RichSetting.Card]s (i.e. 'settings' which are not derived
     * from [AppSettings]).
     */
    protected fun refreshRichSettings() {
        refreshRichSettingsFlag.update { !it }
    }

    fun restoreDefaultSettings() {
        viewModelScope.launch(Dispatchers.io) { repository.restoreDefaultSettings() }
    }

    private fun <O : AnyOptions> buildRichSettings(
        clock: ClockType,
        options: ContextClockOptions<O>,
        globalOptions: GlobalOptions,
    ): RichSettings {
        @Suppress("UNCHECKED_CAST")
        val adapter = buildClockSettingsAdapter<O>(clock)

        var settings = RichSettings.empty(
            listOf(
                chooseClockType(clock, ::setClock)
            )
        )
        settings = adapter.addClockSettings(
            settings,
            options.clockOptions,
            ::setClockOptions,
            globalOptions,
            ::setGlobalOptions
        )
        settings = addDisplaySettings(
            settings, options.displayOptions, ::setDisplayOptions,
            globalOptions,
            ::setGlobalOptions
        )
        settings = adapter.filterRichSettings(settings, options.clockOptions, options.displayContext)

        return settings
    }

    open fun addDisplaySettings(
        settings: RichSettings,
        displayContextOptions: DisplayContext.Options,
        updateDisplayContextOptions: (DisplayContext.Options) -> Unit,
        globalOptions: GlobalOptions,
        updateGlobalOptions: (GlobalOptions) -> Unit,
    ): RichSettings {
        return when (displayContextOptions) {
            is DisplayContextDefaults.WithBackground -> {
                settings.copy(
                    colors = settings.colors.replace(SettingKey.clockColors) { previous ->
                        val previous = previous as RichSetting.ClockColors
                        chooseClockColors(
                            value = previous.value.copy(background = displayContextOptions.backgroundColor),
                            onValueChange = {
                                it.background?.let { backgroundColor ->
                                    updateDisplayContextOptions(
                                        displayContextOptions.copy(
                                            backgroundColor = backgroundColor
                                        )
                                    )
                                }
                                previous.onValueChange(it)
                            },
                            palettes = globalOptions.colorPalettes,
                            onUpdatePalettes = { updateGlobalOptions(globalOptions.copy(colorPalettes = it)) },
                        )
                    },
                    layout = listOf(
                        chooseClockPosition(
                            value = displayContextOptions.position,
                            onUpdate = { updateDisplayContextOptions(displayContextOptions.copy(position = it)) },
                        ),
                    ) + settings.layout,
                )
            }

            else -> throw IllegalStateException("Unhandled DisplayContext.Options: ${displayContextOptions::class}")
        }
    }
}

expect class SettingsEditorViewModel(
    repository: AppSettingsRepository,
    onSave: OnSaveCallback? = null,
) : AbstractSettingsEditorViewModel


@Composable
fun settingsEditorViewModel(
    repository: AppSettingsRepository,
    onSave: (() -> Unit)? = null,
): SettingsEditorViewModel {
    val factory = remember { SettingsEditorViewModelFactory(repository = repository, onSave = onSave) }
    return viewModel<SettingsEditorViewModel>(factory = factory)
}
