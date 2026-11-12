package io.github.youndie.pyatiletka.core.screen

/** The two kits the board is drawn in (feature-skins). */
enum class Skin { Plakat, Toon }

/** A skin's theme, by the name the address and the settings keep (`?skin=toon&theme=night`). */
enum class Theme(
    val skin: Skin,
    val key: String,
) {
    Paper(Skin.Plakat, "paper"),
    BlackPaper(Skin.Plakat, "black"),
    InkGouacheLight(Skin.Toon, "light"),
    InkGouacheNight(Skin.Toon, "night"),
    BrightCel(Skin.Toon, "bright"),
    ;

    companion object {
        fun of(skin: Skin): List<Theme> = entries.filter { it.skin == skin }
    }
}

/** What the appearance sheet chooses, and what the board is drawn in. */
data class Appearance(
    val skin: Skin = Skin.Plakat,
    val theme: Theme = Theme.Paper,
)
