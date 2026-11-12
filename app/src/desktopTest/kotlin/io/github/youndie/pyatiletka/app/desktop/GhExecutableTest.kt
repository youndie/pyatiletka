package io.github.youndie.pyatiletka.app.desktop

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

// B-18: the installed app is started by the system, not a shell, and its PATH has no Homebrew in it.
class GhExecutableTest {
    @Test
    fun anAppStartedFromTheDockStillFindsHomebrewsGh() =
        assertEquals(
            "/opt/homebrew/bin/gh",
            ghExecutable("/usr/bin:/bin:/usr/sbin:/sbin") { it == "/opt/homebrew/bin/gh" },
        )

    @Test
    fun theShellsPathComesFirst() =
        assertEquals(
            "/home/me/bin/gh",
            ghExecutable("/home/me/bin:/usr/bin") { it == "/home/me/bin/gh" || it == "/opt/homebrew/bin/gh" },
        )

    @Test
    fun noGhAnywhereIsNone() = assertNull(ghExecutable(null) { false })
}
