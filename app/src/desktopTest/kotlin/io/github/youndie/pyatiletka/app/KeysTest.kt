package io.github.youndie.pyatiletka.app

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.youndie.pyatiletka.app.data.Data
import io.github.youndie.pyatiletka.core.sample.SampleSnapshot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

// B-27: the window hands its keys to BoardModel.onKey; only Esc, and only while something is open, is taken.
@OptIn(ExperimentalTestApi::class, ExperimentalCoroutinesApi::class)
class KeysTest {
    @Test
    fun escapeClosesTheCardAndOtherKeysDoNot() =
        runComposeUiTest {
            val board =
                BoardModel(
                    MutableStateFlow<Data>(Data.Ready(SampleSnapshot.snapshot, stale = null, refreshing = false)),
                    TestScope(UnconfinedTestDispatcher()),
                ) {}
            val taken = mutableListOf<Boolean>()
            setContent {
                val focus = remember { FocusRequester() }
                Box(
                    Modifier
                        .size(10.dp)
                        .onPreviewKeyEvent { event -> board.onKey(event).also { if (it) taken += true } }
                        .focusRequester(focus)
                        .focusable(),
                )
                LaunchedEffect(Unit) { focus.requestFocus() }
            }
            board.open("youndie/kachok")
            board.openAppearance()

            onRoot().performKeyInput { pressKey(Key.Enter) }
            assertEquals(emptyList(), taken)

            // One Esc closes one layer, on the press: the release must not close the card under the dialog.
            onRoot().performKeyInput { pressKey(Key.Escape) }
            assertEquals(listOf(true), taken)
            assertEquals("youndie/kachok", board.state.value.selected)

            onRoot().performKeyInput { pressKey(Key.Escape) }
            assertEquals(listOf(true, true), taken)
            assertFalse(board.escape(), "two Esc should have closed the dialog and the card")

            onRoot().performKeyInput { pressKey(Key.Escape) }
            assertEquals(listOf(true, true), taken)
        }
}
