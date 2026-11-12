package io.github.youndie.pyatiletka.core

import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * The whole board at one moment — the one contract between the collector, the desktop app and the
 * web page (docs/api/endpoint-snapshot.md). Times are ISO-8601 UTC. [schemaVersion] is checked
 * before anything else is read: a client older than the snapshot shows an error, not an empty board.
 */
@Serializable
data class Snapshot(
    val schemaVersion: Int,
    @Serializable(with = IsoInstantSerializer::class) val takenAt: Instant,
    val owners: List<String>,
    val repos: List<Repo>,
)

/** One repository's row and card. [fullName] is `owner/name`. */
@Serializable
data class Repo(
    val fullName: String,
    val visibility: Visibility,
    val url: String,
    val defaultBranch: String,
    @Serializable(with = IsoInstantSerializer::class) val pushedAt: Instant,
    val main: MainStatus,
    /** Only when [main] is [MainStatus.Red]. */
    val failedChecks: List<FailedCheck> = emptyList(),
    val pullRequests: List<PullRequest> = emptyList(),
    val plan: Plan = Plan.NONE,
)

@Serializable
enum class Visibility { Public, Private }

/** The default branch's head commit. [None]: the commit carries no checks at all. */
@Serializable
enum class MainStatus { Green, Red, Running, None }

@Serializable
data class FailedCheck(
    val name: String,
    val url: String,
)

@Serializable
data class PullRequest(
    val number: Int,
    val title: String,
    val author: Author,
    val isDraft: Boolean,
    @Serializable(with = IsoInstantSerializer::class) val createdAt: Instant,
    val checks: PrChecks,
    val url: String,
)

@Serializable
enum class Author { Human, Bot }

/** The checks on a pull request's last commit. [NoChecks]: none ran. */
@Serializable
enum class PrChecks { Ready, Failing, Running, NoChecks }

/**
 * How a repository keeps its plan: [PlanForm.Items] is a docs-bootstrap index (`backlog.md`), the
 * only form the board counts; [PlanForm.Flat] is one hand-kept `BACKLOG.md`; [PlanForm.None] is no
 * backlog. [counts] and [items] are filled for [PlanForm.Items] only; [items] holds the unclosed ones.
 */
@Serializable
data class Plan(
    val form: PlanForm,
    val counts: PlanCounts? = null,
    val items: List<BacklogItem> = emptyList(),
) {
    companion object {
        val NONE: Plan = Plan(PlanForm.None)
        val FLAT: Plan = Plan(PlanForm.Flat)
    }
}

@Serializable
enum class PlanForm { Items, Flat, None }

@Serializable
data class PlanCounts(
    val done: Int,
    val open: Int,
    val wip: Int,
    val question: Int,
    val dropped: Int,
)

/** An unclosed backlog item. [priority] is the index's column as written (`P1`, `infra`), null when empty. */
@Serializable
data class BacklogItem(
    val id: String,
    val title: String,
    val status: ItemStatus,
    val priority: String?,
    val url: String,
)

@Serializable
enum class ItemStatus { Open, Wip, Question }
