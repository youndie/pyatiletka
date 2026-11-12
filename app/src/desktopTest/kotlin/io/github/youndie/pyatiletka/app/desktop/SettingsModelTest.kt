package io.github.youndie.pyatiletka.app.desktop

import io.github.youndie.pyatiletka.app.data.Settings
import io.github.youndie.pyatiletka.app.data.TokenSource
import io.github.youndie.pyatiletka.collector.CollectException
import io.github.youndie.pyatiletka.collector.CollectFailure
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

// docs/features/feature-settings.md — the scenarios and the business rules.
class SettingsModelTest {
    private val saved = mutableListOf<Settings>()
    private val known = setOf("youndie", "kotlin")

    private fun model(tokens: TokenFinder = TokenFinder({ "t" }, { null })) =
        SettingsModel(Settings(owners = listOf("youndie")), tokens, { _, login -> login in known }, { saved += it })

    // Scenario: No token.
    @Test
    fun noTokenAnywhereIsTokenMissing() {
        assertEquals(TokenStatus.Missing, model(TokenFinder({ null }, { null })).current.value.token)
    }

    @Test
    fun aFoundTokenSaysWhere() {
        assertEquals(TokenStatus.Found(TokenSource.GhCli), model().current.value.token)
    }

    // Scenario: No such owner.
    @Test
    fun anUnknownLoginLeavesTheListAlone() =
        runTest {
            val m = model()
            m.addOwner("youndi")
            assertEquals(listOf("youndie"), m.current.value.settings.owners)
            assertEquals("youndi", m.current.value.ownerNotFound)
            assertEquals(emptyList(), saved)
            m.editingOwner()
            assertEquals(null, m.current.value.ownerNotFound)
        }

    @Test
    fun aKnownLoginJoinsAndIsSaved() =
        runTest {
            val m = model()
            m.addOwner(" kotlin ")
            assertEquals(listOf("youndie", "kotlin"), m.current.value.settings.owners)
            assertEquals(listOf("youndie", "kotlin"), saved.last().owners)
        }

    // Scenario: The token is rejected — met while checking a login.
    @Test
    fun aRejectedTokenWhileCheckingALoginIsTokenRejected() =
        runTest {
            val m =
                SettingsModel(
                    Settings(),
                    TokenFinder({
                        "t"
                    }, { null }),
                    { _, _ -> throw CollectException(CollectFailure.TokenRejected, "401") },
                    {},
                )
            m.addOwner("kotlin")
            assertEquals(TokenStatus.Rejected, m.current.value.token)
            assertEquals(listOf("youndie"), m.current.value.settings.owners)
        }

    @Test
    fun onlyTheOfferedPeriods() {
        val m = model()
        m.choosePeriod(60)
        assertEquals(60, m.current.value.settings.periodMinutes)
        assertFailsWith<IllegalArgumentException> { m.choosePeriod(7) }
    }
}
