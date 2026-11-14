package io.github.youndie.pyatiletka.skin.plakat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.unit.dp
import io.github.youndie.plakat.components.PlakatBanner
import io.github.youndie.plakat.components.PlakatButton
import io.github.youndie.plakat.components.PlakatIconButton
import io.github.youndie.plakat.components.PlakatOutlinedButton
import io.github.youndie.plakat.components.PlakatRadioButton
import io.github.youndie.plakat.components.PlakatSegmentedButton
import io.github.youndie.plakat.components.PlakatStatus
import io.github.youndie.plakat.components.PlakatStatusKind
import io.github.youndie.plakat.components.PlakatTextField
import io.github.youndie.plakat.icons.PlakatIcons
import io.github.youndie.plakat.print.plakatPaper
import io.github.youndie.plakat.theme.PlakatTheme
import io.github.youndie.pyatiletka.core.screen.SettingsScreenState
import io.github.youndie.pyatiletka.core.screen.SettingsUiState
import io.github.youndie.pyatiletka.core.screen.Texts
import io.github.youndie.pyatiletka.core.screen.TokenSource
import io.github.youndie.pyatiletka.core.screen.TokenView

/** What the settings can ask of the desktop's settings model. */
class SettingsActions(
    val back: () -> Unit = {},
    val chooseTokenSource: (TokenSource) -> Unit = {},
    val recheckToken: () -> Unit = {},
    val editOwner: (String) -> Unit = {},
    val addOwner: (String) -> Unit = {},
    val removeOwner: (String) -> Unit = {},
    val choosePeriod: (Int) -> Unit = {},
)

/** The note column on the left of each section and the controls' column on the right (canvas). */
private val NoteWidth = 320.dp
private val ControlsWidth = 560.dp

/**
 * screen-settings on plakat-ui (desktop only, 1280 wide): the token — where it was found, or why there
 * is none, and the way it is looked for — then the owners and the period, both unavailable until there
 * is a token. [ownerField] is what the owner field holds; it belongs to the caller, like every field.
 */
@Composable
fun PlakatSettings(
    state: SettingsUiState,
    ownerField: String,
    modifier: Modifier = Modifier,
    actions: SettingsActions = SettingsActions(),
) {
    val colors = PlakatTheme.colors
    val type = PlakatTheme.typography
    val usable = state.screen != SettingsScreenState.TokenMissing
    Column(modifier.fillMaxSize().background(colors.paper).plakatPaper()) {
        Row(
            Modifier.fillMaxWidth().height(64.dp).padding(start = 20.dp, end = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PlakatIconButton(PlakatIcons.Back, Texts.BACK, actions.back)
            BasicText(
                type.headline.text(Texts.SETTINGS),
                style = type.headline.style.copy(color = colors.ink, shadow = Shadow(colors.red, Offset(1f, 1f), 0f)),
            )
        }
        Box(Modifier.fillMaxWidth().height(8.dp).background(colors.ink))
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 32.dp)) {
            Section(Texts.TOKEN, Texts.TOKEN_ABOUT, enabled = true) { Token(state, actions) }
            Section(Texts.OWNERS, Texts.OWNERS_ABOUT, usable) { Owners(state, ownerField, usable, actions) }
            Section(Texts.PERIOD, Texts.PERIOD_ABOUT, usable) {
                PlakatSegmentedButton(
                    segments = state.periods.map(Texts::periodShort),
                    selectedIndex = state.periods.indexOf(state.periodMinutes),
                    onSelect = { actions.choosePeriod(state.periods[it]) },
                    enabled = usable,
                )
            }
        }
    }
}

/** One section: its name and a note on the left, the controls on the right, a 1 dp rule under both. */
@Composable
private fun Section(
    title: String,
    note: String,
    enabled: Boolean,
    controls: @Composable ColumnScope.() -> Unit,
) {
    val colors = PlakatTheme.colors
    val type = PlakatTheme.typography
    val ink = if (enabled) colors.ink else colors.inkMuted
    Row(
        Modifier
            .fillMaxWidth()
            .drawBehind { drawRect(colors.concrete, Offset(0f, size.height - 1.dp.toPx())) }
            .padding(top = 32.dp, bottom = 32.dp),
        horizontalArrangement = Arrangement.spacedBy(32.dp),
    ) {
        Column(Modifier.width(NoteWidth), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            BasicText(title, style = type.title.style.copy(color = ink))
            BasicText(note, style = type.bodyS.style.copy(color = colors.inkMuted))
        }
        Column(Modifier.width(ControlsWidth), content = controls)
    }
}

/** The token: its state, the two ways to give it, and «Проверить снова» while there is none or it is refused. */
@Composable
private fun ColumnScope.Token(
    state: SettingsUiState,
    actions: SettingsActions,
) {
    val colors = PlakatTheme.colors
    val type = PlakatTheme.typography
    val missing = state.token == TokenView.Missing
    when (val token = state.token) {
        is TokenView.Found -> {
            PlakatStatus(PlakatStatusKind.Success, Texts.tokenFoundLine(token.source))
        }

        TokenView.Missing -> {
            PlakatBanner(PlakatStatusKind.Warning, Texts.TOKEN_MISSING_LINE)
        }

        TokenView.Rejected -> {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                ErrorMark()
                BasicText(Texts.TOKEN_REJECTED, style = type.title.style.copy(color = colors.ink))
            }
        }
    }
    BasicText(
        type.label.text(Texts.SOURCE),
        Modifier.padding(top = 16.dp, bottom = 8.dp),
        style = type.label.style.copy(color = colors.inkMuted),
    )
    Way(
        TokenSource.GhCli,
        if (missing) Texts.GH_CLI_LOGIN else Texts.GH_CLI,
        if (missing) Texts.GH_LOGIN else Texts.GH_TOKEN_COMMAND,
        state,
        actions,
    )
    Way(TokenSource.EnvVar, Texts.ENV_VAR, Texts.TOKEN_VARIABLE, state, actions)
    when (state.token) {
        TokenView.Missing -> PlakatButton(Texts.RECHECK, actions.recheckToken, Modifier.padding(top = 24.dp))
        TokenView.Rejected -> PlakatOutlinedButton(Texts.RECHECK, actions.recheckToken, Modifier.padding(top = 24.dp))
        is TokenView.Found -> Unit
    }
}

/** One way to give the token: the kit's radio, its name, and the command or variable on a concrete plate. */
@Composable
private fun Way(
    source: TokenSource,
    name: String,
    hint: String,
    state: SettingsUiState,
    actions: SettingsActions,
) {
    val colors = PlakatTheme.colors
    val type = PlakatTheme.typography
    Row(Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        PlakatRadioButton(state.chosenSource == source, { actions.chooseTokenSource(source) })
        Column(Modifier.padding(top = 2.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            BasicText(name, style = type.bodyM.style.copy(color = colors.ink))
            // No monospace face in the kit: the canvas sets the command in the body face at 600 (screen-settings §4.4).
            BasicText(
                hint,
                Modifier.background(colors.concrete).padding(horizontal = 8.dp, vertical = 2.dp),
                style = type.label.style.copy(color = colors.ink),
            )
        }
    }
}

/** The owners in rows with ✕, then the field and «Добавить»; the field shows `OwnerNotFound` under it. */
@Composable
private fun ColumnScope.Owners(
    state: SettingsUiState,
    ownerField: String,
    usable: Boolean,
    actions: SettingsActions,
) {
    val colors = PlakatTheme.colors
    val type = PlakatTheme.typography
    state.owners.forEach { owner ->
        Row(
            Modifier
                .fillMaxWidth()
                .height(48.dp)
                .drawBehind { drawRect(colors.concrete, Offset(0f, size.height - 1.dp.toPx())) },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicText(
                owner,
                Modifier.weight(1f),
                style = type.bodyM.style.copy(color = if (usable) colors.ink else colors.inkMuted),
            )
            PlakatIconButton(
                PlakatIcons.Close,
                Texts.removeOwner(owner),
                { actions.removeOwner(owner) },
                enabled = usable,
            )
        }
    }
    Row(
        Modifier.padding(top = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top,
    ) {
        PlakatTextField(
            value = ownerField,
            onValueChange = actions.editOwner,
            modifier = Modifier.width(396.dp),
            placeholder = Texts.OWNER_PLACEHOLDER,
            error = state.ownerNotFound?.let(Texts::ownerNotFound),
            enabled = usable,
            keyboardActions = KeyboardActions(onDone = { actions.addOwner(ownerField) }),
        )
        PlakatOutlinedButton(Texts.ADD, {
            actions.addOwner(ownerField)
        }, Modifier.size(width = 148.dp, height = 48.dp), enabled = usable)
    }
}
