package io.github.youndie.pyatiletka.app.data

import io.github.youndie.pyatiletka.core.SnapshotRead
import io.github.youndie.pyatiletka.core.readSnapshot

/** One HTTP answer, as much of it as the page needs. A null [status] is a request that never got one. */
data class PageResponse(
    val status: Int?,
    val body: String,
)

/**
 * The web's source: `snapshot.json` next to the page (docs/api/endpoint-snapshot.md). [get] is the
 * browser's fetch on wasmJs and a fake in tests; the mapping of what it answers is here, in common code.
 */
class PageSnapshotSource(
    private val get: suspend () -> PageResponse,
) : SnapshotSource {
    override suspend fun fetch(): Fetched {
        val response = get()
        return when (response.status) {
            null -> {
                Fetched.Failed(RefreshFailure.NoNetwork)
            }

            200 -> {
                when (val read = readSnapshot(response.body)) {
                    is SnapshotRead.Ok -> Fetched.Ok(read.snapshot)
                    is SnapshotRead.NewerSchema -> Fetched.Failed(RefreshFailure.NewerSchema(read.found))
                    is SnapshotRead.Unreadable -> Fetched.Failed(RefreshFailure.Other("snapshot.json: ${read.reason}"))
                }
            }

            404 -> {
                Fetched.Failed(RefreshFailure.NotPublished)
            }

            else -> {
                Fetched.Failed(RefreshFailure.Other("snapshot.json: HTTP ${response.status}"))
            }
        }
    }
}
