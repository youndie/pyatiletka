package io.github.youndie.pyatiletka.skin.plakat

import androidx.compose.runtime.Composable
import io.github.youndie.plakat.theme.PlakatPrintSwitches
import io.github.youndie.plakat.theme.PlakatTheme
import io.github.youndie.plakat.tokens.PlakatPalettes
import io.github.youndie.pyatiletka.core.sample.SampleSettingsStates
import io.github.youndie.pyatiletka.core.screen.SettingsUiState
import io.github.youndie.viddik.annotations.ViddikScreenshot

// screen-settings' artboards (design/canvas/Page_Settings.dc.html): desktop only, 1280×800, light only.
// The owner field holds the login GitHub did not know, as the canvas shows it.
@Composable
private fun Screen(state: SettingsUiState) =
    PlakatTheme(
        colors = PlakatPalettes.Paper,
        reduceMotion = true,
        print = PlakatPrintSwitches(texture = false),
        strings = RussianStrings,
    ) {
        PlakatSettings(state, ownerField = state.ownerNotFound.orEmpty())
    }

@ViddikScreenshot(group = "Settings", name = "Content", width = 1280, height = 800)
@Composable
fun SettingsContent() = Screen(SampleSettingsStates.content)

@ViddikScreenshot(group = "Settings", name = "TokenMissing", width = 1280, height = 800)
@Composable
fun SettingsTokenMissing() = Screen(SampleSettingsStates.tokenMissing)

@ViddikScreenshot(group = "Settings", name = "TokenRejected", width = 1280, height = 800)
@Composable
fun SettingsTokenRejected() = Screen(SampleSettingsStates.tokenRejected)

@ViddikScreenshot(group = "Settings", name = "OwnerNotFound", width = 1280, height = 800)
@Composable
fun SettingsOwnerNotFound() = Screen(SampleSettingsStates.ownerNotFound)
