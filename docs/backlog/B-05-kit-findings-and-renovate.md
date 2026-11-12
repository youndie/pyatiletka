---
id: B-05
title: "Учёт находок по китам и Renovate на снапшоты китов"
status: wip
priority: P1
size: S
stage: stage-0-risks
epic: feature-skins
blocked_by: [B-01]
---

# B-05 — Учёт находок по китам и Renovate на снапшоты китов

Итог обкатки ([B-20](B-20-two-week-trial-verdict.md)) пишется по числам, только если каждая находка заведена и учтена ([исследование D4](../research/research-architecture.md)). И доска должна получать каждый main кита в тот же день; поднимает ли Renovate снапшоты с номером прогона — гипотеза (риск 7).

- **Решение и причина.** `docs/kit-findings.md` — одна таблица: находка, кит, issue, место обхода (`// kit: <ссылка>`), статус.
- Issue в `youndie/plakat-ui` и `youndie/toon-ui` заводятся без отдельного вопроса на каждую — *после подтверждения владельцем* (открытый вопрос 3).

- AC: PR Renovate на новую версию кита приходит сам; первая находка заведена issue и видна в `docs/kit-findings.md`.
- Якоря: `renovate.json`, `docs/kit-findings.md`.
