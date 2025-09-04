package org.beatonma.gclocks.app.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import org.beatonma.gclocks.app.AppSettingsRepository
import org.beatonma.gclocks.app.DataStoreAppSettingsRepository


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(DataStoreFileName)
val Context.settingsRepository: AppSettingsRepository
    get() = DataStoreAppSettingsRepository(dataStore)
