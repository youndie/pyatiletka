package io.github.youndie.pyatiletka.app

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import io.github.youndie.pyatiletka.app.data.NoCache
import io.github.youndie.pyatiletka.app.data.SnapshotStore
import io.github.youndie.pyatiletka.app.data.WEB_STALE_AFTER
import io.github.youndie.pyatiletka.core.screen.Appearance
import kotlinx.browser.document
import kotlinx.browser.localStorage
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * The board in a browser. The snapshot is read from `snapshot.json` next to the page; every change of
 * the board's screen is also written to the console, which is how the browser's fetch is checked. The
 * address picks the skin and theme for the first frame (`?skin=toon&theme=night`), else the choice kept
 * in this browser; a choice on the board is kept for the next visit (feature-skins).
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val scope = MainScope()
    val store = SnapshotStore(browserSnapshotSource(), NoCache, scope, WEB_STALE_AFTER)
    val appearance = appearanceOfQuery(window.location.search) ?: keptAppearance() ?: Appearance()
    val board = BoardModel(store.data, scope, BoardFlags(hasSettings = false, appearance = appearance), store::refresh)
    store.start()
    scope.launch {
        board.state.distinctUntilChangedBy { it.screen to it.rows.size }.collect { state ->
            println(
                "pyatiletka: board ${state.screen}, ${state.rows.size} rows, ${state.summary?.percent ?: "-"} %, problem ${state.problem?.kind}",
            )
        }
    }
    scope.launch {
        board.state
            .map { it.appearance }
            .distinctUntilChanged()
            .drop(1)
            .collect(::keepAppearance)
    }
    closeOnEscape(window, board)
    ComposeViewport(document.body!!) { Shell(board, browserZone()) }
}

private const val APPEARANCE_KEY = "pyatiletka.appearance"

/** The choice kept in this browser, as `skin=…&theme=…`; null when there is none or storage is off. */
private fun keptAppearance(): Appearance? =
    try {
        localStorage.getItem(APPEARANCE_KEY)?.let(::appearanceOfQuery)
    } catch (e: Throwable) {
        println("pyatiletka: no kept appearance, storage refused: ${e.message}")
        null
    }

private fun keepAppearance(appearance: Appearance) {
    try {
        localStorage.setItem(APPEARANCE_KEY, appearance.query)
    } catch (e: Throwable) {
        // A private window or blocked storage: the address still works, the choice just is not kept.
        println("pyatiletka: appearance not kept, storage refused: ${e.message}")
    }
}
