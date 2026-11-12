package io.github.youndie.pyatiletka.skin.toon

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
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
    tokens = toonTokens(theme),
    reduceMotion = reduceMotion,
) { content() }

private fun toonTokens(theme: Theme) =
    when (theme) {
        Theme.InkGouacheNight -> ToonThemes.InkGouacheNight
        Theme.BrightCel -> ToonThemes.BrightCel
        else -> ToonThemes.InkGouacheLight
    }

/** The stage and the text of [theme]: what the window's frame takes so it reads as the kit's (B-18). */
fun toonFrameColors(theme: Theme): Pair<Color, Color> = toonTokens(theme).colors.let { it.stage to it.text }
