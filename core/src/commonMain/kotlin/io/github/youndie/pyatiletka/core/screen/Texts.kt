package io.github.youndie.pyatiletka.core.screen

import io.github.youndie.pyatiletka.core.Author
import io.github.youndie.pyatiletka.core.BoardFilter
import io.github.youndie.pyatiletka.core.ItemStatus
import io.github.youndie.pyatiletka.core.MainStatus
import io.github.youndie.pyatiletka.core.PlanCounts
import io.github.youndie.pyatiletka.core.PrChecks
import io.github.youndie.pyatiletka.core.size
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
import kotlin.time.Instant

/**
 * The board's words, once for both skins (the interface is Russian in the first version; translation is
 * a later item, research §3.2). A skin sets them in its own type — capitals included — and adds none.
 */
object Texts {
    const val TITLE: String = "Пятилетка"
    const val SEARCH: String = "Поиск по названию"
    const val COLUMN_REPO: String = "Репозиторий"
    const val COLUMN_MAIN: String = "main"
    const val COLUMN_PR: String = "PR"
    const val COLUMN_PLAN: String = "План"
    const val COLUMN_WAITING: String = "Ждут"
    const val PLAN_FLAT: String = "план одним файлом"
    const val PLAN_NONE: String = "бэклога нет"
    const val ITEMS_OF: String = "пунктов"
    const val RED_MAIN: String = "красный main"
    const val PRS_OPEN: String = "PR открыто"
    const val WAITING_ME: String = "ждут меня"
    const val REFRESH: String = "Обновить"
    const val MENU: String = "Меню"
    const val SETTINGS: String = "Настройки"
    const val RETRY: String = "Повторить"
    const val RESET_SEARCH: String = "Сбросить поиск"
    const val ATTENTION: String = "Внимание"
    const val ERROR: String = "Ошибка"
    const val NO_SNAPSHOT: String = "Снимка нет"
    const val NOTHING_FOUND: String = "Ничего не найдено"
    const val NO_REPOS: String = "Репозиториев нет"
    const val NO_TOKEN: String = "Нет токена GitHub"
    const val NO_TOKEN_BODY: String = "Без токена Пятилетка не может читать репозитории. Дайте его в настройках."
    const val APPEARANCE: String = "Вид"
    const val SKIN: String = "Скин"
    const val THEME: String = "Тема"
    const val DONE: String = "Готово"

    fun skin(skin: Skin): String =
        when (skin) {
            Skin.Plakat -> "Плакат"
            Skin.Toon -> "Мультфильм"
        }

    fun theme(theme: Theme): String =
        when (theme) {
            Theme.Paper -> "Бумага"
            Theme.BlackPaper -> "Чёрная бумага"
            Theme.InkGouacheLight -> "Тушь и гуашь, день"
            Theme.InkGouacheNight -> "Тушь и гуашь, ночь"
            Theme.BrightCel -> "Яркий целлулоид"
        }

    /** `По запросу «zzz» среди 12 репозиториев ничего нет.` */
    fun nothingFound(
        query: String,
        repos: Int,
    ): String =
        "По запросу «$query» среди $repos ${plural(repos, "репозитория", "репозиториев", "репозиториев")} ничего нет."

    /** `У владельца youndie нет репозиториев. Добавьте владельцев в настройках.` */
    fun noRepos(owners: List<String>): String =
        "У ${if (owners.size == 1) "владельца" else "владельцев"} ${owners.joinToString(", ")} нет репозиториев." +
            " Добавьте владельцев в настройках."

    /** Why there is no snapshot, said as a sentence under «Снимка нет». */
    fun problem(problem: Problem?): String =
        when (problem?.kind) {
            Problem.Kind.NoNetwork, null -> "GitHub не ответил: нет соединения с api.github.com."
            Problem.Kind.NoToken -> NO_TOKEN_BODY
            Problem.Kind.TokenRejected -> "GitHub отверг токен (401)."
            Problem.Kind.OwnerNotFound -> "Нет такого владельца: ${problem.detail}."
            Problem.Kind.NotPublished -> "Снимок ещё не опубликован — загляните через час."
            Problem.Kind.NewerSchema -> "Страница старее снимка — обновите вкладку."
            Problem.Kind.Other -> problem.detail ?: "Что-то пошло не так."
        }

    /** The stale bar: `Снимок от 11 ноября, 21:17 — нет связи с GitHub`. */
    fun stale(
        notice: StaleNotice,
        zone: TimeZone,
    ): String {
        val reason =
            when (notice.problem?.kind) {
                null -> "давно не обновлялся"
                Problem.Kind.NoNetwork -> "нет связи с GitHub"
                Problem.Kind.NoToken -> "нет токена GitHub"
                Problem.Kind.TokenRejected -> "GitHub отверг токен"
                else -> "обновить не удалось"
            }
        return "Снимок от ${momentShort(notice.takenAt, zone)} — $reason"
    }

    /** `11 ноября, 21:17`. */
    fun momentShort(
        instant: Instant,
        zone: TimeZone,
    ): String {
        val t = instant.toLocalDateTime(zone)
        return "${t.day} ${MONTHS[t.month.ordinal]}, ${t.hour.toString().padStart(
            2,
            '0',
        )}:${t.minute.toString().padStart(2, '0')}"
    }

    fun filter(filter: BoardFilter): String =
        when (filter) {
            BoardFilter.All -> "Все"
            BoardFilter.Red -> "Красные"
            BoardFilter.WithPullRequests -> "С PR"
            BoardFilter.WaitingForMe -> "Ждут меня"
        }

    fun main(status: MainStatus): String =
        when (status) {
            MainStatus.Green -> "Зелёный"
            MainStatus.Red -> "Ошибка"
            MainStatus.Running -> "Идёт"
            MainStatus.None -> "Нет проверок"
        }

    /** `youndie · 12 репозиториев` — the owners and the count, with the count's plural. */
    fun owners(
        owners: List<String>,
        repos: Int,
    ): String = "${owners.joinToString(", ")} · $repos ${plural(repos, "репозиторий", "репозитория", "репозиториев")}"

    /** `План выполнен\nна 95 %`. */
    fun planDone(percent: Int): String = "План выполнен\nна $percent %"

    /** `План выполнен на 95 %`, in one line. */
    fun planDoneLine(percent: Int): String = "План выполнен на $percent\u00A0%"

    /** The summary's counters in one line: `698 из 728 · 1 красный main · 12 PR открыто · 7 ждут меня`. */
    fun summaryLine(summary: io.github.youndie.pyatiletka.core.Summary): String =
        "${summary.done} из ${summary.plan} · ${summary.red} $RED_MAIN · ${summary.openPullRequests} $PRS_OPEN · ${summary.waiting} $WAITING_ME"

    /** The counters for a narrow card: `698 из 728 · 1 красный · 12 PR · 7 ждут`. */
    fun summaryLineShort(summary: io.github.youndie.pyatiletka.core.Summary): String =
        "${summary.done} из ${summary.plan} · ${summary.red} красный · ${summary.openPullRequests} PR · ${summary.waiting} ждут"

    /** `09:17`. */
    fun time(
        instant: Instant,
        zone: TimeZone,
    ): String {
        val t = instant.toLocalDateTime(zone)
        return "${t.hour.toString().padStart(2, '0')}:${t.minute.toString().padStart(2, '0')}"
    }

    /** `48 из 60 — 80 %`. */
    fun planCell(
        counts: PlanCounts,
        percent: Int,
    ): String = "${counts.done} из ${counts.size} — $percent %"

    fun failingPullRequests(count: Int): String = "$count ${plural(count, "падает", "падают", "падают")}"

    /** `Снимок 12 ноября 2026, 09:17`, in [zone] — the viewer's, a fixed one in the goldens. */
    fun takenAt(
        instant: Instant,
        zone: TimeZone,
    ): String = "Снимок ${moment(instant, zone)}"

    /** `12 ноября 2026, 09:17`. */
    fun moment(
        instant: Instant,
        zone: TimeZone,
    ): String {
        val t = instant.toLocalDateTime(zone)
        val month = MONTHS[t.month.ordinal]
        return "${t.day} $month ${t.year}, ${t.hour.toString().padStart(
            2,
            '0',
        )}:${t.minute.toString().padStart(2, '0')}"
    }

    // screen-repo

    const val FAILED_CHECKS: String = "Упавшие проверки"
    const val CHECK_FAILED: String = "проверка упала"
    const val OPEN_PULL_REQUESTS: String = "Открытые PR"
    const val UNCLOSED_ITEMS: String = "Незакрытые пункты"
    const val NO_BACKLOG: String = "Бэклога в репозитории нет"
    const val FLAT_PLAN: String = "План одним файлом"
    const val FLAT_PLAN_BODY: String = "доска его не считает"
    const val PLAN_COMPLETE: String = "План выполнен"
    const val CLOSE: String = "Закрыть"
    const val BACK: String = "Назад"

    fun author(author: Author): String =
        when (author) {
            Author.Human -> "человек"
            Author.Bot -> "бот"
        }

    fun checks(checks: PrChecks): String =
        when (checks) {
            PrChecks.Failing -> "падает"
            PrChecks.Ready -> "готов"
            PrChecks.Running -> "идёт"
            PrChecks.NoChecks -> "без проверок"
        }

    fun itemStatus(status: ItemStatus): String =
        when (status) {
            ItemStatus.Question -> "вопрос"
            ItemStatus.Wip -> "в работе"
            ItemStatus.Open -> "открыт"
        }

    /** How old a pull request is at the snapshot: `12 минут`, `5 часов`, `2 дня` — the largest whole unit. */
    fun age(age: Duration): String {
        val days = age.inWholeDays.toInt()
        val hours = age.inWholeHours.toInt()
        val minutes = age.inWholeMinutes.toInt().coerceAtLeast(0)
        return when {
            days > 0 -> "$days ${plural(days, "день", "дня", "дней")}"
            hours > 0 -> "$hours ${plural(hours, "час", "часа", "часов")}"
            else -> "$minutes ${plural(minutes, "минута", "минуты", "минут")}"
        }
    }

    /** The card's default branch: `main зелёный`, with the repository's own branch name. */
    fun branch(
        name: String,
        status: MainStatus,
    ): String = "$name ${main(status).lowercase()}"

    /** `73 из 73`. */
    fun doneOf(counts: PlanCounts): String = "${counts.done} из ${counts.size}"

    // screen-repo on plakat: the canvas's section heads and longer words (B-12)

    const val PLAN: String = "План"
    const val PLAN_DONE_ON: String = "План выполнен на"
    const val NOTHING_TO_DO: String = "PR нет. Незакрытых пунктов нет."

    /** `Проверки main · 2 упали`, with the repository's own branch name. */
    fun failedChecksHeading(
        branch: String,
        count: Int,
    ): String = "Проверки $branch · $count ${plural(count, "упала", "упали", "упали")}"

    /** `PR · 2`. */
    fun pullRequestsHeading(count: Int): String = "PR · $count"

    /** `Пункты · 7 незакрытых`. */
    fun itemsHeading(count: Int): String = "Пункты · $count ${plural(count, "незакрытый", "незакрытых", "незакрытых")}"

    /** `177 из 184 выполнено`. */
    fun doneOfLong(counts: PlanCounts): String = "${doneOf(counts)} выполнено"

    /** A share as the canvas prints it, the sign after a no-break space: `96 %`. */
    fun percent(percent: Int): String = "$percent\u00A0%"

    /** A pull request's checks in a word on its mark. */
    fun checksLong(checks: PrChecks): String =
        when (checks) {
            PrChecks.Failing -> "Проверки упали"
            PrChecks.Ready -> "Готов к вливанию"
            PrChecks.Running -> "Проверки идут"
            PrChecks.NoChecks -> "Без проверок"
        }

    /** An item's status on its plate. */
    fun itemStatusLong(status: ItemStatus): String =
        when (status) {
            ItemStatus.Question -> "Ждёт ответа"
            ItemStatus.Wip -> "В работе"
            ItemStatus.Open -> "Открыт"
        }

    // screen-settings

    const val TOKEN: String = "Токен GitHub"
    const val TOKEN_FOUND: String = "Токен найден"
    const val TOKEN_MISSING: String = "Токена нет"
    const val TOKEN_MISSING_BODY: String = "Дайте его одним из двух способов"
    const val TOKEN_REJECTED: String = "GitHub отверг токен (401)"
    const val TOKEN_REJECTED_BODY: String = "Обновите токен и проверьте снова"
    const val RECHECK: String = "Проверить снова"
    const val OWNERS: String = "Владельцы"
    const val OWNER_FIELD: String = "Логин на GitHub"
    const val ADD: String = "Добавить"
    const val PERIOD: String = "Период обновления"
    const val GH_LOGIN: String = "gh auth login"
    const val TOKEN_VARIABLE: String = "PYATILETKA_GITHUB_TOKEN"

    /** Where the token is looked for, as the choice reads. */
    fun tokenSource(source: TokenSource): String =
        when (source) {
            TokenSource.GhCli -> "Через gh auth token"
            TokenSource.EnvVar -> "Переменная $TOKEN_VARIABLE"
        }

    /** Where a found token came from, for the line under "Токен найден". */
    fun tokenFoundAt(source: TokenSource): String =
        when (source) {
            TokenSource.GhCli -> "через gh"
            TokenSource.EnvVar -> "в переменной $TOKEN_VARIABLE"
        }

    /** What gives the token by the chosen way: the command, or the variable's name. */
    fun tokenHint(source: TokenSource): String =
        when (source) {
            TokenSource.GhCli -> GH_LOGIN
            TokenSource.EnvVar -> TOKEN_VARIABLE
        }

    fun ownerNotFound(login: String): String = "На GitHub нет пользователя или организации $login"

    fun period(minutes: Int): String = "$minutes ${plural(minutes, "минута", "минуты", "минут")}"

    /** Russian plural: one, few (2–4), many. */
    fun plural(
        n: Int,
        one: String,
        few: String,
        many: String,
    ): String {
        val mod100 = n % 100
        val mod10 = n % 10
        return when {
            mod100 in 11..14 -> many
            mod10 == 1 -> one
            mod10 in 2..4 -> few
            else -> many
        }
    }

    private val MONTHS =
        listOf(
            "января",
            "февраля",
            "марта",
            "апреля",
            "мая",
            "июня",
            "июля",
            "августа",
            "сентября",
            "октября",
            "ноября",
            "декабря",
        )
}
