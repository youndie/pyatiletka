package io.github.youndie.pyatiletka.skin.plakat

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.youndie.plakat.theme.PlakatTheme
import io.github.youndie.pyatiletka.core.MainStatus
import io.github.youndie.pyatiletka.core.screen.Texts

// kit: https://github.com/youndie/plakat-ui/issues/19 — the kit has no status mark (a word with
// its sign); these are drawn from the kit's tokens as the canvas draws them: a wedge-check for green,
// 45° hatching at a 6 dp step for an error (`plakat/semantic/danger`), a slate plate for running.

/** The default branch's mark in a row: sign and word. */
@Composable
internal fun MainMark(
    status: MainStatus,
    modifier: Modifier = Modifier,
) {
    val colors = PlakatTheme.colors
    val label = PlakatTheme.typography.label
    when (status) {
        MainStatus.Running -> {
            Plate(Texts.main(status), colors.slate, colors.onRed, modifier)
        }

        else -> {
            Row(
                modifier,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                when (status) {
                    MainStatus.Red -> Hatch(16.dp, colors.red)
                    MainStatus.Green -> Check(16.dp, colors.ink)
                    else -> Box(Modifier.size(16.dp))
                }
                BasicText(label.text(Texts.main(status)), style = label.style.copy(color = colors.ink), maxLines = 1)
            }
        }
    }
}

/** A word on a filled plate: `ИДЁТ` on slate. */
@Composable
internal fun Plate(
    word: String,
    fill: Color,
    ink: Color,
    modifier: Modifier = Modifier,
) {
    val label = PlakatTheme.typography.label
    Box(modifier.plateBackground(fill), contentAlignment = Alignment.Center) {
        BasicText(
            label.text(word),
            style = label.style.copy(color = ink),
            maxLines = 1,
            modifier = Modifier.platePadding(),
        )
    }
}

/** 45° hatching, a stripe of 3 dp every 6 dp: the error's sign. */
@Composable
internal fun Hatch(
    size: Dp,
    color: Color,
) = Canvas(Modifier.size(size)) { hatch(color) }

internal fun DrawScope.hatch(color: Color) {
    clipRect {
        val step = 6.dp.toPx()
        val band = 3.dp.toPx()
        var x = -size.height
        while (x < size.width) {
            val p =
                Path().apply {
                    moveTo(x, size.height)
                    lineTo(x + size.height, 0f)
                    lineTo(x + size.height + band * 1.4142f, 0f)
                    lineTo(x + band * 1.4142f, size.height)
                    close()
                }
            drawPath(p, color)
            x += step * 1.4142f
        }
    }
}

/** The wedge-check: the kit's `Done` stencil drawn at the mark's size. */
@Composable
internal fun Check(
    size: Dp,
    color: Color,
) = Canvas(Modifier.size(size)) {
    val s = this.size.width / 24f
    val p =
        Path().apply {
            moveTo(3 * s, 12 * s)
            lineTo(6 * s, 9 * s)
            lineTo(10 * s, 13 * s)
            lineTo(18 * s, 5 * s)
            lineTo(21 * s, 8 * s)
            lineTo(10 * s, 19 * s)
            close()
        }
    drawPath(p, color)
}

// kit: https://github.com/youndie/plakat-ui/issues/21 — no More stencil, and a consumer cannot make one.

/** The header's "more": three squares, as the canvas draws it. */
@Composable
internal fun MoreGlyph(color: Color) =
    Canvas(Modifier.size(24.dp)) {
        val s = size.width / 24f
        listOf(3f, 10f, 17f).forEach { y ->
            drawRect(
                color,
                Offset(10 * s, y * s),
                androidx.compose.ui.geometry
                    .Size(4 * s, 4 * s),
            )
        }
    }
