package io.github.youndie.pyatiletka.skin.plakat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.youndie.plakat.components.PlakatMasthead
import io.github.youndie.plakat.print.plakatPaper
import io.github.youndie.plakat.theme.PlakatTheme
import io.github.youndie.plakat.tokens.PlakatPalettes
import io.github.youndie.pyatiletka.core.Product

/** The product's name on the poster kit: the masthead on paper, nothing else yet (B-01). */
@Composable
fun PlakatTitleScreen(
    modifier: Modifier = Modifier,
    dark: Boolean = false,
) {
    PlakatTheme(colors = if (dark) PlakatPalettes.BlackPaper else PlakatPalettes.Paper) {
        Box(modifier.fillMaxSize().plakatPaper()) {
            PlakatMasthead(title = Product.TITLE)
        }
    }
}
