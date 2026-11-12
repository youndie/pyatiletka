package io.github.youndie.pyatiletka.app

import io.github.youndie.pyatiletka.core.screen.Appearance
import io.github.youndie.pyatiletka.core.screen.Skin
import io.github.youndie.pyatiletka.core.screen.Theme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

// feature-skins: "the address picks the skin" — `?skin=toon&theme=night` and its short theme names.
class AppearanceKeysTest {
    @Test
    fun theBriefsExampleIsTheCartoonNight() =
        assertEquals(Appearance(Skin.Toon, Theme.InkGouacheNight), appearanceOfQuery("?skin=toon&theme=night"))

    @Test
    fun aSkinAloneTakesItsFirstTheme() {
        assertEquals(Appearance(Skin.Toon, Theme.InkGouacheLight), appearanceOfQuery("skin=toon"))
        assertEquals(Appearance(Skin.Plakat, Theme.Paper), appearanceOfQuery("?skin=PLAKAT"))
    }

    @Test
    fun aThemeAlonePicksItsSkin() =
        assertEquals(Appearance(Skin.Plakat, Theme.BlackPaper), appearanceOfQuery("?theme=black"))

    @Test
    fun aThemeOfTheOtherSkinIsDropped() =
        assertEquals(Appearance(Skin.Plakat, Theme.Paper), appearanceOfQuery("?skin=plakat&theme=night"))

    @Test
    fun nothingKnownIsNoAppearance() {
        assertNull(appearanceOfQuery(""))
        assertNull(appearanceOfQuery("?skin=neon&x=1"))
    }

    @Test
    fun everyAppearanceReadsBackFromItsQuery() =
        Theme.entries.forEach { theme ->
            val appearance = Appearance(theme.skin, theme)
            assertEquals(appearance, appearanceOfQuery(appearance.query))
        }
}
