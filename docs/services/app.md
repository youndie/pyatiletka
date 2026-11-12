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

Черновик: модуля ещё нет, пути — *целевые*
([B-01](../backlog/B-01-skeleton-five-modules.md), [B-08](../backlog/B-08-app-snapshot-sources.md),
[B-09](../backlog/B-09-app-screen-state-and-navigation.md)).

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
| `app/src/commonMain/kotlin/io/github/youndie/pyatiletka/app/` | состояние экранов, навигация, выбор скина (*целевой*) |
| `app/src/desktopMain/kotlin/io/github/youndie/pyatiletka/app/` | точка входа, окно appframe, таймер, кэш, токен (*целевой*) |
| `app/src/wasmJsMain/kotlin/io/github/youndie/pyatiletka/app/` | точка входа страницы, чтение `snapshot.json`, разбор адреса (*целевой*) |

## 3. Как устроено

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

Появится в [B-01](../backlog/B-01-skeleton-five-modules.md).

## 7. Конфигурация

| Ключ | Описание | Обязателен |
|---|---|---|
| `settings.json` в каталоге настроек ОС | скин, тема, владельцы, период, источник токена (десктоп) | нет — по умолчанию |
| кэш снимка в каталоге кэша ОС | последний удачный снимок (десктоп) | нет |
| localStorage | скин и тема (веб) | нет |
| `?skin=…&theme=…` | скин и тема из адреса (веб) | нет |
| `PYATILETKA_GITHUB_TOKEN` | токен, если не через `gh auth token` (десктоп) | один из двух источников |

## 8. Причуды

- Десктоп видит всё, что видит токен владельца, в том числе приватные репозитории. Это нормально
  для десктопа и запрещено для всего, что публикуется: снимок с десктопа нельзя приносить в
  фикстуры и эталоны ([исследование, риск 1](../research/research-architecture.md)).
