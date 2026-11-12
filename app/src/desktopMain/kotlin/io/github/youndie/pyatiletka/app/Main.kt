package io.github.youndie.pyatiletka.app

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import io.github.youndie.pyatiletka.app.desktop.FrameTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
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
                    initial =
                        BoardFlags(
                            owners = graph.settings.current.value.settings.owners,
                            appearance = graph.settings.appearance,
                        ),
                    onRefresh = graph.store::refresh,
                )
            }
        val navigator = remember { Navigator() }
        DisposableEffect(Unit) {
            graph.store.start()
            onDispose { graph.store.stop() }
        }
        // The choice made on the board outlives the window (feature-skins: kept locally).
        LaunchedEffect(Unit) {
            board.state
                .map { it.appearance }
                .distinctUntilChanged()
                .drop(1)
                .collect(graph.settings::chooseAppearance)
        }
        val boardState by board.state.collectAsState()
        FrameTheme(boardState.appearance) {
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
    }
