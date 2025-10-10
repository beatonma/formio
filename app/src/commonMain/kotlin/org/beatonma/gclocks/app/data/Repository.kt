package org.beatonma.gclocks.app.data

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import org.beatonma.gclocks.app.data.settings.AppSettings
import org.beatonma.gclocks.app.data.settings.DefaultAppSettings
import org.beatonma.gclocks.app.data.settings.DisplayContext

inline fun <reified T> serialize(value: T): String = Json.encodeToString<T>(value)
inline fun <reified T> deserialize(str: String): T = Json.decodeFromString<T>(str)

interface AppSettingsRepository {
    fun loadAppSettings(): Flow<AppSettings>
    suspend fun save(appSettings: AppSettings)

    suspend fun save(key: String, value: String)
    fun loadString(key: String): Flow<String?>
    suspend fun forgetString(key: String)

    suspend fun restoreDefaultSettings() {
        save(DefaultAppSettings)
    }

    object Keys {
        const val AppState = "state"
        fun settingKey(context: DisplayContext) = "context_${context.name}"
    }
}
