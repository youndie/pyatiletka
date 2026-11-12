---
id: B-06
title: "core: модель, схема snapshot.json, разбор индекса, выполнение и порядок"
status: done
priority: P1
size: M
stage: stage-1-data
epic: feature-board
blocked_by: [B-01]
---

# B-06 — core: модель, схема snapshot.json, разбор индекса, выполнение и порядок

Все правила доски и карточки ([feature-board](../features/feature-board.md), [feature-repo-detail](../features/feature-repo-detail.md)) — чистые функции над снимком; их место — [core](../services/core.md), без UI и без сети.

- **Решение и причина.** Индекс бэклога разбирается по меткам `MARK` docs-bootstrap ([исследование §1.4](../research/research-architecture.md)); тесты — на настоящих индексах трёх публичных репозиториев, потому что формат индекса — чужой.
- Сюда же — типы состояний экранов (если B-01 подтвердило это решение).

- AC: тесты на индексах трёх публичных репозиториев и на правилах §4 брифа зелёные на desktop и wasmJs.
- Якоря: `core/src/commonMain/kotlin/io/github/youndie/pyatiletka/core/`, `core/src/commonTest/`.

## Итог (12.11.2026)

- Модель снимка, `readSnapshot` / `writeSnapshot` со схемой версии 1, `parseBacklogIndex`, выполнение,
  сводка, порядок строк, фильтры, поиск и порядок на карточке — в `core`
  ([docs/services/core.md](../services/core.md), §2a).
- Тесты — 27 на каждом таргете, `desktopTest` и `wasmJsBrowserTest` зелёные на WSL: настоящие индексы
  appframe, kachok, tacku и plakat-ui на коммитах из `RealIndexes.kt`, правила §4 на примерных
  данных. Три мутанта (округление вверх, порядок push, закрытая метка в таблице открытых) убиты.
- Примерные данные брифа — `SampleSnapshot` в `core`: счётчики брифа, заголовки невидимых на
  артбордах пунктов — настоящие открытые пункты тех репозиториев на 12.11.
- Не сделано здесь и не входило: типы состояний экранов — [B-09](B-09-app-screen-state-and-navigation.md).
