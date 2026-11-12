package io.github.youndie.pyatiletka.collector

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.headersOf
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

/** GitHub's GraphQL endpoint, answering from [pages] by the `after` cursor; remembers what it was asked. */
internal class FakeGitHub(
    private val status: HttpStatusCode = HttpStatusCode.OK,
    private val pages: (owner: String, after: String?) -> String,
) {
    val requests = mutableListOf<JsonObject>()
    val headers = mutableListOf<Map<String, String>>()

    val client =
        HttpClient(
            MockEngine { request: HttpRequestData ->
                val body = WireJson.parseToJsonElement((request.body as TextContent).text).jsonObject
                requests += body
                headers += request.headers.entries().associate { it.key to it.value.joinToString() }
                val vars = body["variables"]!!.jsonObject
                val after =
                    vars["after"]?.let {
                        if (it is kotlinx.serialization.json.JsonNull) null else it.jsonPrimitive.content
                    }
                respond(
                    pages(vars["owner"]!!.jsonPrimitive.content, after),
                    status,
                    headersOf(HttpHeaders.ContentType, "application/json"),
                )
            },
        ) { expectSuccess = false }
}

internal fun page(
    vararg repos: JsonObject,
    next: String? = null,
): String =
    buildJsonObject {
        putJsonObject("data") {
            putJsonObject("rateLimit") {
                put("cost", 13)
                put("remaining", 4900)
            }
            putJsonObject("repositoryOwner") {
                putJsonObject("repositories") {
                    putJsonObject("pageInfo") {
                        put("hasNextPage", next != null)
                        put("endCursor", next)
                    }
                    put("nodes", JsonArray(repos.toList()))
                }
            }
        }
    }.toString()

internal fun rollup(
    state: String,
    vararg contexts: JsonObject,
): JsonObject =
    buildJsonObject {
        put("state", state)
        putJsonObject("contexts") { put("nodes", JsonArray(contexts.toList())) }
    }

internal fun checkRun(
    name: String,
    conclusion: String,
): JsonObject =
    buildJsonObject {
        put("__typename", "CheckRun")
        put("name", name)
        put("conclusion", conclusion)
        put("detailsUrl", "https://ci/$name")
    }

internal fun statusContext(
    context: String,
    state: String,
): JsonObject =
    buildJsonObject {
        put("__typename", "StatusContext")
        put("context", context)
        put("state", state)
        put("targetUrl", "https://ci/$context")
    }

internal fun repoJson(
    name: String,
    visibility: String = "PUBLIC",
    rollup: JsonObject? = rollup("SUCCESS"),
    prs: List<JsonObject> = emptyList(),
    backlog: String? = null,
    flat: Boolean = false,
    archived: Boolean = false,
    fork: Boolean = false,
): JsonObject =
    buildJsonObject {
        put("nameWithOwner", "youndie/$name")
        put("url", "https://github.com/youndie/$name")
        put("visibility", visibility)
        put("isArchived", archived)
        put("isFork", fork)
        put("pushedAt", "2026-11-12T06:00:00Z")
        putJsonObject("defaultBranchRef") {
            put("name", "main")
            putJsonObject("target") { put("statusCheckRollup", rollup ?: JsonNull) }
        }
        putJsonObject("pullRequests") { put("nodes", JsonArray(prs)) }
        put("backlog", backlog?.let { buildJsonObject { put("text", it) } } ?: JsonNull)
        put("flatBacklog", if (flat) buildJsonObject { put("byteSize", 10) } else JsonNull)
    }

internal fun prJson(
    number: Int,
    bot: Boolean = false,
    state: String? = "SUCCESS",
): JsonObject =
    buildJsonObject {
        put("number", number)
        put("title", "PR $number")
        put("isDraft", false)
        put("createdAt", "2026-11-10T00:00:00Z")
        put("url", "https://github.com/x/pull/$number")
        putJsonObject("author") {
            put("__typename", if (bot) "Bot" else "User")
            put("login", "someone")
        }
        putJsonObject("commits") {
            put(
                "nodes",
                JsonArray(
                    listOf(
                        buildJsonObject {
                            putJsonObject("commit") {
                                put(
                                    "statusCheckRollup",
                                    state?.let { buildJsonObject { put("state", it) } } ?: JsonNull,
                                )
                            }
                        },
                    ),
                ),
            )
        }
    }
