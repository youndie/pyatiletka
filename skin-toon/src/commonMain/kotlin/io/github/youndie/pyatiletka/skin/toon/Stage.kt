package io.github.youndie.pyatiletka.skin.toon

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.youndie.toon.theme.ToonStage

/**
 * The kit's [ToonStage] behind [content]. The kit samples its tile an eighth of a texel off the lines between texels
 * (toon-ui#71), so the stage draws the same on Intel and AMD runners without the 3/4 px shift the skin kept (B-21, B-30).
 */
@Composable
internal fun Stage(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier) {
        ToonStage(Modifier.matchParentSize())
        content()
    }
}
