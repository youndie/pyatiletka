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

## Iteration 1 (12.11.2026)

Сделано: широкая раскладка доски в plakat-ui, все 8 состояний и тёмный `Content` — фикстуры по
артбордам 1280×800 (`skin-plakat/src/desktopTest/…/BoardFixtures.kt`), viddik 0.7.0 подключён с
допусками кита. Семь находок по plakat-ui заведены (#19…#25, `docs/kit-findings.md`).

Паритет широких (`viddikDesignParity`, допуск 5 % / ±16): Error 1,03 %, NoToken 1,21 % — в допуске;
NoMatches 5,51 %, Empty 7,58 %, Stale 7,89 %, Content 7,90 % (тёмный 7,77 %), Loading 22,66 %,
AppearanceSheet 28,85 % — нет.

Разбор `Content` по областям: поле поиска 1,84 %, полоски плана 1,47 %, сегменты 0,76 % — компоненты
кита рисуют их иначе, чем холст (холст выведен из токенов без страницы «Компоненты», исследование
§1.6); сводка 1,77 % — сдвиг текста ([plakat-ui#22](https://github.com/youndie/plakat-ui/issues/22));
остальное — растеризация. Loading — скелет кита (раппорт) против бетонных плит холста; Empty —
`PlakatEmptyState` (раскат) против диска; AppearanceSheet — сглаживание точек растра по всему кадру.

Осталось: узкие артборды (8 `_Phone`), голдены, `owners` в `boardUiState` приложения, документы
экрана. Вопрос к владельцу (холст против компонентов кита) — после узких, с полными числами.
