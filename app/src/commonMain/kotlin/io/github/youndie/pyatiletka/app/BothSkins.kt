package io.github.youndie.pyatiletka.app

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.youndie.pyatiletka.skin.plakat.PlakatTitleScreen
import io.github.youndie.pyatiletka.skin.toon.ToonTitleScreen

/**
 * Both kits side by side over the same word (B-01): the proof that plakat-ui, toon-ui and the shell
 * live in one build on both targets. Switching between them is B-16.
 */
@Composable
fun BothSkins(modifier: Modifier = Modifier) {
    Row(modifier.fillMaxSize()) {
        PlakatTitleScreen(Modifier.weight(1f).fillMaxHeight())
        ToonTitleScreen(Modifier.weight(1f).fillMaxHeight())
    }
}
