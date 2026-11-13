package io.github.youndie.pyatiletka.skin.toon

import androidx.compose.runtime.Composable
import io.github.youndie.pyatiletka.core.sample.SampleBoardStates
import io.github.youndie.pyatiletka.core.screen.Appearance
import io.github.youndie.pyatiletka.core.screen.BoardUiState
import io.github.youndie.pyatiletka.core.screen.Skin
import io.github.youndie.pyatiletka.core.screen.Theme
import io.github.youndie.viddik.annotations.ViddikScreenshot
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.asTimeZone

// screen-board on toon-ui: the same states and sizes as the poster skin's artboards, named
// `Board_<State>[_Phone]_Toon` (screen-board, section 1.1). No canvas, so these goldens are the record.
private val Moscow = UtcOffset(hours = 3).asTimeZone()

/** The appearance sheet as the cartoon skin shows it: the cartoon kit chosen, its first theme. */
private val ToonAppearance =
    SampleBoardStates.appearanceSheet.copy(
        appearance = Appearance(Skin.Toon, Theme.InkGouacheLight),
    )

@Composable
private fun Screen(state: BoardUiState) =
    ToonSkin(Theme.InkGouacheLight, reduceMotion = true) { ToonBoard(state, Moscow) }

@ViddikScreenshot(group = "Board", name = "Loading_Toon", width = 1280, height = 800)
@Composable
fun BoardLoadingToon() = Screen(SampleBoardStates.loading)

@ViddikScreenshot(group = "Board", name = "Empty_Toon", width = 1280, height = 800)
@Composable
fun BoardEmptyToon() = Screen(SampleBoardStates.empty)

@ViddikScreenshot(group = "Board", name = "Content_Toon", width = 1280, height = 800)
@Composable
fun BoardContentToon() = Screen(SampleBoardStates.content)

@ViddikScreenshot(group = "Board", name = "NoMatches_Toon", width = 1280, height = 800)
@Composable
fun BoardNoMatchesToon() = Screen(SampleBoardStates.noMatches)

@ViddikScreenshot(group = "Board", name = "Stale_Toon", width = 1280, height = 800)
@Composable
fun BoardStaleToon() = Screen(SampleBoardStates.stale)

@ViddikScreenshot(group = "Board", name = "Error_Toon", width = 1280, height = 800)
@Composable
fun BoardErrorToon() = Screen(SampleBoardStates.error)

@ViddikScreenshot(group = "Board", name = "NoToken_Toon", width = 1280, height = 800)
@Composable
fun BoardNoTokenToon() = Screen(SampleBoardStates.noToken)

@ViddikScreenshot(group = "Board", name = "AppearanceSheet_Toon", width = 1280, height = 800)
@Composable
fun BoardAppearanceSheetToon() = Screen(ToonAppearance)

@ViddikScreenshot(group = "Board", name = "Loading_Phone_Toon", width = 390, height = 844)
@Composable
fun BoardLoadingPhoneToon() = Screen(SampleBoardStates.loading)

@ViddikScreenshot(group = "Board", name = "Empty_Phone_Toon", width = 390, height = 844)
@Composable
fun BoardEmptyPhoneToon() = Screen(SampleBoardStates.empty)

@ViddikScreenshot(group = "Board", name = "Content_Phone_Toon", width = 390, height = 844)
@Composable
fun BoardContentPhoneToon() = Screen(SampleBoardStates.content)

@ViddikScreenshot(group = "Board", name = "NoMatches_Phone_Toon", width = 390, height = 844)
@Composable
fun BoardNoMatchesPhoneToon() = Screen(SampleBoardStates.noMatches)

@ViddikScreenshot(group = "Board", name = "Stale_Phone_Toon", width = 390, height = 844)
@Composable
fun BoardStalePhoneToon() = Screen(SampleBoardStates.stale)

@ViddikScreenshot(group = "Board", name = "Error_Phone_Toon", width = 390, height = 844)
@Composable
fun BoardErrorPhoneToon() = Screen(SampleBoardStates.error)

@ViddikScreenshot(group = "Board", name = "AppearanceSheet_Phone_Toon", width = 390, height = 844)
@Composable
fun BoardAppearanceSheetPhoneToon() = Screen(ToonAppearance)

// B-26: a common phone narrower than the 390 artboards, with four-digit counters as the live portfolio
// has them. The summary's meta line has to stay clear of the kit's fixed time badge.
@ViddikScreenshot(group = "Board", name = "Content_Width360_Toon", width = 360, height = 800)
@Composable
fun BoardContentWidth360Toon() =
    Screen(
        SampleBoardStates.content.copy(
            summary =
                SampleBoardStates.content.summary?.copy(
                    done = 1231,
                    plan = 1288,
                    openPullRequests = 29,
                    waiting = 14,
                ),
        ),
    )
