package io.github.youndie.pyatiletka.skin.toon

import androidx.compose.runtime.Composable
import io.github.youndie.pyatiletka.core.screen.Theme
import io.github.youndie.toon.theme.ToonTheme
import io.github.youndie.toon.theme.ToonThemes

/** The kit's theme for one of the board's toon themes (feature-skins). */
@Composable
fun ToonSkin(
    theme: Theme,
    reduceMotion: Boolean? = null,
    content: @Composable () -> Unit,
) = ToonTheme(
    tokens =
        when (theme) {
            Theme.InkGouacheNight -> ToonThemes.InkGouacheNight
            Theme.BrightCel -> ToonThemes.BrightCel
            else -> ToonThemes.InkGouacheLight
        },
    reduceMotion = reduceMotion,
) { content() }
