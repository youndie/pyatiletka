package io.github.youndie.pyatiletka.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.github.youndie.pyatiletka.core.screen.Skin
import io.github.youndie.pyatiletka.skin.plakat.BoardActions
import io.github.youndie.pyatiletka.skin.plakat.PlakatBoard
import io.github.youndie.pyatiletka.skin.plakat.PlakatSkin
import kotlinx.datetime.TimeZone

/**
 * The board as the window and the page show it. Only the poster kit draws it so far (B-11); the
 * cartoon kit's screens and the switch between the two are B-14 and B-16 — until then a toon choice
 * keeps the poster kit on screen. The header's menu opens the appearance sheet; settings are B-13.
 */
@Composable
fun Shell(
    board: BoardModel,
    zone: TimeZone,
    openSettings: () -> Unit = {},
) {
    val state by board.state.collectAsState()
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
    val theme =
        state.appearance.theme.takeIf { state.appearance.skin == Skin.Plakat }
            ?: io.github.youndie.pyatiletka.core.screen.Theme.Paper
    PlakatSkin(theme) { PlakatBoard(state, zone, actions = actions) }
}
