package io.github.youndie.pyatiletka.app.desktop

import io.github.youndie.pyatiletka.app.data.Fetched
import io.github.youndie.pyatiletka.app.data.RefreshFailure
import io.github.youndie.pyatiletka.app.data.Settings
import io.github.youndie.pyatiletka.core.sample.SampleSnapshot
import io.github.youndie.pyatiletka.core.screen.TokenSource
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import java.io.File
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

// feature-refresh and feature-settings on the desktop, against a fake GitHub.
class DesktopSourcesTest {
    private val onePage =
        """{"data":{"repositoryOwner":{"repositories":{"pageInfo":{"hasNextPage":false,"endCursor":null},"nodes":[]}}}}"""

    private fun gitHub(
        status: HttpStatusCode = HttpStatusCode.OK,
        body: String = onePage,
        seen: MutableList<HttpRequestData> = mutableListOf(),
    ) = HttpClient(
        MockEngine { request ->
            seen += request
            respond(body, status, headersOf(HttpHeaders.ContentType, "application/json"))
        },
    ) { expectSuccess = false }

    private val noToken = TokenFinder(ghAuthToken = { null }, env = { null })

    // Scenario (feature-settings): No token.
    @Test
    fun withoutATokenNothingIsAsked() =
        runTest {
            val seen = mutableListOf<HttpRequestData>()
            val source = GitHubSnapshotSource(gitHub(seen = seen), { Settings() }, noToken)
            assertEquals(Fetched.Failed(RefreshFailure.NoToken), source.fetch())
            assertEquals(0, seen.size)
        }

    // Scenario (feature-settings): The token is rejected.
    @Test
    fun aRejectedTokenIsNamed() =
        runTest {
            val source =
                GitHubSnapshotSource(
                    gitHub(HttpStatusCode.Unauthorized, "{}"),
                    { Settings() },
                    TokenFinder({ "t" }, { null }),
                )
            assertEquals(Fetched.Failed(RefreshFailure.TokenRejected), source.fetch())
        }

    // Scenario (feature-refresh): The network is gone — as the desktop's source sees it.
    @Test
    fun anUnreachableGitHubIsNoNetwork() =
        runTest {
            val client = HttpClient(MockEngine { throw IOException("no route to host") }) { expectSuccess = false }
            assertEquals(
                Fetched.Failed(RefreshFailure.NoNetwork),
                GitHubSnapshotSource(client, {
                    Settings()
                }, TokenFinder({ "t" }, { null })).fetch(),
            )
        }

    @Test
    fun theDesktopAsksForEverythingTheTokenSees() =
        runTest {
            val seen = mutableListOf<HttpRequestData>()
            val fetched =
                GitHubSnapshotSource(gitHub(seen = seen), {
                    Settings(owners = listOf("youndie"))
                }, TokenFinder({ "t" }, { null })).fetch()
            assertIs<Fetched.Ok>(fetched)
            val body = (seen.single().body as io.ktor.http.content.TextContent).text
            assertEquals(true, body.contains("\"privacy\":null"), body)
        }

    @Test
    fun theTokenComesFromTheChosenSourceFirst() {
        val both =
            TokenFinder(ghAuthToken = { "from-gh\n" }, env = {
                if (it ==
                    TokenFinder.ENV_VAR
                ) {
                    "from-env"
                } else {
                    null
                }
            })
        assertEquals(TokenSource.GhCli, both.find(TokenSource.GhCli)!!.source)
        assertEquals("from-gh", both.find(TokenSource.GhCli)!!.value)
        assertEquals(TokenSource.EnvVar, both.find(TokenSource.EnvVar)!!.source)
        assertEquals(TokenSource.EnvVar, TokenFinder({ null }, { "from-env" }).find(TokenSource.GhCli)!!.source)
        assertNull(noToken.find(TokenSource.GhCli))
    }

    @Test
    fun theCacheKeepsTheLastGoodSnapshot() =
        runTest {
            val file = File.createTempFile("snapshot", ".json").apply { delete() }
            val cache = FileSnapshotCache(file)
            assertNull(cache.load())
            cache.save(SampleSnapshot.snapshot)
            assertEquals(SampleSnapshot.snapshot, FileSnapshotCache(file).load())
            file.writeText("not a snapshot")
            assertNull(FileSnapshotCache(file).load())
        }

    @Test
    fun settingsNeverHoldTheToken() {
        val file = File.createTempFile("settings", ".json")
        SettingsFile(file).save(Settings(owners = listOf("youndie", "kotlin")))
        assertEquals(listOf("youndie", "kotlin"), SettingsFile(file).load().owners)
        assertEquals(false, file.readText().contains("token\":\""), "a token value would be a string field")
        file.writeText("{ broken")
        assertEquals(Settings(), SettingsFile(file).load())
    }
}
