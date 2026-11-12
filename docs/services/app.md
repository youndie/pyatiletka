---
id: app
title: app — оболочка desktop и web
type: service
module: app
tech_stack: [Compose Multiplatform, desktop (JVM), wasmJs, appframe]
owner: unassigned
depends_on:
  - core
  - collector
  - skin-plakat
  - skin-toon
publishes:
  - desktop application
  - wasm page on gh-pages
---

# app

Модуль заведён в [B-01](../backlog/B-01-skeleton-five-modules.md): окно appframe и страница рисуют
оба скина рядом (`BothSkins`). Источники снимка, кэш, таймер, токен и настройки —
[B-08](../backlog/B-08-app-snapshot-sources.md); состояние экранов и навигация —
[B-09](../backlog/B-09-app-screen-state-and-navigation.md). Страница уже читает `snapshot.json` и пишет
смену экрана доски в консоль; окно десктопа — ещё нет, его подключение придёт с экранами скина.

## 1. Ответственность

- **Источники снимка.** Десктоп — [collector](collector.md) в процессе, по таймеру (5, 15 или 60
  минут, по умолчанию 15) и по кнопке; удачный снимок пишется в кэш на диск. Веб —
  `snapshot.json` рядом со страницей, при открытии и по кнопке.
- **Состояние экранов и навигация.** Из снимка, флагов загрузки и ошибок получается состояние
  Board, Repo и Settings; типы этих состояний — в [core](core.md) (*принято при раскладке*,
  [исследование §3.1, п. 1](../research/research-architecture.md)).
- **Настройки** (только десктоп): владельцы, период, источник токена, скин и тема.
- **Выбор скина** и точки входа desktop и wasm: какой из [skin-plakat](skin-plakat.md) и
  [skin-toon](skin-toon.md) рисует экран.

**Не делает:** не рисует экраны сам — это скины; не хранит токен.

## 2. Контракты

- Веб читает [endpoint-snapshot](../api/endpoint-snapshot.md).
- Десктоп API не вызывает — сбор идёт в процессе через [collector](collector.md).

## 2a. Якоря кода

| Файл | Что там |
|---|---|
| `app/src/commonMain/kotlin/io/github/youndie/pyatiletka/app/` | `BothSkins`; состояние экранов, навигация, выбор скина — *целевые* |
| `app/src/desktopMain/kotlin/io/github/youndie/pyatiletka/app/` | `Main.kt` — окно `AppFrame` 1280×800; таймер, кэш, токен — *целевые* |
| `app/src/wasmJsMain/kotlin/io/github/youndie/pyatiletka/app/` | `Main.kt` — `ComposeViewport`; чтение `snapshot.json`, разбор адреса — *целевые* |
| `app/src/commonMain/kotlin/io/github/youndie/pyatiletka/app/BoardModel.kt` | `BoardModel`: данные + `BoardFlags` → `BoardUiState`, карточка; `boardUiState` — чистая функция |
| `app/src/commonMain/kotlin/io/github/youndie/pyatiletka/app/Navigation.kt` | `Route` (`Board`, `Settings`), `Navigator`; карточка — не маршрут, а `BoardFlags.selected` |
| `app/src/commonMain/kotlin/io/github/youndie/pyatiletka/app/data/SnapshotStore.kt` | `SnapshotStore`: кэш → источник → таймер; `Data` (`Loading`, `Ready` с `Staleness`, `Failed`) |
| `app/src/commonMain/kotlin/io/github/youndie/pyatiletka/app/data/SnapshotSource.kt` | `SnapshotSource`, `RefreshFailure`, `SnapshotCache` |
| `app/src/commonMain/kotlin/io/github/youndie/pyatiletka/app/data/PageSnapshotSource.kt` | ответ `snapshot.json` → данные (веб) |
| `app/src/commonMain/kotlin/io/github/youndie/pyatiletka/app/data/Settings.kt` | `Settings`, `TokenSource` — в `settings.json`, без секретов |
| `app/src/desktopMain/kotlin/io/github/youndie/pyatiletka/app/desktop/` | `GitHubSnapshotSource`, `TokenFinder`, `FileSnapshotCache`, `SettingsFile`, `SettingsModel`, `DesktopGraph`, `DesktopSettings` (экран настроек в окне, по `Navigator`) |
| `app/src/wasmJsMain/kotlin/io/github/youndie/pyatiletka/app/BrowserSnapshot.kt` | `window.fetch` для `snapshot.json` |
| `app/src/wasmJsMain/resources/index.html` | страница, фон — бумага plakat, чтобы не мигал шов до первого кадра |
| `app/src/desktopTest/kotlin/io/github/youndie/pyatiletka/app/` | `BothSkinsTest` — оба кита рисуют название |

## 3. Как устроено

- **Один источник на платформу, одно хранилище на оба.** `SnapshotStore` читает кэш, потом
  источник; на десктопе — снова каждые 5/15/60 минут. Попытка одна за раз: нажатие во время
  попытки к ней присоединяется. Удачный снимок пишется в кэш (`FileSnapshotCache` в каталоге
  кэшей ОС, запись через `.part` и переименование).
- **Устаревание.** Снимок устарел, если он старше двух периодов (десктоп) или трёх часов (веб), или
  если последняя попытка не удалась; `Staleness` говорит, что из двух.
- **Токен ищется заново на каждую попытку** (`TokenFinder`): сначала источник из настроек, потом
  второй. Нет ни одного — `RefreshFailure.NoToken`, запроса нет вовсе.
- **Веб обходит кэш браузера** случайным параметром в адресе `snapshot.json`; возраст снимка —
  по `takenAt`, а не по ответу.

- **Последний удачный снимок показывается всегда.** Неудача при наличии снимка — `Stale` с
  причиной, без снимка — `Error` ([feature-refresh](../features/feature-refresh.md)).
- **Смена скина не теряет места.** Выбранный репозиторий, фильтр и поиск — состояние `app`, а не
  скина; скин только рисует ([feature-skins](../features/feature-skins.md)).

## 4. Зависимости

| Вид | Имя | Зачем |
|---|---|---|
| Модуль | [core](core.md) | модель, правила, типы состояний |
| Модуль | [collector](collector.md) | сбор на десктопе (только `desktopMain`) |
| Модуль | [skin-plakat](skin-plakat.md), [skin-toon](skin-toon.md) | экраны |
| Библиотека | appframe `0.3.0` | рамка окна десктопа |

## 5. Инфраструктура и выкладка

- **Страница:** сборка wasm кладётся в ветку `gh-pages` workflow на push в `main`; адрес
  `youndie.github.io/pyatiletka/` ([B-17](../backlog/B-17-publish-page-and-hourly-snapshot.md)).
- **Десктоп:** в первой версии собирается для машины владельца
  ([B-18](../backlog/B-18-desktop-installed-app.md)).

## 6. Локальный запуск

Как у всего проекта — [core, §6](core.md).

## 7. Конфигурация

| Ключ | Описание | Обязателен |
|---|---|---|
| `settings.json` в каталоге настроек ОС (`~/Library/Application Support/pyatiletka` на маке, `$XDG_CONFIG_HOME/pyatiletka` на Linux, `%APPDATA%\pyatiletka` на Windows) | скин, тема, владельцы, период, источник токена (десктоп); сломанный файл — значения по умолчанию | нет — по умолчанию |
| `snapshot.json` в каталоге кэшей ОС (`~/Library/Caches/pyatiletka` на маке, `$XDG_CACHE_HOME/pyatiletka`) | последний удачный снимок (десктоп); нечитаемый — как будто его нет | нет |
| localStorage | скин и тема (веб) | нет |
| `?skin=…&theme=…` | скин и тема из адреса (веб) | нет |
| `PYATILETKA_GITHUB_TOKEN` | токен, если не через `gh auth token` (десктоп) | один из двух источников |

## 8. Причуды

- appframe тематизирует рамку через `MaterialTheme` (Material 3), а киты Material не используют.
  Пока рамка в цветах Material по умолчанию; привести её к бумаге скина — забота
  [B-18](../backlog/B-18-desktop-installed-app.md), где окно становится приложением.

- Десктоп видит всё, что видит токен владельца, в том числе приватные репозитории. Это нормально
  для десктопа и запрещено для всего, что публикуется: снимок с десктопа нельзя приносить в
  фикстуры и эталоны ([исследование, риск 1](../research/research-architecture.md)).
