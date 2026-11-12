package io.github.youndie.pyatiletka.app.desktop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import io.github.youndie.pyatiletka.app.BoardModel
import io.github.youndie.pyatiletka.core.screen.Skin
import io.github.youndie.pyatiletka.core.screen.Theme
import io.github.youndie.pyatiletka.skin.plakat.PlakatSettings
import io.github.youndie.pyatiletka.skin.plakat.PlakatSkin
import io.github.youndie.pyatiletka.skin.plakat.SettingsActions
import kotlinx.coroutines.launch

/**
 * screen-settings in the window, over [model]: the poster kit draws it in the board's theme (the cartoon
 * kit's settings join with the skin switch, B-16). The owner field is held here; it empties once GitHub
 * knew the login and keeps it when it did not, under the field's error.
 */
@Composable
internal fun DesktopSettings(
    model: SettingsModel,
    board: BoardModel,
    back: () -> Unit,
) {
    val settings by model.current.collectAsState()
    val boardState by board.state.collectAsState()
    var field by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val theme = boardState.appearance.theme.takeIf { boardState.appearance.skin == Skin.Plakat } ?: Theme.Paper
    val actions =
        SettingsActions(
            back = back,
            chooseTokenSource = model::chooseTokenSource,
            recheckToken = model::recheckToken,
            editOwner = {
                field = it
                model.editingOwner()
            },
            addOwner = { login ->
                scope.launch {
                    model.addOwner(login)
                    if (model.current.value.ui.ownerNotFound == null) field = ""
                }
            },
            removeOwner = model::removeOwner,
            choosePeriod = model::choosePeriod,
        )
    PlakatSkin(theme) { PlakatSettings(settings.ui, field, actions = actions) }
}
