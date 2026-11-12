package io.github.youndie.pyatiletka.app.data

import io.github.youndie.pyatiletka.core.Snapshot

/** Where a snapshot comes from: the collector in-process on the desktop, `snapshot.json` on the web. */
fun interface SnapshotSource {
    suspend fun fetch(): Fetched
}

sealed interface Fetched {
    data class Ok(
        val snapshot: Snapshot,
    ) : Fetched

    data class Failed(
        val failure: RefreshFailure,
    ) : Fetched
}

/** Why the last attempt to get a snapshot did not produce one (feature-refresh, feature-settings). */
sealed interface RefreshFailure {
    /** GitHub (desktop) or the page's host (web) could not be reached. */
    data object NoNetwork : RefreshFailure

    /** Desktop: neither `gh auth token` nor `PYATILETKA_GITHUB_TOKEN` gave a token. */
    data object NoToken : RefreshFailure

    /** Desktop: GitHub answered 401. */
    data object TokenRejected : RefreshFailure

    /** Desktop: a login in the owners' list is neither a user nor an organisation. */
    data class OwnerNotFound(
        val login: String,
    ) : RefreshFailure

    /** Web: `snapshot.json` is not there yet (404). */
    data object NotPublished : RefreshFailure

    /** The snapshot's `schemaVersion` is newer than this build: the page is older than the snapshot. */
    data class NewerSchema(
        val found: Int,
    ) : RefreshFailure

    /** Anything else: an answer that is not a snapshot, a refusal from GitHub. */
    data class Other(
        val reason: String,
    ) : RefreshFailure
}

/** The last good snapshot, kept across starts (desktop: a file in the OS cache directory). */
interface SnapshotCache {
    suspend fun load(): Snapshot?

    suspend fun save(snapshot: Snapshot)
}

/** For the web, which keeps nothing: the page reads `snapshot.json` on every open. */
object NoCache : SnapshotCache {
    override suspend fun load(): Snapshot? = null

    override suspend fun save(snapshot: Snapshot) = Unit
}
