package io.github.youndie.pyatiletka.collector

import io.github.youndie.pyatiletka.core.SCHEMA_VERSION
import io.github.youndie.pyatiletka.core.Snapshot
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Collects a [Snapshot] of the owners' repositories from GitHub's GraphQL API, in pages of
 * [PAGE_SIZE]: one page of 100 takes about ten seconds, which is GitHub's limit for one request, and
 * was refused with 502 in two runs of five; pages of 25 never were (research D2, `measurements/graphql/`).
 *
 * A page that fails fails the whole snapshot: a board without some of its rows would look like a
 * board, and nobody would know the rows were missing. In [publicOnly] mode the query asks for public
 * repositories only, and a repository that is not public in the answer is a [CollectException] of
 * kind [CollectFailure.PublicModeViolation] — a second lock behind the token's own right (research D5).
 */
class Collector(
    private val client: HttpClient,
    private val token: String,
    private val endpoint: String = GITHUB_GRAPHQL,
    private val clock: Clock = Clock.System,
) {
    suspend fun collect(
        owners: List<String>,
        publicOnly: Boolean,
    ): Snapshot {
        val takenAt: Instant = clock.now()
        val repos = owners.flatMap { owner -> fetchOwner(owner, publicOnly) }.map(::toRepo)
        if (publicOnly) {
            val leaked = repos.count { it.visibility != io.github.youndie.pyatiletka.core.Visibility.Public }
            if (leaked >
                0
            ) {
                throw CollectException(
                    CollectFailure.PublicModeViolation,
                    "$leaked repositories that are not public in a public snapshot",
                )
            }
        }
        return Snapshot(SCHEMA_VERSION, takenAt, owners, repos)
    }

    private suspend fun fetchOwner(
        owner: String,
        publicOnly: Boolean,
    ): List<GqlRepo> {
        val repos = mutableListOf<GqlRepo>()
        var after: String? = null
        do {
            val page = fetchPage(owner, after, publicOnly)
            repos += page.nodes.filter { !it.isArchived && !it.isFork }
            after = page.pageInfo.endCursor
        } while (page.pageInfo.hasNextPage)
        return repos
    }

    private suspend fun fetchPage(
        owner: String,
        after: String?,
        publicOnly: Boolean,
    ): GqlRepositories {
        val request =
            GqlRequest(
                QUERY,
                mapOf(
                    "owner" to JsonPrimitive(owner),
                    "first" to JsonPrimitive(PAGE_SIZE),
                    "after" to (after?.let(::JsonPrimitive) ?: JsonNull),
                    "privacy" to (if (publicOnly) JsonPrimitive("PUBLIC") else JsonNull),
                ),
            )
        val response =
            client.post(endpoint) {
                header(HttpHeaders.Authorization, "bearer $token")
                header(HttpHeaders.UserAgent, USER_AGENT)
                contentType(ContentType.Application.Json)
                setBody(WireJson.encodeToString(GqlRequest.serializer(), request))
            }
        when (response.status) {
            HttpStatusCode.OK -> Unit

            HttpStatusCode.Unauthorized -> throw CollectException(
                CollectFailure.TokenRejected,
                "GitHub refused the token (401)",
            )

            else -> throw CollectException(
                CollectFailure.GitHubRefused,
                "GitHub answered ${response.status.value} for a page of $owner",
            )
        }
        val body = WireJson.decodeFromString(GqlResponse.serializer(), response.bodyAsText())
        val ownerData = body.data?.repositoryOwner
        if (ownerData == null) {
            if (body.errors.orEmpty().any { it.type == "NOT_FOUND" } || body.errors.isNullOrEmpty()) {
                throw CollectException(CollectFailure.OwnerNotFound, "no user or organisation called $owner")
            }
            throw CollectException(
                CollectFailure.GitHubRefused,
                body.errors.orEmpty().joinToString("; ") { it.message },
            )
        }
        return ownerData.repositories
    }

    companion object {
        const val GITHUB_GRAPHQL: String = "https://api.github.com/graphql"

        /** GitHub refuses API requests without a `User-Agent`. */
        const val USER_AGENT: String = "pyatiletka-collector"

        /** Measured in B-02: 25 per page is the largest size GitHub never refused. */
        const val PAGE_SIZE: Int = 25

        internal val QUERY: String by lazy {
            checkNotNull(Collector::class.java.getResourceAsStream("portfolio.graphql")) {
                "portfolio.graphql is not on the classpath"
            }.bufferedReader()
                .use { it.readText() }
        }
    }
}

/** What a collection can fail on, for the screens to tell apart (feature-refresh, feature-settings). */
enum class CollectFailure { TokenRejected, OwnerNotFound, GitHubRefused, PublicModeViolation }

class CollectException(
    val failure: CollectFailure,
    message: String,
) : Exception(message)

internal val WireJson: Json =
    Json {
        ignoreUnknownKeys = true
        explicitNulls = true
    }
