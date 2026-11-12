package io.github.youndie.pyatiletka.app.data

import io.github.youndie.pyatiletka.core.screen.TokenSource
import kotlinx.serialization.Serializable

/**
 * The desktop's settings (feature-settings), kept in `settings.json`. No secret is ever here: the
 * token is read from its [tokenSource] on every collection and is not written anywhere.
 */
@Serializable
data class Settings(
    val owners: List<String> = listOf("youndie"),
    val periodMinutes: Int = 15,
    val tokenSource: TokenSource = TokenSource.GhCli,
    val skin: String = "plakat",
    val theme: String = "paper",
) {
    companion object {
        /** The periods the settings offer, in minutes. */
        val PERIODS: List<Int> = listOf(5, 15, 60)
    }
}
