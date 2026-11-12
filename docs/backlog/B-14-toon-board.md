---
id: B-14
title: "toon: доска, все состояния, оба размера"
status: open
priority: P1
size: L
stage: stage-3-toon
epic: feature-board
blocked_by: [B-09]
---

# B-14 — toon: доска, все состояния, оба размера

Скин toon собирается в коде без холста ([skin-toon](../services/skin-toon.md)). Скелета, пустого состояния, счётчика и плаката в toon-ui нет ([исследование §1.3](../research/research-architecture.md)) — первые находки известны заранее.

- **Решение и причина.** Не ждать холста plakat: блокирует только состояние экранов ([B-09](B-09-app-screen-state-and-navigation.md)).

- AC: эталоны `Board_*_Toon` записаны; на каждый обход есть issue в toon-ui.
- Якоря: `skin-toon/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/toon/`, `skin-toon/src/desktopTest/`.
