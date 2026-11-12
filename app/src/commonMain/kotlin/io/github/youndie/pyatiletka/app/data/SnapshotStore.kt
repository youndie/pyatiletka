package io.github.youndie.pyatiletka.app.data

import io.github.youndie.pyatiletka.core.Snapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

/**
 * What the board has to show, before any screen decides how (feature-refresh). The last good snapshot
 * is never dropped for a failure: a failure with a snapshot in hand is that snapshot, stale, with the
 * reason; only a failure with nothing in hand is [Data.Failed].
 */
sealed interface Data {
    /** Nothing in hand yet, and the first attempt is under way. */
    data object Loading : Data

    data class Ready(
        val snapshot: Snapshot,
        /** Why it is stale, or null when it is fresh. */
        val stale: Staleness?,
        /** An attempt is under way; the snapshot stays on screen until a new one is whole (B-02: about ten seconds). */
        val refreshing: Boolean,
    ) : Data

    data class Failed(
        val failure: RefreshFailure,
        val refreshing: Boolean,
    ) : Data
}

/** A snapshot is stale when it is too old, or when the last attempt to replace it failed. */
sealed interface Staleness {
    data class TooOld(
        val takenAt: Instant,
    ) : Staleness

    data class LastAttemptFailed(
        val failure: RefreshFailure,
    ) : Staleness
}

/**
 * How old a snapshot may be before it is called stale: two refresh periods on the desktop, three hours
 * on the web, where the hourly schedule may run late (research, risk 2).
 */
fun desktopStaleAfter(period: Duration): Duration = period * 2

val WEB_STALE_AFTER: Duration = 3.hours

fun staleness(
    takenAt: Instant,
    now: Instant,
    staleAfter: Duration,
    lastFailure: RefreshFailure?,
): Staleness? =
    when {
        lastFailure != null -> Staleness.LastAttemptFailed(lastFailure)
        now - takenAt > staleAfter -> Staleness.TooOld(takenAt)
        else -> null
    }

/**
 * Holds [data]: reads the cache, then the source, on [start]; again every [period] when it is not null
 * (the desktop's timer), and on [refresh] (the button, pull-to-refresh). One attempt at a time: a press
 * during an attempt joins it rather than starting a second one.
 */
class SnapshotStore(
    private val source: SnapshotSource,
    private val cache: SnapshotCache,
    private val scope: CoroutineScope,
    private val staleAfter: Duration,
    private val period: Duration? = null,
    private val clock: Clock = Clock.System,
) {
    private val state = MutableStateFlow<Data>(Data.Loading)
    val data: StateFlow<Data> = state.asStateFlow()

    private val attempt = Mutex()
    private var lastFailure: RefreshFailure? = null
    private var timer: Job? = null

    fun start() {
        scope.launch {
            cache.load()?.let { cached -> state.value = ready(cached, refreshing = true) }
            attempt()
            period?.let { every ->
                timer =
                    scope.launch {
                        while (true) {
                            delay(every)
                            attempt()
                        }
                    }
            }
        }
    }

    fun refresh() {
        scope.launch { attempt() }
    }

    fun stop() {
        timer?.cancel()
    }

    private suspend fun attempt() {
        if (attempt.isLocked) return
        attempt.withLock {
            state.update { it.refreshing(true) }
            when (val fetched = source.fetch()) {
                is Fetched.Ok -> {
                    lastFailure = null
                    cache.save(fetched.snapshot)
                    state.value = ready(fetched.snapshot, refreshing = false)
                }

                is Fetched.Failed -> {
                    lastFailure = fetched.failure
                    state.update { current ->
                        when (current) {
                            is Data.Ready -> ready(current.snapshot, refreshing = false)
                            else -> Data.Failed(fetched.failure, refreshing = false)
                        }
                    }
                }
            }
        }
    }

    private fun ready(
        snapshot: Snapshot,
        refreshing: Boolean,
    ): Data.Ready = Data.Ready(snapshot, staleness(snapshot.takenAt, clock.now(), staleAfter, lastFailure), refreshing)

    private fun Data.refreshing(on: Boolean): Data =
        when (this) {
            Data.Loading -> this
            is Data.Ready -> copy(refreshing = on)
            is Data.Failed -> copy(refreshing = on)
        }
}
