package io.github.youndie.pyatiletka.core

import io.github.youndie.pyatiletka.core.sample.SampleSnapshot
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

// docs/features/feature-repo-detail.md — business rules and scenarios.
class RepoCardTest {
    @Test
    fun pullRequestsFailingFirstThenOldestFirst() {
        assertEquals(listOf(154, 152), cardPullRequests(SampleSnapshot.repo("kachok")).map { it.number })
        val shuffled = SampleSnapshot.repo("sborka").let { it.copy(pullRequests = it.pullRequests.reversed()) }
        assertEquals(listOf(236, 237, 238, 239, 240, 241), cardPullRequests(shuffled).map { it.number })
    }

    @Test
    fun itemsQuestionsThenWorkThenOpenByPriority() {
        val kachok =
            SampleSnapshot
                .repo(
                    "kachok",
                ).let { it.copy(plan = it.plan.copy(items = it.plan.items.reversed())) }
        assertEquals(
            listOf("B-190", "B-184", "B-176", "B-187", "B-178", "B-179", "B-180"),
            cardItems(kachok).map { it.id },
        )
    }

    // Scenario: A red main.
    @Test
    fun aRedMainCarriesItsFailedChecks() {
        val tacku = SampleSnapshot.repo("tacku")
        assertEquals(MainStatus.Red, tacku.main)
        assertEquals(listOf("server / test", "client / tck"), tacku.failedChecks.map { it.name })
        assertEquals(listOf("B-61", "B-58", "B-53"), cardItems(tacku).take(3).map { it.id })
    }

    // Scenario: Nothing to do.
    @Test
    fun plakatUiIsQuiet() {
        assertTrue(SampleSnapshot.repo("plakat-ui").isQuiet)
        assertFalse(SampleSnapshot.repo("kachok").isQuiet)
        assertFalse(SampleSnapshot.repo("proba").isQuiet)
    }

    // Scenario: No plan in the repository.
    @Test
    fun probaHasNoPlanAndOnePullRequest() {
        val proba = SampleSnapshot.repo("proba")
        assertEquals(PlanForm.None, proba.plan.form)
        assertEquals(listOf(49), cardPullRequests(proba).map { it.number })
    }
}
