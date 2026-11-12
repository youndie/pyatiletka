---
id: B-17
title: "Страница: сборка в gh-pages, снимок раз в час"
status: wip
priority: P1
size: M
stage: stage-4-daily-use
epic: feature-refresh
blocked_by: [B-03, B-07, B-11]
---

# B-17 — Страница: сборка в gh-pages, снимок раз в час

Два workflow пишут в одну ветку `gh-pages`: сборка wasm на push в `main` и снимок раз в час ([endpoint-snapshot](../api/endpoint-snapshot.md)).

- **Решение и причина.** Общая группа `concurrency` против гонки двух workflow — гипотеза ([исследование, риск 6](../research/research-architecture.md)), проверяется здесь.
- После выкладки — неделя наблюдения за фактическими интервалами расписания (риск 2).

- AC: страница открывается по публичному адресу; `snapshot.json` обновился сам дважды подряд.
- Якоря: `.github/workflows/`.
