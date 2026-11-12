package io.github.youndie.pyatiletka.app.desktop

import io.github.youndie.pyatiletka.app.data.SnapshotStore
import io.github.youndie.pyatiletka.app.data.desktopStaleAfter
import io.github.youndie.pyatiletka.collector.Collector
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.CoroutineScope
import kotlin.time.Duration.Companion.minutes

/**
 * The desktop's data, wired once per window: settings from `settings.json`, the snapshot from the
 * collector every [io.github.youndie.pyatiletka.app.data.Settings.periodMinutes], the last good one
 * from the cache. The screens read [store] and [settings] (B-09).
 */
class DesktopGraph(
    scope: CoroutineScope,
    settingsFile: SettingsFile = SettingsFile(),
    tokens: TokenFinder = TokenFinder(),
    client: HttpClient = HttpClient(CIO) { expectSuccess = false },
) {
    val settings: SettingsModel =
        SettingsModel(
            initial = settingsFile.load(),
            tokens = tokens,
            ownerExists = { token, login -> Collector(client, token).ownerExists(login) },
            persist = settingsFile::save,
        )

    private val period = settings.current.value.settings.periodMinutes.minutes

    val store: SnapshotStore =
        SnapshotStore(
            source = GitHubSnapshotSource(client, { settings.current.value.settings }, tokens),
            cache = FileSnapshotCache(),
            scope = scope,
            staleAfter = desktopStaleAfter(period),
            period = period,
        )
}
