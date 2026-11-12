---
id: B-10
title: "Дизайн: холст по брифу, эталонные PNG в skin-plakat"
status: open
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
