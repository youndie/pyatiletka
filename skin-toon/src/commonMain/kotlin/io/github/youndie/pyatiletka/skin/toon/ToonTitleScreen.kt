package io.github.youndie.pyatiletka.skin.toon

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.youndie.pyatiletka.core.Product
import io.github.youndie.toon.component.ToonTitleCard
import io.github.youndie.toon.theme.ToonStage
import io.github.youndie.toon.theme.ToonTheme
import io.github.youndie.toon.theme.ToonThemes

/**
 * The product's name on the cartoon kit: a title card on the stage (B-01). The card is the kit's own
 * component, so a run of Cyrillic in its title is drawn in the face the kit backs its display face
 * with; none of toon-ui's display faces has Cyrillic (research §1.3).
 */
@Composable
fun ToonTitleScreen(
    modifier: Modifier = Modifier,
    night: Boolean = false,
) {
    ToonTheme(tokens = if (night) ToonThemes.InkGouacheNight else ToonThemes.InkGouacheLight) {
        ToonStage(modifier.fillMaxSize()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                // An empty `sub` is none; null would draw the kit's own demo line.
                ToonTitleCard(title = Product.TITLE, sub = "")
            }
        }
    }
}
