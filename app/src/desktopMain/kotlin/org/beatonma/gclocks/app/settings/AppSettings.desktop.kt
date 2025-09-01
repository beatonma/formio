package org.beatonma.gclocks.app.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.beatonma.gclocks.core.util.debug
import org.beatonma.gclocks.core.util.dump
import java.util.prefs.Preferences

private const val PREFERENCES_KEY = "settings"

actual enum class SettingsContext {
    Default,
    Alternative
    ;
}

private fun getPreferences(): Preferences = Preferences.userRoot().node("org.beatonma.gclocks")

actual fun loadAppSettings(): AppSettings {
    val text = getPreferences().get(PREFERENCES_KEY, null)

    return try {
        debug("loadAppSettings decode")
        Json.decodeFromString<AppSettings>(text)
    } catch (e: Exception) {
        debug("loadAppSettings error: $e")
        AppSettings(
            SettingsContext.Default,
            CommonAppSettings.Companion.Clock.Form,
            mapOf(SettingsContext.Default to ContextSettings())
        )
    }
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@Serializable(with = AppSettingsSerializer::class)
actual class AppSettings(
    defaultContext: SettingsContext = SettingsContext.Default,
    defaultClock: CommonAppSettings.Companion.Clock,
    settings: Map<SettingsContext, ContextSettings>,
) : CommonAppSettings {
    override var context: SettingsContext by mutableStateOf(defaultContext)
    override var clock: CommonAppSettings.Companion.Clock by mutableStateOf(defaultClock)
    override var settings: Map<SettingsContext, ContextSettings> by mutableStateOf(settings)

    override suspend fun save() {
        val json: String = Json.encodeToString(this)
        debug("save: $json")
        getPreferences().put(PREFERENCES_KEY, json)
    }

    override fun toString(): String {
        return "AppSettings($context)"
    }
}


private object AppSettingsSerializer : KSerializer<AppSettings> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(
        "org.beatonma.gclocks.app.options.AppSettings"
    ) {
        element<SettingsContext>("context")
        element<CommonAppSettings.Companion.Clock>("clock")
        element<Map<SettingsContext, ContextSettings>>("settings")
    }

    override fun serialize(
        encoder: Encoder,
        value: AppSettings,
    ) {
        debug("serialize")
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, serializer<SettingsContext>(), value.context)
            encodeSerializableElement(
                descriptor,
                1,
                serializer<CommonAppSettings.Companion.Clock>(),
                value.clock
            )
            encodeSerializableElement(
                descriptor,
                2,
                MapSerializer(serializer<SettingsContext>(), serializer<ContextSettings>()),
                value.settings
            )
        }
    }

    override fun deserialize(decoder: Decoder): AppSettings {
        debug("deserialize")
        return decoder.decodeStructure(descriptor) {
            var context: SettingsContext? = null
            var clock: CommonAppSettings.Companion.Clock? = null
            var settings: Map<SettingsContext, ContextSettings>? = null

            while (true) {
                when (val index = decodeElementIndex(descriptor).dump("index")) {
                    0 -> context =
                        decodeSerializableElement(descriptor, 0, serializer<SettingsContext>())

                    1 -> clock =
                        decodeSerializableElement(
                            descriptor,
                            1,
                            serializer<CommonAppSettings.Companion.Clock>()
                        )

                    2 -> settings =
                        decodeSerializableElement(
                            descriptor,
                            1,
                            MapSerializer(
                                serializer<SettingsContext>(),
                                serializer<ContextSettings>()
                            )
                        )

                    -1 -> break
                    else -> error("Unknown index $index")
                }
            }

            AppSettings(
                context ?: error("context was not deserialized"),
                clock ?: error("clock was not deserialized"),
                settings ?: error("settings were not deserialized")
            )
        }
    }
}