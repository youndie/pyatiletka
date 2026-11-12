package io.github.youndie.pyatiletka.skin.plakat

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.youndie.plakat.agit.PlakatSkeleton
import io.github.youndie.plakat.components.PlakatButton
import io.github.youndie.plakat.components.PlakatRadioButton
import io.github.youndie.plakat.components.PlakatTextButton
import io.github.youndie.plakat.theme.PlakatTheme
import io.github.youndie.pyatiletka.core.screen.Appearance
import io.github.youndie.pyatiletka.core.screen.Skin
import io.github.youndie.pyatiletka.core.screen.StaleNotice
import io.github.youndie.pyatiletka.core.screen.Texts
import io.github.youndie.pyatiletka.core.screen.Theme
import kotlinx.datetime.TimeZone

/** A screen-wide message: an optional mark, the headline, a sentence, one action — centred, 24 dp apart. */
@Composable
internal fun Message(
    modifier: Modifier,
    title: String,
    body: String,
    mark: (@Composable () -> Unit)? = null,
    action: @Composable () -> Unit,
) {
    val colors = PlakatTheme.colors
    val type = PlakatTheme.typography
    Column(
        modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
    ) {
        mark?.invoke()
        BasicText(
            type.headline.text(title),
            style =
                type.headline.style.copy(
                    color = colors.ink,
                    shadow = Shadow(colors.red, Offset(1f, 1f), 0f),
                    textAlign = TextAlign.Center,
                ),
            modifier = Modifier.widthIn(max = 640.dp),
        )
        BasicText(
            body,
            style = type.bodyL.style.copy(color = colors.inkMuted, textAlign = TextAlign.Center),
            modifier = Modifier.widthIn(max = 480.dp),
        )
        action()
    }
}

/** «ОШИБКА» with its 24 dp hatch, over the error's headline. */
@Composable
internal fun ErrorMark() {
    val colors = PlakatTheme.colors
    val label = PlakatTheme.typography.label
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Hatch(24.dp, colors.red)
        BasicText(label.text(Texts.ERROR), style = label.style.copy(color = colors.ink))
    }
}

// kit: https://github.com/youndie/plakat-ui/issues/25 — no in-screen banner: the warning plate is drawn here.

/** The stale bar under the header: ocher, «ВНИМАНИЕ», the moment and the reason, «ПОВТОРИТЬ». */
@Composable
internal fun StaleBar(
    notice: StaleNotice,
    zone: TimeZone,
    retry: () -> Unit,
) {
    val colors = PlakatTheme.colors
    val type = PlakatTheme.typography
    Row(
        Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(colors.ocher)
            .padding(start = 32.dp, end = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicText(type.label.text(Texts.ATTENTION), style = type.label.style.copy(color = colors.ink))
        BasicText(
            Texts.stale(notice, zone),
            style = type.bodyS.style.copy(color = colors.ink),
            modifier = Modifier.weight(1f),
        )
        PlakatTextButton(Texts.RETRY, retry)
    }
}

/** Loading: the summary, the controls and eight rows in the kit's skeleton, the column heads in place (canvas note). */
@Composable
internal fun ColumnScope.LoadingBoard() {
    Row(
        Modifier.padding(start = 32.dp, end = 32.dp, top = 24.dp).fillMaxWidth().height(136.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        PlakatSkeleton(seed = 1, modifier = Modifier.width(640.dp).height(136.dp))
        Column(Modifier.weight(1f).height(136.dp), verticalArrangement = Arrangement.SpaceBetween) {
            PlakatSkeleton(seed = 2, modifier = Modifier.fillMaxWidth().height(64.dp))
            PlakatSkeleton(seed = 3, modifier = Modifier.fillMaxWidth().height(8.dp))
        }
    }
    Row(
        Modifier.padding(start = 32.dp, end = 32.dp, top = 24.dp).fillMaxWidth().height(56.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlakatSkeleton(seed = 4, modifier = Modifier.width(400.dp).height(48.dp))
        Spacer(Modifier.weight(1f))
        PlakatSkeleton(seed = 5, modifier = Modifier.width(360.dp).height(56.dp))
    }
    TableHeaderRow(Modifier.padding(start = 24.dp, end = 24.dp, top = 16.dp))
    Column(Modifier.padding(horizontal = 24.dp)) {
        repeat(8) { i ->
            Box(Modifier.fillMaxWidth().height(36.dp).padding(horizontal = 8.dp, vertical = 12.dp)) {
                PlakatSkeleton(seed = 10 + i, modifier = Modifier.fillMaxSize())
            }
        }
    }
}

// kit: https://github.com/youndie/plakat-ui/issues/23 — PlakatDialog has no content slot; and
// https://github.com/youndie/plakat-ui/issues/24 — no scrim of the kit's own to put under it.

/** «ВИД» over the board: the skin and its themes as radio rows, «ГОТОВО» — 480 wide in a 3 dp frame (canvas). */
@Composable
internal fun AppearanceDialog(
    appearance: Appearance,
    actions: BoardActions,
) {
    val colors = PlakatTheme.colors
    val type = PlakatTheme.typography
    Box(
        Modifier
            .fillMaxSize()
            .drawBehind {
                halftone(colors.ink)
            }.clickable(MutableInteractionSource(), indication = null, onClick = actions.closeAppearance),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .width(480.dp)
                .background(colors.paper)
                .border(3.dp, colors.ink)
                .clickable(MutableInteractionSource(), indication = null) {}
                .padding(27.dp),
        ) {
            BasicText(
                type.headline.text(Texts.APPEARANCE),
                style = type.headline.style.copy(color = colors.ink, shadow = Shadow(colors.red, Offset(1f, 1f), 0f)),
                modifier = Modifier.padding(bottom = 16.dp),
            )
            Box(Modifier.fillMaxWidth().height(3.dp).background(colors.ink))
            Group(Texts.SKIN, top = 8.dp) {
                Skin.entries.forEach { Choice(Texts.skin(it), it == appearance.skin) { actions.chooseSkin(it) } }
            }
            Group(Texts.THEME, top = 16.dp) {
                Theme.of(appearance.skin).forEach {
                    Choice(
                        Texts.theme(it),
                        it == appearance.theme,
                    ) { actions.chooseTheme(it) }
                }
            }
            Row(Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Spacer(Modifier.weight(1f))
                PlakatButton(Texts.DONE, actions.closeAppearance)
            }
        }
    }
}

@Composable
private fun Group(
    title: String,
    top: androidx.compose.ui.unit.Dp,
    content: @Composable () -> Unit,
) {
    val type = PlakatTheme.typography
    Column(Modifier.padding(top = top)) {
        BasicText(type.label.text(title), style = type.label.style.copy(color = PlakatTheme.colors.inkMuted))
        content()
    }
}

@Composable
private fun Choice(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val type = PlakatTheme.typography
    Row(
        Modifier.fillMaxWidth().height(48.dp).clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        PlakatRadioButton(selected, onClick = null)
        BasicText(text, style = type.bodyM.style.copy(color = PlakatTheme.colors.ink))
    }
}

/** The canvas's scrim: one ink in dots of r 1.25 on a 4 dp grid turned 45°. */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.halftone(ink: androidx.compose.ui.graphics.Color) {
    val pitch = 4.dp.toPx()
    val r = 1.25.dp.toPx()
    val c = 0.70710677f
    val reach = (size.width + size.height) / pitch + 2
    var i = -reach
    while (i <= reach) {
        var j = -reach
        while (j <= reach) {
            val u = (i + 0.5f) * pitch
            val v = (j + 0.5f) * pitch
            val x = u * c - v * c
            val y = u * c + v * c
            if (x > -r && y > -r && x < size.width + r && y < size.height + r) drawCircle(ink, r, Offset(x, y))
            j += 1f
        }
        i += 1f
    }
}
