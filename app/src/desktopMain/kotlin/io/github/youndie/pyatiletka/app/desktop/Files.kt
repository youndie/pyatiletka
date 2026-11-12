package io.github.youndie.pyatiletka.app.desktop

import io.github.youndie.pyatiletka.app.data.Settings
import io.github.youndie.pyatiletka.app.data.SnapshotCache
import io.github.youndie.pyatiletka.core.Snapshot
import io.github.youndie.pyatiletka.core.SnapshotRead
import io.github.youndie.pyatiletka.core.readSnapshot
import io.github.youndie.pyatiletka.core.writeSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.File

/** The OS's own places: settings where the OS keeps settings, the snapshot where it keeps caches. */
object AppDirs {
    private val home = File(System.getProperty("user.home"))
    private val os = System.getProperty("os.name").lowercase()

    val config: File
        get() =
            when {
                "mac" in os -> home.resolve("Library/Application Support/pyatiletka")
                "win" in os -> File(System.getenv("APPDATA") ?: home.path).resolve("pyatiletka")
                else -> File(System.getenv("XDG_CONFIG_HOME") ?: home.resolve(".config").path).resolve("pyatiletka")
            }

    val cache: File
        get() =
            when {
                "mac" in os -> home.resolve("Library/Caches/pyatiletka")
                "win" in os -> File(System.getenv("LOCALAPPDATA") ?: home.path).resolve("pyatiletka/cache")
                else -> File(System.getenv("XDG_CACHE_HOME") ?: home.resolve(".cache").path).resolve("pyatiletka")
            }
}

/** The last good snapshot as `snapshot.json` in the cache directory; a file the board cannot read is treated as none. */
class FileSnapshotCache(
    private val file: File = AppDirs.cache.resolve("snapshot.json"),
) : SnapshotCache {
    override suspend fun load(): Snapshot? =
        withContext(Dispatchers.IO) {
            if (!file.isFile) return@withContext null
            (readSnapshot(file.readText()) as? SnapshotRead.Ok)?.snapshot
        }

    override suspend fun save(snapshot: Snapshot) =
        withContext(Dispatchers.IO) {
            file.parentFile.mkdirs()
            val part = File(file.path + ".part")
            part.writeText(writeSnapshot(snapshot))
            if (!part.renameTo(file)) {
                file.delete()
                part.renameTo(file)
            }
            Unit
        }
}

/** `settings.json`; a file that does not parse gives the defaults rather than a crash at start. */
class SettingsFile(
    private val file: File = AppDirs.config.resolve("settings.json"),
) {
    fun load(): Settings =
        try {
            if (file.isFile) json.decodeFromString(Settings.serializer(), file.readText()) else Settings()
        } catch (
            @Suppress("SwallowedException") e: SerializationException,
        ) {
            Settings()
        } catch (
            @Suppress("SwallowedException") e: IllegalArgumentException,
        ) {
            Settings()
        }

    fun save(settings: Settings) {
        file.parentFile.mkdirs()
        file.writeText(json.encodeToString(Settings.serializer(), settings))
    }

    private val json =
        Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            prettyPrint = true
        }
}
