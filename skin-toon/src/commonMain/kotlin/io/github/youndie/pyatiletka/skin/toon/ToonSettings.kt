package io.github.youndie.pyatiletka.skin.toon

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.youndie.pyatiletka.core.screen.SettingsUiState
import io.github.youndie.pyatiletka.core.screen.Texts
import io.github.youndie.pyatiletka.core.screen.TokenSource
import io.github.youndie.pyatiletka.core.screen.TokenView
import io.github.youndie.toon.component.ToonButton
import io.github.youndie.toon.component.ToonButtonVariant
import io.github.youndie.toon.component.ToonChip
import io.github.youndie.toon.component.ToonChipVariant
import io.github.youndie.toon.component.ToonListItem
import io.github.youndie.toon.component.ToonListTrailing
import io.github.youndie.toon.component.ToonRadioGroup
import io.github.youndie.toon.component.ToonSegmented
import io.github.youndie.toon.component.ToonTextField
import io.github.youndie.toon.component.ToonTopAppBar
import io.github.youndie.toon.icon.ToonIcons
import io.github.youndie.toon.theme.Toon
import io.github.youndie.toon.theme.textStyle
import io.github.youndie.toon.tokens.ToonTypeScale

/** What the settings can ask of the desktop's settings model. */
class ToonSettingsActions(
    val back: () -> Unit = {},
    val chooseTokenSource: (TokenSource) -> Unit = {},
    val recheckToken: () -> Unit = {},
    val editOwner: (String) -> Unit = {},
    val addOwner: (String) -> Unit = {},
    val removeOwner: (String) -> Unit = {},
    val choosePeriod: (Int) -> Unit = {},
)

private val ColumnWidth = 640.dp

/**
 * screen-settings on toon-ui (desktop only): the token block — where it was found, or why there is
 * none, and the way it is looked for — then the owners and the period, both unavailable until there
 * is a token. [ownerField] is what the owner field holds; it belongs to the caller, like every field.
 */
@Composable
fun ToonSettings(
    state: SettingsUiState,
    ownerField: String,
    modifier: Modifier = Modifier,
    actions: ToonSettingsActions = ToonSettingsActions(),
) {
    Stage(modifier.fillMaxSize()) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val width = maxWidth
            val inner = minOf(ColumnWidth, width - 32.dp)
            Column(Modifier.fillMaxSize()) {
                ToonTopAppBar(
                    title = Texts.SETTINGS,
                    nav = true,
                    actions = emptyList(),
                    width = width,
                    onNav = actions.back,
                )
                Column(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Column(Modifier.width(inner), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        TokenBlock(state, inner, actions)
                        val ready = state.token != TokenView.Missing
                        Owners(state, ownerField, inner, ready, actions)
                        Heading(Texts.PERIOD)
                        ToonSegmented(
                            labels = state.periods.map(Texts::period),
                            selected = state.periods.indexOf(state.periodMinutes).coerceAtLeast(0),
                            width = 420.dp,
                            enabled = ready,
                            onChange = { actions.choosePeriod(state.periods[it]) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TokenBlock(
    state: SettingsUiState,
    width: Dp,
    actions: ToonSettingsActions,
) {
    Heading(Texts.TOKEN)
    val token = state.token
    ToonListItem(
        title =
            when (token) {
                is TokenView.Found -> Texts.TOKEN_FOUND
                TokenView.Missing -> Texts.TOKEN_MISSING
                TokenView.Rejected -> Texts.TOKEN_REJECTED
            },
        sub =
            when (token) {
                is TokenView.Found -> Texts.tokenFoundAt(token.source)
                TokenView.Missing -> Texts.TOKEN_MISSING_BODY
                TokenView.Rejected -> Texts.TOKEN_REJECTED_BODY
            },
        icon = if (token is TokenView.Found) ToonIcons.Check else ToonIcons.Warning,
        trailing = ToonListTrailing.None,
        last = true,
        width = width,
    )
    ToonRadioGroup(
        TokenSource.entries.map(Texts::tokenSource),
        TokenSource.entries.indexOf(state.chosenSource),
        { actions.chooseTokenSource(TokenSource.entries[it]) },
    )
    if (token == TokenView.Missing) Command(Texts.tokenHint(state.chosenSource))
    if (token !is TokenView.Found) {
        ToonButton(Texts.RECHECK, actions.recheckToken, variant = ToonButtonVariant.Outlined)
    }
}

// kit: https://github.com/youndie/toon-ui/issues/31 — the kit has no monospace face and its Cyrillic backing is
// internal: the command goes in the kit's label style on a plate of the kit's colours.

/** The command or the variable that gives the token, on a plate. */
@Composable
private fun Command(text: String) =
    Box(
        Modifier
            .background(Toon.colors.surface, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        BasicText(text, style = ToonTypeScale.Title.textStyle.copy(color = Toon.colors.text))
    }

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Owners(
    state: SettingsUiState,
    field: String,
    width: Dp,
    ready: Boolean,
    actions: ToonSettingsActions,
) {
    Heading(Texts.OWNERS)
    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        ToonTextField(
            field,
            actions.editOwner,
            label = Texts.OWNER_FIELD,
            error = state.ownerNotFound?.let(Texts::ownerNotFound).orEmpty(),
            width = width - 160.dp,
            enabled = ready,
        )
        ToonButton(Texts.ADD, { actions.addOwner(field) }, enabled = ready && field.isNotBlank())
    }
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        state.owners.forEach { owner ->
            ToonChip(
                owner,
                variant = ToonChipVariant.Input,
                enabled = ready,
                onClick = { actions.removeOwner(owner) },
            )
        }
    }
}

@Composable
private fun Heading(text: String) =
    BasicText(
        text,
        Modifier.padding(top = 12.dp),
        style = ToonTypeScale.Label.textStyle.copy(color = Toon.colors.textMuted),
    )
