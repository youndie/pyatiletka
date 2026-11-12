package io.github.youndie.pyatiletka.skin.plakat

import androidx.compose.runtime.Composable
import io.github.youndie.plakat.theme.PlakatPrintSwitches
import io.github.youndie.plakat.theme.PlakatTheme
import io.github.youndie.plakat.tokens.PlakatColors
import io.github.youndie.plakat.tokens.PlakatPalettes
import io.github.youndie.pyatiletka.core.sample.SampleBoardStates
import io.github.youndie.pyatiletka.core.sample.SampleRepoStates
import io.github.youndie.pyatiletka.core.screen.RepoUiState
import io.github.youndie.viddik.annotations.ViddikScreenshot
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.asTimeZone

// screen-repo's artboards (design/canvas/Page_Repo.dc.html), one fixture per artboard at its size: on the
// wide one the card stands beside the board with its row selected, on the phone it is a screen of its own.
private val Moscow = UtcOffset(hours = 3).asTimeZone()

@Composable
private fun Card(
    card: RepoUiState,
    colors: PlakatColors = PlakatPalettes.Paper,
) = PlakatTheme(colors = colors, reduceMotion = true, print = PlakatPrintSwitches(texture = false)) {
    PlakatBoard(SampleBoardStates.content.copy(selected = card.repo.fullName), Moscow, card = card)
}

@ViddikScreenshot(group = "Repo", name = "Content", width = 1280, height = 800)
@Composable
fun RepoContent() = Card(SampleRepoStates.content)

@ViddikScreenshot(group = "Repo", name = "Red", width = 1280, height = 800)
@Composable
fun RepoRed() = Card(SampleRepoStates.red)

@ViddikScreenshot(group = "Repo", name = "Quiet", width = 1280, height = 800)
@Composable
fun RepoQuiet() = Card(SampleRepoStates.quiet)

@ViddikScreenshot(group = "Repo", name = "NoPlan", width = 1280, height = 800)
@Composable
fun RepoNoPlan() = Card(SampleRepoStates.noPlan)

@ViddikScreenshot(group = "Repo", name = "Content_Dark", width = 1280, height = 800)
@Composable
fun RepoContentDark() = Card(SampleRepoStates.content, PlakatPalettes.BlackPaper)

@ViddikScreenshot(group = "Repo", name = "Content_Phone", width = 390, height = 844)
@Composable
fun RepoContentPhone() = Card(SampleRepoStates.content)

@ViddikScreenshot(group = "Repo", name = "Red_Phone", width = 390, height = 844)
@Composable
fun RepoRedPhone() = Card(SampleRepoStates.red)

@ViddikScreenshot(group = "Repo", name = "Quiet_Phone", width = 390, height = 844)
@Composable
fun RepoQuietPhone() = Card(SampleRepoStates.quiet)

@ViddikScreenshot(group = "Repo", name = "NoPlan_Phone", width = 390, height = 844)
@Composable
fun RepoNoPlanPhone() = Card(SampleRepoStates.noPlan)

@ViddikScreenshot(group = "Repo", name = "Content_Phone_Dark", width = 390, height = 844)
@Composable
fun RepoContentPhoneDark() = Card(SampleRepoStates.content, PlakatPalettes.BlackPaper)
