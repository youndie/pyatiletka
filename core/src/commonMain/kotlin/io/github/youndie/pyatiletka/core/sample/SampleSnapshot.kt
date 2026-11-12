package io.github.youndie.pyatiletka.core.sample

import io.github.youndie.pyatiletka.core.Author
import io.github.youndie.pyatiletka.core.BacklogItem
import io.github.youndie.pyatiletka.core.FailedCheck
import io.github.youndie.pyatiletka.core.ItemStatus
import io.github.youndie.pyatiletka.core.MainStatus
import io.github.youndie.pyatiletka.core.Plan
import io.github.youndie.pyatiletka.core.PlanCounts
import io.github.youndie.pyatiletka.core.PlanForm
import io.github.youndie.pyatiletka.core.PrChecks
import io.github.youndie.pyatiletka.core.PullRequest
import io.github.youndie.pyatiletka.core.Repo
import io.github.youndie.pyatiletka.core.SCHEMA_VERSION
import io.github.youndie.pyatiletka.core.Snapshot
import io.github.youndie.pyatiletka.core.Visibility
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

/**
 * The board's sample data — the values every artboard, golden and screen-state test is drawn from
 * (docs/screens/screen-board.md, section 6). Twelve public repositories only. The counters are the
 * ones the brief fixed on 12.11.2026; the titles of the items no artboard shows are those
 * repositories' own open items of that day.
 *
 * The brief's times are Moscow's: the snapshot was taken at 09:17, the stale one at 21:17 the day
 * before. [pushedAt] falls in the order of the table inside every group, so the board's own order
 * reproduces it.
 */
object SampleSnapshot {
    /** 12 November 2026, 09:17 in Moscow. */
    val TAKEN_AT: Instant = Instant.parse("2026-11-12T06:17:00Z")

    /** The stale bar's snapshot: 11 November 2026, 21:17 in Moscow. */
    val STALE_TAKEN_AT: Instant = Instant.parse("2026-11-11T18:17:00Z")

    val repos: List<Repo> by lazy { buildRepos() }

    val snapshot: Snapshot by lazy { Snapshot(SCHEMA_VERSION, TAKEN_AT, listOf("youndie"), repos) }

    /** The same rows, taken the evening before: what `Board_Stale` shows. */
    val stale: Snapshot by lazy { snapshot.copy(takenAt = STALE_TAKEN_AT) }

    fun repo(name: String): Repo = repos.first { it.fullName == "youndie/$name" }
}

private fun buildRepos(): List<Repo> {
    var pushed = SampleSnapshot.TAKEN_AT - 7.minutes

    fun nextPush(): Instant = pushed.also { pushed -= 47.minutes }
    return listOf(
        repo(
            "tacku",
            nextPush(),
            MainStatus.Red,
            failed = listOf(check("tacku", "server / test"), check("tacku", "client / tck")),
            prs =
                listOf(
                    pr("tacku", 77, "chore(deps): update kompot to v0.40.1", Author.Bot, PrChecks.Running, 1.hours),
                ),
            plan =
                items(
                    "tacku",
                    done = 48,
                    item("B-61", ItemStatus.Question, "P3", "Читать бэклог docs-as-code как доску"),
                    item("B-58", ItemStatus.Wip, "P1", "Прогнать корпус проверок для клиента"),
                    item(
                        "B-52",
                        ItemStatus.Open,
                        "P2",
                        "Пригласить человека: сегодня участник заводится только из шелла",
                    ),
                    item("B-53", ItemStatus.Wip, "P2", "Витрина: бэклог внешнего docs-репозитория виден как доска"),
                    item("B-55", ItemStatus.Wip, "P2", "Провалиться в задачу витрины: карточка открывает её текст"),
                    item("B-57", ItemStatus.Wip, "P2", "Агент видит витрину: два инструмента поверх того же источника"),
                    item(
                        "B-36",
                        ItemStatus.Open,
                        "P2",
                        "Замерить, где на самом деле меняют статус: на доске или в задаче",
                    ),
                    item("B-51", ItemStatus.Open, "P2", "Вернуть в сверку два экрана, выпавших из съёмки"),
                    item(
                        "B-54",
                        ItemStatus.Open,
                        "P2",
                        "Корпус тел экранов протухает молча: сверять его с сервером в гейте",
                    ),
                    item("B-56", ItemStatus.Open, "P2", "Проба не в гейте, и она уже протухла незаметно"),
                    item("B-38", ItemStatus.Open, "P3", "Померить порог визита в догоняющей ленте"),
                    item("B-41", ItemStatus.Open, "P3", "Шлюз подтверждения: опасное действие агента ждёт человека"),
                ),
        ),
        repo(
            "kachok",
            nextPush(),
            MainStatus.Green,
            prs =
                listOf(
                    pr(
                        "kachok",
                        154,
                        "fix(engine): hand a stalled block to an idle peer",
                        Author.Human,
                        PrChecks.Failing,
                        2.days,
                    ),
                    pr(
                        "kachok",
                        152,
                        "chore(deps): update dependency gradle to v9.8.3",
                        Author.Bot,
                        PrChecks.Ready,
                        5.hours,
                    ),
                ),
            plan =
                items(
                    "kachok",
                    done = 177,
                    item("B-190", ItemStatus.Question, "P3", "v2 and hybrid torrents (BEP 52): SHA-256 piece layers"),
                    item(
                        "B-184",
                        ItemStatus.Wip,
                        "P1",
                        "An idle peer takes over a block a slow peer has sat on, before the endgame",
                    ),
                    item("B-176", ItemStatus.Wip, "P2", "The UI moves to commonMain"),
                    item(
                        "B-187",
                        ItemStatus.Open,
                        "P2",
                        "The README says what an unsigned installer looks like on first start, platform by platform",
                    ),
                    item(
                        "B-178",
                        ItemStatus.Open,
                        "P3",
                        "The browser build of the UI is a client of the headless engine",
                    ),
                    item("B-179", ItemStatus.Open, "P3", "Phase 3: Android and iOS targets on the engine"),
                    item(
                        "B-180",
                        ItemStatus.Open,
                        "P3",
                        "Phase 3: a headless server with a web face, installable on a box that is always on",
                    ),
                ),
        ),
        repo(
            "appframe",
            nextPush(),
            MainStatus.Green,
            plan =
                items(
                    "appframe",
                    done = 2,
                    item(
                        "B-07",
                        ItemStatus.Question,
                        "P1",
                        "Owner: one sitting at Windows 11 — snapping, two monitors and the edge scrollbar on an AppFrame window, checked by hand",
                    ),
                    item(
                        "B-03",
                        ItemStatus.Question,
                        "P2",
                        "Linux, GNOME on Wayland: check that an AppFrame window moves and resizes, on the JetBrains Runtime and on Temurin",
                    ),
                    item(
                        "B-04",
                        ItemStatus.Question,
                        "P2",
                        "Tell README readers about the resize strip and resizeBorder, and check by hand that a thinner border frees an edge scrollbar",
                    ),
                    item(
                        "B-02",
                        ItemStatus.Open,
                        "P1",
                        "Windows 11: find out whether Snap Layouts and Aero Snap work on an AppFrame window, and act on the answer",
                    ),
                    item(
                        "B-05",
                        ItemStatus.Open,
                        "P2",
                        "Windows with two monitors: check the README's multi-monitor claim, or narrow it",
                    ),
                ),
        ),
        repo(
            "katcher",
            nextPush(),
            MainStatus.Green,
            plan =
                items(
                    "katcher",
                    done = 3,
                    item(
                        "B-03",
                        ItemStatus.Question,
                        "P1",
                        "A CI check sees a crash from a linked Kotlin/Native consumer arrive on the server, and the production silence is explained",
                    ),
                    item(
                        "B-02",
                        ItemStatus.Question,
                        "P2",
                        "One failure is one group: request data in the message no longer opens a group per variant",
                    ),
                ),
        ),
        repo(
            "sborka",
            nextPush(),
            MainStatus.Green,
            prs =
                listOf(
                    pr("sborka", 241, "chore(deps): update ci actions", Author.Bot, PrChecks.Ready, 6.hours),
                    pr(
                        "sborka",
                        240,
                        "chore(deps): update dependency gradle to v9.8.3",
                        Author.Bot,
                        PrChecks.Ready,
                        7.hours,
                    ),
                    pr("sborka", 239, "chore(deps): update kotlinx", Author.Bot, PrChecks.Ready, 1.days),
                    pr(
                        "sborka",
                        238,
                        "chore(deps): update dependency com.squareup.okio:okio to v3.18.3",
                        Author.Bot,
                        PrChecks.Ready,
                        2.days,
                    ),
                    pr("sborka", 237, "chore(deps): update junit to v6.1.4", Author.Bot, PrChecks.Ready, 3.days),
                    pr("sborka", 236, "chore(deps): update ktor to v3.6.1", Author.Bot, PrChecks.Ready, 4.days),
                ),
            plan =
                items(
                    "sborka",
                    done = 34,
                    item(
                        "B-24",
                        ItemStatus.Open,
                        "P2",
                        "File the two hardcoded sources upstream, with a reproduction that reaches main",
                    ),
                ),
        ),
        repo(
            "bochka",
            nextPush(),
            MainStatus.Running,
            prs =
                listOf(
                    pr(
                        "bochka",
                        190,
                        "chore(deps): update dependency org.duckdb:duckdb_jdbc to v1.5.1",
                        Author.Bot,
                        PrChecks.Running,
                        2.hours,
                    ),
                ),
            plan = Plan.FLAT,
        ),
        repo(
            "toon-ui",
            nextPush(),
            MainStatus.Green,
            prs = listOf(pr("toon-ui", 31, "chore(deps): update ci actions", Author.Bot, PrChecks.Ready, 9.hours)),
            plan = items("toon-ui", done = 66),
        ),
        repo(
            "proba",
            nextPush(),
            MainStatus.Green,
            prs = listOf(pr("proba", 49, "chore(deps): update npm to v12", Author.Bot, PrChecks.Ready, 3.days)),
            plan = Plan.NONE,
        ),
        repo(
            "haul",
            nextPush(),
            MainStatus.Green,
            plan =
                items(
                    "haul",
                    done = 179,
                    item("B-156", ItemStatus.Open, "P3", "chore: walk the selection and its toolbar on a phone"),
                ),
        ),
        repo(
            "zavarnik",
            nextPush(),
            MainStatus.Green,
            plan =
                items(
                    "zavarnik",
                    done = 33,
                    item(
                        "B-09",
                        ItemStatus.Open,
                        "P3",
                        "Переносимость кэша между CPU: AOTAdapterCaching включается сам, и кэш несёт машинный код",
                    ),
                    item(
                        "B-13",
                        ItemStatus.Open,
                        "P3",
                        "Выпуск v0.1: Plugin Portal / Central, README с измерением, объявление — и четыре недели наблюдения",
                    ),
                ),
        ),
        repo("kompot", nextPush(), MainStatus.Green, plan = items("kompot", done = 83)),
        repo("plakat-ui", nextPush(), MainStatus.Green, plan = items("plakat-ui", done = 73, dropped = 1)),
    )
}

private fun repo(
    name: String,
    pushedAt: Instant,
    main: MainStatus,
    failed: List<FailedCheck> = emptyList(),
    prs: List<PullRequest> = emptyList(),
    plan: Plan,
): Repo =
    Repo(
        "youndie/$name",
        Visibility.Public,
        "https://github.com/youndie/$name",
        "main",
        pushedAt,
        main,
        failed,
        prs,
        plan,
    )

private fun check(
    repo: String,
    name: String,
): FailedCheck = FailedCheck(name, "https://github.com/youndie/$repo/actions")

private fun pr(
    repo: String,
    number: Int,
    title: String,
    author: Author,
    checks: PrChecks,
    age: Duration,
): PullRequest =
    PullRequest(
        number,
        title,
        author,
        false,
        SampleSnapshot.TAKEN_AT - age,
        checks,
        "https://github.com/youndie/$repo/pull/$number",
    )

private class SampleItem(
    val id: String,
    val status: ItemStatus,
    val priority: String,
    val title: String,
)

private fun item(
    id: String,
    status: ItemStatus,
    priority: String,
    title: String,
) = SampleItem(id, status, priority, title)

private fun items(
    repo: String,
    done: Int,
    vararg unclosed: SampleItem,
    dropped: Int = 0,
): Plan =
    Plan(
        PlanForm.Items,
        PlanCounts(
            done = done,
            open = unclosed.count { it.status == ItemStatus.Open },
            wip = unclosed.count { it.status == ItemStatus.Wip },
            question = unclosed.count { it.status == ItemStatus.Question },
            dropped = dropped,
        ),
        unclosed.map {
            BacklogItem(
                it.id,
                it.title,
                it.status,
                it.priority,
                "https://github.com/youndie/$repo/blob/main/backlog.md",
            )
        },
    )
