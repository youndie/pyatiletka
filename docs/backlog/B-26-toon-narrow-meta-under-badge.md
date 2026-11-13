---
id: B-26
title: "toon, узкий: на живых числах строка сводки уходит под бейдж времени"
status: done
priority: P2
size: S
stage: stage-4-daily-use
epic: feature-board
blocked_by: []
---

# B-26 — toon, узкий: на живых числах строка сводки уходит под бейдж времени

QA-сессия 13.11.2026, опубликованная страница, окно 375×812, обе темы Ink & Gouache. Строка счётчиков
сводки «1174 из 1225 · 0 красный · 27 PR · 11 ждут» длиннее места: «11 ждут» уходит под бейдж
«12:07». Причина у кита — `ToonCard` с бейджем фиксированной ширины и строкой `meta` без многоточия
([toon-ui#32](https://github.com/youndie/toon-ui/issues/32), находка 10 в
[kit-findings](../kit-findings.md)). Обход доски — короткие счётчики на узком
(`Texts.summaryLineShort`). На фикстуре (698 из 728, 390 dp) он помещается, на живом портфеле с
четырёхзначными числами и на 375 — нет, поэтому голдены этого не видят.

- **Решение и причина.** Обход должен помещаться на 360 dp при четырёхзначных числах: например, на
  узком в строке только `done из plan`, остальное — в фильтрах, у которых свои счётчики не нужны;
  или вторая строка. Что именно — в этом пункте; правило D4 (обход только с issue) соблюдено, issue
  есть.
- Не входит: широкая раскладка, plakat.

- AC: голден toon на 360 dp с четырёхзначными счётчиками (как `Board_Content_Width800` у plakat),
  где строка сводки не заходит под бейдж.
- Якоря: `skin-toon/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/toon/ToonBoard.kt`,
  `core/src/commonMain/kotlin/io/github/youndie/pyatiletka/core/screen/Texts.kt` —
  `summaryLineShort`.

## Итог (13.11.2026)

- На узком в строке сводки только три счётчика: «1 красный · 29 PR · 14 ждут». Доля плана — заголовок
  карточки, а `done из plan` было той частью, которой не хватало места рядом с бейджем.
  `Texts.summaryLineShort` другим не пользуется: у plakat на узком свои ячейки счётчиков.
- Голден `Board_Content_Width360_Toon` с четырёхзначными счётчиками: строка заканчивается до бейджа.
  Перезаписаны и просмотрены `Board_Content_Phone_Toon`, `Board_Stale_Phone_Toon`,
  `Board_NoMatches_Phone_Toon`, `Board_AppearanceSheet_Phone_Toon`. Остальные голдены toon не изменились.
- Мутация: прежняя короткая строка при новых голденах — `viddikVerify` падает на всех пяти. Тест
  `TextsTest.theSummaryCountersAgreeWithTheirNumbers` проверяет новую строку.
- Находка по ходу: на 360 dp подпись «Ждут меня» в `ToonSegmented` шире своего сегмента (82 dp) и
  заходит на рамку — кит не измеряет подписи ([toon-ui#42](https://github.com/youndie/toon-ui/issues/42),
  строка 17 в [kit-findings](../kit-findings.md)). Обхода в доске нет; голден `Board_Content_Width360_Toon`
  держит это как есть.
- `./gradlew check` прогнан на WSL с `CHROME_BIN`.
