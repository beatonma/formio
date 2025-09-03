package org.beatonma.gclocks.app.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.core.util.debug
import org.beatonma.gclocks.form.FormOptions
import org.beatonma.gclocks.io16.Io16Options
import org.beatonma.gclocks.io18.Io18Options
import kotlin.enums.enumEntries


private val CurrentStatePreference = stringPreferencesKey("state")
private val SettingsContext.preference get() = stringPreferencesKey("context_$name")


fun DataStore<Preferences>.loadAppSettings(): Flow<AppSettings> {
    return data.mapLatest { preferences ->
        try {
            return@mapLatest AppSettings(
                state = Json.decodeFromString(preferences[CurrentStatePreference]!!),
                settings = enumEntries<SettingsContext>().associateWith {
                    Json.decodeFromString(preferences[it.preference]!!)
                }
            )
        } catch (e: Exception) {
            debug("Failed to load preferences: $e")
        }
        return@mapLatest DefaultAppSettings
    }
}

suspend fun DataStore<Preferences>.saveAppSettings(appSettings: AppSettings) {
    edit { prefs ->
        prefs[CurrentStatePreference] = Json.encodeToString(appSettings.state)

        enumEntries<SettingsContext>().forEach {
            prefs[it.preference] = Json.encodeToString(appSettings.settings[it])
        }
    }
}

/**
 * Context in which a set of [org.beatonma.gclocks.core.options.Options] is applied.
 */
expect enum class SettingsContext

/** Per-context set of options for each clock type. */
@Serializable
data class ContextSettings(
    val form: FormOptions = FormOptions(),
    val io16: Io16Options = Io16Options(),
    val io18: Io18Options = Io18Options(),
) {
    fun get(clock: AppSettings.Clock): Options<*> = when (clock) {
        AppSettings.Clock.Form -> this.form
        AppSettings.Clock.Io16 -> this.io16
        AppSettings.Clock.Io18 -> this.io18
    }
}

@Serializable
data class AppState(
    val context: SettingsContext,
    val clock: AppSettings.Clock,
)


@Serializable
data class AppSettings(
    val state: AppState,
    val settings: Map<SettingsContext, ContextSettings>,
) {
    val options: Options<*> get() = getSettings(state.context).get(state.clock)

    fun copyWithOptions(options: Options<*>): AppSettings {
        val updated = settings.toMutableMap().apply {
            val previous = this[state.context] ?: ContextSettings()
            this[state.context] = when (options) {
                is FormOptions -> previous.copy(form = options)
                is Io16Options -> previous.copy(io16 = options)
                is Io18Options -> previous.copy(io18 = options)
                else -> throw IllegalStateException("Unhandled options class ${options::class}")
            }
        }

        return copy(settings = updated.toMap())
    }

    private fun getSettings(context: SettingsContext): ContextSettings =
        settings[context] ?: ContextSettings()

    enum class Clock {
        Form,
        Io16,
        Io18,
        ;
    }

    companion object {
        val DefaultSettings get() = SettingsContext.entries.associateWith { ContextSettings() }
    }
}


expect val DefaultAppSettings: AppSettings
