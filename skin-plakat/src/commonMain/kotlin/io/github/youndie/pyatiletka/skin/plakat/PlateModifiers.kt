package io.github.youndie.pyatiletka.skin.plakat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** A plate: 20 dp high as the kit's badge (canvas note "Бейдж 20 по высоте"). */
internal fun Modifier.plateBackground(fill: Color): Modifier = height(20.dp).background(fill)

internal fun Modifier.platePadding(): Modifier = padding(horizontal = 6.dp)
