---
id: core
title: core — модель, схема снимка, правила доски
type: service
module: core
tech_stack: [Kotlin Multiplatform, desktop (JVM), wasmJs, kotlinx.serialization]
owner: unassigned
depends_on: []
publishes: []
---

# core

Модуль заведён в [B-01](../backlog/B-01-skeleton-five-modules.md); модель, схема снимка, разбор индекса и
правила доски — [B-06](../backlog/B-06-core-model-and-rules.md); типы состояний экранов —
[B-09](../backlog/B-09-app-screen-state-and-navigation.md).

## 1. Ответственность

- Модель домена и схема `snapshot.json` — единственный контракт между частями продукта
  ([endpoint-snapshot](../api/endpoint-snapshot.md)).
- Разбор индекса бэклога docs-bootstrap (`backlog.md`) в счётчики плана и список незакрытых
  пунктов.
- Правила доски: выполнение, сводка портфеля, порядок строк, фильтры, поиск, порядок PR и пунктов
  на карточке ([feature-board](../features/feature-board.md),
  [feature-repo-detail](../features/feature-repo-detail.md)).
- Типы состояний экранов, которые рисуют скины (*принято при раскладке*, см.
  [исследование §3.1, п. 1](../research/research-architecture.md)): скины зависят только от `core`,
  поэтому то, что они рисуют, живёт здесь.

**Не делает:** не ходит в сеть, не знает GitHub API (это [collector](collector.md)), не хранит
ничего на диске, не зависит от Compose и от китов.

## 2. Контракты

- **Снимок:** класс `Snapshot` и его поддеревья, JSON kotlinx.serialization с
  `ignoreUnknownKeys = true`, времена ISO-8601 UTC, целое `schemaVersion` в корне.
- Версия схемы, которую понимает клиент, — `SCHEMA_VERSION`; `readSnapshot` читает
  `schemaVersion` раньше всего остального и отвечает `SnapshotRead.NewerSchema`, а не пустой доской.
  Сломанный текст — `SnapshotRead.Unreadable`.

## 2a. Якоря кода

| Файл | Что там |
|---|---|
| `core/src/commonMain/kotlin/io/github/youndie/pyatiletka/core/Snapshot.kt` | модель: `Snapshot`, `Repo`, `PullRequest`, `Plan`, `BacklogItem` |
| `core/src/commonMain/kotlin/io/github/youndie/pyatiletka/core/SnapshotJson.kt` | `readSnapshot`, `writeSnapshot`, `SCHEMA_VERSION`, времена ISO-8601 UTC |
| `core/src/commonMain/kotlin/io/github/youndie/pyatiletka/core/BacklogIndex.kt` | `parseBacklogIndex` |
| `core/src/commonMain/kotlin/io/github/youndie/pyatiletka/core/Completion.kt` | план, выполнение, «ждут» |
| `core/src/commonMain/kotlin/io/github/youndie/pyatiletka/core/Board.kt` | `summarize`, `boardOrder`, `BoardFilter`, `boardRows` |
| `core/src/commonMain/kotlin/io/github/youndie/pyatiletka/core/RepoCard.kt` | `cardPullRequests`, `cardItems`, `isQuiet` |
| `core/src/commonMain/kotlin/io/github/youndie/pyatiletka/core/screen/` | типы состояний экранов: `BoardUiState`, `RepoUiState` и `repoUiState`, `SettingsUiState`, `Problem`, `TokenSource` |
| `core/src/commonMain/kotlin/io/github/youndie/pyatiletka/core/sample/SampleBoardStates.kt` | состояния доски на примерных данных — фикстуры обоих скинов |
| `core/src/commonMain/kotlin/io/github/youndie/pyatiletka/core/sample/SampleSnapshot.kt` | примерные данные экранов — источник тестов состояний и эталонов |
| `core/src/commonTest/` | тесты на обоих таргетах; `RealIndexes.kt` — настоящие индексы, генерирует `scripts/index-fixtures.py` |
| `core/build.gradle.kts` | таргеты `jvm("desktop")` и `wasmJs` |

## 3. Модель

Классы — `core/src/commonMain/kotlin/io/github/youndie/pyatiletka/core/Snapshot.kt`; поля не
копируются сюда. `Settings` брифа (скин, тема, владельцы, период, источник токена) — не часть
снимка и появится в [app](app.md) ([B-08](../backlog/B-08-app-snapshot-sources.md)).

Тенантности нет. Единственная граница доступа — видимость репозитория: всё, что публикуется,
содержит только `Public`.

## 4. Как устроено

- **Индекс читается по меткам, а не по файлам пунктов.** Блок между `<!-- BEGIN INDEX -->` и
  `<!-- END INDEX -->`, разделы `## Open (N)` / `## Closed (N)`, метки `[ ]` `[~]` `[x]` `[?]`
  `[-]` — словарь `MARK` в `youndie/docs-bootstrap@v0.4.1!/scripts/backlog_index.py`. Так снимок
  обходится одним запросом на репозиторий вместо чтения десятков файлов. Цена — зависимость от
  формата генератора; тесты идут на настоящих индексах четырёх публичных репозиториев — appframe,
  kachok, tacku, plakat-ui (`scripts/index-fixtures.py` пишет их в общий тест на названных коммитах).
  Заголовок пункта — всё, что стоит до трёх последних ячеек строки, так что `|` в заголовке его не рвёт.
- **Порядок строк** — группы брифа (красный main, падающий PR, вопросы, открытые PR, остальное), а
  внутри группы — последний push сверху ([исследование §3.1, п. 2](../research/research-architecture.md)).
- **Примерные данные живут в `core`, а не в тестах.** Их читают тесты состояний экранов в `app` и
  эталоны обоих скинов; у KMP нет общих тестовых источников между модулями.
- **Выполнение округляется вниз.** `done / (done + open + wip + question)`, вниз до целого
  процента: 179 из 180 — 99 %, а не 100 %.

## 5. Зависимости

| Вид | Имя | Зачем |
|---|---|---|
| Библиотека | kotlinx.serialization | схема `snapshot.json` |

## 6. Локальный запуск

```bash
~/.claude/bin/wsl-run ./gradlew check                       # тесты desktop и wasmJs, ktlint
~/.claude/bin/wsl-run ./gradlew :app:wasmJsBrowserDistribution
LOCAL=1 ./gradlew :app:run                                  # окно — на маке, где есть экран
```

wasmJs-тестам на WSL нужен Chrome: `CHROME_BIN` указывает на Chrome for Testing из кэша
playwright. CI делает то же на `ubuntu-latest` (`.github/workflows/check.yaml`, джоба `gradle`).

## 7. Конфигурация

Нет.

## 8. Причуды

- Времена брифа («снимок взят в 09:17») — московские; в `SampleSnapshot` они записаны в UTC
  (`06:17Z`). Показ в часовом поясе зрителя — забота экранов.

- Тесты `core` гоняются на обоих таргетах: разбор индекса на wasmJs и на JVM должен давать одно и
  то же (регулярные выражения и разбор чисел — разные движки).
