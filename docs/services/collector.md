---
id: collector
title: collector — сбор снимка из GitHub
type: service
module: collector
tech_stack: [Kotlin/JVM, Ktor client, GitHub GraphQL]
owner: unassigned
depends_on:
  - core
  - GitHub GraphQL API
publishes:
  - snapshot.json
---

# collector

Запрос, публичный режим и CLI — [B-07](../backlog/B-07-collector-query-and-cli.md). Вызов из
десктопа — *целевой*, [B-08](../backlog/B-08-app-snapshot-sources.md); из workflow —
[B-17](../backlog/B-17-publish-page-and-hourly-snapshot.md), workflow `snapshot`.

## 1. Ответственность

Запрос к GitHub → `Snapshot` из [core](core.md). Две формы поставки:

- **библиотека** для десктопа: [app](app.md) зовёт её по таймеру и по кнопке;
- **CLI** для scheduled workflow: `collector --owner <login> --public-only --out snapshot.json`
  пишет файл, который публикуется в `gh-pages` ([endpoint-snapshot](../api/endpoint-snapshot.md)).
  Перед записью CLI читает снимок обратно `readSnapshot` из `core`: файл, который доска не прочтёт,
  не остаётся лежать как удачный.

**Не делает:** не пишет в GitHub (доска только читает), не хранит токен, не держит кэш —
кэш снимка у [app](app.md).

## 2. Контракты

- Выход — `Snapshot` из `core`; другого формата у сборщика нет.
- Вход — GitHub GraphQL. Это внешняя зависимость, не наш API; запрос и его цена описываются здесь.

## 2a. Якоря кода

| Файл | Что там |
|---|---|
| `collector/src/main/kotlin/io/github/youndie/pyatiletka/collector/Collector.kt` | `Collector.collect`: страницы, публичный режим, `CollectFailure` |
| `collector/src/main/kotlin/io/github/youndie/pyatiletka/collector/Mapping.kt` | ответ GitHub → `Repo`: статусы, упавшие проверки, PR, форма плана |
| `collector/src/main/kotlin/io/github/youndie/pyatiletka/collector/Main.kt` | CLI: аргументы, токен из окружения, проверка чтением |
| `collector/src/main/resources/io/github/youndie/pyatiletka/collector/portfolio.graphql` | запрос, которым ходит сборщик |
| `collector/build.gradle.kts` | Kotlin/JVM, Ktor CIO, `application` (`installDist` → `bin/collector`) |
| `collector/src/test/kotlin/io/github/youndie/pyatiletka/collector/` | `CollectorTest` на поддельном GitHub (`FakeGitHub`, Ktor `MockEngine`) |
| `measurements/token/` | замер права `GITHUB_TOKEN`: `probe.sh` печатает только числа |
| `measurements/graphql/` | замер запроса: запросы, `run.sh`, `runs.tsv`, ответы без текстов бэклога |

## 3. Как устроено

- **Что считается чем.** Сводка проверок `SUCCESS` — `Green`, `FAILURE`/`ERROR` — `Red`, любая другая —
  `Running`, нет сводки — `None`; так же для последнего коммита PR (`Ready`, `Failing`, `Running`,
  `NoChecks`). Упавшие проверки — `CheckRun` с выводом `FAILURE`, `TIMED_OUT`, `CANCELLED`,
  `ACTION_REQUIRED`, `STARTUP_FAILURE` и `StatusContext` в `FAILURE`/`ERROR`; пишутся только при `Red`.
  Автор PR — `Bot`, если GitHub называет его `Bot`. План: `backlog.md` с маркерами индекса — `Items`;
  `backlog.md` без маркеров или `BACKLOG.md` — `Flat`; ни того ни другого — `None`.
- **Архивные и форки не входят в снимок** ([исследование D10](../research/research-architecture.md)).

- **GraphQL страницами по 25 репозиториев.** Запрос — `measurements/graphql/portfolio.graphql`
  (замер [B-02](../backlog/B-02-measure-full-graphql-query.md)): три запроса подряд, около 10 с и
  39 очков лимита на снимок из 54 репозиториев. Страница в 100 — один запрос, но около 10 с — это
  предел GitHub, и 2 прогона из 5 получили `502` ([исследование D2](../research/research-architecture.md)).
  Отказ страницы — неудача всего снимка, а не снимок без части репозиториев.
- **Публичный режим падает, а не только фильтрует.** `--public-only` отбрасывает любой репозиторий с
  видимостью не `Public` и завершается ошибкой, если такой попал в результат: тихое отбрасывание
  скрыло бы, что токен видит лишнее ([исследование D5](../research/research-architecture.md)).

## 4. Зависимости

| Вид | Имя | Зачем |
|---|---|---|
| Модуль | [core](core.md) | модель и схема снимка |
| Внешнее | GitHub GraphQL API | CI, PR, индекс бэклога |
| Библиотека | Ktor client | HTTP |

## 5. Инфраструктура и выкладка

- Workflow `snapshot` (`.github/workflows/snapshot.yaml`) раз в час запускает CLI с
  `--public-only` под `GITHUB_TOKEN` репозитория и кладёт `snapshot.json` в `gh-pages`
  ([B-17](../backlog/B-17-publish-page-and-hourly-snapshot.md)). Запускает его будильник на WSL-машине
  (`scripts/snapshot-alarm.sh`: cron в :07 вызывает `gh workflow run`), потому что расписание GitHub
  на этом аккаунте не срабатывает; строка `schedule` в :23 оставлена запасной. Ветка — один коммит без истории:
  каждый прогон берёт её дерево и заменяет только своё. С workflow страницы у него общая группа
  `concurrency: gh-pages`; запущенные одним push оба отработали по очереди, и ни один файл не
  потерялся (прогоны 43313517100 и 43313517076).

## 6. Локальный запуск

Сборка и тесты — как у всего проекта, [core, §6](core.md). CLI против живого GitHub (на маке,
токен `gh` в окружении, в лог он не попадает):

```bash
PYATILETKA_GITHUB_TOKEN=$(gh auth token) LOCAL=1 ./gradlew :collector:run --args="--owner youndie --public-only --out /tmp/snapshot.json"
```

Коды выхода: `0` — файл записан и прочитан обратно; `1` — сбор не удался (`CollectFailure` в логе),
файл не записан; `2` — неверные аргументы или нет токена.

## 7. Конфигурация

| Ключ | Описание | Обязателен |
|---|---|---|
| `PYATILETKA_GITHUB_TOKEN` | токен GitHub для чтения | один из двух |
| `GITHUB_TOKEN` | токен workflow; принято — только для CLI ([исследование §3.1, п. 5](../research/research-architecture.md)) | один из двух |

Токен не пишется ни на диск, ни в логи.

## 8. Причуды

- Публичность снимка держится на праве токена workflow, а не только на фильтре: `GITHUB_TOKEN`
  репозитория видит у владельца только публичные репозитории и читает их целиком
  ([B-03](../backlog/B-03-measure-workflow-token-scope.md), `measurements/token/README.md`). На
  десктопе токен владельца видит и приватные — там фильтр `--public-only` не включается, и
  снимок десктопа никуда не публикуется.
