package org.beatonma.gclocks.app

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow
import org.beatonma.gclocks.app.settings.AppSettings
import org.beatonma.gclocks.app.settings.loadAppSettings
import org.beatonma.gclocks.app.settings.saveAppSettings

interface AppSettingsRepository {
    suspend fun save(appSettings: AppSettings)
    fun load(): Flow<AppSettings>
}

class DataStoreAppSettingsRepository(
    private val dataStore: DataStore<Preferences>,
) : AppSettingsRepository {
    override suspend fun save(appSettings: AppSettings) {
        dataStore.saveAppSettings(appSettings)
    }

    override fun load(): Flow<AppSettings> {
        return dataStore.loadAppSettings()
    }
}