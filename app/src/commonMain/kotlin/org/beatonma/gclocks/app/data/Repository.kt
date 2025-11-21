package org.beatonma.gclocks.app.data

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.beatonma.gclocks.app.data.settings.AppSettings
import org.beatonma.gclocks.app.data.settings.DefaultAppSettings
import org.beatonma.gclocks.app.data.settings.DisplayContext
import org.beatonma.gclocks.core.Clock
import org.beatonma.gclocks.form.FormClock
import org.beatonma.gclocks.io16.Io16Clock
import org.beatonma.gclocks.io18.Io18Clock

val JsonSerializer = Json {
    serializersModule = SerializersModule {
        polymorphic(Clock::class) {
            subclass(FormClock::class, FormClock.serializer())
            subclass(Io16Clock::class, Io16Clock.serializer())
            subclass(Io18Clock::class, Io18Clock.serializer())
        }
    }
}

inline fun <reified T> serialize(value: T): String =
    JsonSerializer.encodeToString<T>(value)

inline fun <reified T> deserialize(str: String): T = JsonSerializer.decodeFromString<T>(str)

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
