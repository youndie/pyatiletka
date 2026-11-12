package io.github.youndie.pyatiletka.core.screen

import io.github.youndie.pyatiletka.core.BacklogItem
import io.github.youndie.pyatiletka.core.MainStatus
import io.github.youndie.pyatiletka.core.PlanForm
import io.github.youndie.pyatiletka.core.PullRequest
import io.github.youndie.pyatiletka.core.Repo
import io.github.youndie.pyatiletka.core.cardItems
import io.github.youndie.pyatiletka.core.cardPullRequests
import io.github.youndie.pyatiletka.core.isQuiet
import io.github.youndie.pyatiletka.core.percent

/** The states of screen-repo, section 1. */
enum class RepoScreenState { Content, Red, Quiet, NoPlan }

/** What a skin needs to draw a repository's card; lists are already in the card's order. */
data class RepoUiState(
    val screen: RepoScreenState,
    val repo: Repo,
    val pullRequests: List<PullRequest>,
    val items: List<BacklogItem>,
    /** Rounded down; null when the plan is not counted. */
    val percent: Int?,
)

/**
 * The card's state: red when the default branch is red (failed checks first), quiet when there is
 * nothing to do, no plan when the repository has none the board counts, otherwise content.
 */
fun repoUiState(repo: Repo): RepoUiState {
    val screen =
        when {
            repo.main == MainStatus.Red -> RepoScreenState.Red
            repo.isQuiet -> RepoScreenState.Quiet
            repo.plan.form != PlanForm.Items -> RepoScreenState.NoPlan
            else -> RepoScreenState.Content
        }
    return RepoUiState(screen, repo, cardPullRequests(repo), cardItems(repo), repo.plan.counts?.percent)
}
