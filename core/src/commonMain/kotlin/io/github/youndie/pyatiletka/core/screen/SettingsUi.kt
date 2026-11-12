package io.github.youndie.pyatiletka.core.screen

import kotlinx.serialization.Serializable

/** The states of screen-settings, section 1. */
enum class SettingsScreenState { Content, TokenMissing, TokenRejected, OwnerNotFound }

/** Where the token comes from: `gh auth token`, or the `PYATILETKA_GITHUB_TOKEN` variable. There is no third way. */
@Serializable
enum class TokenSource { GhCli, EnvVar }

/** The token block: found and where, missing, or refused by GitHub. Never the token itself. */
sealed interface TokenView {
    data class Found(
        val source: TokenSource,
    ) : TokenView

    data object Missing : TokenView

    data object Rejected : TokenView
}

/** What a skin needs to draw the settings (desktop only). */
data class SettingsUiState(
    val token: TokenView,
    val chosenSource: TokenSource,
    val owners: List<String>,
    val periodMinutes: Int,
    val periods: List<Int>,
    /** The login last tried that GitHub does not know; shown under the field until the field is edited. */
    val ownerNotFound: String? = null,
) {
    /** A missing token wins: the rest is unavailable until there is one; then a refused token; then the field's error. */
    val screen: SettingsScreenState
        get() =
            when {
                token == TokenView.Missing -> SettingsScreenState.TokenMissing
                token == TokenView.Rejected -> SettingsScreenState.TokenRejected
                ownerNotFound != null -> SettingsScreenState.OwnerNotFound
                else -> SettingsScreenState.Content
            }
}
