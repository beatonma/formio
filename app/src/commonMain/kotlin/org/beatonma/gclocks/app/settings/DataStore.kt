package org.beatonma.gclocks.app.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath
import org.beatonma.gclocks.core.util.debug
import org.beatonma.gclocks.core.util.dump
import kotlin.enums.enumEntries

internal const val DataStoreFileName = ".clocks.preferences_pb"

private val CurrentStatePreference = stringPreferencesKey("state")
private val DisplayContext.preference get() = stringPreferencesKey("context_$name")

fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = { producePath().toPath() }
    )

fun DataStore<Preferences>.loadAppSettings(): Flow<AppSettings> {
    return data.mapLatest { preferences ->
        try {
            return@mapLatest AppSettings(
                state = Json.decodeFromString(preferences[CurrentStatePreference]!!),
                settings = enumEntries<DisplayContext>().associateWith {
                    Json.decodeFromString(preferences[it.preference]!!)
                }
            )
        } catch (e: Exception) {
            debug("Failed to load preferences: $e")
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
        prefs[CurrentStatePreference] = Json.encodeToString(appSettings.state)

        enumEntries<DisplayContext>().forEach { context ->
            prefs[context.preference] =
                Json.encodeToString(appSettings.settings[context].dump("${context.name}"))
        }
    }
}