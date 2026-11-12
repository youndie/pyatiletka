package io.github.youndie.pyatiletka.core

/** The card's pull requests: failing, ready, running, without checks; inside each, the oldest first. */
fun cardPullRequests(repo: Repo): List<PullRequest> =
    repo.pullRequests.sortedWith(compareBy<PullRequest> { PR_ORDER.indexOf(it.checks) }.thenBy { it.createdAt })

/** The card's unclosed items: questions, then work in progress, then open; inside each, by priority, then by number. */
fun cardItems(repo: Repo): List<BacklogItem> =
    repo.plan.items.sortedWith(
        compareBy<BacklogItem> { ITEM_ORDER.indexOf(it.status) }
            .thenBy { priorityRank(it.priority) }
            .thenBy { it.id.removePrefix("B-").toIntOrNull() ?: Int.MAX_VALUE },
    )

/** Nothing to do: a green default branch, no open pull request and no unclosed item. */
val Repo.isQuiet: Boolean
    get() =
        main == MainStatus.Green && pullRequests.isEmpty() && plan.items.isEmpty() &&
            (plan.counts?.unclosed ?: 0) == 0

private val PR_ORDER = listOf(PrChecks.Failing, PrChecks.Ready, PrChecks.Running, PrChecks.NoChecks)
private val ITEM_ORDER = listOf(ItemStatus.Question, ItemStatus.Wip, ItemStatus.Open)

/** P0 before P1 … before P3, then `infra`, then anything unprioritised — docs-bootstrap's order. */
private fun priorityRank(priority: String?): Int =
    when (priority) {
        "P0" -> 0
        "P1" -> 1
        "P2" -> 2
        "P3" -> 3
        "infra" -> 4
        else -> 5
    }
