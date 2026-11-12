package io.github.youndie.pyatiletka.core.screen

import io.github.youndie.pyatiletka.core.BoardFilter
import io.github.youndie.pyatiletka.core.MainStatus
import io.github.youndie.pyatiletka.core.PlanCounts
import io.github.youndie.pyatiletka.core.size
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
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
