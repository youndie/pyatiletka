package io.github.youndie.pyatiletka.app.desktop

import io.github.youndie.pyatiletka.app.data.Settings
import io.github.youndie.pyatiletka.collector.CollectException
import io.github.youndie.pyatiletka.collector.CollectFailure
import io.github.youndie.pyatiletka.core.screen.TokenSource
import io.github.youndie.pyatiletka.core.screen.TokenView
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
        assertEquals(TokenView.Missing, model(TokenFinder({ null }, { null })).current.value.token)
    }

    @Test
    fun aFoundTokenSaysWhere() {
        assertEquals(TokenView.Found(TokenSource.GhCli), model().current.value.token)
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
            assertEquals(TokenView.Rejected, m.current.value.token)
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

// B-09's acceptance for screen-settings: its four states, from the settings of the artboards
// (token through gh, owner youndie, 15 minutes).
class SettingsStatesTest {
    private fun model(tokens: TokenFinder) =
        SettingsModel(
            io.github.youndie.pyatiletka.app.data
                .Settings(),
            tokens,
            { _, login -> login == "youndie" },
            {},
        )

    @Test
    fun theFourStates() =
        kotlinx.coroutines.test.runTest {
            val found = model(TokenFinder({ "t" }, { null }))
            assertEquals(
                io.github.youndie.pyatiletka.core.screen.SettingsScreenState.Content,
                found.current.value.ui.screen,
            )
            assertEquals(listOf("youndie"), found.current.value.ui.owners)
            assertEquals(15, found.current.value.ui.periodMinutes)
            // The skins' goldens draw the sample, so the sample is what the model gives by default.
            assertEquals(
                io.github.youndie.pyatiletka.core.sample.SampleSettingsStates.content,
                found.current.value.ui,
            )

            val missing = model(TokenFinder({ null }, { null }))
            assertEquals(
                io.github.youndie.pyatiletka.core.screen.SettingsScreenState.TokenMissing,
                missing.current.value.ui.screen,
            )

            found.tokenRejected()
            assertEquals(
                io.github.youndie.pyatiletka.core.screen.SettingsScreenState.TokenRejected,
                found.current.value.ui.screen,
            )

            val typing = model(TokenFinder({ "t" }, { null }))
            typing.addOwner("youndi")
            assertEquals(
                io.github.youndie.pyatiletka.core.screen.SettingsScreenState.OwnerNotFound,
                typing.current.value.ui.screen,
            )
            assertEquals("youndi", typing.current.value.ui.ownerNotFound)
        }
}
