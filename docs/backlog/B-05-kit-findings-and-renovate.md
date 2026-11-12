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

## Iteration 1 (12.11.2026)

- `renovate.json`: пресеты sborka и `automerge-harness`, как у plakat-ui и oldge-ui; плюс своё правило —
  plakat-core и toon-core предлагаются в любое время, а не по понедельникам (бриф §8: каждый main
  кита — в доске в тот же день).
- Положительный контроль: toon-core намеренно на сборку назад, `0.1.0.12`. Если Renovate находит
  снапшоты китов в reposilite, он сам откроет PR на `0.1.0.13` вскоре после вливания этого
  изменения; этот PR вливается в рамках пункта, и пункт закрывается вторым PR с доказательством.
- `docs/kit-findings.md` с первой находкой ([toon-ui#30](https://github.com/youndie/toon-ui/issues/30))
  заведён ещё в B-01.
