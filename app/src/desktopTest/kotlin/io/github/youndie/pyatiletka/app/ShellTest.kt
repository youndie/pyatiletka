package io.github.youndie.pyatiletka.app

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.v2.runComposeUiTest
import io.github.youndie.pyatiletka.app.data.Data
import io.github.youndie.pyatiletka.core.sample.SampleSnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.asTimeZone
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
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
}
