package io.github.youndie.pyatiletka.skin.toon

import androidx.compose.runtime.Composable
import io.github.youndie.pyatiletka.core.sample.SampleBoardStates
import io.github.youndie.pyatiletka.core.sample.SampleRepoStates
import io.github.youndie.pyatiletka.core.screen.RepoUiState
import io.github.youndie.pyatiletka.core.screen.Theme
import io.github.youndie.viddik.annotations.ViddikScreenshot
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.asTimeZone

// screen-repo on toon-ui: the four cards of section 6, wide (the board beside the panel) and narrow,
// named `Repo_<State>[_Phone]_Toon` (screen-repo, section 1.1). No canvas: these goldens are the record.
private val Moscow = UtcOffset(hours = 3).asTimeZone()

@Composable
private fun Screen(card: RepoUiState) =
    ToonSkin(Theme.InkGouacheLight, reduceMotion = true) {
        ToonRepoScreen(SampleBoardStates.content.copy(selected = card.repo.fullName), card, Moscow)
    }

@ViddikScreenshot(group = "Repo", name = "Content_Toon", width = 1280, height = 800)
@Composable
fun RepoContentToon() = Screen(SampleRepoStates.content)

@ViddikScreenshot(group = "Repo", name = "Content_Phone_Toon", width = 390, height = 844)
@Composable
fun RepoContentPhoneToon() = Screen(SampleRepoStates.content)

@ViddikScreenshot(group = "Repo", name = "Red_Toon", width = 1280, height = 800)
@Composable
fun RepoRedToon() = Screen(SampleRepoStates.red)

@ViddikScreenshot(group = "Repo", name = "Red_Phone_Toon", width = 390, height = 844)
@Composable
fun RepoRedPhoneToon() = Screen(SampleRepoStates.red)

@ViddikScreenshot(group = "Repo", name = "Quiet_Toon", width = 1280, height = 800)
@Composable
fun RepoQuietToon() = Screen(SampleRepoStates.quiet)

@ViddikScreenshot(group = "Repo", name = "Quiet_Phone_Toon", width = 390, height = 844)
@Composable
fun RepoQuietPhoneToon() = Screen(SampleRepoStates.quiet)

@ViddikScreenshot(group = "Repo", name = "NoPlan_Toon", width = 1280, height = 800)
@Composable
fun RepoNoPlanToon() = Screen(SampleRepoStates.noPlan)

@ViddikScreenshot(group = "Repo", name = "NoPlan_Phone_Toon", width = 390, height = 844)
@Composable
fun RepoNoPlanPhoneToon() = Screen(SampleRepoStates.noPlan)
