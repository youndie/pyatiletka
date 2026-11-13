package io.github.youndie.pyatiletka.app

import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget
import org.w3c.dom.events.KeyboardEvent

/**
 * Esc on the page closes the top layer (B-27). The page listens itself, in the capture phase, rather
 * than through Compose: a Compose key handler hears only what reaches a focused node, and after a
 * click on the board nothing may be focused.
 */
fun closeOnEscape(
    target: EventTarget,
    board: BoardModel,
) {
    target.addEventListener(
        "keydown",
        { event: Event ->
            if ((event as KeyboardEvent).key == "Escape" && board.escape()) event.preventDefault()
        },
        true,
    )
}
