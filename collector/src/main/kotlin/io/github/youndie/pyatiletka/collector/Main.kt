package io.github.youndie.pyatiletka.collector

import io.github.youndie.pyatiletka.core.SnapshotRead
import io.github.youndie.pyatiletka.core.readSnapshot
import io.github.youndie.pyatiletka.core.writeSnapshot
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.system.exitProcess

/**
 * `collector --owner <login> [--owner <login>…] [--public-only] --out <file>`. The token comes from
 * `PYATILETKA_GITHUB_TOKEN`, else `GITHUB_TOKEN`; it is never printed. The file is read back with
 * core's reader before the command succeeds, so a snapshot the board could not read is never left
 * behind as if it were fine.
 */
fun main(args: Array<String>) {
    val code =
        runBlocking {
            HttpClient(CIO) { expectSuccess = false }.use { client ->
                runCollect(args.toList(), System.getenv(), client) { line -> System.err.println(line) }
            }
        }
    exitProcess(code)
}

internal suspend fun runCollect(
    args: List<String>,
    env: Map<String, String>,
    client: HttpClient,
    log: (String) -> Unit,
): Int {
    val parsed = parseArgs(args) ?: return 2.also { log(USAGE) }
    val token =
        env["PYATILETKA_GITHUB_TOKEN"]?.takeIf { it.isNotBlank() } ?: env["GITHUB_TOKEN"]?.takeIf { it.isNotBlank() }
    if (token == null) {
        log("no token: set PYATILETKA_GITHUB_TOKEN or GITHUB_TOKEN")
        return 2
    }
    val snapshot =
        try {
            Collector(client, token).collect(parsed.owners, parsed.publicOnly)
        } catch (e: CollectException) {
            log("collect failed (${e.failure}): ${e.message}")
            return 1
        }
    val text = writeSnapshot(snapshot)
    if (readSnapshot(text) !is SnapshotRead.Ok) {
        log("the snapshot does not read back; nothing written")
        return 1
    }
    File(parsed.out).writeText(text)
    log("wrote ${snapshot.repos.size} repositories of ${parsed.owners.joinToString()} to ${parsed.out}")
    return 0
}

private data class Args(
    val owners: List<String>,
    val publicOnly: Boolean,
    val out: String,
)

private fun parseArgs(args: List<String>): Args? {
    val owners = mutableListOf<String>()
    var publicOnly = false
    var out: String? = null
    var i = 0
    while (i < args.size) {
        when (args[i]) {
            "--owner" -> owners += args.getOrNull(++i) ?: return null
            "--public-only" -> publicOnly = true
            "--out" -> out = args.getOrNull(++i) ?: return null
            else -> return null
        }
        i++
    }
    return if (owners.isEmpty() || out == null) null else Args(owners, publicOnly, out)
}

private const val USAGE = "usage: collector --owner <login> [--owner <login>...] [--public-only] --out <file>"
