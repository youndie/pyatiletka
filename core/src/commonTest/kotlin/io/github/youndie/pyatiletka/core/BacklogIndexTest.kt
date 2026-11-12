package io.github.youndie.pyatiletka.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

// Real indexes, counted independently of the parser (marks per section, by hand from the files at
// the commits in RealIndexes.kt), so a regression in the regexes cannot agree with itself.
class BacklogIndexTest {
    @Test
    fun appframeHasThreeQuestionsAndTwoOpen() {
        val plan = parseBacklogIndex(appframeIndex)!!
        assertEquals(PlanCounts(done = 2, open = 2, wip = 0, question = 3, dropped = 0), plan.counts)
        assertEquals(listOf("B-02", "B-07", "B-03", "B-04", "B-05"), plan.items.map { it.id })
        val b07 = plan.items.single { it.id == "B-07" }
        assertEquals(ItemStatus.Question, b07.status)
        assertEquals("P1", b07.priority)
        assertEquals(
            "Owner: one sitting at Windows 11 — snapping, two monitors and the edge scrollbar on an AppFrame window, checked by hand",
            b07.title,
        )
    }

    @Test
    fun kachokCountsDroppedOutsideThePlan() {
        val counts = parseBacklogIndex(kachokIndex)!!.counts!!
        assertEquals(PlanCounts(done = 177, open = 4, wip = 2, question = 1, dropped = 6), counts)
        assertEquals(184, counts.size)
        assertEquals(96, counts.percent)
    }

    @Test
    fun tackuReadsCyrillicTitles() {
        val plan = parseBacklogIndex(tackuIndex)!!
        assertEquals(PlanCounts(done = 48, open = 7, wip = 4, question = 1, dropped = 0), plan.counts)
        assertEquals(
            "Замерить, где на самом деле меняют статус: на доске или в задаче",
            plan.items
                .single {
                    it.id ==
                        "B-36"
                }.title,
        )
        assertEquals(80, plan.counts!!.percent)
    }

    @Test
    fun plakatUiWithNothingOpenIsAHundred() {
        val plan = parseBacklogIndex(plakatUiIndex)!!
        assertEquals(PlanCounts(done = 73, open = 0, wip = 0, question = 0, dropped = 1), plan.counts)
        assertEquals(emptyList(), plan.items)
        assertEquals(100, plan.counts!!.percent)
    }

    @Test
    fun itemLinksGoThroughTheGivenResolver() {
        val plan = parseBacklogIndex(appframeIndex) { "https://github.com/youndie/appframe/blob/main/$it" }!!
        assertEquals(
            "https://github.com/youndie/appframe/blob/main/docs/backlog/B-02-windows-snap.md",
            plan.items.first().url,
        )
    }

    // docs-bootstrap never puts a closed mark in the open table, but a hand-edited index can: such a
    // row is counted where its mark says and never listed as unclosed.
    @Test
    fun aClosedMarkInTheOpenTableIsCountedNotListed() {
        val text =
            """
            <!-- BEGIN INDEX -->
            | [B-01](docs/backlog/B-01-a.md) `[-]` | Refused | P2 | S | - |
            | [B-02](docs/backlog/B-02-b.md) `[x]` | Done | P2 | S | - |
            | [B-03](docs/backlog/B-03-c.md) `[ ]` | Open | P2 | S | - |
            <!-- END INDEX -->
            """.trimIndent()
        val plan = parseBacklogIndex(text)!!
        assertEquals(PlanCounts(done = 1, open = 1, wip = 0, question = 0, dropped = 1), plan.counts)
        assertEquals(listOf("B-03"), plan.items.map { it.id })
    }

    @Test
    fun aFileWithoutTheMarkersIsNotAnIndex() {
        assertNull(parseBacklogIndex("# Backlog\n\n- [ ] M-01 something\n"))
    }

    @Test
    fun aTitleWithAPipeStaysWhole() {
        val text =
            """
            <!-- BEGIN INDEX -->
            | [B-09](docs/backlog/B-09-x.md) `[ ]` | Read a | b in a title | P2 | S | - |
            <!-- END INDEX -->
            """.trimIndent()
        assertEquals("Read a | b in a title", parseBacklogIndex(text)!!.items.single().title)
    }
}
