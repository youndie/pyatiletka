package io.github.youndie.pyatiletka.skin.plakat

import androidx.compose.runtime.Composable
import io.github.youndie.plakat.theme.PlakatPrintSwitches
import io.github.youndie.plakat.theme.PlakatTheme
import io.github.youndie.plakat.tokens.PlakatColors
import io.github.youndie.plakat.tokens.PlakatPalettes
import io.github.youndie.pyatiletka.core.sample.SampleBoardStates
import io.github.youndie.pyatiletka.core.screen.BoardUiState
import io.github.youndie.viddik.annotations.ViddikScreenshot
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.asTimeZone

// screen-board's artboards (design/canvas/Page_Board.dc.html), one fixture per artboard at its size, on the
// sample data. The canvas's moments are Moscow's; the texture is off, as the kit's own fixtures have it —
// the canvas's paper is its own noise, within the channel tolerance (B-11).
private val Moscow = UtcOffset(hours = 3).asTimeZone()

@Composable
private fun Screen(
    colors: PlakatColors = PlakatPalettes.Paper,
    state: BoardUiState,
) = PlakatTheme(colors = colors, reduceMotion = true, print = PlakatPrintSwitches(texture = false)) {
    PlakatBoard(state, Moscow)
}

@ViddikScreenshot(group = "Board", name = "Loading", width = 1280, height = 800)
@Composable
fun BoardLoading() = Screen(state = SampleBoardStates.loading)

@ViddikScreenshot(group = "Board", name = "Empty", width = 1280, height = 800)
@Composable
fun BoardEmpty() = Screen(state = SampleBoardStates.empty)

@ViddikScreenshot(group = "Board", name = "Content", width = 1280, height = 800)
@Composable
fun BoardContent() = Screen(state = SampleBoardStates.content)

@ViddikScreenshot(group = "Board", name = "NoMatches", width = 1280, height = 800)
@Composable
fun BoardNoMatches() = Screen(state = SampleBoardStates.noMatches)

@ViddikScreenshot(group = "Board", name = "Stale", width = 1280, height = 800)
@Composable
fun BoardStale() = Screen(state = SampleBoardStates.stale)

@ViddikScreenshot(group = "Board", name = "Error", width = 1280, height = 800)
@Composable
fun BoardError() = Screen(state = SampleBoardStates.error)

@ViddikScreenshot(group = "Board", name = "NoToken", width = 1280, height = 800)
@Composable
fun BoardNoToken() = Screen(state = SampleBoardStates.noToken)

@ViddikScreenshot(group = "Board", name = "AppearanceSheet", width = 1280, height = 800)
@Composable
fun BoardAppearanceSheet() = Screen(state = SampleBoardStates.appearanceSheet)

@ViddikScreenshot(group = "Board", name = "Content_Dark", width = 1280, height = 800)
@Composable
fun BoardContentDark() = Screen(PlakatPalettes.BlackPaper, SampleBoardStates.content)

@ViddikScreenshot(group = "Board", name = "Loading_Phone", width = 390, height = 844)
@Composable
fun BoardLoadingPhone() = Screen(state = SampleBoardStates.loading)

@ViddikScreenshot(group = "Board", name = "Empty_Phone", width = 390, height = 844)
@Composable
fun BoardEmptyPhone() = Screen(state = SampleBoardStates.empty)

@ViddikScreenshot(group = "Board", name = "Content_Phone", width = 390, height = 844)
@Composable
fun BoardContentPhone() = Screen(state = SampleBoardStates.content)

@ViddikScreenshot(group = "Board", name = "NoMatches_Phone", width = 390, height = 844)
@Composable
fun BoardNoMatchesPhone() = Screen(state = SampleBoardStates.noMatches)

@ViddikScreenshot(group = "Board", name = "Stale_Phone", width = 390, height = 844)
@Composable
fun BoardStalePhone() = Screen(state = SampleBoardStates.stale)

@ViddikScreenshot(group = "Board", name = "Error_Phone", width = 390, height = 844)
@Composable
fun BoardErrorPhone() = Screen(state = SampleBoardStates.error)

@ViddikScreenshot(group = "Board", name = "AppearanceSheet_Phone", width = 390, height = 844)
@Composable
fun BoardAppearanceSheetPhone() = Screen(state = SampleBoardStates.appearanceSheet)

@ViddikScreenshot(group = "Board", name = "Content_Phone_Dark", width = 390, height = 844)
@Composable
fun BoardContentPhoneDark() = Screen(PlakatPalettes.BlackPaper, SampleBoardStates.content)
