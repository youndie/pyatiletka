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

Модуль заведён в [B-01](../backlog/B-01-skeleton-five-modules.md) и пока почти пуст: `Collector.USER_AGENT`.
Запрос, публичный режим и CLI — *целевые* ([B-07](../backlog/B-07-collector-query-and-cli.md)).

## 1. Ответственность

Запрос к GitHub → `Snapshot` из [core](core.md). Две формы поставки:

- **библиотека** для десктопа: [app](app.md) зовёт её по таймеру и по кнопке;
- **CLI** для scheduled workflow: `collect --public-only` пишет `snapshot.json`, который
  публикуется в `gh-pages` ([endpoint-snapshot](../api/endpoint-snapshot.md)).

**Не делает:** не пишет в GitHub (доска только читает), не хранит токен, не держит кэш —
кэш снимка у [app](app.md).

## 2. Контракты

- Выход — `Snapshot` из `core`; другого формата у сборщика нет.
- Вход — GitHub GraphQL. Это внешняя зависимость, не наш API; запрос и его цена описываются здесь.

## 2a. Якоря кода

| Файл | Что там |
|---|---|
| `collector/src/main/kotlin/io/github/youndie/pyatiletka/collector/` | `Collector`; запрос, разбор ответа, публичный режим, CLI — *целевые* |
| `collector/build.gradle.kts` | Kotlin/JVM, конвенции `sborka.jvm` |
| `collector/src/test/` | тесты на поддельном GitHub (*целевой*) |
| `measurements/token/` | замер права `GITHUB_TOKEN`: `probe.sh` печатает только числа |
| `measurements/graphql/` | замер запроса: запросы, `run.sh`, `runs.tsv`, ответы без текстов бэклога |

## 3. Как устроено

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

- Workflow по расписанию, раз в час, запускает CLI и кладёт `snapshot.json` в `gh-pages`
  ([B-17](../backlog/B-17-publish-page-and-hourly-snapshot.md)). Общая группа `concurrency` со
  сборкой страницы — *гипотеза* против гонки двух workflow.

## 6. Локальный запуск

Как у всего проекта — [core, §6](core.md). Свой запуск CLI появится в [B-07](../backlog/B-07-collector-query-and-cli.md).

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
