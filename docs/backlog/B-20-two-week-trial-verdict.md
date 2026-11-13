---
id: B-20
title: "Итог обкатки: две недели ежедневного использования"
status: wip
priority: P1
size: S
stage: stage-4-daily-use
epic: feature-skins
blocked_by: [B-15, B-17, B-18]
---

# B-20 — Итог обкатки: две недели ежедневного использования

Ради этого продукт и сделан: вердикт по каждому киту, готов ли он к Maven Central.

- **Решение и причина.** Вердикт пишется по `docs/kit-findings.md` ([B-05](B-05-kit-findings-and-renovate.md)), а не по памяти.
- Гипотезы исследования, решённые за две недели, переписываются в факты или опровержения.

- AC: по каждому киту записано: сколько находок заведено, сколько закрыто, вердикт «готов к Central» или список того, что мешает.
- Якоря: `docs/kit-findings.md`, `docs/research/research-architecture.md`.

## Iteration 1 (13.11.2026)

Обкатка началась 13.11.2026, когда закрылся последний блокер ([B-17](B-17-publish-page-and-hourly-snapshot.md)):
страница обновляется сама раз в час, приложение стоит в `/Applications` ([B-18](B-18-desktop-installed-app.md)),
оба скина переключаются на лету ([B-16](B-16-live-skin-switch-and-url.md)). Две недели — до **27.11.2026**;
вердикт раньше был бы вердиктом по одному дню.

Точка отсчёта, с которой итог сравнит:

| Кит | Находок в `kit-findings.md` | Issue | Открыто | Закрыто | Обходов в доске | Где проверено |
|---|---|---|---|---|---|---|
| plakat-ui | 8 (строки 2–8, 16) | 7 — [#19](https://github.com/youndie/plakat-ui/issues/19)…[#25](https://github.com/youndie/plakat-ui/issues/25), ↗ — комментарием в #21 | 7 | 0 | 6 (`Marks.kt`, колонки в `PlakatBoard.kt`, `MoreGlyph`, `AppearanceDialog`, `halftone`, `StaleBar`/`MissingBanner`, `ExternalGlyph`) | `gh issue list -R youndie/plakat-ui` |
| toon-ui | 8 (строки 1, 9–15) | 8 — [#30](https://github.com/youndie/toon-ui/issues/30)…[#37](https://github.com/youndie/toon-ui/issues/37) | 8 | 0 | 5 (`ToonBoard.kt` — час и короткие счётчики, короткая подстрока, `AppearancePanel`; `Stage.kt`; `ToonRepo.kt` — `Entry`) | `gh issue list -R youndie/toon-ui` |

Киты на точке отсчёта: plakat-core `0.1.0.15`, toon-core `0.1.0.13`; Renovate предлагает toon-core
`0.1.0.14` (PR #25, не влит). Итог должен считаться на тех версиях, что стоят в доске на 27.11.

Что сделает итог: заново прочитает обе таблицы (находки, заведённые за две недели, тоже считаются),
снимет обходы, где кит исправил, по одному пункту на кит, и запишет вердикт. Гипотезы исследования,
которые решит неделя наблюдения расписания (риск 2: час и порог в три часа), переписываются в
факты здесь же.
