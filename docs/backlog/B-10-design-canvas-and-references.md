---
id: B-10
title: "Дизайн: холст по брифу, эталонные PNG в skin-plakat"
status: done
priority: P1
size: M
stage: stage-2-plakat
epic: feature-board
---

# B-10 — Дизайн: холст по брифу, эталонные PNG в skin-plakat

Экраны plakat принимаются по холсту ([исследование D6](../research/research-architecture.md)). Холста ещё нет; бриф дизайнеру — 31 артборд в plakat-ui.

- **Решение и причина.** Артборды отдаются файлами (`.dc.html`, `canvas.json`, картинки), PNG кладутся в `skin-plakat/src/desktopTest/snapshots/design/` под именами артбордов — блок `design` экранов уже ссылается на них.
- Заметки «нет в ките» на холсте становятся пунктами бэклога plakat-ui.

- AC: по одному PNG на каждый из 31 артборда, размеры как в документах экранов.
- Якоря: `skin-plakat/src/desktopTest/snapshots/design/`.

## Итог (12.11.2026)

- Экспорт холста получен архивом: 31 артборд `.dc.html`, три страницы, `canvas.json` с заметками,
  PNG @2x. Исходники — `design/canvas/`; PNG экспорта в репозиторий не взяты.
- Эталоны получены уменьшением экспорта вдвое (`scripts/design-references.py`), решение владельца
  вместо рендера в 1x — [исследование D9](../research/research-architecture.md).
- 31 PNG в `skin-plakat/src/desktopTest/snapshots/design/`: 18 по 1280×800 и 13 по 390×844 — каждый
  размера своей рамки в `canvas.json`.
- Восемь заметок «Нет в ките» и заметки «Не из токенов» — в
  [исследовании §1.6](../research/research-architecture.md); issue по ним — в
  [B-05](B-05-kit-findings-and-renovate.md).
