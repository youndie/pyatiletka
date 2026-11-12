package io.github.youndie.pyatiletka.core

import io.github.youndie.pyatiletka.core.sample.SampleSnapshot
import kotlinx.datetime.asTimeZone
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

class TextsTest {
    @Test
    fun theSnapshotsMomentInMoscow() {
        val moscow = kotlinx.datetime.UtcOffset(hours = 3).asTimeZone()
        assertEquals(
            "Снимок 12 ноября 2026, 09:17",
            io.github.youndie.pyatiletka.core.screen.Texts
                .takenAt(SampleSnapshot.TAKEN_AT, moscow),
        )
        assertEquals(
            "11 ноября 2026, 21:17",
            io.github.youndie.pyatiletka.core.screen.Texts
                .moment(SampleSnapshot.STALE_TAKEN_AT, moscow),
        )
    }

    @Test
    fun plurals() {
        val t = io.github.youndie.pyatiletka.core.screen.Texts
        assertEquals("youndie · 12 репозиториев", t.owners(listOf("youndie"), 12))
        assertEquals("youndie · 21 репозиторий", t.owners(listOf("youndie"), 21))
        assertEquals("youndie · 3 репозитория", t.owners(listOf("youndie"), 3))
        assertEquals("1 падает", t.failingPullRequests(1))
    }

    // screen-repo §6: a pull request's age is its largest whole unit at the snapshot.
    @Test
    fun theCardsHeadingsAsTheCanvasWritesThem() {
        val t = io.github.youndie.pyatiletka.core.screen.Texts
        assertEquals("Проверки main · 2 упали", t.failedChecksHeading("main", 2))
        assertEquals("Проверки main · 1 упала", t.failedChecksHeading("main", 1))
        assertEquals("PR · 2", t.pullRequestsHeading(2))
        assertEquals("Пункты · 7 незакрытых", t.itemsHeading(7))
        assertEquals("Пункты · 21 незакрытый", t.itemsHeading(21))
        assertEquals(
            "177 из 184 выполнено",
            t.doneOfLong(PlanCounts(done = 177, open = 7, wip = 0, question = 0, dropped = 0)),
        )
        assertEquals("96\u00A0%", t.percent(96))
    }

    @Test
    fun theSettingsWordsAsTheCanvasWritesThem() {
        val t = io.github.youndie.pyatiletka.core.screen.Texts
        assertEquals(
            "Токен найден через gh",
            t.tokenFoundLine(
                io.github.youndie.pyatiletka.core.screen.TokenSource.GhCli,
            ),
        )
        assertEquals("15\u00A0мин", t.periodShort(15))
    }

    @Test
    fun theCardsAges() {
        val t = io.github.youndie.pyatiletka.core.screen.Texts
        assertEquals("2 дня", t.age(kotlin.time.Duration.parse("2d 3h")))
        assertEquals("5 часов", t.age(kotlin.time.Duration.parse("5h 59m")))
        assertEquals("1 час", t.age(kotlin.time.Duration.parse("1h")))
        assertEquals("21 минута", t.age(kotlin.time.Duration.parse("21m")))
        assertEquals("0 минут", t.age(kotlin.time.Duration.parse("-1m")))
        assertEquals("15 минут", t.period(15))
    }

    @Test
    fun theSampleCardsAreTheFourStates() {
        val s = io.github.youndie.pyatiletka.core.sample.SampleRepoStates
        assertEquals(
            io.github.youndie.pyatiletka.core.screen.RepoScreenState.entries,
            listOf(s.content, s.red, s.quiet, s.noPlan).map { it.screen },
        )
        val settings = io.github.youndie.pyatiletka.core.sample.SampleSettingsStates
        assertEquals(
            io.github.youndie.pyatiletka.core.screen.SettingsScreenState.entries,
            listOf(settings.content, settings.tokenMissing, settings.tokenRejected, settings.ownerNotFound).map {
                it.screen
            },
        )
    }
}
