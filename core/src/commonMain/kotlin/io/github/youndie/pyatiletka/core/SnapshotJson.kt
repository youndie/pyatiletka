package io.github.youndie.pyatiletka.core

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.time.Instant

/** The schema this build reads and writes. A snapshot with a newer one is refused, not guessed at. */
const val SCHEMA_VERSION: Int = 1

/** The wire format of `snapshot.json`: unknown keys are ignored, so an older client reads a newer snapshot's known part. */
val SnapshotJson: Json =
    Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

/** What reading a snapshot can come to (docs/api/endpoint-snapshot.md, errors). */
sealed interface SnapshotRead {
    data class Ok(
        val snapshot: Snapshot,
    ) : SnapshotRead

    /** The snapshot is newer than this client: the page is older than the snapshot. */
    data class NewerSchema(
        val found: Int,
    ) : SnapshotRead

    data class Unreadable(
        val reason: String,
    ) : SnapshotRead
}

/** Reads `snapshot.json`, checking `schemaVersion` before the rest so a newer schema is named as such. */
fun readSnapshot(text: String): SnapshotRead {
    val version =
        try {
            SnapshotJson
                .parseToJsonElement(text)
                .jsonObject["schemaVersion"]
                ?.jsonPrimitive
                ?.int
        } catch (e: SerializationException) {
            return SnapshotRead.Unreadable(e.message ?: "not JSON")
        } catch (e: IllegalArgumentException) {
            return SnapshotRead.Unreadable(e.message ?: "no schemaVersion")
        } ?: return SnapshotRead.Unreadable("no schemaVersion")
    if (version > SCHEMA_VERSION) return SnapshotRead.NewerSchema(version)
    return try {
        SnapshotRead.Ok(SnapshotJson.decodeFromString(Snapshot.serializer(), text))
    } catch (e: SerializationException) {
        SnapshotRead.Unreadable(e.message ?: "does not match the schema")
    } catch (e: IllegalArgumentException) {
        SnapshotRead.Unreadable(e.message ?: "does not match the schema")
    }
}

fun writeSnapshot(snapshot: Snapshot): String = SnapshotJson.encodeToString(Snapshot.serializer(), snapshot)

/** ISO-8601 in UTC, as GitHub writes it. */
object IsoInstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("pyatiletka.Instant", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: Instant,
    ) = encoder.encodeString(value.toString())

    override fun deserialize(decoder: Decoder): Instant = Instant.parse(decoder.decodeString())
}
