package io.github.youndie.pyatiletka.app

import io.github.youndie.pyatiletka.app.data.Data
import io.github.youndie.pyatiletka.app.data.RefreshFailure
import io.github.youndie.pyatiletka.app.data.Staleness
import io.github.youndie.pyatiletka.core.BoardFilter
import io.github.youndie.pyatiletka.core.Summary
import io.github.youndie.pyatiletka.core.sample.SampleSnapshot
import io.github.youndie.pyatiletka.core.screen.BoardScreenState
import io.github.youndie.pyatiletka.core.screen.Problem
import io.github.youndie.pyatiletka.core.screen.RepoScreenState
import io.github.youndie.pyatiletka.core.screen.StaleNotice
import io.github.youndie.pyatiletka.core.screen.repoUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

// B-09's acceptance: every state of screen-board and screen-repo, section 1, reached from the sample
// data — the same data the artboards and the goldens are drawn from.
@OptIn(ExperimentalCoroutinesApi::class)
class ScreenStatesTest {
    private val fresh = Data.Ready(SampleSnapshot.snapshot, stale = null, refreshing = false)

    @Test
    fun loading() = assertEquals(BoardScreenState.Loading, boardUiState(Data.Loading, BoardFlags()).screen)

    @Test
    fun content() {
        val ui = boardUiState(fresh, BoardFlags())
        assertEquals(BoardScreenState.Content, ui.screen)
        assertEquals(Summary(12, 698, 728, 95, 1, 12, 7), ui.summary)
        assertEquals(12, ui.rows.size)
        assertEquals(SampleSnapshot.TAKEN_AT, ui.takenAt)
    }

    @Test
    fun empty() =
        assertEquals(
            BoardScreenState.Empty,
            boardUiState(fresh.copy(snapshot = SampleSnapshot.snapshot.copy(repos = emptyList())), BoardFlags()).screen,
        )

    @Test
    fun noMatchesKeepsTheSummaryAndTheFilter() {
        val ui = boardUiState(fresh, BoardFlags(filter = BoardFilter.Red, query = "zzz"))
        assertEquals(BoardScreenState.NoMatches, ui.screen)
        assertEquals(95, ui.summary!!.percent)
        assertEquals(BoardFilter.Red, ui.filter)
    }

    @Test
    fun staleShowsYesterdaysMomentAndTheReason() {
        val stale =
            Data.Ready(
                SampleSnapshot.stale,
                Staleness.LastAttemptFailed(RefreshFailure.NoNetwork),
                refreshing = false,
            )
        val ui = boardUiState(stale, BoardFlags())
        assertEquals(BoardScreenState.Stale, ui.screen)
        assertEquals(StaleNotice(SampleSnapshot.STALE_TAKEN_AT, Problem(Problem.Kind.NoNetwork)), ui.stale)
        assertEquals(12, ui.rows.size)
    }

    @Test
    fun error() {
        val ui = boardUiState(Data.Failed(RefreshFailure.NotPublished, refreshing = false), BoardFlags())
        assertEquals(BoardScreenState.Error, ui.screen)
        assertEquals(Problem(Problem.Kind.NotPublished), ui.problem)
    }

    @Test
    fun noToken() =
        assertEquals(
            BoardScreenState.NoToken,
            boardUiState(Data.Failed(RefreshFailure.NoToken, false), BoardFlags()).screen,
        )

    @Test
    fun appearanceSheetIsDrawnOverTheBoard() {
        val ui = boardUiState(fresh, BoardFlags(appearanceOpen = true))
        assertEquals(BoardScreenState.AppearanceSheet, ui.screen)
        assertEquals(12, ui.rows.size)
    }

    @Test
    fun theFourCards() {
        assertEquals(RepoScreenState.Content, repoUiState(SampleSnapshot.repo("kachok")).screen)
        assertEquals(RepoScreenState.Red, repoUiState(SampleSnapshot.repo("tacku")).screen)
        assertEquals(RepoScreenState.Quiet, repoUiState(SampleSnapshot.repo("plakat-ui")).screen)
        assertEquals(RepoScreenState.NoPlan, repoUiState(SampleSnapshot.repo("proba")).screen)
        assertEquals(96, repoUiState(SampleSnapshot.repo("kachok")).percent)
    }

    // The model: a row opens the card, the card follows the snapshot, the place survives a refresh.
    @Test
    fun aRowOpensItsCardAndThePlaceSurvivesANewSnapshot() =
        runTest {
            val data = MutableStateFlow<Data>(fresh)
            var refreshes = 0
            val model = BoardModel(data, backgroundScope) { refreshes++ }
            model.filter(BoardFilter.WithPullRequests)
            model.open("youndie/kachok")
            runCurrent()
            assertEquals(RepoScreenState.Content, model.card.value!!.screen)
            data.value =
                Data.Ready(
                    SampleSnapshot.stale,
                    Staleness.LastAttemptFailed(RefreshFailure.NoNetwork),
                    refreshing = false,
                )
            runCurrent()
            assertEquals("youndie/kachok", model.state.value.selected)
            assertEquals(BoardFilter.WithPullRequests, model.state.value.filter)
            assertEquals(BoardScreenState.Stale, model.state.value.screen)
            model.refresh()
            assertEquals(1, refreshes)
            model.closeCard()
            runCurrent()
            assertNull(model.card.value)
        }

    @Test
    fun resettingTheSearchKeepsTheFilter() =
        runTest {
            val model = BoardModel(MutableStateFlow(fresh), backgroundScope) {}
            model.filter(BoardFilter.Red)
            model.search("zzz")
            runCurrent()
            assertEquals(BoardScreenState.NoMatches, model.state.value.screen)
            model.resetSearch()
            runCurrent()
            assertEquals(BoardScreenState.Content, model.state.value.screen)
            assertEquals(
                listOf("youndie/tacku", "youndie/kachok"),
                model.state.value.rows
                    .map { it.fullName },
            )
        }
}
