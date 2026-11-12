package io.github.youndie.pyatiletka.core

import io.github.youndie.pyatiletka.core.sample.SampleSnapshot
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

// docs/features/feature-board.md — business rules and scenarios, on the sample data of screen-board §6.
class BoardTest {
    private val repos = SampleSnapshot.repos

    // Scenario: An ordinary day.
    @Test
    fun anOrdinaryDaySummarisesAsTheArtboardsDo() {
        assertEquals(
            Summary(repos = 12, done = 698, plan = 728, percent = 95, red = 1, openPullRequests = 12, waiting = 7),
            summarize(repos),
        )
        assertEquals("youndie/tacku", boardOrder(repos).first().fullName)
    }

    @Test
    fun theDefaultOrderIsTheTableOfTheBrief() {
        assertEquals(
            listOf(
                "tacku",
                "kachok",
                "appframe",
                "katcher",
                "sborka",
                "bochka",
                "toon-ui",
                "proba",
                "haul",
                "zavarnik",
                "kompot",
                "plakat-ui",
            ),
            boardOrder(repos.shuffled()).map { it.fullName.removePrefix("youndie/") },
        )
    }

    // Scenario: The search finds nothing.
    @Test
    fun aSearchThatFindsNothingLeavesTheSummaryAlone() {
        assertEquals(emptyList(), boardRows(repos, BoardFilter.All, "zzz"))
        assertEquals(95, summarize(repos).percent)
    }

    @Test
    fun theSearchIgnoresCase() {
        assertEquals(listOf("youndie/plakat-ui"), boardRows(repos, BoardFilter.All, "PLAKAT").map { it.fullName })
    }

    // Scenario: The plan is not quite done.
    @Test
    fun oneItemShortIsNinetyNine() {
        assertEquals(99, PlanCounts(done = 179, open = 1, wip = 0, question = 0, dropped = 0).percent)
        assertEquals(100, PlanCounts(done = 66, open = 0, wip = 0, question = 0, dropped = 3).percent)
        assertNull(PlanCounts(0, 0, 0, 0, 0).percent)
    }

    @Test
    fun theRedFilterTakesAFailingPullRequestTooButTheSummaryDoesNot() {
        assertEquals(
            listOf("youndie/tacku", "youndie/kachok"),
            boardRows(repos, BoardFilter.Red, "").map { it.fullName },
        )
        assertEquals(1, summarize(repos).red)
    }

    @Test
    fun theOtherFilters() {
        assertEquals(
            listOf("tacku", "kachok", "sborka", "bochka", "toon-ui", "proba"),
            boardRows(repos, BoardFilter.WithPullRequests, "").map { it.fullName.removePrefix("youndie/") },
        )
        assertEquals(
            listOf(
                "youndie/tacku",
                "youndie/kachok",
                "youndie/appframe",
                "youndie/katcher",
            ),
            boardRows(repos, BoardFilter.WaitingForMe, "").map {
                it.fullName
            },
        )
    }

    @Test
    fun flatAndNonePlansStayOutOfTheSummary() {
        val bochka = SampleSnapshot.repo("bochka")
        assertEquals(PlanForm.Flat, bochka.plan.form)
        assertEquals(summarize(repos - bochka - SampleSnapshot.repo("proba")).percent, summarize(repos).percent)
        assertTrue(summarize(repos).plan == 728)
    }
}
