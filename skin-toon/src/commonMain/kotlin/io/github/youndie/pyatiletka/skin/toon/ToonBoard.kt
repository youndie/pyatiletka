package io.github.youndie.pyatiletka.skin.toon

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.youndie.pyatiletka.core.BoardFilter
import io.github.youndie.pyatiletka.core.MainStatus
import io.github.youndie.pyatiletka.core.PlanForm
import io.github.youndie.pyatiletka.core.PrChecks
import io.github.youndie.pyatiletka.core.Repo
import io.github.youndie.pyatiletka.core.Summary
import io.github.youndie.pyatiletka.core.percent
import io.github.youndie.pyatiletka.core.screen.Appearance
import io.github.youndie.pyatiletka.core.screen.BoardScreenState
import io.github.youndie.pyatiletka.core.screen.BoardUiState
import io.github.youndie.pyatiletka.core.screen.Skin
import io.github.youndie.pyatiletka.core.screen.Texts
import io.github.youndie.pyatiletka.core.screen.Theme
import io.github.youndie.pyatiletka.core.waiting
import io.github.youndie.toon.component.ToonButton
import io.github.youndie.toon.component.ToonCard
import io.github.youndie.toon.component.ToonListItem
import io.github.youndie.toon.component.ToonListTrailing
import io.github.youndie.toon.component.ToonProgress
import io.github.youndie.toon.component.ToonProgressKind
import io.github.youndie.toon.component.ToonPullToRefreshBox
import io.github.youndie.toon.component.ToonRadioGroup
import io.github.youndie.toon.component.ToonSegmented
import io.github.youndie.toon.component.ToonSnackbar
import io.github.youndie.toon.component.ToonTextField
import io.github.youndie.toon.component.ToonTitleCard
import io.github.youndie.toon.component.ToonTitleCardVariant
import io.github.youndie.toon.component.ToonTopAppBar
import io.github.youndie.toon.icon.ToonIconGlyph
import io.github.youndie.toon.icon.ToonIcons
import io.github.youndie.toon.theme.Toon
import io.github.youndie.toon.theme.textStyle
import io.github.youndie.toon.tokens.ToonTypeScale
import kotlinx.datetime.TimeZone

/** What the board can ask of the shell — the same callbacks the poster skin takes. */
class ToonBoardActions(
    val filter: (BoardFilter) -> Unit = {},
    val search: (String) -> Unit = {},
    val resetSearch: () -> Unit = {},
    val refresh: () -> Unit = {},
    val openMenu: () -> Unit = {},
    val open: (String) -> Unit = {},
    val openSettings: () -> Unit = {},
    val chooseSkin: (Skin) -> Unit = {},
    val chooseTheme: (Theme) -> Unit = {},
    val closeAppearance: () -> Unit = {},
)

private val WideFrom = 720.dp

/**
 * screen-board on toon-ui: no canvas (research D6) — the board is built from the kit's own components:
 * the top app bar, a card for the summary, the segmented filter, the field, list items for the rows, the
 * title card for every screen that has nothing to list, and the snackbar for a stale snapshot.
 */
@Composable
fun ToonBoard(
    state: BoardUiState,
    zone: TimeZone,
    modifier: Modifier = Modifier,
    actions: ToonBoardActions = ToonBoardActions(),
) {
    Stage(modifier.fillMaxSize()) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val wide = maxWidth >= WideFrom
            val width = maxWidth
            Column(Modifier.fillMaxSize()) {
                val full = state.summary != null
                ToonTopAppBar(
                    title = Texts.TITLE,
                    nav = false,
                    actions = if (full) listOf(ToonIcons.Refresh, ToonIcons.More) else listOf(ToonIcons.More),
                    width = width,
                    onAction = { if (it == ToonIcons.Refresh) actions.refresh() else actions.openMenu() },
                )
                when (state.screen) {
                    BoardScreenState.Loading -> {
                        Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                            ToonProgress(kind = ToonProgressKind.Circular)
                        }
                    }

                    BoardScreenState.Error -> {
                        Card(
                            Modifier.weight(1f),
                            Texts.NO_SNAPSHOT,
                            Texts.problem(state.problem),
                            Texts.RETRY,
                            actions.refresh,
                        )
                    }

                    BoardScreenState.NoToken -> {
                        Card(
                            Modifier.weight(1f),
                            Texts.NO_TOKEN,
                            Texts.NO_TOKEN_BODY,
                            Texts.SETTINGS,
                            actions.openSettings,
                        )
                    }

                    BoardScreenState.Empty -> {
                        Card(
                            Modifier.weight(1f),
                            Texts.NO_REPOS,
                            Texts.noRepos(state.owners),
                            Texts.SETTINGS,
                            actions.openSettings,
                        )
                    }

                    else -> {
                        if (wide) {
                            WideContent(state, zone, actions, width, Modifier.weight(1f))
                        } else {
                            ToonPullToRefreshBox(state.refreshing, actions.refresh, Modifier.weight(1f)) {
                                PhoneContent(state, zone, actions, width)
                            }
                        }
                    }
                }
            }
            if (state.screen ==
                BoardScreenState.AppearanceSheet
            ) {
                AppearancePanel(
                    state.appearance,
                    actions,
                    if (wide) {
                        420.dp
                    } else {
                        width -
                            32.dp
                    },
                )
            }
        }
    }
}

@Composable
private fun WideContent(
    state: BoardUiState,
    zone: TimeZone,
    actions: ToonBoardActions,
    width: Dp,
    modifier: Modifier,
) {
    val column = (width - 72.dp) / 2
    Column(modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        state.stale?.let {
            ToonSnackbar(
                Texts.stale(it, zone),
                action = Texts.RETRY,
                width = column * 2 + 24.dp,
                onAction = actions.refresh,
            )
        }
        state.summary?.let { SummaryCard(it, state, zone, column * 2 + 24.dp, narrow = false) }
        Row(verticalAlignment = Alignment.CenterVertically) {
            ToonSegmented(
                labels = BoardFilter.entries.map(Texts::filter),
                selected = state.filter.ordinal,
                width = 480.dp,
                onChange = { actions.filter(BoardFilter.entries[it]) },
            )
            Spacer(Modifier.weight(1f))
            ToonTextField(state.query, actions.search, label = Texts.SEARCH, width = 360.dp)
        }
        if (state.screen == BoardScreenState.NoMatches) {
            NothingFound(state, actions)
        } else {
            LazyVerticalGrid(GridCells.Fixed(2), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                itemsIndexed(state.rows, key = { _, r -> r.fullName }) { i, repo ->
                    Row(
                        repo,
                        i,
                        last = i >= state.rows.size - 2,
                        width = column,
                        narrow = false,
                    ) { actions.open(repo.fullName) }
                }
            }
        }
    }
}

@Composable
private fun PhoneContent(
    state: BoardUiState,
    zone: TimeZone,
    actions: ToonBoardActions,
    width: Dp,
) {
    val inner = width - 32.dp
    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        state.stale?.let { notice ->
            item {
                ToonSnackbar(
                    Texts.stale(notice, zone),
                    action = Texts.RETRY,
                    width = inner,
                    onAction = actions.refresh,
                )
            }
        }
        state.summary?.let { s -> item { SummaryCard(s, state, zone, inner, narrow = true) } }
        item {
            ToonSegmented(
                labels = BoardFilter.entries.map(Texts::filter),
                selected = state.filter.ordinal,
                width = inner,
                onChange = { actions.filter(BoardFilter.entries[it]) },
            )
        }
        item { ToonTextField(state.query, actions.search, label = Texts.SEARCH, width = inner) }
        if (state.screen == BoardScreenState.NoMatches) {
            item { NothingFound(state, actions) }
        } else {
            itemsIndexed(state.rows, key = { _, r -> r.fullName }) { i, repo ->
                Row(
                    repo,
                    i,
                    last = i == state.rows.lastIndex,
                    width = inner,
                    narrow = true,
                ) { actions.open(repo.fullName) }
            }
        }
    }
}

// kit: https://github.com/youndie/toon-ui/issues/32 — the card's time badge has a fixed width and its meta line
// is not ellipsized: the time is the hour alone, and a narrow card gets the short counters.

/** The summary as the kit's card: the plan's share as the title, the counters as the meta line, the snapshot's hour as the time. */
@Composable
private fun SummaryCard(
    summary: Summary,
    state: BoardUiState,
    zone: TimeZone,
    width: Dp,
    narrow: Boolean,
) {
    ToonCard(
        title = Texts.planDoneLine(summary.percent ?: 0),
        meta = if (narrow) Texts.summaryLineShort(summary) else Texts.summaryLine(summary),
        time = state.takenAt?.let { Texts.time(it, zone) }.orEmpty(),
        tilt = false,
        width = width,
    )
}

// kit: https://github.com/youndie/toon-ui/issues/33 — the sub line is not ellipsized: a narrow row drops the
// default branch's word (its icon says it) and the plan's counts, keeping the percentage.

/** A repository as the kit's list item: its default branch's state as the icon, the rest in the line under the name. */
@Composable
private fun Row(
    repo: Repo,
    index: Int,
    last: Boolean,
    width: Dp,
    narrow: Boolean,
    onClick: () -> Unit,
) {
    val counts = repo.plan.counts
    val percent = counts?.percent
    val failing = repo.pullRequests.count { it.checks == PrChecks.Failing }
    val sub =
        buildList {
            if (!narrow) add(Texts.main(repo.main))
            add(
                if (failing >
                    0
                ) {
                    "PR ${repo.pullRequests.size}, ${Texts.failingPullRequests(failing)}"
                } else {
                    "PR ${repo.pullRequests.size}"
                },
            )
            when {
                repo.plan.form == PlanForm.Items && counts != null && percent != null -> {
                    add(if (narrow) "$percent\u00A0%" else Texts.planCell(counts, percent))
                }

                repo.plan.form == PlanForm.Flat -> {
                    add(Texts.PLAN_FLAT)
                }

                else -> {
                    add(Texts.PLAN_NONE)
                }
            }
            if (repo.plan.form == PlanForm.Items) add("ждут ${repo.waiting}")
        }.joinToString(" · ")
    ToonListItem(
        title = repo.fullName,
        sub = sub,
        icon = repo.main.glyph,
        trailing = ToonListTrailing.Chevron,
        index = index,
        last = last,
        width = width,
        onClick = onClick,
    )
}

private val MainStatus.glyph: ToonIconGlyph
    get() =
        when (this) {
            MainStatus.Green -> ToonIcons.Check
            MainStatus.Red -> ToonIcons.Warning
            MainStatus.Running -> ToonIcons.Refresh
            MainStatus.None -> ToonIcons.Info
        }

/** A screen with nothing to list: the kit's empty title card and one action under it. */
@Composable
private fun Card(
    modifier: Modifier,
    title: String,
    sub: String,
    action: String,
    onAction: () -> Unit,
) = Column(
    modifier.fillMaxWidth().padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
) {
    ToonTitleCard(variant = ToonTitleCardVariant.Empty, title = title, sub = sub, width = 340.dp, height = 300.dp)
    ToonButton(action, onAction)
}

@Composable
private fun NothingFound(
    state: BoardUiState,
    actions: ToonBoardActions,
) = Card(
    Modifier,
    Texts.NOTHING_FOUND,
    Texts.nothingFound(state.query, state.summary?.repos ?: 0),
    Texts.RESET_SEARCH,
    actions.resetSearch,
)

// kit: https://github.com/youndie/toon-ui/issues/34 — neither ToonDialog nor ToonBottomSheet takes content: the
// appearance's two radio groups go on a surface of the kit's colours instead; and
// https://github.com/youndie/toon-ui/issues/31 — its own words ("Вид", "Скин", "Тема") have no Cyrillic backing.
@Composable
private fun AppearancePanel(
    appearance: Appearance,
    actions: ToonBoardActions,
    width: Dp,
) {
    val colors = Toon.colors
    Box(
        Modifier
            .fillMaxSize()
            .background(colors.ink.copy(alpha = 0.45f))
            .clickable(MutableInteractionSource(), indication = null, onClick = actions.closeAppearance),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .width(width)
                .background(colors.surface, RoundedCornerShape(16.dp))
                .clickable(MutableInteractionSource(), indication = null) {}
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BasicText(Texts.APPEARANCE, style = ToonTypeScale.Headline.textStyle)
            BasicText(Texts.SKIN, style = ToonTypeScale.Label.textStyle.copy(color = colors.textMuted))
            ToonRadioGroup(Skin.entries.map(Texts::skin), Skin.entries.indexOf(appearance.skin), {
                actions.chooseSkin(Skin.entries[it])
            })
            BasicText(Texts.THEME, style = ToonTypeScale.Label.textStyle.copy(color = colors.textMuted))
            val themes = Theme.of(appearance.skin)
            ToonRadioGroup(themes.map(Texts::theme), themes.indexOf(appearance.theme).coerceAtLeast(0), {
                actions.chooseTheme(themes[it])
            })
            Row {
                Spacer(Modifier.weight(1f))
                ToonButton(Texts.DONE, actions.closeAppearance)
            }
        }
    }
}
