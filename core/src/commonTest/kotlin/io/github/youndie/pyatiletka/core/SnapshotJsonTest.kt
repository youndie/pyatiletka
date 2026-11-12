package io.github.youndie.pyatiletka.core

import io.github.youndie.pyatiletka.core.sample.SampleSnapshot
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

// docs/api/endpoint-snapshot.md — the schema and its errors.
class SnapshotJsonTest {
    @Test
    fun theSampleSurvivesARoundTrip() {
        val read = readSnapshot(writeSnapshot(SampleSnapshot.snapshot))
        assertEquals(SnapshotRead.Ok(SampleSnapshot.snapshot), read)
    }

    @Test
    fun timesAreIsoUtc() {
        val text = writeSnapshot(SampleSnapshot.snapshot)
        assertEquals(true, text.contains("\"takenAt\":\"2026-11-12T06:17:00Z\""))
    }

    // Scenario (feature-refresh): Unknown schema version.
    @Test
    fun aNewerSchemaIsNamedNotGuessed() {
        assertEquals(
            SnapshotRead.NewerSchema(SCHEMA_VERSION + 1),
            readSnapshot("""{"schemaVersion": ${SCHEMA_VERSION + 1}, "anything": []}"""),
        )
    }

    @Test
    fun unknownKeysAreIgnored() {
        val text = writeSnapshot(SampleSnapshot.snapshot).replaceFirst("{", """{"addedLater":true,""")
        assertIs<SnapshotRead.Ok>(readSnapshot(text))
    }

    @Test
    fun garbageIsUnreadable() {
        assertIs<SnapshotRead.Unreadable>(readSnapshot("<html>404</html>"))
        assertIs<SnapshotRead.Unreadable>(readSnapshot("""{"repos": []}"""))
        assertIs<SnapshotRead.Unreadable>(readSnapshot("""{"schemaVersion": 1}"""))
    }
}
