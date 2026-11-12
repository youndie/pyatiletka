package io.github.youndie.pyatiletka.core.sample

import io.github.youndie.pyatiletka.core.BoardFilter
import io.github.youndie.pyatiletka.core.boardRows
import io.github.youndie.pyatiletka.core.screen.BoardScreenState
import io.github.youndie.pyatiletka.core.screen.BoardUiState
import io.github.youndie.pyatiletka.core.screen.Problem
import io.github.youndie.pyatiletka.core.screen.StaleNotice
import io.github.youndie.pyatiletka.core.summarize

/**
 * The board's states on the sample data, built from core as the app's `boardUiState` builds them, for
 * both skins' goldens: a skin depends on core and its kit only (research D3), so it cannot borrow the
 * app's mapping. Each is the artboard of the same name (screen-board, section 1.1).
 */
object SampleBoardStates {
    private val snapshot = SampleSnapshot.snapshot
    private val owners = listOf("youndie")

    val content: BoardUiState =
        BoardUiState(
            screen = BoardScreenState.Content,
            summary = summarize(snapshot.repos),
            rows = boardRows(snapshot.repos, BoardFilter.All, ""),
            takenAt = snapshot.takenAt,
            owners = owners,
        )

    val loading = BoardUiState(BoardScreenState.Loading, owners = owners, refreshing = true)

    val empty = content.copy(screen = BoardScreenState.Empty, summary = summarize(emptyList()), rows = emptyList())

    val noMatches = content.copy(screen = BoardScreenState.NoMatches, query = "zzz", rows = emptyList())

    val stale =
        content.copy(
            screen = BoardScreenState.Stale,
            takenAt = SampleSnapshot.STALE_TAKEN_AT,
            stale = StaleNotice(SampleSnapshot.STALE_TAKEN_AT, Problem(Problem.Kind.NoNetwork)),
        )

    val error = BoardUiState(BoardScreenState.Error, owners = owners, problem = Problem(Problem.Kind.NoNetwork))

    val noToken = BoardUiState(BoardScreenState.NoToken, owners = owners, problem = Problem(Problem.Kind.NoToken))

    val appearanceSheet = content.copy(screen = BoardScreenState.AppearanceSheet)
}
