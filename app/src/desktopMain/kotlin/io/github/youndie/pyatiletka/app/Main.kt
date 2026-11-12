package io.github.youndie.pyatiletka.app

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.youndie.appframe.AppFrame
import io.github.youndie.pyatiletka.app.desktop.DesktopGraph
import io.github.youndie.pyatiletka.app.desktop.DesktopSettings
import kotlinx.datetime.TimeZone

/**
 * `./gradlew :app:run`: the board in an appframe window, 1280×800 like the wide artboards, collected
 * in-process; «Настройки» swaps the board for the settings and ← swaps it back.
 */
fun main() =
    application {
        val scope = rememberCoroutineScope()
        val graph = remember { DesktopGraph(scope) }
        val board =
            remember {
                BoardModel(
                    data = graph.store.data,
                    scope = scope,
                    initial = BoardFlags(owners = graph.settings.current.value.settings.owners),
                    onRefresh = graph.store::refresh,
                )
            }
        val navigator = remember { Navigator() }
        DisposableEffect(Unit) {
            graph.store.start()
            onDispose { graph.store.stop() }
        }
        AppFrame(
            onCloseRequest = ::exitApplication,
            state = rememberWindowState(size = DpSize(1280.dp, 800.dp)),
            title = "Пятилетка",
        ) {
            val route by navigator.current.collectAsState()
            when (route) {
                Route.Settings -> DesktopSettings(graph.settings, board, navigator::back)
                Route.Board -> Shell(board, TimeZone.currentSystemDefault(), openSettings = navigator::openSettings)
            }
        }
    }
