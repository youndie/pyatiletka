package io.github.youndie.pyatiletka.collector

import io.github.youndie.pyatiletka.core.Author
import io.github.youndie.pyatiletka.core.FailedCheck
import io.github.youndie.pyatiletka.core.MainStatus
import io.github.youndie.pyatiletka.core.Plan
import io.github.youndie.pyatiletka.core.PrChecks
import io.github.youndie.pyatiletka.core.PullRequest
import io.github.youndie.pyatiletka.core.Repo
import io.github.youndie.pyatiletka.core.Visibility
import io.github.youndie.pyatiletka.core.parseBacklogIndex
import kotlin.time.Instant

/** One repository of the answer as the board sees it. */
internal fun toRepo(node: GqlRepo): Repo {
    val branch = node.defaultBranchRef?.name ?: "main"
    val rollup = node.defaultBranchRef?.target?.statusCheckRollup
    val main = mainStatus(rollup?.state)
    return Repo(
        fullName = node.nameWithOwner,
        visibility = if (node.visibility == "PUBLIC") Visibility.Public else Visibility.Private,
        url = node.url,
        defaultBranch = branch,
        pushedAt = node.pushedAt?.let(Instant::parse) ?: Instant.DISTANT_PAST,
        main = main,
        failedChecks = if (main == MainStatus.Red) failedChecks(rollup) else emptyList(),
        pullRequests = node.pullRequests.nodes.map(::toPullRequest),
        plan = plan(node, branch),
    )
}

/** A commit's rollup: green when every check passed, red when one failed, running while one is pending, none without checks. */
internal fun mainStatus(state: String?): MainStatus =
    when (state) {
        null -> MainStatus.None
        "SUCCESS" -> MainStatus.Green
        "FAILURE", "ERROR" -> MainStatus.Red
        else -> MainStatus.Running
    }

private fun prChecks(state: String?): PrChecks =
    when (state) {
        null -> PrChecks.NoChecks
        "SUCCESS" -> PrChecks.Ready
        "FAILURE", "ERROR" -> PrChecks.Failing
        else -> PrChecks.Running
    }

private val FAILED_CONCLUSIONS = setOf("FAILURE", "TIMED_OUT", "CANCELLED", "ACTION_REQUIRED", "STARTUP_FAILURE")

private fun failedChecks(rollup: GqlRollup?): List<FailedCheck> =
    rollup?.contexts?.nodes.orEmpty().mapNotNull { c ->
        when (c.typename) {
            "CheckRun" -> {
                if (c.conclusion in
                    FAILED_CONCLUSIONS
                ) {
                    FailedCheck(c.name.orEmpty(), c.detailsUrl.orEmpty())
                } else {
                    null
                }
            }

            "StatusContext" -> {
                if (c.state == "FAILURE" ||
                    c.state == "ERROR"
                ) {
                    FailedCheck(c.context.orEmpty(), c.targetUrl.orEmpty())
                } else {
                    null
                }
            }

            else -> {
                null
            }
        }
    }

private fun toPullRequest(pr: GqlPullRequest): PullRequest =
    PullRequest(
        number = pr.number,
        title = pr.title,
        author = if (pr.author?.typename == "Bot") Author.Bot else Author.Human,
        isDraft = pr.isDraft,
        createdAt = Instant.parse(pr.createdAt),
        checks =
            prChecks(
                pr.commits.nodes
                    .lastOrNull()
                    ?.commit
                    ?.statusCheckRollup
                    ?.state,
            ),
        url = pr.url,
    )

/**
 * `backlog.md` with docs-bootstrap's index markers is a counted plan; a `backlog.md` without them, or a
 * `BACKLOG.md`, is a plan kept by hand that the board does not count; neither is no plan.
 */
private fun plan(
    node: GqlRepo,
    branch: String,
): Plan {
    node.backlog?.text?.let { text ->
        parseBacklogIndex(text) { path -> "${node.url}/blob/$branch/$path" }?.let { return it }
        return Plan.FLAT
    }
    return if (node.flatBacklog != null) Plan.FLAT else Plan.NONE
}
