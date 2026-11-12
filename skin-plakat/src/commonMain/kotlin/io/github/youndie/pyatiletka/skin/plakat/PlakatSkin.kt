package io.github.youndie.pyatiletka.skin.plakat

import androidx.compose.runtime.Composable
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
    colors =
        if (theme ==
            Theme.BlackPaper
        ) {
            PlakatPalettes.BlackPaper
        } else {
            PlakatPalettes.Paper
        },
    strings = RussianStrings,
) { content() }

/** The kit's own words in the board's language: its defaults are English (the field's error prefix, the loading control). */
val RussianStrings: PlakatStrings = PlakatStrings(refresh = Texts.REFRESH, loading = Texts.LOADING, error = Texts.ERROR)
