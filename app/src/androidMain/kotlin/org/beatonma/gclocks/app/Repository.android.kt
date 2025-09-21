package org.beatonma.gclocks.app

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import org.beatonma.gclocks.app.settings.DisplayMetrics


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(DataStoreFileName)
val Context.settingsRepository: AppSettingsRepository
    get() = DataStoreAppSettingsRepository(dataStore)

private const val DisplayMetricsKey = "display_metrics"


@OptIn(ExperimentalCoroutinesApi::class)
fun AppSettingsRepository.loadDisplayMetrics(): Flow<DisplayMetrics> =
    loadString(DisplayMetricsKey).mapLatest { value ->
        value?.let {
            deserialize<DisplayMetrics>(it)
        } ?: DisplayMetrics()
    }


suspend fun AppSettingsRepository.save(metrics: DisplayMetrics) =
    save(DisplayMetricsKey, serialize(metrics))
