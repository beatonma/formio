package org.beatonma.gclocks.app.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.beatonma.gclocks.core.util.debug
import org.beatonma.gclocks.core.util.warn

private val PreferencesKey = stringPreferencesKey("settings")
val dataStore: DataStore<Preferences> = createDataStore()

fun DataStore<Preferences>.loadAppSettings(): Flow<AppSettings> {
    return data.mapLatest { preferences ->
        val data = preferences[PreferencesKey]

        try {
            if (data != null) {
                return@mapLatest Json.decodeFromString<AppSettings>(data)
            }
        } catch (e: Exception) {
            warn("Failed to load app settings: $e")
        }

        return@mapLatest DefaultAppSettings
    }
}


actual enum class SettingsContext {
    Default,
    Alternative
    ;
}

@Serializable(with = AppSettingsSerializer::class)
actual class AppSettings actual constructor(
    defaultContext: SettingsContext,
    defaultClock: CommonAppSettings.Companion.Clock,
    settings: Map<SettingsContext, ContextSettings>,
) : CommonAppSettings {
    override var context: SettingsContext by mutableStateOf(defaultContext)
    override var clock: CommonAppSettings.Companion.Clock by mutableStateOf(defaultClock)
    override var settings: Map<SettingsContext, ContextSettings> by mutableStateOf(settings)

    override suspend fun save() {
        val json: String = Json.encodeToString(this)
        debug("save: $json")
        dataStore.edit { it[PreferencesKey] = json }
    }

    override fun toString(): String {
        return "AppSettings($context)"
    }
}

actual val DefaultAppSettings: AppSettings
    get() = AppSettings(
        SettingsContext.Default,
        CommonAppSettings.Companion.Clock.Form,
        mapOf(SettingsContext.Default to ContextSettings())
    )
