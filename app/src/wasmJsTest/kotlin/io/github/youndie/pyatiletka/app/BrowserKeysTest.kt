package io.github.youndie.pyatiletka.app

import io.github.youndie.pyatiletka.app.data.Data
import io.github.youndie.pyatiletka.core.sample.SampleSnapshot
import kotlinx.browser.document
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestScope
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.KeyboardEventInit
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

// B-27: Esc on the page closes the card; with nothing open the key is left alone.
class BrowserKeysTest {
    private fun escape() = KeyboardEvent("keydown", KeyboardEventInit(key = "Escape", cancelable = true))

    @Test
    fun escapeOnThePageClosesTheCard() {
        val board =
            BoardModel(
                MutableStateFlow<Data>(Data.Ready(SampleSnapshot.snapshot, stale = null, refreshing = false)),
                TestScope(),
            ) {}
        val page = document.createElement("div")
        closeOnEscape(page, board)
        board.open("youndie/kachok")

        val first = escape()
        page.dispatchEvent(first)
        assertTrue(first.defaultPrevented)
        assertFalse(board.escape(), "the first Esc should have closed the card")

        val second = escape()
        page.dispatchEvent(second)
        assertFalse(second.defaultPrevented)
    }
}
