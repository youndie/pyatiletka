---
id: B-11
title: "plakat: доска, все состояния, оба размера"
status: wip
priority: P1
size: L
stage: stage-2-plakat
epic: feature-board
blocked_by: [B-09, B-10]
---

# B-11 — plakat: доска, все состояния, оба размера

Доска в plakat-ui — первый экран, принятый по холсту ([screen-board](../screens/screen-board.md)).

- **Решение и причина.** Паритет — `viddikDesignParity` с допусками кита; число выше допуска приходит с причиной, а не с ослабленным порогом.
- Обход кита — только с issue и `// kit: <ссылка>`.

- AC: `viddikDesignParity` в допуске для каждого `Board_*`; эталоны записаны.
- Якоря: `skin-plakat/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/plakat/`, `skin-plakat/src/desktopTest/`.
