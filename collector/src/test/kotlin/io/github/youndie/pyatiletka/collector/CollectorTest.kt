package io.github.youndie.pyatiletka.collector

import io.github.youndie.pyatiletka.core.Author
import io.github.youndie.pyatiletka.core.ItemStatus
import io.github.youndie.pyatiletka.core.MainStatus
import io.github.youndie.pyatiletka.core.PlanCounts
import io.github.youndie.pyatiletka.core.PlanForm
import io.github.youndie.pyatiletka.core.PrChecks
import io.github.youndie.pyatiletka.core.SnapshotRead
import io.github.youndie.pyatiletka.core.readSnapshot
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

// docs/services/collector.md and feature-refresh's public-mode rule, against a fake GitHub.
class CollectorTest {
    private val index =
        """
        # Backlog
        <!-- BEGIN INDEX -->
        ## Open (2)
        | Task | | Priority | Size | Blocked by |
        |---|---|---|---|---|
        | [B-02](docs/backlog/B-02-b.md) `[?]` | Ask the owner | P1 | S | - |
        | [B-03](docs/backlog/B-03-c.md) `[ ]` | Do the thing | P2 | M | - |
        ## Closed (1)
        - [B-01](docs/backlog/B-01-a.md) `[x]` - Done already
        <!-- END INDEX -->
        """.trimIndent()

    @Test
    fun mapsWhatTheBoardReads() =
        runTest {
            val red =
                rollup(
                    "FAILURE",
                    checkRun("server / test", "FAILURE"),
                    checkRun("lint", "SUCCESS"),
                    statusContext("client / tck", "ERROR"),
                )
            val gitHub =
                FakeGitHub { _, _ ->
                    page(
                        repoJson(
                            "tacku",
                            rollup = red,
                            prs = listOf(prJson(77, bot = true, state = "PENDING")),
                            backlog = index,
                        ),
                        repoJson(
                            "bochka",
                            rollup = rollup("PENDING"),
                            prs = listOf(prJson(1, state = "FAILURE")),
                            flat = true,
                        ),
                        repoJson("proba", rollup = null, prs = listOf(prJson(49, state = null))),
                        repoJson("notes", backlog = "# just a list\n- [ ] something\n"),
                    )
                }
            val snapshot = Collector(gitHub.client, "t").collect(listOf("youndie"), publicOnly = true)
            val (tacku, bochka, proba, notes) = snapshot.repos

            assertEquals(MainStatus.Red, tacku.main)
            assertEquals(listOf("server / test", "client / tck"), tacku.failedChecks.map { it.name })
            assertEquals(Author.Bot to PrChecks.Running, tacku.pullRequests.single().let { it.author to it.checks })
            assertEquals(PlanCounts(done = 1, open = 1, wip = 0, question = 1, dropped = 0), tacku.plan.counts)
            assertEquals(
                "https://github.com/youndie/tacku/blob/main/docs/backlog/B-02-b.md",
                tacku.plan.items
                    .first {
                        it.status ==
                            ItemStatus.Question
                    }.url,
            )

            assertEquals(MainStatus.Running, bochka.main)
            assertEquals(emptyList(), bochka.failedChecks)
            assertEquals(Author.Human to PrChecks.Failing, bochka.pullRequests.single().let { it.author to it.checks })
            assertEquals(PlanForm.Flat, bochka.plan.form)

            assertEquals(MainStatus.None, proba.main)
            assertEquals(PrChecks.NoChecks, proba.pullRequests.single().checks)
            assertEquals(PlanForm.None, proba.plan.form)

            assertEquals(
                PlanForm.Flat,
                notes.plan.form,
                "a backlog.md without the index markers is a plan kept by hand",
            )
        }

    @Test
    fun pagesByTwentyFiveUntilTheLastAndAsksForPublicOnly() =
        runTest {
            val gitHub =
                FakeGitHub { _, after ->
                    when (after) {
                        null -> page(repoJson("a"), repoJson("b"), next = "c1")
                        "c1" -> page(repoJson("c"), next = "c2")
                        else -> page(repoJson("d"))
                    }
                }
            val snapshot = Collector(gitHub.client, "secret").collect(listOf("youndie"), publicOnly = true)
            assertEquals(listOf("a", "b", "c", "d"), snapshot.repos.map { it.fullName.removePrefix("youndie/") })
            assertEquals(3, gitHub.requests.size)
            val vars = gitHub.requests.map { it["variables"]!!.jsonObject }
            assertTrue(
                vars.all {
                    it["first"]!!.jsonPrimitive.content == "25" &&
                        it["privacy"]!!.jsonPrimitive.content == "PUBLIC"
                },
            )
            assertEquals(
                listOf(JsonNull.toString(), "c1", "c2"),
                vars.map {
                    it["after"]!!.let { a ->
                        if (a is JsonNull) a.toString() else a.jsonPrimitive.content
                    }
                },
            )
            assertEquals("bearer secret", gitHub.headers.first()["Authorization"])
            assertEquals(Collector.USER_AGENT, gitHub.headers.first()["User-Agent"])
        }

    @Test
    fun withoutPublicOnlyThereIsNoPrivacyFilter() =
        runTest {
            val gitHub = FakeGitHub { _, _ -> page(repoJson("a"), repoJson("secret-thing", visibility = "PRIVATE")) }
            val snapshot = Collector(gitHub.client, "t").collect(listOf("youndie"), publicOnly = false)
            assertEquals(2, snapshot.repos.size)
            assertEquals(JsonNull, gitHub.requests.single()["variables"]!!.jsonObject["privacy"])
        }

    // feature-refresh: "the public mode drops anything not public and fails if one got into the result".
    @Test
    fun aPrivateRepositoryInPublicModeFailsTheCollection() =
        runTest {
            val gitHub = FakeGitHub { _, _ -> page(repoJson("a"), repoJson("leaked", visibility = "PRIVATE")) }
            val e =
                assertFailsWith<CollectException> {
                    Collector(
                        gitHub.client,
                        "t",
                    ).collect(listOf("youndie"), publicOnly = true)
                }
            assertEquals(CollectFailure.PublicModeViolation, e.failure)
        }

    @Test
    fun archivedAndForkedRepositoriesAreLeftOut() =
        runTest {
            val gitHub =
                FakeGitHub {
                    _,
                    _,
                    ->
                    page(repoJson("a"), repoJson("old", archived = true), repoJson("theirs", fork = true))
                }
            assertEquals(
                listOf(
                    "youndie/a",
                ),
                Collector(gitHub.client, "t").collect(listOf("youndie"), publicOnly = true).repos.map {
                    it.fullName
                },
            )
        }

    @Test
    fun aRefusedPageFailsTheWholeSnapshot() =
        runTest {
            val gitHub = FakeGitHub(HttpStatusCode.BadGateway) { _, _ -> page(repoJson("a")) }
            assertEquals(
                CollectFailure.GitHubRefused,
                assertFailsWith<CollectException> {
                    Collector(gitHub.client, "t").collect(listOf("youndie"), true)
                }.failure,
            )
        }

    // feature-settings: The token is rejected.
    @Test
    fun aRejectedTokenIsNamed() =
        runTest {
            val gitHub = FakeGitHub(HttpStatusCode.Unauthorized) { _, _ -> "{}" }
            assertEquals(
                CollectFailure.TokenRejected,
                assertFailsWith<CollectException> {
                    Collector(gitHub.client, "t").collect(listOf("youndie"), true)
                }.failure,
            )
        }

    // feature-settings: No such owner.
    @Test
    fun anUnknownOwnerIsNamed() =
        runTest {
            val gitHub =
                FakeGitHub {
                    _,
                    _,
                    ->
                    """{"data":{"repositoryOwner":null},"errors":[{"type":"NOT_FOUND","message":"Could not resolve to a RepositoryOwner with the login of 'youndi'."}]}"""
                }
            assertEquals(
                CollectFailure.OwnerNotFound,
                assertFailsWith<CollectException> {
                    Collector(gitHub.client, "t").collect(listOf("youndi"), true)
                }.failure,
            )
        }

    // B-07's acceptance: `collect --public-only` writes a file that core reads.
    @Test
    fun theCliWritesAFileCoreReads() =
        runTest {
            val out = File.createTempFile("snapshot", ".json").apply { deleteOnExit() }
            val gitHub = FakeGitHub { _, _ -> page(repoJson("a", backlog = index), repoJson("b")) }
            val log = mutableListOf<String>()
            val code =
                runCollect(
                    listOf("--owner", "youndie", "--public-only", "--out", out.path),
                    mapOf("GITHUB_TOKEN" to "t"),
                    gitHub.client,
                ) { log += it }
            assertEquals(0, code, log.joinToString())
            val read = readSnapshot(out.readText())
            assertTrue(read is SnapshotRead.Ok)
            assertEquals(listOf("youndie/a", "youndie/b"), read.snapshot.repos.map { it.fullName })
            assertTrue(log.none { "t" == it }, "the token is never logged")
        }

    @Test
    fun theCliRefusesAPrivateLeakAndWritesNothing() =
        runTest {
            val out = File(File.createTempFile("snapshot", ".json").path + ".absent")
            val gitHub = FakeGitHub { _, _ -> page(repoJson("a"), repoJson("leaked", visibility = "PRIVATE")) }
            val code =
                runCollect(
                    listOf("--owner", "youndie", "--public-only", "--out", out.path),
                    mapOf("GITHUB_TOKEN" to "t"),
                    gitHub.client,
                ) {}
            assertEquals(1, code)
            assertTrue(!out.exists())
        }

    @Test
    fun theCliWantsAToken() =
        runTest {
            val gitHub = FakeGitHub { _, _ -> page() }
            assertEquals(2, runCollect(listOf("--owner", "youndie", "--out", "x.json"), emptyMap(), gitHub.client) {})
            assertEquals(0, gitHub.requests.size)
        }

    @Test
    fun anOwnerIsCheckedBeforeItJoinsTheList() =
        runTest {
            val gitHub =
                FakeGitHub { owner, _ ->
                    if (owner ==
                        "youndie"
                    ) {
                        """{"data":{"repositoryOwner":{"login":"youndie"}}}"""
                    } else {
                        """{"data":{"repositoryOwner":null}}"""
                    }
                }
            val collector = Collector(gitHub.client, "t")
            assertTrue(collector.ownerExists("youndie"))
            assertTrue(!collector.ownerExists("youndi"))
        }

    @Test
    fun anUnreachableGitHubIsANetworkFailure() =
        runTest {
            val client =
                io.ktor.client.HttpClient(
                    io.ktor.client.engine.mock
                        .MockEngine { throw java.io.IOException("no route to host") },
                ) {
                    expectSuccess = false
                }
            assertEquals(
                CollectFailure.Network,
                assertFailsWith<CollectException> {
                    Collector(client, "t").collect(listOf("youndie"), true)
                }.failure,
            )
        }
}
