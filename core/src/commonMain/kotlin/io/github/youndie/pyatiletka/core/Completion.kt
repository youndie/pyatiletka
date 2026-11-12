package io.github.youndie.pyatiletka.core

/** done + open + wip + question; `dropped` is not part of the plan. */
val PlanCounts.size: Int get() = done + open + wip + question

/** open + wip + question: what is still to do, and what keeps 100 % off the board. */
val PlanCounts.unclosed: Int get() = open + wip + question

/**
 * Completion in whole percent, rounded down, so 179 of 180 reads 99 % and 100 % means nothing is
 * left (docs/features/feature-board.md). Null for an empty plan, which has nothing to complete.
 */
val PlanCounts.percent: Int?
    get() {
        val plan = size
        if (plan == 0) return null
        return if (unclosed == 0) 100 else minOf(done * 100 / plan, 99)
    }

/** Items waiting for the owner: those marked as a question. */
val Repo.waiting: Int get() = plan.counts?.question ?: 0
