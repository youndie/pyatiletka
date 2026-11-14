package io.github.youndie.pyatiletka.skin.plakat

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.github.youndie.plakat.theme.PlakatStrings
import io.github.youndie.plakat.theme.PlakatTheme
import io.github.youndie.plakat.tokens.PlakatPalettes
import io.github.youndie.pyatiletka.core.screen.Texts
import io.github.youndie.pyatiletka.core.screen.Theme

/** The kit's theme for one of the board's themes: «Бумага» or «Чёрная бумага» (feature-skins). */
@Composable
fun PlakatSkin(
    theme: Theme,
    content: @Composable () -> Unit,
) = PlakatTheme(
    colors = plakatColors(theme),
    strings = RussianStrings,
) { content() }

/** The kit's own words in the board's language: its defaults are English (the error prefix, the loading control, the warning banner). */
val RussianStrings: PlakatStrings =
    PlakatStrings(refresh = Texts.REFRESH, loading = Texts.LOADING, error = Texts.ERROR, warning = Texts.ATTENTION)

private fun plakatColors(theme: Theme) =
    if (theme ==
        Theme.BlackPaper
    ) {
        PlakatPalettes.BlackPaper
    } else {
        PlakatPalettes.Paper
    }

/** The paper and the ink of [theme]: what the window's frame takes so it reads as the kit's (B-18). */
fun plakatFrameColors(theme: Theme): Pair<Color, Color> = plakatColors(theme).let { it.paper to it.ink }
