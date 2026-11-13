package io.github.youndie.pyatiletka.skin.plakat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.youndie.plakat.theme.PlakatPrintSwitches
import io.github.youndie.plakat.theme.PlakatTheme
import io.github.youndie.plakat.tokens.PlakatPalettes
import io.github.youndie.pyatiletka.core.sample.SampleBoardStates
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.asTimeZone
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class BoardScrollTest {
    // B-23, at the test window's 1024 dp: on the wide board the summary and the controls scroll
    // away with the rows; the header stays.
    @Test
    fun theSummaryScrollsAwayWithTheRows() =
        runComposeUiTest {
            setContent {
                PlakatTheme(
                    colors = PlakatPalettes.Paper,
                    reduceMotion = true,
                    print = PlakatPrintSwitches(texture = false),
                    strings = RussianStrings,
                ) {
                    Box(Modifier.size(1024.dp, 600.dp)) {
                        PlakatBoard(SampleBoardStates.content, UtcOffset(hours = 3).asTimeZone())
                    }
                }
            }
            onNodeWithText("ВЫПОЛНЕН", substring = true).assertIsDisplayed()

            onNode(hasScrollToIndexAction()).performScrollToIndex(SampleBoardStates.content.rows.size)

            onNodeWithText("ВЫПОЛНЕН", substring = true).assertIsNotDisplayed()
            onNodeWithText("ПЯТИЛЕТКА").assertIsDisplayed()
        }
}
