package io.github.youndie.pyatiletka.skin.plakat

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import io.github.youndie.plakat.components.PlakatIconButton
import io.github.youndie.plakat.icons.PlakatIcons
import io.github.youndie.plakat.theme.PlakatTheme
import io.github.youndie.pyatiletka.core.BacklogItem
import io.github.youndie.pyatiletka.core.FailedCheck
import io.github.youndie.pyatiletka.core.ItemStatus
import io.github.youndie.pyatiletka.core.PlanForm
import io.github.youndie.pyatiletka.core.PrChecks
import io.github.youndie.pyatiletka.core.PullRequest
import io.github.youndie.pyatiletka.core.screen.RepoScreenState
import io.github.youndie.pyatiletka.core.screen.RepoUiState
import io.github.youndie.pyatiletka.core.screen.Texts
import kotlin.time.Instant

/** What the card can ask of the shell: close it, open a pull request, an item or a check on GitHub. */
class RepoActions(
    val close: () -> Unit = {},
    val openUrl: (String) -> Unit = {},
)

/** The card's width beside the board, as the canvas draws it. */
internal val PanelWidth = 480.dp

/**
 * screen-repo beside the board: a 480 dp panel behind a 3 dp rule, the repository's name and ✕, then the
 * card. A pull request's age is counted to [takenAt], the snapshot's moment.
 */
@Composable
internal fun RepoPanel(
    state: RepoUiState,
    takenAt: Instant,
    actions: RepoActions,
    modifier: Modifier = Modifier,
) {
    val colors = PlakatTheme.colors
    Column(
        modifier
            .width(PanelWidth)
            .drawBehind { drawRect(colors.ink, size = Size(3.dp.toPx(), size.height)) }
            .padding(start = 27.dp, end = 24.dp),
    ) {
        Row(Modifier.fillMaxWidth().height(56.dp), verticalAlignment = Alignment.CenterVertically) {
            RepoTitle(state.repo.fullName, Modifier.weight(1f))
            PlakatIconButton(PlakatIcons.Close, Texts.CLOSE, actions.close, Modifier.offset(x = 12.dp))
        }
        RepoCard(state, takenAt, actions, PaddingValues(top = 4.dp, bottom = 24.dp))
    }
}

/** screen-repo at 390: its own screen, ← back to the board, the card under the header's 8 dp rule. */
@Composable
internal fun PhoneRepo(
    state: RepoUiState,
    takenAt: Instant,
    actions: RepoActions,
) {
    val colors = PlakatTheme.colors
    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().height(56.dp).padding(start = 4.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PlakatIconButton(PlakatIcons.Back, Texts.BACK, actions.close)
            RepoTitle(state.repo.fullName, Modifier.weight(1f).padding(start = 4.dp))
        }
        Box(Modifier.fillMaxWidth().height(8.dp).background(colors.ink))
        RepoCard(state, takenAt, actions, PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp))
    }
}

/** `youndie/` muted, the name in ink, one line with an ellipsis (canvas note for the phone). */
@Composable
private fun RepoTitle(
    fullName: String,
    modifier: Modifier,
) {
    val colors = PlakatTheme.colors
    BasicText(
        repoName(fullName, colors.inkMuted),
        modifier,
        style =
            PlakatTheme.typography.title.style
                .copy(color = colors.ink),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

/**
 * The card under its header: the default branch, failed checks first when it is red, the plan, the open
 * pull requests, the unclosed items — in core's order (`cardPullRequests`, `cardItems`), all of them. A
 * repository with nothing to do gets the poster instead of the plan and the lists.
 */
@Composable
private fun RepoCard(
    state: RepoUiState,
    takenAt: Instant,
    actions: RepoActions,
    padding: PaddingValues,
) = LazyColumn(Modifier.fillMaxSize(), contentPadding = padding) {
    item { BranchLine(state) }
    failedChecks(state, actions)
    if (state.screen == RepoScreenState.Quiet) {
        item { Quiet(state) }
    } else {
        item { Plan(state) }
        pullRequests(state.pullRequests, takenAt, actions)
        unclosedItems(state, actions)
    }
}

@Composable
private fun BranchLine(state: RepoUiState) {
    val colors = PlakatTheme.colors
    val label = PlakatTheme.typography.label
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        BasicText(label.text(state.repo.defaultBranch), style = label.style.copy(color = colors.inkMuted))
        MainMark(state.repo.main)
    }
}

/** A section's name over a 3 dp rule. */
@Composable
private fun Heading(text: String) {
    val colors = PlakatTheme.colors
    val label = PlakatTheme.typography.label
    Box(
        Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
            .drawBehind { drawRect(colors.ink, Offset(0f, size.height - 3.dp.toPx())) }
            .padding(bottom = 11.dp),
    ) {
        BasicText(label.text(text), style = label.style.copy(color = colors.inkMuted))
    }
}

private fun LazyListScope.failedChecks(
    state: RepoUiState,
    actions: RepoActions,
) {
    val checks = state.repo.failedChecks
    if (checks.isEmpty()) return
    item { Heading(Texts.failedChecksHeading(state.repo.defaultBranch, checks.size)) }
    items(checks, key = { "check:${it.name}" }) { CheckRow(it) { actions.openUrl(it.url) } }
}

@Composable
private fun CheckRow(
    check: FailedCheck,
    onClick: () -> Unit,
) {
    val colors = PlakatTheme.colors
    val type = PlakatTheme.typography
    Row(
        Modifier
            .fillMaxWidth()
            .height(48.dp)
            .rule(colors.concrete)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Hatch(16.dp, colors.red)
        BasicText(
            check.name,
            Modifier.weight(1f),
            style = type.bodyM.style.copy(color = colors.ink),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        BasicText(type.label.text(Texts.ERROR), style = type.label.style.copy(color = colors.ink))
        ExternalGlyph(colors.inkMuted)
    }
}

/** The plan: its share and count over the bar, or a concrete plate when the board does not count it. */
@Composable
private fun Plan(state: RepoUiState) {
    val colors = PlakatTheme.colors
    val type = PlakatTheme.typography
    Column {
        Heading(Texts.PLAN)
        val counts = state.repo.plan.counts
        when {
            state.repo.plan.form == PlanForm.Items && counts != null -> {
                Row(
                    Modifier.padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    BasicText(
                        type.headline.text(Texts.percent(state.percent ?: 0)),
                        style =
                            type.headline.style.copy(
                                color = colors.ink,
                                shadow = Shadow(colors.red, Offset(1f, 1f), 0f),
                            ),
                    )
                    BasicText(Texts.doneOfLong(counts), style = type.bodyS.style.copy(color = colors.inkMuted))
                }
                PlanBar((state.percent ?: 0) / 100f, Modifier.padding(top = 12.dp))
            }

            state.repo.plan.form == PlanForm.Flat -> {
                PlanPlate(Texts.FLAT_PLAN, Texts.FLAT_PLAN_BODY)
            }

            else -> {
                PlanPlate(Texts.NO_BACKLOG, null)
            }
        }
    }
}

@Composable
private fun PlanPlate(
    title: String,
    body: String?,
) {
    val colors = PlakatTheme.colors
    val type = PlakatTheme.typography
    Column(
        Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
            .background(colors.concrete)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        BasicText(title, style = type.title.style.copy(color = colors.ink))
        body?.let { BasicText(it, style = type.bodyS.style.copy(color = colors.inkMuted)) }
    }
}

/** Nothing to do: the red poster «План выполнен», the count over a full bar, and why there are no lists. */
@Composable
private fun Quiet(state: RepoUiState) {
    val colors = PlakatTheme.colors
    val type = PlakatTheme.typography
    val counts = state.repo.plan.counts
    Column(Modifier.padding(top = 24.dp)) {
        Box(
            Modifier
                .fillMaxWidth()
                .drawBehind { drawRect(colors.ink, topLeft = Offset(1f, 1f)) }
                .background(colors.red)
                // The canvas's poster runs «ВЫПОЛНЕН» almost to its right edge: at 390 a 24 dp right
                // padding breaks the word.
                .padding(start = 24.dp, end = 4.dp, top = 20.dp, bottom = 20.dp),
        ) {
            BasicText(
                type.displayM.text(Texts.PLAN_COMPLETE),
                style = type.displayM.style.copy(color = colors.onRed),
            )
        }
        if (counts != null) {
            Row(
                Modifier.padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                BasicText(
                    type.headline.text(Texts.doneOf(counts)),
                    style =
                        type.headline.style.copy(
                            color = colors.ink,
                            shadow = Shadow(colors.red, Offset(1f, 1f), 0f),
                        ),
                )
                BasicText(Texts.percent(state.percent ?: 100), style = type.bodyS.style.copy(color = colors.inkMuted))
            }
        }
        PlanBar(1f, Modifier.padding(top = 12.dp))
        BasicText(
            Texts.NOTHING_TO_DO,
            Modifier.padding(top = 24.dp),
            style = type.bodyM.style.copy(color = colors.inkMuted),
        )
    }
}

/** The card's bar: the share in ink on concrete, 8 dp, the panel's width. */
@Composable
private fun PlanBar(
    share: Float,
    modifier: Modifier,
) {
    val colors = PlakatTheme.colors
    Box(
        modifier.fillMaxWidth().height(8.dp).background(colors.concrete).drawBehind {
            drawRect(colors.ink, size = Size(size.width * share.coerceIn(0f, 1f), size.height))
        },
    )
}

private fun LazyListScope.pullRequests(
    prs: List<PullRequest>,
    takenAt: Instant,
    actions: RepoActions,
) {
    if (prs.isEmpty()) return
    item { Heading(Texts.pullRequestsHeading(prs.size)) }
    items(prs, key = { "pr:${it.number}" }) { pr ->
        Entry({ actions.openUrl(pr.url) }) {
            val colors = PlakatTheme.colors
            val type = PlakatTheme.typography
            BasicText(
                buildAnnotatedString {
                    withStyle(SpanStyle(color = colors.inkMuted)) { append("#${pr.number}") }
                    append(" ${pr.title}")
                },
                style = type.bodyS.style.copy(color = colors.ink),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Muted(Texts.author(pr.author))
                ChecksMark(pr.checks)
                Muted(Texts.age(takenAt - pr.createdAt))
            }
        }
    }
}

private fun LazyListScope.unclosedItems(
    state: RepoUiState,
    actions: RepoActions,
) {
    if (state.repo.plan.form != PlanForm.Items || state.items.isEmpty()) return
    item { Heading(Texts.itemsHeading(state.items.size)) }
    items(state.items, key = { "item:${it.id}" }) { entry ->
        Entry({ actions.openUrl(entry.url) }) {
            val colors = PlakatTheme.colors
            val type = PlakatTheme.typography
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                BasicText(type.label.text(entry.id), style = type.label.style.copy(color = colors.ink))
                StatusPlate(entry)
                entry.priority?.let { Muted(it) }
            }
            BasicText(entry.title, style = type.bodyS.style.copy(color = colors.ink))
        }
    }
}

/** A pull request or an item: its lines, 8 dp apart, and ↗ at the top right; the whole row opens GitHub. */
@Composable
private fun Entry(
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) = Row(
    Modifier
        .fillMaxWidth()
        .rule(PlakatTheme.colors.concrete)
        .clickable(onClick = onClick)
        .padding(vertical = 12.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
) {
    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) { content() }
    ExternalGlyph(PlakatTheme.colors.inkMuted)
}

@Composable
private fun Muted(text: String) {
    val label = PlakatTheme.typography.label
    BasicText(label.text(text), style = label.style.copy(color = PlakatTheme.colors.inkMuted), maxLines = 1)
}

/** A pull request's checks: hatching for failed, the wedge-check for ready, a slate plate while running. */
@Composable
private fun ChecksMark(checks: PrChecks) {
    val colors = PlakatTheme.colors
    val label = PlakatTheme.typography.label
    val word = Texts.checksLong(checks)
    when (checks) {
        PrChecks.Running -> {
            Plate(word, colors.slate, colors.onRed)
        }

        else -> {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                when (checks) {
                    PrChecks.Failing -> Hatch(16.dp, colors.red)
                    PrChecks.Ready -> Check(16.dp, colors.ink)
                    else -> Unit
                }
                BasicText(label.text(word), style = label.style.copy(color = colors.ink), maxLines = 1)
            }
        }
    }
}

/** An item's status on a plate: ocher for a question, slate for work in progress, a 1 dp frame for open. */
@Composable
private fun StatusPlate(item: BacklogItem) {
    val colors = PlakatTheme.colors
    val word = Texts.itemStatusLong(item.status)
    when (item.status) {
        ItemStatus.Question -> {
            Plate(word, colors.ocher, colors.ink)
        }

        ItemStatus.Wip -> {
            Plate(word, colors.slate, colors.onRed)
        }

        ItemStatus.Open -> {
            Plate(word, Color.Transparent, colors.ink, Modifier.border(1.dp, colors.ink))
        }
    }
}

/** A row's 1 dp concrete rule under it. */
private fun Modifier.rule(color: Color): Modifier =
    drawBehind { drawRect(color, Offset(0f, size.height - 1.dp.toPx())) }

// kit: https://github.com/youndie/plakat-ui/issues/21 — no stencil for "opens elsewhere", and a consumer cannot
// make one; drawn here from the canvas's own path.

/** ↗: a box open at its top right with an arrow out of it, 16 dp, as the canvas draws it. */
@Composable
private fun ExternalGlyph(color: Color) =
    Canvas(Modifier.size(16.dp)) {
        val s = size.width / 24f
        val stroke = Stroke(width = 3 * s, cap = StrokeCap.Butt, join = StrokeJoin.Miter)
        val p =
            Path().apply {
                moveTo(13 * s, 4 * s)
                lineTo(20 * s, 4 * s)
                lineTo(20 * s, 11 * s)
                moveTo(20 * s, 4 * s)
                lineTo(10 * s, 14 * s)
                moveTo(17 * s, 15 * s)
                lineTo(17 * s, 20 * s)
                lineTo(4 * s, 20 * s)
                lineTo(4 * s, 7 * s)
                lineTo(9 * s, 7 * s)
            }
        drawPath(p, color, style = stroke)
    }
