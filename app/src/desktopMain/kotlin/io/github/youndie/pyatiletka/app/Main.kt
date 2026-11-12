package io.github.youndie.pyatiletka.app

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.youndie.appframe.AppFrame

/** `./gradlew :app:run`: the board in an appframe window, 1280×800 like the wide artboards. */
fun main() =
    application {
        AppFrame(
            onCloseRequest = ::exitApplication,
            state = rememberWindowState(size = DpSize(1280.dp, 800.dp)),
            title = "Пятилетка",
        ) {
            BothSkins()
        }
    }
