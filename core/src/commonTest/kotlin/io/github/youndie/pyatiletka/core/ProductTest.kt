package io.github.youndie.pyatiletka.core

import kotlin.test.Test
import kotlin.test.assertEquals

class ProductTest {
    // Runs on both targets: the title is Cyrillic, and a source file read in the wrong encoding on
    // one of them would show here before it shows on a screen.
    @Test
    fun theTitleIsNineCyrillicCapitals() {
        assertEquals(9, Product.TITLE.length)
        assertEquals(true, Product.TITLE.all { it in 'А'..'Я' })
    }
}
