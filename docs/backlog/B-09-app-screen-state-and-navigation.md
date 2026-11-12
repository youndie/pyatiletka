---
id: B-09
title: "app: состояние и навигация экранов Board, Repo, Settings"
status: open
priority: P1
size: M
stage: stage-1-data
epic: feature-board
blocked_by: [B-08]
---

# B-09 — app: состояние и навигация экранов Board, Repo, Settings

Скины только рисуют; всё состояние экранов и навигация — в `app` поверх типов из `core`. Пока это не покрыто тестами без UI, каждый скин будет изобретать его заново.

- **Решение и причина.** Каждое состояние §1 экранов [screen-board](../screens/screen-board.md), [screen-repo](../screens/screen-repo.md), [screen-settings](../screens/screen-settings.md) получается из примерных данных в тесте — эти же данные потом станут фикстурами эталонов.

- AC: каждое состояние экранов достижимо в тесте из примерных данных.
- Якоря: `app/src/commonMain/kotlin/io/github/youndie/pyatiletka/app/`, `app/src/commonTest/`.
