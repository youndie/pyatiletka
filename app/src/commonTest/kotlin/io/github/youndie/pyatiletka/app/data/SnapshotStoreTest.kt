package io.github.youndie.pyatiletka.app.data

import io.github.youndie.pyatiletka.core.SCHEMA_VERSION
import io.github.youndie.pyatiletka.core.Snapshot
import io.github.youndie.pyatiletka.core.sample.SampleSnapshot
import io.github.youndie.pyatiletka.core.writeSnapshot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

// docs/features/feature-refresh.md — the scenarios, on fake sources and a clock set to the sample's morning.
@OptIn(ExperimentalCoroutinesApi::class)
class SnapshotStoreTest {
    private val morning = SampleSnapshot.TAKEN_AT + 5.minutes
    private val clock = FixedClock(morning)

    private class FixedClock(
        var now: Instant,
    ) : Clock {
        override fun now(): Instant = now
    }

    private class FakeSource(
        var next: Fetched,
    ) : SnapshotSource {
        var calls = 0

        override suspend fun fetch(): Fetched {
            calls++
            return next
        }
    }

    private class MemoryCache(
        var snapshot: Snapshot? = null,
    ) : SnapshotCache {
        override suspend fun load() = snapshot

        override suspend fun save(snapshot: Snapshot) {
            this.snapshot = snapshot
        }
    }

    private fun TestScope.store(
        source: SnapshotSource,
        cache: SnapshotCache = NoCache,
        period: kotlin.time.Duration? = null,
    ) = SnapshotStore(source, cache, backgroundScope, desktopStaleAfter(15.minutes), period, clock)

    @Test
    fun aGoodSnapshotIsFreshAndCached() =
        runTest {
            val cache = MemoryCache()
            val s = store(FakeSource(Fetched.Ok(SampleSnapshot.snapshot)), cache).apply { start() }
            runCurrent()
            assertEquals(Data.Ready(SampleSnapshot.snapshot, stale = null, refreshing = false), s.data.value)
            assertEquals(SampleSnapshot.snapshot, cache.snapshot)
        }

    // Scenario: The network is gone.
    @Test
    fun theNetworkIsGoneAndYesterdaysSnapshotStays() =
        runTest {
            val s =
                store(
                    FakeSource(Fetched.Failed(RefreshFailure.NoNetwork)),
                    MemoryCache(SampleSnapshot.stale),
                ).apply { start() }
            runCurrent()
            val ready = assertIs<Data.Ready>(s.data.value)
            assertEquals(SampleSnapshot.STALE_TAKEN_AT, ready.snapshot.takenAt)
            assertEquals(Staleness.LastAttemptFailed(RefreshFailure.NoNetwork), ready.stale)
        }

    // Scenario: First start without network.
    @Test
    fun aFirstStartWithoutNetworkIsAnError() =
        runTest {
            val source = FakeSource(Fetched.Failed(RefreshFailure.NoNetwork))
            val s = store(source).apply { start() }
            runCurrent()
            assertEquals(Data.Failed(RefreshFailure.NoNetwork, refreshing = false), s.data.value)
            source.next = Fetched.Ok(SampleSnapshot.snapshot)
            s.refresh()
            runCurrent()
            assertIs<Data.Ready>(s.data.value)
        }

    @Test
    fun aSnapshotOlderThanTwoPeriodsIsStaleEvenWhenItWasReadWell() =
        runTest {
            clock.now = SampleSnapshot.TAKEN_AT + 31.minutes
            val s = store(FakeSource(Fetched.Ok(SampleSnapshot.snapshot))).apply { start() }
            runCurrent()
            assertEquals(Staleness.TooOld(SampleSnapshot.TAKEN_AT), assertIs<Data.Ready>(s.data.value).stale)
        }

    @Test
    fun theTimerRefreshesEveryPeriod() =
        runTest {
            val source = FakeSource(Fetched.Ok(SampleSnapshot.snapshot))
            store(source, period = 15.minutes).start()
            runCurrent()
            assertEquals(1, source.calls)
            advanceTimeBy(15.minutes)
            runCurrent()
            assertEquals(2, source.calls)
            advanceTimeBy(1.hours)
            runCurrent()
            assertEquals(6, source.calls)
        }

    @Test
    fun theStalenessRuleItself() {
        val taken = Instant.parse("2026-11-12T06:00:00Z")
        assertEquals(null, staleness(taken, taken + 3.hours, WEB_STALE_AFTER, null))
        assertEquals(Staleness.TooOld(taken), staleness(taken, taken + 3.hours + 1.minutes, WEB_STALE_AFTER, null))
        assertEquals(
            Staleness.LastAttemptFailed(RefreshFailure.NotPublished),
            staleness(taken, taken, WEB_STALE_AFTER, RefreshFailure.NotPublished),
        )
    }
}

// The web's source: what `snapshot.json` answers, mapped.
class PageSnapshotSourceTest {
    // Scenario: The snapshot is not published yet.
    @Test
    fun aMissingSnapshotIsNotPublished() =
        runTest {
            assertEquals(
                Fetched.Failed(RefreshFailure.NotPublished),
                PageSnapshotSource { PageResponse(404, "Not Found") }.fetch(),
            )
        }

    // Scenario: Unknown schema version.
    @Test
    fun aNewerSchemaSaysThePageIsOlder() =
        runTest {
            val newer = """{"schemaVersion": ${SCHEMA_VERSION + 1}}"""
            assertEquals(
                Fetched.Failed(RefreshFailure.NewerSchema(SCHEMA_VERSION + 1)),
                PageSnapshotSource {
                    PageResponse(200, newer)
                }.fetch(),
            )
        }

    @Test
    fun aGoodAnswerIsTheSnapshot() =
        runTest {
            assertEquals(
                Fetched.Ok(SampleSnapshot.snapshot),
                PageSnapshotSource {
                    PageResponse(200, writeSnapshot(SampleSnapshot.snapshot))
                }.fetch(),
            )
        }

    @Test
    fun noAnswerAtAllIsNoNetwork() =
        runTest {
            assertEquals(
                Fetched.Failed(RefreshFailure.NoNetwork),
                PageSnapshotSource { PageResponse(null, "") }.fetch(),
            )
        }
}
