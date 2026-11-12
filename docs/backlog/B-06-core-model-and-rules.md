---
id: B-06
title: "core: модель, схема snapshot.json, разбор индекса, выполнение и порядок"
status: open
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
