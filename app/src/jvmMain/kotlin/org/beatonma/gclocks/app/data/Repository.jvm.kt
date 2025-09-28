package org.beatonma.gclocks.app.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import okio.Path.Companion.toPath
import org.beatonma.gclocks.app.data.settings.AppSettings
import org.beatonma.gclocks.app.data.settings.DefaultAppSettings
import org.beatonma.gclocks.app.data.settings.DisplayContext
import org.beatonma.gclocks.core.util.debug
import kotlin.enums.enumEntries


class DataStoreAppSettingsRepository(
    private val dataStore: DataStore<Preferences>,
) : AppSettingsRepository {
    override suspend fun save(appSettings: AppSettings) {
        dataStore.saveAppSettings(appSettings)
    }

    override fun loadAppSettings(): Flow<AppSettings> {
        return dataStore.loadAppSettings()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun loadString(key: String): Flow<String?> {
        return dataStore.data.mapLatest { it[stringPreferencesKey(key)] }
    }

    override suspend fun save(key: String, value: String) {
        dataStore.edit { it[stringPreferencesKey(key)] = value }
    }

    override suspend fun forgetString(key: String) {
        dataStore.edit {
            it.remove(stringPreferencesKey(key))
        }
    }
}


internal const val DataStoreFileName = ".clocks.preferences_pb"

private val CurrentStatePreference = stringPreferencesKey(AppSettingsRepository.Keys.AppState)
private val DisplayContext.preference
    get() = stringPreferencesKey(
        AppSettingsRepository.Keys.settingKey(
            this
        )
    )

fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = { producePath().toPath() }
    )

@OptIn(ExperimentalCoroutinesApi::class)
fun DataStore<Preferences>.loadAppSettings(): Flow<AppSettings> {
    return data.mapLatest { preferences ->
        try {
            return@mapLatest AppSettings(
                state = deserialize(preferences[CurrentStatePreference]!!),
                settings = enumEntries<DisplayContext>().associateWith {
                    deserialize(preferences[it.preference]!!)
                }
            )
        } catch (e: Exception) {
            debug("Failed to load preferences: $e\n$preferences")
        }
        return@mapLatest DefaultAppSettings
    }
}

/**
 * Save app state and settings. All settings stored as JSON objects.
 *
 * Keys:
 * - [CurrentStatePreference]: Save the current [org.beatonma.gclocks.app.data.settings.AppState].
 * - [DisplayContext.preference]: Save settings for each context under a separate key.
 */
suspend fun DataStore<Preferences>.saveAppSettings(appSettings: AppSettings) {
    edit { prefs ->
        prefs[CurrentStatePreference] = serialize(appSettings.state)

        enumEntries<DisplayContext>().forEach { context ->
            prefs[context.preference] = serialize(appSettings.settings[context])
        }
    }
}
