package io.github.youndie.pyatiletka.skin.toon

import androidx.compose.runtime.Composable
import io.github.youndie.pyatiletka.core.sample.SampleSettingsStates
import io.github.youndie.pyatiletka.core.screen.SettingsUiState
import io.github.youndie.pyatiletka.core.screen.Theme
import io.github.youndie.viddik.annotations.ViddikScreenshot

// screen-settings on toon-ui: the four states of section 1, desktop only, named `Settings_<State>_Toon`.
@Composable
private fun Screen(state: SettingsUiState) =
    ToonSkin(
        Theme.InkGouacheLight,
        reduceMotion = true,
    ) { ToonSettings(state, ownerField = state.ownerNotFound.orEmpty()) }

@ViddikScreenshot(group = "Settings", name = "Content_Toon", width = 1280, height = 800)
@Composable
fun SettingsContentToon() = Screen(SampleSettingsStates.content)

@ViddikScreenshot(group = "Settings", name = "TokenMissing_Toon", width = 1280, height = 800)
@Composable
fun SettingsTokenMissingToon() = Screen(SampleSettingsStates.tokenMissing)

@ViddikScreenshot(group = "Settings", name = "TokenRejected_Toon", width = 1280, height = 800)
@Composable
fun SettingsTokenRejectedToon() = Screen(SampleSettingsStates.tokenRejected)

@ViddikScreenshot(group = "Settings", name = "OwnerNotFound_Toon", width = 1280, height = 800)
@Composable
fun SettingsOwnerNotFoundToon() = Screen(SampleSettingsStates.ownerNotFound)
