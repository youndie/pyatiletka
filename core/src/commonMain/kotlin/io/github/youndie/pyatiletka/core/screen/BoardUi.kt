package io.github.youndie.pyatiletka.core.screen

import io.github.youndie.pyatiletka.core.BoardFilter
import io.github.youndie.pyatiletka.core.Repo
import io.github.youndie.pyatiletka.core.Summary
import kotlin.time.Instant

/** The states of screen-board, section 1 — one name per artboard, and nothing the document does not list. */
enum class BoardScreenState { Loading, Empty, Content, NoMatches, Stale, Error, NoToken, AppearanceSheet }

/**
 * What a skin needs to draw the board. [summary] describes the portfolio and never follows the filter
 * or the search; [rows] are already ordered, filtered and searched. [stale] is set when the snapshot
 * in hand is old or the last attempt to replace it failed; [problem] is the reason of an [BoardScreenState.Error].
 */
data class BoardUiState(
    val screen: BoardScreenState,
    val summary: Summary? = null,
    val rows: List<Repo> = emptyList(),
    val filter: BoardFilter = BoardFilter.All,
    val query: String = "",
    val takenAt: Instant? = null,
    val stale: StaleNotice? = null,
    val problem: Problem? = null,
    val refreshing: Boolean = false,
    /** The repository whose card is open: a panel beside the board when wide, its own screen when narrow. */
    val selected: String? = null,
)

/** The bar under the header: the snapshot's moment and why it is not fresh. */
data class StaleNotice(
    val takenAt: Instant,
    /** Null when the snapshot is merely old; otherwise why the last attempt failed. */
    val problem: Problem?,
)
