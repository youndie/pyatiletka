---
id: endpoint-snapshot
title: Снимок портфеля
type: api_endpoints
status: active
services:
  - collector
  - app
contract_source:
  - pyatiletka:core Snapshot
parent_feature: feature-refresh
---

# API: снимок портфеля

Своего сервера нет. Единственный контракт между частями продукта — файл снимка: его пишет
[collector](../services/collector.md) в scheduled workflow, кладёт в ветку `gh-pages`, читает
веб-сборка [app](../services/app.md). Десктоп этот файл не читает — он собирает снимок сам.

Схема и её разбор ([B-06](../backlog/B-06-core-model-and-rules.md)) и CLI, который пишет файл
([B-07](../backlog/B-07-collector-query-and-cli.md)), есть; workflow, который его публикует, — тоже
([B-17](../backlog/B-17-publish-page-and-hourly-snapshot.md)): файл лежит в ветке `gh-pages`, а включить
GitHub Pages для неё — шаг владельца (пункт в `question`).

## Маршруты — все, без исключений

| Метод и путь | Сервис | Уровень | Мин. роль | В публичной схеме? | Назначение |
|---|---|---|---|---|---|
| `GET /snapshot.json` (относительно страницы) | статический файл на GitHub Pages | аноним | — | да | весь портфель на один момент, только `Public` |

Других маршрутов нет: GitHub GraphQL — внешняя зависимость [collector](../services/collector.md), не
наш API.

## Обработчики (якоря кода)

| Маршрут | Где |
|---|---|
| запись `snapshot.json` | `collector/src/main/kotlin/io/github/youndie/pyatiletka/collector/Main.kt` — CLI `collector --public-only` |
| чтение `snapshot.json` | `app/src/wasmJsMain/kotlin/io/github/youndie/pyatiletka/app/BrowserSnapshot.kt` |
| схема | `core/src/commonMain/kotlin/io/github/youndie/pyatiletka/core/Snapshot.kt`, `core/src/commonMain/kotlin/io/github/youndie/pyatiletka/core/SnapshotJson.kt` — `readSnapshot`, `SCHEMA_VERSION` |
| публикация | `.github/workflows/snapshot.yaml` — раз в час, в :17; `.github/workflows/page.yaml` — страница на push в `main`; оба через `.github/scripts/publish-to-pages.sh` |

## Тела запроса и ответа

Ответ — `Snapshot` из [core](../services/core.md). Поля не копируются сюда; соглашения:
JSON kotlinx.serialization, `ignoreUnknownKeys = true`, времена ISO-8601 UTC, целое поле
`schemaVersion` в корне.

## Ошибки

| Условие | Статус | Что видит клиент |
|---|---|---|
| снимок ещё не опубликован | `404` | `Error`, а не пустая доска |
| тело не разбирается | `200` | `Error` |
| `schemaVersion` новее, чем знает клиент | `200` | `Error` «страница старее снимка — обновите вкладку» |

## Причуды

- **Путь относителен странице.** Бриф пишет `/snapshot.json`, а страница живёт на
  `youndie.github.io/pyatiletka/`, так что фактический адрес — `/pyatiletka/snapshot.json`.
  Клиент запрашивает файл относительно адреса страницы, не от корня хоста.
- **Свежесть по `takenAt`, а не по ответу.** GitHub Pages отдаёт файл с кэшированием; устаревание
  на вебе считается от `takenAt` в теле (порог — три часа), а не от заголовков.
