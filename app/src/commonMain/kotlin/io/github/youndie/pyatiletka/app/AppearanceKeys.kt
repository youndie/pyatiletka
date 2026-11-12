package io.github.youndie.pyatiletka.app

import io.github.youndie.pyatiletka.core.screen.Appearance
import io.github.youndie.pyatiletka.core.screen.Skin
import io.github.youndie.pyatiletka.core.screen.Theme

/**
 * The appearance by the words the address (`?skin=toon&theme=night`) and `settings.json` keep: a skin
 * by its name (`plakat`, `toon`), a theme by its [Theme.key] (`paper`, `black`, `light`, `night`,
 * `bright`), both ignoring case. A theme alone picks its skin; a skin alone takes its first theme; a
 * theme of the other skin is dropped for the skin's first. Null when neither names anything.
 */
fun appearanceOf(
    skin: String?,
    theme: String?,
): Appearance? {
    val s = skin?.let { name -> Skin.entries.find { it.name.equals(name.trim(), ignoreCase = true) } }
    val t =
        theme?.let { key ->
            Theme.entries.find { it.key.equals(key.trim(), ignoreCase = true) && (s == null || it.skin == s) }
        }
    return when {
        t != null -> Appearance(t.skin, t)
        s != null -> Appearance(s, Theme.of(s).first())
        else -> null
    }
}

/** [appearanceOf] for a query string, with or without its `?`; other parameters are ignored. */
fun appearanceOfQuery(query: String): Appearance? {
    val params =
        query
            .removePrefix("?")
            .split('&')
            .mapNotNull { pair ->
                val i = pair.indexOf('=')
                if (i <= 0) null else pair.substring(0, i) to pair.substring(i + 1)
            }.toMap()
    return appearanceOf(params["skin"], params["theme"])
}

/** The words [appearanceOf] reads back: `skin=toon&theme=night`. */
val Appearance.query: String get() = "skin=${skin.name.lowercase()}&theme=${theme.key}"
