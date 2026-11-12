package io.github.youndie.pyatiletka.app

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.v2.runComposeUiTest
import io.github.youndie.pyatiletka.core.Product
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class BothSkinsTest {
    // B-01's acceptance on the desktop target: the window's content draws the product's name in each
    // kit. plakat-ui's masthead exposes its stretched title as text, toon-ui's title card as the
    // heading's description; both are the kits' own semantics, not ones added here.
    @Test
    fun eachKitDrawsTheTitle() =
        runComposeUiTest {
            setContent { BothSkins() }
            onNodeWithText(Product.TITLE).assertIsDisplayed()
            onNodeWithContentDescription(Product.TITLE).assertIsDisplayed()
        }
}
