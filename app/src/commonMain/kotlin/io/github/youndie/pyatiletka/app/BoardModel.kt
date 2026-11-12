package io.github.youndie.pyatiletka.app

import io.github.youndie.pyatiletka.app.data.Data
import io.github.youndie.pyatiletka.app.data.RefreshFailure
import io.github.youndie.pyatiletka.app.data.Staleness
import io.github.youndie.pyatiletka.core.BoardFilter
import io.github.youndie.pyatiletka.core.boardRows
import io.github.youndie.pyatiletka.core.screen.Appearance
import io.github.youndie.pyatiletka.core.screen.BoardScreenState
import io.github.youndie.pyatiletka.core.screen.BoardUiState
import io.github.youndie.pyatiletka.core.screen.Problem
import io.github.youndie.pyatiletka.core.screen.RepoUiState
import io.github.youndie.pyatiletka.core.screen.Skin
import io.github.youndie.pyatiletka.core.screen.StaleNotice
import io.github.youndie.pyatiletka.core.screen.Theme
import io.github.youndie.pyatiletka.core.screen.repoUiState
import io.github.youndie.pyatiletka.core.summarize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

/** What the person did on the board — the part of its state that is not the snapshot. Survives a skin switch (feature-skins). */
data class BoardFlags(
    val filter: BoardFilter = BoardFilter.All,
    val query: String = "",
    val appearanceOpen: Boolean = false,
    val selected: String? = null,
    val appearance: Appearance = Appearance(),
    /** The owners asked for, shown while there is no snapshot to name them (`Loading`). */
    val owners: List<String> = emptyList(),
    val hasSettings: Boolean = true,
)

/**
 * The board's state for both skins: [Data] from the snapshot store and the person's [BoardFlags],
 * combined (screen-board, section 1). The repository card is derived here too, from the same snapshot:
 * it has no loading of its own (feature-repo-detail).
 */
class BoardModel(
    data: StateFlow<Data>,
    scope: CoroutineScope,
    initial: BoardFlags = BoardFlags(),
    private val onRefresh: () -> Unit,
) {
    private val flags = MutableStateFlow(initial)

    val state: StateFlow<BoardUiState> =
        combine(
            data,
            flags,
            ::boardUiState,
        ).stateIn(scope, SharingStarted.Eagerly, boardUiState(data.value, flags.value))

    val card: StateFlow<RepoUiState?> =
        combine(data, flags, ::cardOf).stateIn(scope, SharingStarted.Eagerly, cardOf(data.value, flags.value))

    fun filter(filter: BoardFilter) = flags.update { it.copy(filter = filter) }

    fun search(query: String) = flags.update { it.copy(query = query) }

    /** "Сбросить поиск" and the field's cross: the query goes, the filter stays (canvas note, Board_NoMatches). */
    fun resetSearch() = flags.update { it.copy(query = "") }

    fun refresh() = onRefresh()

    fun openAppearance() = flags.update { it.copy(appearanceOpen = true) }

    fun closeAppearance() = flags.update { it.copy(appearanceOpen = false) }

    fun open(fullName: String) = flags.update { it.copy(selected = fullName) }

    fun closeCard() = flags.update { it.copy(selected = null) }

    /** A skin is chosen with its first theme; the window and the page keep it, and the page reads it from the address (B-16). */
    fun chooseSkin(skin: Skin) =
        flags.update {
            if (it.appearance.skin ==
                skin
            ) {
                it
            } else {
                it.copy(appearance = Appearance(skin, Theme.of(skin).first()))
            }
        }

    fun chooseTheme(theme: Theme) = flags.update { it.copy(appearance = Appearance(theme.skin, theme)) }
}

/**
 * The board's state from the data and the flags. The screen is the one thing an artboard shows; the
 * rest — the stale bar, a refresh under way — rides along: an open appearance sheet is drawn over
 * whatever is beneath it, and a search that finds nothing on a stale snapshot still has its bar.
 */
fun boardUiState(
    data: Data,
    flags: BoardFlags,
): BoardUiState =
    when (data) {
        Data.Loading -> {
            BoardUiState(BoardScreenState.Loading, filter = flags.filter, query = flags.query, refreshing = true)
        }

        is Data.Failed -> {
            val noToken = data.failure == RefreshFailure.NoToken
            BoardUiState(
                screen = if (noToken) BoardScreenState.NoToken else BoardScreenState.Error,
                filter = flags.filter,
                query = flags.query,
                problem = data.failure.toProblem(),
                refreshing = data.refreshing,
                owners = flags.owners,
                appearance = flags.appearance,
                hasSettings = flags.hasSettings,
            )
        }

        is Data.Ready -> {
            val repos = data.snapshot.repos
            val rows = boardRows(repos, flags.filter, flags.query)
            val stale =
                data.stale?.let {
                    StaleNotice(data.snapshot.takenAt, (it as? Staleness.LastAttemptFailed)?.failure?.toProblem())
                }
            val screen =
                when {
                    repos.isEmpty() -> BoardScreenState.Empty
                    flags.appearanceOpen -> BoardScreenState.AppearanceSheet
                    rows.isEmpty() -> BoardScreenState.NoMatches
                    stale != null -> BoardScreenState.Stale
                    else -> BoardScreenState.Content
                }
            BoardUiState(
                screen = screen,
                summary = summarize(repos),
                rows = rows,
                filter = flags.filter,
                query = flags.query,
                takenAt = data.snapshot.takenAt,
                stale = stale,
                refreshing = data.refreshing,
                selected = flags.selected?.takeIf { name -> repos.any { it.fullName == name } },
                owners = data.snapshot.owners,
                appearance = flags.appearance,
                hasSettings = flags.hasSettings,
            )
        }
    }

private fun cardOf(
    data: Data,
    flags: BoardFlags,
): RepoUiState? {
    val name = flags.selected ?: return null
    val repo = (data as? Data.Ready)?.snapshot?.repos?.firstOrNull { it.fullName == name } ?: return null
    return repoUiState(repo)
}

internal fun RefreshFailure.toProblem(): Problem =
    when (this) {
        RefreshFailure.NoNetwork -> Problem(Problem.Kind.NoNetwork)
        RefreshFailure.NoToken -> Problem(Problem.Kind.NoToken)
        RefreshFailure.TokenRejected -> Problem(Problem.Kind.TokenRejected)
        RefreshFailure.NotPublished -> Problem(Problem.Kind.NotPublished)
        is RefreshFailure.OwnerNotFound -> Problem(Problem.Kind.OwnerNotFound, login)
        is RefreshFailure.NewerSchema -> Problem(Problem.Kind.NewerSchema, found.toString())
        is RefreshFailure.Other -> Problem(Problem.Kind.Other, reason)
    }
