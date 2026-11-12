package io.github.youndie.pyatiletka.app

import io.github.youndie.pyatiletka.app.data.PageResponse
import io.github.youndie.pyatiletka.app.data.PageSnapshotSource
import io.github.youndie.pyatiletka.app.data.SnapshotSource
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.w3c.fetch.Response
import kotlin.coroutines.cancellation.CancellationException
import kotlin.random.Random

/**
 * `snapshot.json` next to the page — relative, so the page works under `/pyatiletka/` on GitHub Pages
 * and at the root of a local server alike (docs/api/endpoint-snapshot.md, quirks). A random query keeps
 * the browser's cache out of the way; the snapshot's age is read from its `takenAt`, not from the clock.
 */
fun browserSnapshotSource(): SnapshotSource = PageSnapshotSource(::fetchSnapshotJson)

@OptIn(ExperimentalWasmJsInterop::class)
private suspend fun fetchSnapshotJson(): PageResponse =
    try {
        val response = window.fetch("snapshot.json?nocache=${Random.nextLong().toULong()}").await<Response>()
        PageResponse(response.status.toInt(), response.text().await<JsString>().toString())
    } catch (e: CancellationException) {
        throw e
    } catch (
        @Suppress("TooGenericExceptionCaught") e: Throwable,
    ) {
        PageResponse(null, e.message.orEmpty())
    }
