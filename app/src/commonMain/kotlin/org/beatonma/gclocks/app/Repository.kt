package org.beatonma.gclocks.app

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import org.beatonma.gclocks.app.settings.AppSettings
import org.beatonma.gclocks.app.settings.DisplayContext

inline fun <reified T> serialize(value: T): String = Json.encodeToString<T>(value)
inline fun <reified T> deserialize(str: String): T = Json.decodeFromString<T>(str)

interface AppSettingsRepository {
    fun loadAppSettings(): Flow<AppSettings>
    suspend fun save(appSettings: AppSettings)

    suspend fun save(key: String, value: String)
    fun loadString(key: String): Flow<String?>
    suspend fun forgetString(key: String)

    object Keys {
        const val AppState = "state"
        fun settingKey(context: DisplayContext) = "context_${context.name}"
    }
}
