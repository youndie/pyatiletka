package io.github.youndie.pyatiletka.app.desktop

import io.github.youndie.pyatiletka.core.screen.TokenSource
import java.util.concurrent.TimeUnit

/** A token and where it was found. Its [value] is never logged, stored or shown. */
class FoundToken(
    val value: String,
    val source: TokenSource,
) {
    override fun toString(): String = "FoundToken($source)"
}

/**
 * Finds the token (feature-settings): the source the settings chose first, the other one second, so
 * a machine that has either works. Nothing here writes the token anywhere.
 */
class TokenFinder(
    private val ghAuthToken: () -> String? = ::runGhAuthToken,
    private val env: (String) -> String? = System::getenv,
) {
    fun find(preferred: TokenSource): FoundToken? {
        val order = if (preferred == TokenSource.GhCli) TokenSource.entries else TokenSource.entries.reversed()
        return order.firstNotNullOfOrNull { source -> read(source)?.let { FoundToken(it, source) } }
    }

    private fun read(source: TokenSource): String? =
        when (source) {
            TokenSource.GhCli -> ghAuthToken()
            TokenSource.EnvVar -> env(ENV_VAR)
        }?.trim()?.takeIf { it.isNotEmpty() }

    companion object {
        const val ENV_VAR: String = "PYATILETKA_GITHUB_TOKEN"
    }
}

/** `gh auth token`, or null when `gh` is not installed, not signed in, or does not answer in five seconds. */
private fun runGhAuthToken(): String? =
    try {
        val process =
            ProcessBuilder("gh", "auth", "token")
                .redirectError(ProcessBuilder.Redirect.DISCARD)
                .start()
        if (!process.waitFor(5, TimeUnit.SECONDS)) {
            process.destroyForcibly()
            null
        } else if (process.exitValue() != 0) {
            null
        } else {
            process.inputStream.bufferedReader().use { it.readText() }
        }
    } catch (
        @Suppress("SwallowedException") e: java.io.IOException,
    ) {
        null
    }
