package org.beatonma.gclocks.app.settings

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
import kotlinx.serialization.serializer
import org.beatonma.gclocks.core.options.Options
import org.beatonma.gclocks.form.FormOptions
import org.beatonma.gclocks.io16.Io16Options
import org.beatonma.gclocks.io18.Io18Options


/**
 * Context in which a set of [org.beatonma.gclocks.core.options.Options] is applied.
 */
expect enum class SettingsContext

/** Per-context set of options for each clock type. */
@Serializable
data class ContextSettings(
    val form: FormOptions = FormOptions(),
    val io16: Io16Options = Io16Options(),
    val io18: Io18Options = Io18Options(),
)


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@Serializable(with = AppSettingsSerializer::class)
expect class AppSettings(
    defaultContext: SettingsContext,
    defaultClock: CommonAppSettings.Companion.Clock,
    settings: Map<SettingsContext, ContextSettings>,
) : CommonAppSettings

expect val DefaultAppSettings: AppSettings

interface CommonAppSettings {
    var context: SettingsContext

    var clock: Clock
    var settings: Map<SettingsContext, ContextSettings>

    suspend fun getSettings(context: SettingsContext): ContextSettings =
        settings[context] ?: ContextSettings()

    suspend fun <O : Options<*>> saveSettings(context: SettingsContext, options: O) {
        val updated = settings.toMutableMap().apply {
            val ctx = this[context] ?: ContextSettings()

            this[context] = when (options) {
                is FormOptions -> ctx.copy(form = options)
                is Io16Options -> ctx.copy(io16 = options)
                is Io18Options -> ctx.copy(io18 = options)
                else -> throw IllegalStateException("Unhandled options class ${options::class}")
            }
        }

        this.settings = updated.toMap()
        save()
    }

    suspend fun save()

    companion object {
        enum class Clock {
            Form,
            Io16,
            Io18,
            ;
        }
    }
}


object AppSettingsSerializer : KSerializer<AppSettings> {
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
        return decoder.decodeStructure(descriptor) {
            var context: SettingsContext? = null
            var clock: CommonAppSettings.Companion.Clock? = null
            var settings: Map<SettingsContext, ContextSettings>? = null

            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
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