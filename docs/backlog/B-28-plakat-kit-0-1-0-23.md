---
id: B-28
title: "plakat-core 0.1.0.23: обходы plakat-ui#19–#25 заменены компонентами кита"
status: wip
priority: P2
size: M
stage: stage-4-daily-use
epic: feature-board
blocked_by: []
---

# B-28 — plakat-core 0.1.0.23: обходы plakat-ui#19–#25 заменены компонентами кита

Кит закрыл семь находок доски (`docs/kit-findings.md`, строки 2–8 и 16) пунктами B-75…B-81 и выпустил их в
`0.1.0.23` (youndie/plakat-ui#27–#33). Правило D4: обход живёт, только пока в ките нет нужного, — значит, обходы
уходят, а строки находок закрываются. Renovate держит тот же бамп в #45; этот пункт его заменяет.

- **Решение.** Версия `plakat = "0.1.0.23"`, и каждый обход — на компонент кита:
  | Обход | Чем заменён |
  |---|---|
  | `Marks.kt`: `MainMark`, `Hatch`, `Check`, `Plate` «ИДЁТ» (#19) | `PlakatStatus` с `PlakatStatusKind` |
  | `PlakatBoard.kt`: `TableHeaderRow`, `TableRow` (#20) | `PlakatTable` |
  | `Marks.kt`: `MoreGlyph`; `PlakatRepo.kt`: `ExternalGlyph` (#21) | `PlakatIcons.More`, `PlakatIcons.OpensElsewhere` |
  | display-строки без тени: проценты сводки, «ПЛАН ВЫПОЛНЕН» (#22) | `PlakatParagraph` |
  | `BoardBlocks.kt`: `AppearanceDialog` и `halftone` (#23, #24) | `PlakatDialog` со слотом `content` над `PlakatScrim` кита |
  | `BoardBlocks.kt`: `StaleBar`; `PhoneBoard.kt`: `PhoneStaleBar`; `PlakatSettings.kt`: `MissingBanner` (#25) | `PlakatBanner`, слова видов — в `RussianStrings` |
- **Отвергнуто.** Оставить обходы, где кит рисует иначе, чем холст доски (рамка 480 у «ВИД», строка 36 у таблицы):
  обход без открытой находки запрещён D4, а расхождение с холстом — это паритет, который записывается цифрой.
- **Не входит.** Заголовки с «приводкой» (тень со сдвигом) и с автоподбором размера: `PlakatParagraph` не принимает ни
  тени, ни `autoSize`, они остаются на `BasicText` — новая находка в кит, если цифры паритета её подтвердят.

- AC: `skin-plakat` не содержит `// kit:` со ссылками на plakat-ui#19–#25; строки 2–8 и 16 `docs/kit-findings.md`
  закрыты с номером версии; голдены `skin-plakat` перезаписаны и просмотрены, паритет с холстом перемерен и записан
  здесь с причиной каждого сдвига; оба гейта зелёные; Renovate #45 закрыт со ссылкой на этот PR.
- Anchors: `skin-plakat/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/plakat/Marks.kt`, `skin-plakat/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/plakat/BoardBlocks.kt`, `skin-plakat/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/plakat/PlakatBoard.kt`, `gradle/libs.versions.toml`
