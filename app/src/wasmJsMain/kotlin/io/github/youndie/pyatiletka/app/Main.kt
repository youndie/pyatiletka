package io.github.youndie.pyatiletka.app

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import io.github.youndie.pyatiletka.app.data.NoCache
import io.github.youndie.pyatiletka.app.data.SnapshotStore
import io.github.youndie.pyatiletka.app.data.WEB_STALE_AFTER
import kotlinx.browser.document
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch

/**
 * The board in a browser. The snapshot is read from `snapshot.json` next to the page; every change of
 * the board's screen is also written to the console, which is how the browser's fetch is checked.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val scope = MainScope()
    val store = SnapshotStore(browserSnapshotSource(), NoCache, scope, WEB_STALE_AFTER)
    val board = BoardModel(store.data, scope, BoardFlags(hasSettings = false), store::refresh)
    store.start()
    scope.launch {
        board.state.distinctUntilChangedBy { it.screen to it.rows.size }.collect { state ->
            println(
                "pyatiletka: board ${state.screen}, ${state.rows.size} rows, ${state.summary?.percent ?: "-"} %, problem ${state.problem?.kind}",
            )
        }
    }
    ComposeViewport(document.body!!) { Shell(board, browserZone()) }
}
