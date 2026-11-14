package io.github.youndie.pyatiletka.skin.plakat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.youndie.plakat.components.PlakatParagraph
import io.github.youndie.plakat.components.PlakatStatus
import io.github.youndie.plakat.components.PlakatStatusKind
import io.github.youndie.plakat.theme.PlakatTheme
import io.github.youndie.pyatiletka.core.MainStatus
import io.github.youndie.pyatiletka.core.PrChecks
import io.github.youndie.pyatiletka.core.screen.Texts

// The marks are the kit's status since 0.1.0.23 (plakat-ui#19, B-28): the sign of the semantic kind beside the word.

/** The default branch's mark in a row: green the wedge-check, red the hatch, running a slate plate; none, the word. */
@Composable
internal fun MainMark(
    status: MainStatus,
    modifier: Modifier = Modifier,
) {
    when (status) {
        MainStatus.Green -> PlakatStatus(PlakatStatusKind.Success, Texts.main(status), modifier)
        MainStatus.Red -> PlakatStatus(PlakatStatusKind.Danger, Texts.main(status), modifier)
        MainStatus.Running -> PlakatStatus(PlakatStatusKind.Info, Texts.main(status), modifier)
        MainStatus.None -> Muted(Texts.main(status), modifier)
    }
}

/** A pull request's checks: failing the hatch, ready the wedge-check, running a slate plate; none, the word. */
@Composable
internal fun ChecksMark(
    checks: PrChecks,
    modifier: Modifier = Modifier,
) {
    val word = Texts.checksLong(checks)
    when (checks) {
        PrChecks.Failing -> PlakatStatus(PlakatStatusKind.Danger, word, modifier)
        PrChecks.Ready -> PlakatStatus(PlakatStatusKind.Success, word, modifier)
        PrChecks.Running -> PlakatStatus(PlakatStatusKind.Info, word, modifier)
        PrChecks.NoChecks -> Muted(word, modifier)
    }
}

/** The board's error: the kit's danger with its word. */
@Composable
internal fun ErrorMark(modifier: Modifier = Modifier) = PlakatStatus(PlakatStatusKind.Danger, Texts.ERROR, modifier)

/** A word with no sign, in `label`, muted. */
@Composable
internal fun Muted(
    text: String,
    modifier: Modifier = Modifier,
) = PlakatParagraph(text, PlakatTheme.typography.label, modifier, color = PlakatTheme.colors.inkMuted, maxLines = 1)

/** A word on a plate: an open item's word in a 1 dp frame, the board's own (no semantic kind is "open"). */
@Composable
internal fun Plate(
    word: String,
    fill: Color,
    ink: Color,
    modifier: Modifier = Modifier,
) {
    Box(modifier.plateBackground(fill), contentAlignment = Alignment.Center) {
        PlakatParagraph(word, PlakatTheme.typography.label, Modifier.platePadding(), color = ink, maxLines = 1)
    }
}
