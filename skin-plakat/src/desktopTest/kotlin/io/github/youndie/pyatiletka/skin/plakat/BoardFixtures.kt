package io.github.youndie.pyatiletka.skin.plakat

import androidx.compose.runtime.Composable
import io.github.youndie.plakat.theme.PlakatPrintSwitches
import io.github.youndie.plakat.theme.PlakatTheme
import io.github.youndie.plakat.tokens.PlakatColors
import io.github.youndie.plakat.tokens.PlakatPalettes
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
fun BoardLoading() = Screen(state = BoardStates.loading)

@ViddikScreenshot(group = "Board", name = "Empty", width = 1280, height = 800)
@Composable
fun BoardEmpty() = Screen(state = BoardStates.empty)

@ViddikScreenshot(group = "Board", name = "Content", width = 1280, height = 800)
@Composable
fun BoardContent() = Screen(state = BoardStates.content)

@ViddikScreenshot(group = "Board", name = "NoMatches", width = 1280, height = 800)
@Composable
fun BoardNoMatches() = Screen(state = BoardStates.noMatches)

@ViddikScreenshot(group = "Board", name = "Stale", width = 1280, height = 800)
@Composable
fun BoardStale() = Screen(state = BoardStates.stale)

@ViddikScreenshot(group = "Board", name = "Error", width = 1280, height = 800)
@Composable
fun BoardError() = Screen(state = BoardStates.error)

@ViddikScreenshot(group = "Board", name = "NoToken", width = 1280, height = 800)
@Composable
fun BoardNoToken() = Screen(state = BoardStates.noToken)

@ViddikScreenshot(group = "Board", name = "AppearanceSheet", width = 1280, height = 800)
@Composable
fun BoardAppearanceSheet() = Screen(state = BoardStates.appearanceSheet)

@ViddikScreenshot(group = "Board", name = "Content_Dark", width = 1280, height = 800)
@Composable
fun BoardContentDark() = Screen(PlakatPalettes.BlackPaper, BoardStates.content)
