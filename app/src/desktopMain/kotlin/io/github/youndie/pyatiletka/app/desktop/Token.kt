package io.github.youndie.pyatiletka.app.desktop

import io.github.youndie.pyatiletka.core.screen.TokenSource
import java.io.File
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

/**
 * The `gh` to run: the first on [path], then the places Homebrew and the Linux packages put it. An app
 * started from the Finder or the Dock gets a `PATH` of `/usr/bin:/bin:/usr/sbin:/sbin` only, where `gh`
 * never is, and the installed board would always say there is no token (B-18). Null when none [exists].
 */
internal fun ghExecutable(
    path: String?,
    exists: (String) -> Boolean,
): String? =
    (path?.split(File.pathSeparatorChar).orEmpty() + KNOWN_GH_DIRS)
        .filter { it.isNotBlank() }
        .map { "${it.trimEnd('/')}/gh" }
        .firstOrNull(exists)

private val KNOWN_GH_DIRS = listOf("/opt/homebrew/bin", "/usr/local/bin", "/home/linuxbrew/.linuxbrew/bin", "/snap/bin")

/** `gh auth token`, or null when `gh` is not installed, not signed in, or does not answer in five seconds. */
private fun runGhAuthToken(): String? =
    try {
        val gh = ghExecutable(System.getenv("PATH")) { File(it).canExecute() } ?: "gh"
        val process =
            ProcessBuilder(gh, "auth", "token")
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
