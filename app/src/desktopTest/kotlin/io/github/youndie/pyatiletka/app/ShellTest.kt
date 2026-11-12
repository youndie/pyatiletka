package io.github.youndie.pyatiletka.app

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.v2.runComposeUiTest
import io.github.youndie.pyatiletka.app.data.Data
import io.github.youndie.pyatiletka.core.BoardFilter
import io.github.youndie.pyatiletka.core.sample.SampleSnapshot
import io.github.youndie.pyatiletka.core.screen.Skin
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.asTimeZone
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class, ExperimentalCoroutinesApi::class)
class ShellTest {
    // The shell draws the board the store holds: the poster kit's title, a row, the summary's percentage.
    @Test
    fun theShellDrawsTheBoardFromTheStore() =
        runComposeUiTest {
            val data = MutableStateFlow<Data>(Data.Ready(SampleSnapshot.snapshot, stale = null, refreshing = false))
            val board = BoardModel(data, TestScope()) {}
            setContent { Shell(board, UtcOffset(hours = 3).asTimeZone()) }
            onNodeWithText("ПЯТИЛЕТКА").assertIsDisplayed()
            onNodeWithText("СНИМОК 12 НОЯБРЯ 2026, 09:17").assertIsDisplayed()
        }

    // feature-skins, "Switching skins on the fly": the open card, the filter and the query outlive the kit.
    @Test
    fun switchingSkinsKeepsThePlace() =
        runComposeUiTest {
            val data = MutableStateFlow<Data>(Data.Ready(SampleSnapshot.snapshot, stale = null, refreshing = false))
            val scope = TestScope()
            val board = BoardModel(data, scope) {}
            board.open("youndie/kachok")
            board.filter(BoardFilter.WithPullRequests)
            board.search("ka")
            scope.advanceUntilIdle()
            setContent { Shell(board, UtcOffset(hours = 3).asTimeZone()) }
            onNodeWithText("ПЯТИЛЕТКА").assertIsDisplayed()

            board.chooseSkin(Skin.Toon)
            scope.advanceUntilIdle()
            waitForIdle()

            onNodeWithText("ПЯТИЛЕТКА").assertDoesNotExist()
            onNodeWithText("Пятилетка").assertIsDisplayed()
            onAllNodesWithText("youndie/kachok").onFirst().assertExists()
            assertEquals(
                "youndie/kachok",
                board.card.value
                    ?.repo
                    ?.fullName,
            )
            assertEquals(BoardFilter.WithPullRequests, board.state.value.filter)
            assertEquals("ka", board.state.value.query)
        }
}
