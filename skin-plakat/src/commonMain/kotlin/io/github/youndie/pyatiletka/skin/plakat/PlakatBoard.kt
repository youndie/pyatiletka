package io.github.youndie.pyatiletka.skin.plakat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.youndie.plakat.components.PlakatButton
import io.github.youndie.plakat.components.PlakatEmptyState
import io.github.youndie.plakat.components.PlakatIconButton
import io.github.youndie.plakat.components.PlakatLinearProgress
import io.github.youndie.plakat.components.PlakatSegmentedButton
import io.github.youndie.plakat.components.PlakatTextButton
import io.github.youndie.plakat.components.PlakatTextField
import io.github.youndie.plakat.icons.PlakatIcons
import io.github.youndie.plakat.print.plakatPaper
import io.github.youndie.plakat.theme.PlakatTheme
import io.github.youndie.pyatiletka.core.BoardFilter
import io.github.youndie.pyatiletka.core.PlanForm
import io.github.youndie.pyatiletka.core.PrChecks
import io.github.youndie.pyatiletka.core.Repo
import io.github.youndie.pyatiletka.core.Summary
import io.github.youndie.pyatiletka.core.percent
import io.github.youndie.pyatiletka.core.screen.BoardScreenState
import io.github.youndie.pyatiletka.core.screen.BoardUiState
import io.github.youndie.pyatiletka.core.screen.RepoUiState
import io.github.youndie.pyatiletka.core.screen.Skin
import io.github.youndie.pyatiletka.core.screen.Texts
import io.github.youndie.pyatiletka.core.screen.Theme
import io.github.youndie.pyatiletka.core.waiting
import kotlinx.datetime.TimeZone

/** What the board can ask of the shell (`app`'s `BoardModel`). */
class BoardActions(
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

/** Wide from here: the artboards are 1280 and 390, and anything between takes the nearer one's layout. */
private val WideFrom = 720.dp

/**
 * The 640 dp red plate of the summary fits from here; below it the board takes the summary it has beside
 * the card, the 248 dp plate, or the four counters get a few dp each and break digit by digit (B-22).
 */
private val RoomyFrom = 1200.dp

/**
 * screen-board on plakat-ui: the header, the summary, the filter and the search, the table. With a
 * [card] open (screen-repo), the wide board narrows beside it and the phone shows the card alone. [zone]
 * is the viewer's time zone for the snapshot's moment — fixed in the goldens.
 */
@Composable
fun PlakatBoard(
    state: BoardUiState,
    zone: TimeZone,
    modifier: Modifier = Modifier,
    actions: BoardActions = BoardActions(),
    card: RepoUiState? = null,
    cardActions: RepoActions = RepoActions(),
) {
    val colors = PlakatTheme.colors
    BoxWithConstraints(modifier.fillMaxSize().background(colors.paper).plakatPaper()) {
        when {
            maxWidth >= WideFrom -> WideBoard(state, zone, actions, card, cardActions, roomy = maxWidth >= RoomyFrom)
            card != null -> PhoneRepo(card, state.takenAt ?: card.repo.pushedAt, cardActions)
            else -> PhoneBoard(state, zone, actions)
        }
    }
}

@Composable
private fun WideBoard(
    state: BoardUiState,
    zone: TimeZone,
    actions: BoardActions,
    card: RepoUiState?,
    cardActions: RepoActions,
    roomy: Boolean,
) {
    val colors = PlakatTheme.colors
    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            Header(state, zone, actions, roomy)
            Box(Modifier.fillMaxWidth().height(8.dp).background(colors.ink))
            if (card == null) {
                BoardBody(state, zone, actions, compact = false, roomy = roomy)
            } else {
                Row(Modifier.fillMaxWidth().weight(1f)) {
                    Column(
                        Modifier.weight(1f).fillMaxHeight(),
                    ) { BoardBody(state, zone, actions, compact = true, roomy = false) }
                    RepoPanel(card, state.takenAt ?: card.repo.pushedAt, cardActions, Modifier.fillMaxHeight())
                }
            }
        }
        if (state.screen == BoardScreenState.AppearanceSheet) AppearanceDialog(state.appearance, actions)
    }
}

/** Under the header: the board's state; [compact] beside the card, as the canvas narrows it to 800. */
@Composable
private fun ColumnScope.BoardBody(
    state: BoardUiState,
    zone: TimeZone,
    actions: BoardActions,
    compact: Boolean,
    roomy: Boolean,
) {
    val columns = if (compact || !roomy) CompactColumns else Columns
    when (state.screen) {
        BoardScreenState.Loading -> {
            LoadingBoard()
        }

        BoardScreenState.Error -> {
            Message(
                Modifier.weight(1f),
                mark = { ErrorMark() },
                title = Texts.NO_SNAPSHOT,
                body = Texts.problem(state.problem),
            ) { PlakatButton(Texts.RETRY, actions.refresh) }
        }

        BoardScreenState.NoToken -> {
            Message(Modifier.weight(1f), title = Texts.NO_TOKEN, body = Texts.NO_TOKEN_BODY) {
                PlakatButton(Texts.SETTINGS, actions.openSettings)
            }
        }

        BoardScreenState.Empty -> {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                PlakatEmptyState(
                    Texts.NO_REPOS,
                    Texts.noRepos(state.owners),
                    Texts.SETTINGS,
                    actions.openSettings,
                    Modifier.widthIn(max = 480.dp),
                )
            }
        }

        BoardScreenState.NoMatches -> {
            state.stale?.let { StaleBar(it, zone, actions.refresh) }
            BoardTop(state, actions, compact, roomy)
            Message(
                Modifier.weight(1f),
                title = Texts.NOTHING_FOUND,
                body = Texts.nothingFound(state.query, state.summary?.repos ?: 0),
            ) { PlakatTextButton(Texts.RESET_SEARCH, actions.resetSearch) }
        }

        else -> {
            // One scroll under the header: the summary and the controls go up with the rows; the stale bar
            // stays in view, as a warning should (B-23).
            state.stale?.let { StaleBar(it, zone, actions.refresh) }
            LazyColumn(Modifier.fillMaxWidth().weight(1f)) {
                item(key = TOP_KEY) { BoardTop(state, actions, compact, roomy) }
                item(key = TABLE_HEADER_KEY) {
                    TableHeaderRow(Modifier.padding(start = 24.dp, end = 24.dp, top = 16.dp), columns)
                }
                items(state.rows, key = { it.fullName }) { repo ->
                    Box(Modifier.padding(horizontal = 24.dp)) {
                        TableRow(
                            repo,
                            selected = repo.fullName == state.selected,
                            columns = columns,
                        ) { actions.open(repo.fullName) }
                    }
                }
            }
        }
    }
}

// Not a repository's full name, which always has a slash.
private const val TOP_KEY = "board:top"
private const val TABLE_HEADER_KEY = "board:table-header"

/** Above the table, scrolling with it: the summary, the filter and the search. */
@Composable
private fun BoardTop(
    state: BoardUiState,
    actions: BoardActions,
    compact: Boolean,
    roomy: Boolean,
) = Column {
    state.summary?.let {
        val at = Modifier.padding(start = 32.dp, end = 32.dp, top = 24.dp)
        if (compact || !roomy) CompactSummary(it, at) else Summary(it, at)
    }
    Controls(state, actions, compact)
}

/** The filter and the search: the search 360 wide at the right, or filling what is left when [compact]. */
@Composable
private fun Controls(
    state: BoardUiState,
    actions: BoardActions,
    compact: Boolean,
) = Row(
    Modifier.padding(start = 32.dp, end = 32.dp, top = 24.dp).fillMaxWidth().height(56.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(16.dp),
) {
    PlakatSegmentedButton(
        segments = BoardFilter.entries.map(Texts::filter),
        selectedIndex = state.filter.ordinal,
        onSelect = { actions.filter(BoardFilter.entries[it]) },
    )
    if (!compact) Spacer(Modifier.weight(1f))
    PlakatTextField(
        value = state.query,
        onValueChange = actions.search,
        placeholder = Texts.SEARCH,
        modifier = if (compact) Modifier.weight(1f) else Modifier.width(360.dp),
    )
}

/**
 * The header: the full one over a board; over a screen with no snapshot, the title and the menu only
 * (canvas). Below [RoomyFrom] the owners' line goes: the title, the moment and the two buttons fill the
 * row, and the summary already says how many repositories there are (B-22).
 */
@Composable
private fun Header(
    state: BoardUiState,
    zone: TimeZone,
    actions: BoardActions,
    roomy: Boolean,
) {
    val colors = PlakatTheme.colors
    val type = PlakatTheme.typography
    val label = type.label.style.copy(color = colors.inkMuted)
    val summary = state.summary
    val full = summary != null
    Row(
        Modifier.fillMaxWidth().height(64.dp).padding(start = 32.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        BasicText(type.headline.text(Texts.TITLE), style = type.headline.style.registered(colors.ink, colors.red))
        when {
            roomy && summary != null -> {
                BasicText(type.label.text(Texts.owners(state.owners, summary.repos)), style = label)
            }

            roomy && state.screen == BoardScreenState.Loading && state.owners.isNotEmpty() -> {
                BasicText(type.label.text(state.owners.joinToString(", ")), style = label)
            }
        }
        Spacer(Modifier.weight(1f))
        if (full) state.takenAt?.let { BasicText(type.label.text(Texts.takenAt(it, zone)), style = label) }
        Row {
            if (full) PlakatIconButton(PlakatIcons.Refresh, Texts.REFRESH, actions.refresh)
            Box(Modifier.size(48.dp).clickable(onClick = actions.openMenu), contentAlignment = Alignment.Center) {
                MoreGlyph(colors.ink)
            }
        }
    }
}

/** Display text with the registration of the canvas: the ink 1 dp off a red impression under it ("Приводка 1,1"). */
private fun TextStyle.registered(
    ink: androidx.compose.ui.graphics.Color,
    red: androidx.compose.ui.graphics.Color,
): TextStyle = copy(color = ink, shadow = Shadow(red, Offset(1f, 1f), 0f))

@Composable
private fun Summary(
    summary: Summary,
    modifier: Modifier,
) {
    val colors = PlakatTheme.colors
    val type = PlakatTheme.typography
    Row(modifier.fillMaxWidth().height(136.dp), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
        Box(
            Modifier
                .width(640.dp)
                .height(136.dp)
                .drawBehind { drawRect(colors.ink, topLeft = Offset(1f, 1f)) }
                .background(colors.red)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            BasicText(
                type.displayM.text(Texts.planDone(summary.percent ?: 0)),
                style = type.displayM.style.copy(color = colors.onRed),
            )
        }
        Column(Modifier.weight(1f).height(136.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Stat(summary.done.toString(), "из ${summary.plan} ${Texts.itemsOf(summary.plan)}")
                Stat(summary.red.toString(), Texts.redMain(summary.red))
                Stat(summary.openPullRequests.toString(), Texts.pullRequestsOpen(summary.openPullRequests))
                Stat(summary.waiting.toString(), Texts.waitingMe(summary.waiting))
            }
            Bar(if (summary.plan == 0) 0f else summary.done.toFloat() / summary.plan)
        }
    }
}

/** The summary beside the card: the share on a 248 wide red plate, the four counters, no bar (canvas). */
@Composable
private fun CompactSummary(
    summary: Summary,
    modifier: Modifier,
) {
    val colors = PlakatTheme.colors
    val type = PlakatTheme.typography
    Row(modifier.fillMaxWidth().height(96.dp), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
        Column(
            Modifier
                .width(248.dp)
                .fillMaxHeight()
                .drawBehind { drawRect(colors.ink, topLeft = Offset(1f, 1f)) }
                .background(colors.red)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            BasicText(type.label.text(Texts.PLAN_DONE_ON), style = type.label.style.copy(color = colors.onRed))
            BasicText(
                type.displayM.text(Texts.percent(summary.percent ?: 0)),
                style = type.displayM.style.copy(color = colors.onRed),
                maxLines = 1,
            )
        }
        Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Stat(summary.done.toString(), "из ${summary.plan}")
            Stat(summary.red.toString(), Texts.redMain(summary.red))
            Stat(summary.openPullRequests.toString(), Texts.pullRequestsOpen(summary.openPullRequests))
            Stat(summary.waiting.toString(), Texts.waitingMe(summary.waiting))
        }
    }
}

@Composable
private fun RowScope.Stat(
    number: String,
    caption: String,
) {
    val colors = PlakatTheme.colors
    val type = PlakatTheme.typography
    Column(
        Modifier
            .weight(1f)
            .drawBehind {
                drawRect(
                    colors.ink,
                    size =
                        androidx.compose.ui.geometry
                            .Size(size.width, 3.dp.toPx()),
                )
            }.padding(top = 15.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // One line at any width: a counter shrinks to fit rather than break digit by digit (B-22).
        BasicText(
            number,
            style = type.headline.style.registered(colors.ink, colors.red),
            maxLines = 1,
            softWrap = false,
            autoSize = TextAutoSize.StepBased(minFontSize = 12.sp, maxFontSize = type.headline.style.fontSize),
        )
        BasicText(
            type.label.text(caption),
            style = type.label.style.copy(color = colors.inkMuted),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** The summary's bar: the plan's share in ink on concrete, 8 dp, as the canvas draws it. */
@Composable
private fun Bar(
    share: Float,
    modifier: Modifier = Modifier.fillMaxWidth(),
) {
    val colors = PlakatTheme.colors
    Box(
        modifier.height(8.dp).background(colors.concrete).drawBehind {
            drawRect(
                colors.ink,
                size =
                    androidx.compose.ui.geometry
                        .Size(size.width * share.coerceIn(0f, 1f), size.height),
            )
        },
    )
}

// kit: https://github.com/youndie/plakat-ui/issues/20 — no table: the columns are laid out here with the kit's styles.
private val Columns: List<Dp> = listOf(152.dp, 160.dp, 320.dp, 88.dp)

/** Beside the card the canvas narrows the columns and the plan's bar to 64. */
private val CompactColumns: List<Dp> = listOf(112.dp, 120.dp, 208.dp, 48.dp)
private val ColumnGap = 16.dp

@Composable
internal fun TableHeaderRow(
    modifier: Modifier,
    columns: List<Dp> = Columns,
) {
    val colors = PlakatTheme.colors
    val type = PlakatTheme.typography
    val style = type.label.style.copy(color = colors.inkMuted)
    Row(
        modifier
            .fillMaxWidth()
            .height(32.dp)
            .drawBehind { drawRect(colors.ink, topLeft = Offset(0f, size.height - 3.dp.toPx())) }
            .padding(start = 8.dp, end = 8.dp, bottom = 11.dp),
        horizontalArrangement = Arrangement.spacedBy(ColumnGap),
        verticalAlignment = Alignment.Bottom,
    ) {
        BasicText(type.label.text(Texts.COLUMN_REPO), style = style, modifier = Modifier.weight(1f))
        BasicText(Texts.COLUMN_MAIN.uppercase(), style = style, modifier = Modifier.width(columns[0]))
        BasicText(type.label.text(Texts.COLUMN_PR), style = style, modifier = Modifier.width(columns[1]))
        BasicText(type.label.text(Texts.COLUMN_PLAN), style = style, modifier = Modifier.width(columns[2]))
        BasicText(
            type.label.text(Texts.COLUMN_WAITING),
            style = style.copy(textAlign = TextAlign.End),
            modifier = Modifier.width(columns[3]),
        )
    }
}

@Composable
private fun TableRow(
    repo: Repo,
    selected: Boolean,
    columns: List<Dp>,
    onClick: () -> Unit,
) {
    val colors = PlakatTheme.colors
    val type = PlakatTheme.typography
    val body = type.bodyS.style.copy(color = colors.ink)
    Row(
        Modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(if (selected) colors.concrete else androidx.compose.ui.graphics.Color.Transparent)
            .drawBehind { drawRect(colors.concrete, topLeft = Offset(0f, size.height - 1.dp.toPx())) }
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(ColumnGap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicText(
            repoName(repo.fullName, colors.inkMuted),
            style = type.bodyM.style.copy(color = colors.ink),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Box(Modifier.width(columns[0])) { MainMark(repo.main) }
        Row(
            Modifier.width(columns[1]),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicText(repo.pullRequests.size.toString(), style = body)
            val failing = repo.pullRequests.count { it.checks == PrChecks.Failing }
            if (failing > 0) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Hatch(12.dp, colors.red)
                    BasicText(
                        type.label.text(Texts.failingPullRequests(failing)),
                        style = type.label.style.copy(color = colors.ink),
                        maxLines = 1,
                    )
                }
            }
        }
        PlanCell(repo, Modifier.width(columns[2]), bar = if (columns === CompactColumns) 64.dp else 96.dp)
        BasicText(
            if (repo.plan.form == PlanForm.Items) repo.waiting.toString() else "—",
            style = body.copy(textAlign = TextAlign.End),
            modifier = Modifier.width(columns[3]),
        )
    }
}

@Composable
private fun PlanCell(
    repo: Repo,
    modifier: Modifier,
    bar: Dp,
) {
    val colors = PlakatTheme.colors
    val body = PlakatTheme.typography.bodyS.style
    val counts = repo.plan.counts
    val percent = counts?.percent
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        when {
            repo.plan.form == PlanForm.Items && counts != null && percent != null -> {
                PlakatLinearProgress(percent / 100f, Modifier.width(bar))
                BasicText(Texts.planCell(counts, percent), style = body.copy(color = colors.ink), maxLines = 1)
            }

            repo.plan.form == PlanForm.Flat -> {
                BasicText(Texts.PLAN_FLAT, style = body.copy(color = colors.inkMuted))
            }

            else -> {
                BasicText(Texts.PLAN_NONE, style = body.copy(color = colors.inkMuted))
            }
        }
    }
}

/** `youndie/` muted, the name in ink. */
internal fun repoName(
    fullName: String,
    muted: androidx.compose.ui.graphics.Color,
): AnnotatedString =
    buildAnnotatedString {
        val slash = fullName.indexOf('/') + 1
        withStyle(SpanStyle(color = muted)) { append(fullName.substring(0, slash)) }
        append(fullName.substring(slash))
    }
