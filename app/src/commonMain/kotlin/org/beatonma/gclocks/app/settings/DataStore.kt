package org.beatonma.gclocks.app.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import okio.Path.Companion.toPath
import org.beatonma.gclocks.app.deserialize
import org.beatonma.gclocks.app.serialize
import org.beatonma.gclocks.core.util.debug
import kotlin.enums.enumEntries

internal const val DataStoreFileName = ".clocks.preferences_pb"

private val CurrentStatePreference = stringPreferencesKey("state")
private val DisplayContext.preference get() = stringPreferencesKey("context_$name")

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
 * - [CurrentStatePreference]: Save the current [AppState].
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
