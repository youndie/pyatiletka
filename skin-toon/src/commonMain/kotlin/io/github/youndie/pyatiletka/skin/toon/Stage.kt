package io.github.youndie.pyatiletka.skin.toon

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import io.github.youndie.toon.theme.ToonStage

/**
 * The kit's [ToonStage] behind [content], drawn 3/4 px up and to the left.
 *
 * The stage tiles its 448 px texture every 224 dp
 * with nearest sampling, so at density 1 every pixel centre lands exactly on the boundary between two
 * texels, and a difference in the last bit of the coordinate picks the other one. viddik's capture
 * matrix carries a 1e-9 perspective term, and Intel and AMD runners divide by it differently: the
 * same goldens failed on every Intel runner in rows 30–88 (B-21). Shifted by 3/4 px, every sample
 * sits in a texel's middle, and the stage draws the same on both; the texture looks the same, one
 * texel over. Drop this once the kit samples its tile off the boundary.
 */
// kit: https://github.com/youndie/toon-ui/issues/35
@Composable
internal fun Stage(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier.clipToBounds()) {
        ToonStage(
            Modifier
                .matchParentSize()
                .layout { measurable, constraints ->
                    // One pixel larger, so the shift leaves no edge of the stage uncovered.
                    val placeable =
                        measurable.measure(Constraints.fixed(constraints.maxWidth + 1, constraints.maxHeight + 1))
                    layout(constraints.maxWidth, constraints.maxHeight) { placeable.place(0, 0) }
                }.drawWithContent { translate(-STAGE_SHIFT, -STAGE_SHIFT) { this@drawWithContent.drawContent() } },
        )
        content()
    }
}

/** In pixels: a pixel centre at x + 0.5 then samples the tile at 2x + 2.5 texels at density 1. */
private const val STAGE_SHIFT = .75f
