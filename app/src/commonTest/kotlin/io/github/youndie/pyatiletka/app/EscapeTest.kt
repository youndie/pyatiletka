package io.github.youndie.pyatiletka.app

import io.github.youndie.pyatiletka.app.data.Data
import io.github.youndie.pyatiletka.core.sample.SampleSnapshot
import io.github.youndie.pyatiletka.core.screen.BoardScreenState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

// B-27: Esc closes the top layer — the appearance dialog over the card first, then the card.
@OptIn(ExperimentalCoroutinesApi::class)
class EscapeTest {
    @Test
    fun escapeClosesTheDialogThenTheCardThenNothing() =
        runTest {
            val data = MutableStateFlow<Data>(Data.Ready(SampleSnapshot.snapshot, stale = null, refreshing = false))
            val board = BoardModel(data, backgroundScope) {}
            board.open("youndie/kachok")
            board.openAppearance()
            runCurrent()
            assertEquals(BoardScreenState.AppearanceSheet, board.state.value.screen)

            assertTrue(board.escape())
            runCurrent()
            assertEquals(BoardScreenState.Content, board.state.value.screen)
            assertNotNull(board.card.value)

            assertTrue(board.escape())
            runCurrent()
            assertNull(board.card.value)

            assertFalse(board.escape())
        }
}
