package io.github.youndie.pyatiletka.core

/** The summary above the board: it describes the portfolio, never what the filter or the search show. */
data class Summary(
    val repos: Int,
    val done: Int,
    val plan: Int,
    /** Rounded down like a repository's; null when no repository has a counted plan. */
    val percent: Int?,
    /** Repositories whose default branch is red — not counting red pull requests (feature-board, quirks). */
    val red: Int,
    val openPullRequests: Int,
    val waiting: Int,
)

/** The portfolio's summary: completion over repositories with an `Items` plan only. */
fun summarize(repos: List<Repo>): Summary {
    val counted = repos.mapNotNull { it.plan.counts.takeIf { _ -> it.plan.form == PlanForm.Items } }
    val done = counted.sumOf { it.done }
    val plan = counted.sumOf { it.size }
    val unclosed = counted.sumOf { it.unclosed }
    return Summary(
        repos = repos.size,
        done = done,
        plan = plan,
        percent = PlanCounts(done, unclosed, 0, 0, 0).percent,
        red = repos.count { it.main == MainStatus.Red },
        openPullRequests = repos.sumOf { it.pullRequests.size },
        waiting = repos.sumOf { it.waiting },
    )
}

/** The board's filter chips. [Red] takes a red default branch or a failing pull request. */
enum class BoardFilter { All, Red, WithPullRequests, WaitingForMe }

fun Repo.passes(filter: BoardFilter): Boolean =
    when (filter) {
        BoardFilter.All -> true
        BoardFilter.Red -> main == MainStatus.Red || pullRequests.any { it.checks == PrChecks.Failing }
        BoardFilter.WithPullRequests -> pullRequests.isNotEmpty()
        BoardFilter.WaitingForMe -> waiting > 0
    }

/** The search: a case-insensitive substring of `owner/name`; a blank query finds everything. */
fun Repo.matches(query: String): Boolean = query.isBlank() || fullName.contains(query.trim(), ignoreCase = true)

/**
 * The default order of the board's rows: a red default branch first, then a failing pull request, then
 * items waiting for the owner, then any open pull request, then the rest — and inside each group the
 * most recently pushed first (research §3.1, item 2: the brief names no direction).
 */
fun boardOrder(repos: List<Repo>): List<Repo> =
    repos.sortedWith(compareBy<Repo> { it.urgency }.thenByDescending { it.pushedAt })

private val Repo.urgency: Int
    get() =
        when {
            main == MainStatus.Red -> 0
            pullRequests.any { it.checks == PrChecks.Failing } -> 1
            waiting > 0 -> 2
            pullRequests.isNotEmpty() -> 3
            else -> 4
        }

/** The board's rows as shown: ordered, then filtered and searched. */
fun boardRows(
    repos: List<Repo>,
    filter: BoardFilter,
    query: String,
): List<Repo> = boardOrder(repos).filter { it.passes(filter) && it.matches(query) }
