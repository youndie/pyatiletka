package io.github.youndie.pyatiletka.app.desktop

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import io.github.youndie.pyatiletka.core.screen.Appearance
import io.github.youndie.pyatiletka.core.screen.Skin
import io.github.youndie.pyatiletka.core.screen.Theme
import io.github.youndie.pyatiletka.skin.plakat.plakatFrameColors
import io.github.youndie.pyatiletka.skin.toon.toonFrameColors

/**
 * The window's frame in the kit's paper and ink. appframe draws its title bar from `MaterialTheme`
 * (`surfaceVariant`, `onSurfaceVariant`), and neither kit uses Material, so without this the frame is
 * Material's default lilac over a poster or a cartoon (B-18).
 */
@Composable
fun FrameTheme(
    appearance: Appearance,
    content: @Composable () -> Unit,
) {
    val (paper, ink) =
        when (appearance.skin) {
            Skin.Plakat -> plakatFrameColors(appearance.theme)
            Skin.Toon -> toonFrameColors(appearance.theme)
        }
    val dark = appearance.theme == Theme.BlackPaper || appearance.theme == Theme.InkGouacheNight
    val base = if (dark) darkColorScheme() else lightColorScheme()
    val scheme =
        base.copy(
            surface = paper,
            onSurface = ink,
            surfaceVariant = paper,
            onSurfaceVariant = ink,
            background = paper,
            onBackground = ink,
        )
    MaterialTheme(colorScheme = scheme, content = content)
}
