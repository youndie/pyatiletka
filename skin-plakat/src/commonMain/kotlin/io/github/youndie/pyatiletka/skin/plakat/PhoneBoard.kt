package io.github.youndie.pyatiletka.skin.plakat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.youndie.plakat.agit.PlakatSkeleton
import io.github.youndie.plakat.components.PlakatBottomSheet
import io.github.youndie.plakat.components.PlakatButton
import io.github.youndie.plakat.components.PlakatChip
import io.github.youndie.plakat.components.PlakatEmptyState
import io.github.youndie.plakat.components.PlakatIconButton
import io.github.youndie.plakat.components.PlakatPullToRefreshBox
import io.github.youndie.plakat.components.PlakatTextButton
import io.github.youndie.plakat.components.PlakatTextField
import io.github.youndie.plakat.icons.PlakatIcons
import io.github.youndie.plakat.theme.PlakatTheme
import io.github.youndie.pyatiletka.core.BoardFilter
import io.github.youndie.pyatiletka.core.PlanForm
import io.github.youndie.pyatiletka.core.PrChecks
import io.github.youndie.pyatiletka.core.Repo
import io.github.youndie.pyatiletka.core.Summary
import io.github.youndie.pyatiletka.core.percent
import io.github.youndie.pyatiletka.core.screen.BoardScreenState
import io.github.youndie.pyatiletka.core.screen.BoardUiState
import io.github.youndie.pyatiletka.core.screen.StaleNotice
import io.github.youndie.pyatiletka.core.screen.Texts
import io.github.youndie.pyatiletka.core.waiting
import kotlinx.datetime.TimeZone

/**
 * The board at 390: the header stays, everything under it scrolls and is pulled to refresh (canvas
 * note), rows in two lines, the appearance choice in the kit's bottom sheet.
 */
@Composable
internal fun PhoneBoard(
    state: BoardUiState,
    zone: TimeZone,
    actions: BoardActions,
) {
    val colors = PlakatTheme.colors
    val type = PlakatTheme.typography
    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            Row(
                Modifier.fillMaxWidth().height(56.dp).padding(start = 16.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BasicText(
                    type.headline.text(Texts.TITLE),
                    style =
                        type.headline.style.copy(
                            color = colors.ink,
                            shadow = Shadow(colors.red, Offset(1f, 1f), 0f),
                        ),
                    modifier = Modifier.weight(1f),
                )
                Box(Modifier.size(48.dp).clickable(onClick = actions.openMenu), contentAlignment = Alignment.Center) {
                    MoreGlyph(colors.ink)
                }
            }
            Box(Modifier.fillMaxWidth().height(8.dp).background(colors.ink))
            when (state.screen) {
                BoardScreenState.Loading -> {
                    PhoneLoading()
                }

                BoardScreenState.Error -> {
                    Message(
                        Modifier.weight(1f),
                        Texts.NO_SNAPSHOT,
                        Texts.problem(state.problem),
                        mark = { ErrorMark() },
                    ) {
                        PlakatButton(Texts.RETRY, actions.refresh)
                    }
                }

                BoardScreenState.NoToken -> {
                    Message(
                        Modifier.weight(1f),
                        Texts.NO_TOKEN,
                        Texts.NO_TOKEN_BODY,
                    ) { PlakatButton(Texts.SETTINGS, actions.openSettings) }
                }

                BoardScreenState.Empty -> {
                    Box(Modifier.weight(1f).fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        PlakatEmptyState(
                            Texts.NO_REPOS,
                            Texts.noRepos(state.owners),
                            Texts.SETTINGS,
                            actions.openSettings,
                        )
                    }
                }

                else -> {
                    PlakatPullToRefreshBox(state.refreshing, actions.refresh, Modifier.weight(1f).fillMaxWidth()) {
                        LazyColumn(Modifier.fillMaxSize()) {
                            state.stale?.let { notice -> item { PhoneStaleBar(notice, zone, actions.refresh) } }
                            item { PhoneTop(state, zone, actions) }
                            if (state.screen == BoardScreenState.NoMatches) {
                                item {
                                    Message(
                                        Modifier.padding(top = 48.dp),
                                        Texts.NOTHING_FOUND,
                                        Texts.nothingFound(
                                            state.query,
                                            state.summary?.repos ?: 0,
                                        ),
                                    ) {
                                        PlakatTextButton(Texts.RESET_SEARCH, actions.resetSearch)
                                    }
                                }
                            } else {
                                item {
                                    Box(
                                        Modifier
                                            .padding(
                                                start = 16.dp,
                                                end = 16.dp,
                                                top = 8.dp,
                                            ).fillMaxWidth()
                                            .height(3.dp)
                                            .background(colors.ink),
                                    )
                                }
                                items(
                                    state.rows,
                                    key = { it.fullName },
                                ) { repo -> PhoneRow(repo) { actions.open(repo.fullName) } }
                            }
                        }
                    }
                }
            }
        }
        PlakatBottomSheet(
            visible = state.screen == BoardScreenState.AppearanceSheet,
            onDismissRequest = actions.closeAppearance,
            paneTitle = Texts.APPEARANCE,
        ) {
            Column(Modifier.padding(horizontal = 16.dp)) {
                BasicText(
                    type.headline.text(Texts.APPEARANCE),
                    style =
                        type.headline.style.copy(
                            color = colors.ink,
                            shadow = Shadow(colors.red, Offset(1f, 1f), 0f),
                        ),
                    modifier = Modifier.padding(bottom = 16.dp),
                )
                Box(Modifier.fillMaxWidth().height(3.dp).background(colors.ink))
                AppearanceChoices(state.appearance, actions)
            }
        }
    }
}

@Composable
private fun PhoneTop(
    state: BoardUiState,
    zone: TimeZone,
    actions: BoardActions,
) {
    val colors = PlakatTheme.colors
    val type = PlakatTheme.typography
    Column(Modifier.padding(horizontal = 16.dp)) {
        state.takenAt?.let {
            BasicText(
                type.label.text(Texts.takenAt(it, zone)),
                style = type.label.style.copy(color = colors.inkMuted),
                modifier = Modifier.padding(top = 16.dp),
            )
        }
        state.summary?.let { PhoneSummary(it) }
        Row(Modifier.padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BoardFilter.entries.forEach { f -> PlakatChip(Texts.filter(f), f == state.filter, { actions.filter(f) }) }
        }
        PlakatTextField(
            state.query,
            actions.search,
            Modifier.padding(top = 16.dp).fillMaxWidth(),
            placeholder = Texts.SEARCH,
        )
    }
}

@Composable
private fun PhoneSummary(summary: Summary) {
    val colors = PlakatTheme.colors
    val type = PlakatTheme.typography
    val onRed = type.label.style.copy(color = colors.onRed)
    Row(
        Modifier
            .padding(top = 8.dp)
            .fillMaxWidth()
            .height(96.dp)
            .drawBehind { drawRect(colors.ink, topLeft = Offset(1f, 1f)) }
            .background(colors.red)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        // The column is taller than the plate's 64 dp of content: as in CSS's `align-items: flex-end`, it
        // stands on the bottom and rises past the top padding rather than being cut.
        Column(
            Modifier.weight(1f).wrapContentHeight(Alignment.Bottom, unbounded = true),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BasicText(type.label.text("План выполнен на"), style = onRed)
            BasicText("${summary.percent ?: 0}\u00A0%", style = type.displayM.style.copy(color = colors.onRed))
        }

        BasicText(type.label.text("${summary.done} из ${summary.plan}"), style = onRed)
    }
    Row(Modifier.padding(top = 8.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        PhoneStat(summary.red.toString(), Texts.red(summary.red))
        PhoneStat(summary.openPullRequests.toString(), Texts.COLUMN_PR)
        PhoneStat(summary.waiting.toString(), Texts.waitingMe(summary.waiting))
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.PhoneStat(
    number: String,
    caption: String,
) {
    val colors = PlakatTheme.colors
    val type = PlakatTheme.typography
    Column(
        Modifier
            .weight(
                1f,
            ).drawBehind { drawRect(colors.ink, size = Size(size.width, 3.dp.toPx())) }
            .padding(top = 15.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        BasicText(
            number,
            style = type.headline.style.copy(color = colors.ink, shadow = Shadow(colors.red, Offset(1f, 1f), 0f)),
        )
        BasicText(
            type.label.text(caption),
            style = type.label.style.copy(color = colors.inkMuted),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** A repository in two lines: the name and the default branch's mark; the plan and the counters. */
@Composable
private fun PhoneRow(
    repo: Repo,
    onClick: () -> Unit,
) {
    val colors = PlakatTheme.colors
    val type = PlakatTheme.typography
    val counts = repo.plan.counts
    val percent = counts?.percent
    val failing = repo.pullRequests.count { it.checks == PrChecks.Failing }
    val counters =
        buildList {
            add("PR ${repo.pullRequests.size}")
            if (failing > 0) add(Texts.failingPullRequests(failing))
            if (repo.plan.form == PlanForm.Items) add("ждут ${repo.waiting}")
        }.joinToString(" · ")
    Column(
        Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(76.dp)
            .drawBehind { drawRect(colors.concrete, topLeft = Offset(0f, size.height - 1.dp.toPx())) }
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BasicText(
                repoName(repo.fullName, colors.inkMuted),
                style = type.bodyM.style.copy(color = colors.ink),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            MainMark(repo.main)
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            val plan =
                when {
                    repo.plan.form == PlanForm.Items && counts != null && percent != null -> {
                        Texts.planCell(
                            counts,
                            percent,
                        )
                    }

                    repo.plan.form == PlanForm.Flat -> {
                        Texts.PLAN_FLAT
                    }

                    else -> {
                        Texts.PLAN_NONE
                    }
                }
            BasicText(
                plan,
                style = type.bodyS.style.copy(color = colors.ink),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            BasicText(type.label.text(counters), style = type.label.style.copy(color = colors.inkMuted), maxLines = 1)
        }
    }
}

// kit: https://github.com/youndie/plakat-ui/issues/25 — the stale bar, at 390 with the retry as ↻ (canvas note).
@Composable
private fun PhoneStaleBar(
    notice: StaleNotice,
    zone: TimeZone,
    retry: () -> Unit,
) {
    val colors = PlakatTheme.colors
    val type = PlakatTheme.typography
    Row(
        Modifier.fillMaxWidth().background(colors.ocher).padding(start = 16.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            BasicText(type.label.text(Texts.ATTENTION), style = type.label.style.copy(color = colors.ink))
            BasicText(Texts.stale(notice, zone), style = type.bodyS.style.copy(color = colors.ink))
        }
        PlakatIconButton(PlakatIcons.Refresh, Texts.RETRY, retry)
    }
}

@Composable
private fun PhoneLoading() {
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        PlakatSkeleton(seed = 1, modifier = Modifier.fillMaxWidth().height(96.dp))
        PlakatSkeleton(seed = 2, modifier = Modifier.fillMaxWidth().height(56.dp))
        repeat(6) { i -> PlakatSkeleton(seed = 10 + i, modifier = Modifier.fillMaxWidth().height(52.dp)) }
    }
}
