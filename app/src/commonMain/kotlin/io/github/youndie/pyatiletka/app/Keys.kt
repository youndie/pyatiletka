package io.github.youndie.pyatiletka.app

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type

/**
 * The window's keys over the board (B-27): Esc closes the top layer ([BoardModel.escape]). True only
 * when it closed something, so an Esc with nothing open is left to the focused element.
 */
fun BoardModel.onKey(event: KeyEvent): Boolean =
    event.type == KeyEventType.KeyDown && event.key == Key.Escape && escape()
