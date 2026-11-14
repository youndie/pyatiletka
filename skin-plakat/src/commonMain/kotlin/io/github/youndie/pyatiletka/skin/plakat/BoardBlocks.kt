package io.github.youndie.pyatiletka.skin.plakat

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import io.github.youndie.plakat.components.PlakatBanner
import io.github.youndie.plakat.components.PlakatButton
import io.github.youndie.plakat.components.PlakatRadioButton
import io.github.youndie.plakat.components.PlakatScrim
import io.github.youndie.plakat.components.PlakatStatusKind
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

/** The stale bar under the header: the kit's warning banner, the moment and the reason, «ПОВТОРИТЬ» (plakat-ui#25, B-28). */
@Composable
internal fun StaleBar(
    notice: StaleNotice,
    zone: TimeZone,
    retry: () -> Unit,
) = PlakatBanner(PlakatStatusKind.Warning, Texts.stale(notice, zone), action = Texts.RETRY, onAction = retry)

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

// kit: https://github.com/youndie/plakat-ui/issues/35 — PlakatDialog (with its content slot since #23) takes a wide
// window's width; the canvas's 480 dp sheet stays the board's own, over the kit's scrim (#24).

/** «ВИД» over the board: the kit's scrim, then the skin and its themes as radio rows, «ГОТОВО», 480 wide in a 3 dp frame. */
@Composable
internal fun BoxScope.AppearanceDialog(
    visible: Boolean,
    appearance: Appearance,
    actions: BoardActions,
) {
    val colors = PlakatTheme.colors
    val type = PlakatTheme.typography
    PlakatScrim(visible, actions.closeAppearance)
    if (!visible) return
    Column(
        Modifier
            .align(Alignment.Center)
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
        Column(Modifier.padding(top = 8.dp)) { AppearanceGroups(appearance, actions) }
        Row(Modifier.fillMaxWidth().padding(top = 16.dp)) {
            Spacer(Modifier.weight(1f))
            PlakatButton(Texts.DONE, actions.closeAppearance)
        }
    }
}

/** The skin and the theme as radio rows: the dialog's content, and the phone's sheet over its «ГОТОВО». */
@Composable
internal fun AppearanceGroups(
    appearance: Appearance,
    actions: BoardActions,
) {
    Group(Texts.SKIN, top = 0.dp) {
        Skin.entries.forEach { Choice(Texts.skin(it), it == appearance.skin) { actions.chooseSkin(it) } }
    }
    Group(Texts.THEME, top = 16.dp) {
        Theme
            .of(
                appearance.skin,
            ).forEach { Choice(Texts.theme(it), it == appearance.theme) { actions.chooseTheme(it) } }
    }
}

/** The appearance's body, shared by the wide dialog and the phone's bottom sheet. */
@Composable
internal fun AppearanceChoices(
    appearance: Appearance,
    actions: BoardActions,
) {
    Column(Modifier.padding(top = 8.dp)) { AppearanceGroups(appearance, actions) }
    Row(Modifier.fillMaxWidth().padding(top = 16.dp)) {
        Spacer(Modifier.weight(1f))
        PlakatButton(Texts.DONE, actions.closeAppearance)
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
