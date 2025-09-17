package org.beatonma.gclocks.app

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.serialization.json.Json
import org.beatonma.gclocks.app.settings.AppSettings
import org.beatonma.gclocks.app.settings.loadAppSettings
import org.beatonma.gclocks.app.settings.saveAppSettings

inline fun <reified T> serialize(value: T): String = Json.encodeToString<T>(value)
inline fun <reified T> deserialize(str: String): T = Json.decodeFromString<T>(str)

interface AppSettingsRepository {
    fun loadAppSettings(): Flow<AppSettings>
    suspend fun save(appSettings: AppSettings)

    suspend fun save(key: String, value: String)
    fun loadString(key: String): Flow<String?>
    suspend fun forgetString(key: String)
}

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
