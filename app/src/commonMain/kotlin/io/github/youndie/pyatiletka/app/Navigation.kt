package io.github.youndie.pyatiletka.app

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * The screens one can be on. The repository card is not a route: on a wide window it is a panel beside
 * the board ([BoardFlags.selected]), and on a narrow one the skin draws it in the board's place.
 */
enum class Route { Board, Settings }

class Navigator {
    private val route = MutableStateFlow(Route.Board)
    val current: StateFlow<Route> = route.asStateFlow()

    /** The header's menu → "Настройки", and the buttons of `NoToken` and `Empty` (desktop only: the web has no settings). */
    fun openSettings() {
        route.value = Route.Settings
    }

    fun back() {
        route.value = Route.Board
    }
}
