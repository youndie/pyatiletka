package io.github.youndie.pyatiletka.core.sample

import io.github.youndie.pyatiletka.core.screen.RepoUiState
import io.github.youndie.pyatiletka.core.screen.SettingsUiState
import io.github.youndie.pyatiletka.core.screen.TokenSource
import io.github.youndie.pyatiletka.core.screen.TokenView
import io.github.youndie.pyatiletka.core.screen.repoUiState

/** The four cards of screen-repo, section 6, on the sample data — for both skins' goldens. */
object SampleRepoStates {
    val content: RepoUiState = repoUiState(SampleSnapshot.repo("kachok"))
    val red: RepoUiState = repoUiState(SampleSnapshot.repo("tacku"))
    val quiet: RepoUiState = repoUiState(SampleSnapshot.repo("plakat-ui"))
    val noPlan: RepoUiState = repoUiState(SampleSnapshot.repo("proba"))
}

/**
 * The four states of screen-settings, section 1, as the desktop's settings model gives them (its
 * defaults: the token through gh, the owner youndie, 15 minutes of the three periods).
 */
object SampleSettingsStates {
    private val periods = listOf(5, 15, 60)

    val content: SettingsUiState =
        SettingsUiState(
            token = TokenView.Found(TokenSource.GhCli),
            chosenSource = TokenSource.GhCli,
            owners = listOf("youndie"),
            periodMinutes = 15,
            periods = periods,
        )

    val tokenMissing: SettingsUiState = content.copy(token = TokenView.Missing)

    val tokenRejected: SettingsUiState = content.copy(token = TokenView.Rejected)

    val ownerNotFound: SettingsUiState = content.copy(ownerNotFound = "youndi")
}
