package io.github.youndie.pyatiletka.app.desktop

import io.github.youndie.pyatiletka.app.appearanceOf
import io.github.youndie.pyatiletka.app.data.Settings
import io.github.youndie.pyatiletka.collector.CollectException
import io.github.youndie.pyatiletka.collector.CollectFailure
import io.github.youndie.pyatiletka.core.screen.Appearance
import io.github.youndie.pyatiletka.core.screen.SettingsUiState
import io.github.youndie.pyatiletka.core.screen.TokenSource
import io.github.youndie.pyatiletka.core.screen.TokenView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SettingsState(
    val settings: Settings,
    val token: TokenView,
    /** `OwnerNotFound`: the login last tried, while it stays in the field; cleared by editing it. */
    val ownerNotFound: String? = null,
) {
    /** What screen-settings draws. */
    val ui: SettingsUiState
        get() =
            SettingsUiState(
                token,
                settings.tokenSource,
                settings.owners,
                settings.periodMinutes,
                Settings.PERIODS,
                ownerNotFound,
            )
}

/**
 * The settings' logic (feature-settings). [ownerExists] asks GitHub — with the token as found now —
 * whether a login is a user or an organisation; [persist] writes `settings.json`.
 */
class SettingsModel(
    initial: Settings,
    private val tokens: TokenFinder,
    private val ownerExists: suspend (token: String, login: String) -> Boolean,
    private val persist: (Settings) -> Unit,
) {
    private val state = MutableStateFlow(SettingsState(initial, tokenStatus(initial.tokenSource)))
    val current: StateFlow<SettingsState> = state.asStateFlow()

    /** Looks for the token again ("Проверить снова"). */
    fun recheckToken() = state.update { it.copy(token = tokenStatus(it.settings.tokenSource)) }

    /** What a refresh learned: a 401 turns the token block into `TokenRejected`. */
    fun tokenRejected() = state.update { it.copy(token = TokenView.Rejected) }

    fun chooseTokenSource(source: TokenSource) = change { it.copy(tokenSource = source) }.also { recheckToken() }

    fun choosePeriod(minutes: Int) {
        require(minutes in Settings.PERIODS) { "a period of $minutes minutes is not offered" }
        change { it.copy(periodMinutes = minutes) }
    }

    fun removeOwner(login: String) = change { it.copy(owners = it.owners - login) }

    /** The skin and theme chosen on the board, kept by the words the address uses (`toon`, `night`). */
    fun chooseAppearance(appearance: Appearance) =
        change { it.copy(skin = appearance.skin.name.lowercase(), theme = appearance.theme.key) }

    /** The appearance `settings.json` keeps; the poster kit's paper when it names nothing known. */
    val appearance: Appearance
        get() = state.value.settings.let { appearanceOf(it.skin, it.theme) } ?: Appearance()

    /** The owner field was edited: its error goes away. */
    fun editingOwner() = state.update { it.copy(ownerNotFound = null) }

    /** Adds [login] once GitHub knows it; otherwise the list stays as it was and the field shows `OwnerNotFound`. */
    suspend fun addOwner(login: String) {
        val name = login.trim()
        if (name.isEmpty() || name in state.value.settings.owners) return
        val token = tokens.find(state.value.settings.tokenSource)
        if (token == null) {
            state.update { it.copy(token = TokenView.Missing) }
            return
        }
        val exists =
            try {
                ownerExists(token.value, name)
            } catch (e: CollectException) {
                if (e.failure == CollectFailure.TokenRejected) tokenRejected()
                return
            }
        if (exists) {
            change { it.copy(owners = it.owners + name) }
            state.update { it.copy(ownerNotFound = null) }
        } else {
            state.update { it.copy(ownerNotFound = name) }
        }
    }

    private fun tokenStatus(source: TokenSource): TokenView =
        tokens.find(source)?.let { TokenView.Found(it.source) } ?: TokenView.Missing

    private fun change(edit: (Settings) -> Settings) {
        state.update { it.copy(settings = edit(it.settings)) }
        persist(state.value.settings)
    }
}
