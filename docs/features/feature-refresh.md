---
id: feature-refresh
title: Свежесть данных
type: feature
status: active
owner: unassigned
involved_services:
  - core
  - collector
  - app
client_entries:
  - screen-board
api:
  - endpoint-snapshot
tags: []
---

# Свежесть данных

## 1. Обзор

Доска всегда показывает последний удачный снимок и честно говорит, насколько он стар. Десктоп
собирает снимок сам ([collector](../services/collector.md) в процессе), веб читает готовый
[snapshot.json](../api/endpoint-snapshot.md), который раз в час кладёт workflow. Состояния —
на [screen-board](../screens/screen-board.md): `Loading`, `Stale`, `Error`.

## 2. Бизнес-правила

* Десктоп собирает снимок при запуске, затем каждые 5, 15 или 60 минут (по умолчанию 15) и по
  кнопке; удачный снимок пишется на диск.
* Веб читает `snapshot.json` рядом со страницей при открытии и по кнопке.
* Снимок считается устаревшим, если он старше двух периодов (десктоп) или трёх часов (веб), либо
  если последняя попытка обновления не удалась.
* Неудача при наличии старого снимка даёт состояние `Stale` с причиной; без снимка — `Error`.
* Публичный режим сборщика отбрасывает любой репозиторий с видимостью не `Public` и падает, если
  такой попал в результат.

## 4. Якоря кода

Данные и правила — в коде ([B-08](../backlog/B-08-app-snapshot-sources.md)); экраны — *целевые*.

| Модуль | Код |
|---|---|
| core | `core/src/commonMain/kotlin/io/github/youndie/pyatiletka/core/` — `Snapshot`, `schemaVersion` |
| collector | `collector/src/main/kotlin/io/github/youndie/pyatiletka/collector/Collector.kt` — страницы, публичный режим, отказ страницы — неудача снимка |
| app | `app/src/commonMain/kotlin/io/github/youndie/pyatiletka/app/data/SnapshotStore.kt` — `SnapshotStore`, `Data`, `staleness` |
| app | `app/src/desktopMain/kotlin/io/github/youndie/pyatiletka/app/desktop/GitHubSnapshotSource.kt` — сбор на десктопе |
| app | `app/src/desktopMain/kotlin/io/github/youndie/pyatiletka/app/desktop/Files.kt` — кэш снимка |
| app | `app/src/commonMain/kotlin/io/github/youndie/pyatiletka/app/data/PageSnapshotSource.kt` — ответ `snapshot.json` → данные |
| app | `app/src/wasmJsMain/kotlin/io/github/youndie/pyatiletka/app/BrowserSnapshot.kt` — `window.fetch` |

## 5. Сценарии

Сценарии проверены на уровне данных (строки `Automated`); на экране — *целевые*, до экранов.

### Scenario: The network is gone
* **Given:** десктоп со снимком от 11 ноября, 21:17.
* **When:** обновление не удалось.
* **Then:** доска показывает прежние строки и полосу «снимок от 11 ноября, 21:17 — нет связи с
  GitHub» (`Stale`).
* **Automated:** `SnapshotStoreTest.theNetworkIsGoneAndYesterdaysSnapshotStays`, `DesktopSourcesTest.anUnreachableGitHubIsNoNetwork` — данные; полоса `Stale` на экране — B-09, B-11

### Scenario: First start without network
* **Given:** снимка на диске нет.
* **When:** сбор не удался.
* **Then:** доска показывает `Error` с кнопкой повтора.
* **Automated:** `SnapshotStoreTest.aFirstStartWithoutNetworkIsAnError` — данные; экран `Error` — B-09, B-11

### Scenario: The snapshot is not published yet
* **Given:** веб-страница.
* **When:** запрос `snapshot.json` вернул `404`.
* **Then:** страница показывает `Error`, а не пустую доску.
* **Automated:** `PageSnapshotSourceTest.aMissingSnapshotIsNotPublished` — данные; экран `Error` — B-09, B-11

### Scenario: Unknown schema version
* **Given:** `snapshot.json` с `schemaVersion`, которого клиент не знает.
* **Then:** `Error` с текстом «страница старее снимка — обновите вкладку».
* **Automated:** `SnapshotJsonTest.aNewerSchemaIsNamedNotGuessed`, `PageSnapshotSourceTest.aNewerSchemaSaysThePageIsOlder` — данные; текст ошибки на экране — B-09, B-11

### Scenario: A private repository in public mode
* **Given:** `collect --public-only`, в ответе GitHub подмешан репозиторий с видимостью `Private`.
* **Then:** сбор завершается ошибкой, файл снимка не пишется (приёмка
  [B-07](../backlog/B-07-collector-query-and-cli.md)).
* **Automated:** `CollectorTest.aPrivateRepositoryInPublicModeFailsTheCollection`, `CollectorTest.theCliRefusesAPrivateLeakAndWritesNothing`

## 6. Вне объёма

* Уведомления, трей, автозапуск.
* Чтение истории снимков из `gh-pages`.

## 7. Причуды

* Снимок собирается около 10 с: три запроса GraphQL по 25 репозиториев
  ([B-02](../backlog/B-02-measure-full-graphql-query.md), `measurements/graphql/README.md`). Пока он
  идёт, на экране остаётся прежний снимок; отказ любой страницы — неудача обновления целиком, а не
  доска без части строк.

* Порог на вебе (три часа) больше периода публикации (час), потому что расписание GitHub может
  запаздывать; верный ли порог — *гипотеза* ([исследование, риск 2](../research/research-architecture.md)).
* Устаревание на вебе считается от `takenAt` в теле снимка, а не от времени ответа: Pages отдаёт
  файл из кэша.
