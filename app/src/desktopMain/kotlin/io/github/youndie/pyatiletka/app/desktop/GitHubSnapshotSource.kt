package io.github.youndie.pyatiletka.app.desktop

import io.github.youndie.pyatiletka.app.data.Fetched
import io.github.youndie.pyatiletka.app.data.RefreshFailure
import io.github.youndie.pyatiletka.app.data.Settings
import io.github.youndie.pyatiletka.app.data.SnapshotSource
import io.github.youndie.pyatiletka.collector.CollectException
import io.github.youndie.pyatiletka.collector.CollectFailure
import io.github.youndie.pyatiletka.collector.Collector
import io.ktor.client.HttpClient

/**
 * The desktop's source: the collector, in-process, with the token found afresh on every attempt and
 * every repository the token can see — the desktop's snapshot is never published (research D5), so
 * there is no public-only filter here.
 */
class GitHubSnapshotSource(
    private val client: HttpClient,
    private val settings: () -> Settings,
    private val tokens: TokenFinder,
    private val endpoint: String = Collector.GITHUB_GRAPHQL,
) : SnapshotSource {
    override suspend fun fetch(): Fetched {
        val current = settings()
        val token = tokens.find(current.tokenSource) ?: return Fetched.Failed(RefreshFailure.NoToken)
        return try {
            Fetched.Ok(Collector(client, token.value, endpoint).collect(current.owners, publicOnly = false))
        } catch (e: CollectException) {
            Fetched.Failed(e.toRefreshFailure(current.owners))
        }
    }
}

internal fun CollectException.toRefreshFailure(owners: List<String>): RefreshFailure =
    when (failure) {
        CollectFailure.Network -> {
            RefreshFailure.NoNetwork
        }

        CollectFailure.TokenRejected -> {
            RefreshFailure.TokenRejected
        }

        CollectFailure.OwnerNotFound -> {
            RefreshFailure.OwnerNotFound(owners.firstOrNull { message?.contains(it) == true } ?: owners.first())
        }

        CollectFailure.GitHubRefused, CollectFailure.PublicModeViolation -> {
            RefreshFailure.Other(message.orEmpty())
        }
    }
