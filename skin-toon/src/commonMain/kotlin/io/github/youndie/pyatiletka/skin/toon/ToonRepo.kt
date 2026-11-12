package io.github.youndie.pyatiletka.skin.toon

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.youndie.pyatiletka.core.BacklogItem
import io.github.youndie.pyatiletka.core.ItemStatus
import io.github.youndie.pyatiletka.core.PlanForm
import io.github.youndie.pyatiletka.core.PrChecks
import io.github.youndie.pyatiletka.core.screen.BoardUiState
import io.github.youndie.pyatiletka.core.screen.RepoScreenState
import io.github.youndie.pyatiletka.core.screen.RepoUiState
import io.github.youndie.pyatiletka.core.screen.Texts
import io.github.youndie.toon.component.ToonCard
import io.github.youndie.toon.component.ToonDivider
import io.github.youndie.toon.component.ToonListItem
import io.github.youndie.toon.component.ToonListTrailing
import io.github.youndie.toon.component.ToonTitleCard
import io.github.youndie.toon.component.ToonTitleCardVariant
import io.github.youndie.toon.component.ToonTopAppBar
import io.github.youndie.toon.icon.ToonIconGlyph
import io.github.youndie.toon.icon.ToonIcons
import io.github.youndie.toon.theme.Toon
import io.github.youndie.toon.theme.ToonStage
import io.github.youndie.toon.theme.textStyle
import io.github.youndie.toon.tokens.ToonTypeScale
import kotlinx.datetime.TimeZone
import kotlin.time.Instant

/** What the card can ask of the shell: close it (or go back on a phone), open a link on GitHub. */
class ToonRepoActions(
    val close: () -> Unit = {},
    val openUrl: (String) -> Unit = {},
)

/** The panel's width next to the board on a wide window (screen-repo, section 0). */
private val PanelWidth = 480.dp

/**
 * screen-repo on toon-ui, as the screen document places it: on a wide window the board on the left and
 * the card in a 480 dp panel on the right; on a narrow one the card alone, with a back arrow.
 */
@Composable
fun ToonRepoScreen(
    board: BoardUiState,
    card: RepoUiState,
    zone: TimeZone,
    modifier: Modifier = Modifier,
    boardActions: ToonBoardActions = ToonBoardActions(),
    actions: ToonRepoActions = ToonRepoActions(),
) {
    val takenAt = board.takenAt ?: card.repo.pushedAt
    BoxWithConstraints(modifier.fillMaxSize()) {
        if (maxWidth >= WideFrom) {
            Row(Modifier.fillMaxSize()) {
                // kit: https://github.com/youndie/toon-ui/issues/37 — a list item has no selected state: the
                // board's row of the open repository is left unmarked.
                ToonBoard(board, zone, Modifier.weight(1f), boardActions)
                ToonRepo(card, takenAt, wide = true, Modifier.width(PanelWidth).fillMaxHeight(), actions)
            }
        } else {
            ToonRepo(card, takenAt, wide = false, Modifier.fillMaxSize(), actions)
        }
    }
}

/**
 * One repository's card: failed checks first when the default branch is red, then the open pull
 * requests, then the unclosed items — in core's order (`cardPullRequests`, `cardItems`); a repository
 * with nothing to do gets the kit's title card instead of lists. A pull request's age is counted to
 * [takenAt], the snapshot's moment, so the card says what the snapshot says.
 */
@Composable
fun ToonRepo(
    state: RepoUiState,
    takenAt: Instant,
    wide: Boolean,
    modifier: Modifier = Modifier,
    actions: ToonRepoActions = ToonRepoActions(),
) {
    ToonStage(modifier) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val width = maxWidth
            val inner = width - 32.dp
            Column(Modifier.fillMaxSize()) {
                ToonTopAppBar(
                    title = state.repo.fullName,
                    nav = !wide,
                    actions = if (wide) listOf(ToonIcons.Close) else emptyList(),
                    width = width,
                    onNav = actions.close,
                    onAction = { actions.close() },
                )
                LazyColumn(
                    Modifier.fillMaxWidth().weight(1f).padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item { SummaryCard(state, inner) }
                    if (state.screen == RepoScreenState.Quiet) {
                        item { Quiet(state, inner) }
                    } else {
                        failedChecks(state, inner, actions)
                        pullRequests(state, inner, takenAt, actions)
                        plan(state, inner, actions)
                    }
                }
            }
        }
    }
}

// kit: https://github.com/youndie/toon-ui/issues/32 — the time badge is fixed-width: it carries the share only.

/** The default branch, the counters and the plan's share, as the kit's card. */
@Composable
private fun SummaryCard(
    state: RepoUiState,
    width: Dp,
) {
    val repo = state.repo
    val meta =
        buildList {
            add("PR ${repo.pullRequests.size}")
            repo.plan.counts?.let { add(Texts.doneOf(it)) }
        }.joinToString(" · ")
    ToonCard(
        title = Texts.branch(repo.defaultBranch, repo.main),
        meta = meta,
        time = state.percent?.let { "$it %" }.orEmpty(),
        tilt = false,
        width = width,
    )
}

private fun LazyListScope.failedChecks(
    state: RepoUiState,
    width: Dp,
    actions: ToonRepoActions,
) {
    val checks = state.repo.failedChecks
    if (checks.isEmpty()) return
    item { Heading(Texts.FAILED_CHECKS) }
    itemsIndexed(checks, key = { _, c -> "check:${c.name}" }) { i, check ->
        ToonListItem(
            title = check.name,
            sub = Texts.CHECK_FAILED,
            icon = ToonIcons.Warning,
            trailing = ToonListTrailing.Chevron,
            index = i,
            last = i == checks.lastIndex,
            width = width,
            onClick = { actions.openUrl(check.url) },
        )
    }
}

// kit: https://github.com/youndie/toon-ui/issues/36 — a list item is one 72 dp line of title and one of sub,
// drawn at fixed places with no width: a pull request's or an item's own title (which screen-repo says is
// never cut) goes as a wrapped paragraph under its row, and the row carries the number and the short words.

private fun LazyListScope.pullRequests(
    state: RepoUiState,
    width: Dp,
    now: Instant,
    actions: ToonRepoActions,
) {
    val prs = state.pullRequests
    if (prs.isEmpty()) return
    item { Heading("${Texts.OPEN_PULL_REQUESTS} · ${prs.size}") }
    itemsIndexed(prs, key = { _, pr -> "pr:${pr.number}" }) { i, pr ->
        Entry(
            title = "#${pr.number} · ${Texts.checks(pr.checks)}",
            sub = "${Texts.author(pr.author)} · ${Texts.age(now - pr.createdAt)}",
            text = pr.title,
            icon = pr.checks.glyph,
            index = i,
            last = i == prs.lastIndex,
            width = width,
        ) { actions.openUrl(pr.url) }
    }
}

private fun LazyListScope.plan(
    state: RepoUiState,
    width: Dp,
    actions: ToonRepoActions,
) {
    when (state.repo.plan.form) {
        PlanForm.Items -> {
            val items = state.items
            if (items.isEmpty()) return
            item { Heading("${Texts.UNCLOSED_ITEMS} · ${items.size}") }
            itemsIndexed(items, key = { _, it -> "item:${it.id}" }) { i, item ->
                Entry(
                    title = listOfNotNull(item.id, item.priority).joinToString(" · "),
                    sub = Texts.itemStatus(item.status),
                    text = item.title,
                    icon = item.glyph,
                    index = i,
                    last = i == items.lastIndex,
                    width = width,
                ) { actions.openUrl(item.url) }
            }
        }

        PlanForm.Flat -> {
            item { Note(Texts.FLAT_PLAN, Texts.FLAT_PLAN_BODY, width) }
        }

        PlanForm.None -> {
            item { Note(Texts.NO_BACKLOG, "", width) }
        }
    }
}

/** A section's name, in the kit's label style. */
@Composable
private fun Heading(text: String) =
    BasicText(
        text,
        Modifier.padding(top = 8.dp),
        style = ToonTypeScale.Label.textStyle.copy(color = Toon.colors.textMuted),
    )

/**
 * A row of the kit with the entry's own title wrapped under it, aligned with the row's text. The row's
 * own rule would cut the title off its row, so the rule goes under the title instead.
 */
@Composable
private fun Entry(
    title: String,
    sub: String,
    text: String,
    icon: ToonIconGlyph,
    index: Int,
    last: Boolean,
    width: Dp,
    onClick: () -> Unit,
) = Column(Modifier.width(width)) {
    ToonListItem(
        title = title,
        sub = sub,
        icon = icon,
        trailing = ToonListTrailing.Chevron,
        index = index,
        last = true,
        width = width,
        onClick = onClick,
    )
    BasicText(
        text,
        Modifier.padding(start = 56.dp, end = 16.dp, bottom = 8.dp),
        style = ToonTypeScale.Body.textStyle.copy(color = Toon.colors.text),
    )
    if (!last) ToonDivider(width = width, inset = true, seed = index)
}

@Composable
private fun Note(
    title: String,
    sub: String,
    width: Dp,
) = ToonListItem(
    title = title,
    sub = sub,
    icon = ToonIcons.Info,
    trailing = ToonListTrailing.None,
    last = true,
    width = width,
)

/** Nothing to do: the kit's title card with the plan's count. */
@Composable
private fun Quiet(
    state: RepoUiState,
    width: Dp,
) = Box(Modifier.fillMaxWidth().padding(top = 24.dp), contentAlignment = Alignment.Center) {
    ToonTitleCard(
        variant = ToonTitleCardVariant.Empty,
        title = Texts.PLAN_COMPLETE,
        sub =
            state.repo.plan.counts
                ?.let(Texts::doneOf)
                .orEmpty(),
        width = minOf(width, 340.dp),
        height = 300.dp,
    )
}

private val PrChecks.glyph: ToonIconGlyph
    get() =
        when (this) {
            PrChecks.Failing -> ToonIcons.Warning
            PrChecks.Ready -> ToonIcons.Check
            PrChecks.Running -> ToonIcons.Refresh
            PrChecks.NoChecks -> ToonIcons.Info
        }

private val BacklogItem.glyph: ToonIconGlyph
    get() =
        when (status) {
            ItemStatus.Question -> ToonIcons.Info
            ItemStatus.Wip -> ToonIcons.Edit
            ItemStatus.Open -> ToonIcons.Calendar
        }
