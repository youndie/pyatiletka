package io.github.youndie.pyatiletka.skin.toon

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.youndie.pyatiletka.core.sample.SampleBoardStates
import io.github.youndie.pyatiletka.core.screen.Theme
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.asTimeZone
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class BoardScrollTest {
    // B-23: on the wide board the summary and the controls scroll away with the rows; the app bar stays.
    @Test
    fun theSummaryScrollsAwayWithTheRows() =
        runComposeUiTest {
            setContent {
                ToonSkin(Theme.InkGouacheLight, reduceMotion = true) {
                    Box(Modifier.size(1280.dp, 560.dp)) {
                        ToonBoard(SampleBoardStates.content, UtcOffset(hours = 3).asTimeZone())
                    }
                }
            }
            onNodeWithText("План выполнен", substring = true).assertIsDisplayed()

            onNode(hasScrollToIndexAction()).performScrollToNode(hasText("youndie/plakat-ui"))

            onNodeWithText("План выполнен", substring = true).assertIsNotDisplayed()
            onNodeWithText("Пятилетка").assertIsDisplayed()
        }
}
