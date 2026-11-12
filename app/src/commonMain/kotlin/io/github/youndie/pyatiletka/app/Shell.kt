package io.github.youndie.pyatiletka.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalUriHandler
import io.github.youndie.pyatiletka.core.screen.Skin
import io.github.youndie.pyatiletka.skin.plakat.BoardActions
import io.github.youndie.pyatiletka.skin.plakat.PlakatBoard
import io.github.youndie.pyatiletka.skin.plakat.PlakatSkin
import io.github.youndie.pyatiletka.skin.plakat.RepoActions
import io.github.youndie.pyatiletka.skin.toon.ToonBoard
import io.github.youndie.pyatiletka.skin.toon.ToonBoardActions
import io.github.youndie.pyatiletka.skin.toon.ToonRepoActions
import io.github.youndie.pyatiletka.skin.toon.ToonRepoScreen
import io.github.youndie.pyatiletka.skin.toon.ToonSkin
import kotlinx.datetime.TimeZone

/**
 * The board as the window and the page show it, in the kit the appearance names (feature-skins). The
 * place on screen — the open card, the filter, the query — lives in [board], not in a skin, so switching
 * kits keeps it. A board row opens the repository's card; its rows open GitHub in the platform's browser.
 */
@Composable
fun Shell(
    board: BoardModel,
    zone: TimeZone,
    openSettings: () -> Unit = {},
) {
    val state by board.state.collectAsState()
    val card by board.card.collectAsState()
    val uris = LocalUriHandler.current
    val appearance = state.appearance
    when (appearance.skin) {
        Skin.Plakat -> {
            val actions =
                BoardActions(
                    filter = board::filter,
                    search = board::search,
                    resetSearch = board::resetSearch,
                    refresh = board::refresh,
                    openMenu = board::openAppearance,
                    open = board::open,
                    openSettings = openSettings,
                    chooseSkin = board::chooseSkin,
                    chooseTheme = board::chooseTheme,
                    closeAppearance = board::closeAppearance,
                )
            val cardActions = RepoActions(close = board::closeCard, openUrl = uris::openUri)
            PlakatSkin(appearance.theme) {
                PlakatBoard(state, zone, actions = actions, card = card, cardActions = cardActions)
            }
        }

        Skin.Toon -> {
            val actions =
                ToonBoardActions(
                    filter = board::filter,
                    search = board::search,
                    resetSearch = board::resetSearch,
                    refresh = board::refresh,
                    openMenu = board::openAppearance,
                    open = board::open,
                    openSettings = openSettings,
                    chooseSkin = board::chooseSkin,
                    chooseTheme = board::chooseTheme,
                    closeAppearance = board::closeAppearance,
                )
            ToonSkin(appearance.theme) {
                val open = card
                if (open == null) {
                    ToonBoard(state, zone, actions = actions)
                } else {
                    ToonRepoScreen(
                        state,
                        open,
                        zone,
                        boardActions = actions,
                        actions = ToonRepoActions(close = board::closeCard, openUrl = uris::openUri),
                    )
                }
            }
        }
    }
}
