---
id: B-28
title: "plakat-core 0.1.0.23: обходы plakat-ui#19–#25 заменены компонентами кита"
status: done
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
  | `Marks.kt`: `MainMark`, `Hatch`, `Check`, `Plate` «ИДЁТ» (#19) | `PlakatStatus`: зелёный и «готов» — `Success`, красный и «упали» — `Danger`, «идёт» — `Info`, «ждёт ответа» — `Warning`, «в работе» — `Info` |
  | `Marks.kt`: `MoreGlyph`; `PlakatRepo.kt`: `ExternalGlyph` (#21) | `PlakatIconButton(PlakatIcons.More)`, `PlakatIcon(PlakatIcons.OpensElsewhere)` |
  | display-строки без тени: проценты сводки, «ПЛАН ВЫПОЛНЕН» (#22) | `PlakatParagraph` |
  | `BoardBlocks.kt`: `halftone` под листом «ВИД» (#24) | `PlakatScrim` |
  | `BoardBlocks.kt`: `StaleBar`; `PhoneBoard.kt`: `PhoneStaleBar`; `PlakatSettings.kt`: `MissingBanner` (#25) | `PlakatBanner(Warning)`, слово «Внимание» — `warning` в `RussianStrings` |
- **Отвергнуто.** Рисовать «под холст» там, где компонент кита есть и подходит (метки, ↗, ⋮, полосы, растр): обход без
  открытой находки запрещён D4, а расхождение с холстом — паритет, записанный цифрой. Где компонент кита не подходит
  (таблица, лист «ВИД»), обход остаётся только с новым issue — см. находки.
- **Не входит.** Заголовки с «приводкой» (тень со сдвигом) и с автоподбором размера: `PlakatParagraph` не принимает ни
  тени, ни `autoSize`, они остаются на `BasicText`.

- AC: `skin-plakat` не содержит `// kit:` со ссылками на plakat-ui#19–#25; строки 2–8 и 16 `docs/kit-findings.md`
  закрыты с номером версии; голдены `skin-plakat` перезаписаны и просмотрены, паритет с холстом перемерен и записан
  здесь с причиной каждого сдвига; оба гейта зелёные; Renovate #45 закрыт со ссылкой на этот PR.
- Anchors: `skin-plakat/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/plakat/Marks.kt`, `skin-plakat/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/plakat/BoardBlocks.kt`, `skin-plakat/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/plakat/PlakatBoard.kt`, `gradle/libs.versions.toml`

## Находки (14.11.2026)

- **Два обхода остались, с новыми находками** (D4: обход — только с открытым issue):
  - **таблица** ([plakat-ui#34](https://github.com/youndie/plakat-ui/issues/34), строка 18 находок): у `PlakatTable`
    нет выбранной строки, а доска выделяет строку открытой карточки; и строки не ниже 48 dp, когда холст указательной
    доски рисует 36 — двенадцать репозиториев в окне стали бы девятью. Комментарий `// kit:` переведён на #34;
  - **лист «ВИД»** ([plakat-ui#35](https://github.com/youndie/plakat-ui/issues/35), строка 19): `PlakatDialog` со
    слотом сначала встал на место листа и на окне 1280 растянулся в 1232 dp — диалог кита телефонный. Лист 480 остался
    своим, под ним — `PlakatScrim` кита, растр холста удалён.
- **Заголовки с «приводкой»** (`registered`, тень со сдвигом) и заголовок карточки с `autoSize` остались на
  `BasicText`: `PlakatParagraph` не берёт ни тени, ни автоподбора. Отдельную находку не заводил: по паритету их доля не
  выделена, а строки без тени (сводка) улучшили `Board_Content`.
- **«Нет знака»** — `MainStatus.None`, `PrChecks.NoChecks` — слово `label` приглушённым цветом без знака; «открыт» у
  пункта — слово в рамке 1 dp, своя плашка доски: у «открыт» нет семантического вида.
- **Паритет с холстом** (`viddikDesignParity`, ±16 на канал), `main` → ветка, на WSL обе:
  | Сцена | main | B-28 | Почему |
  |---|---|---|---|
  | Board_Content / _Dark | 7,90 / 7,77 % | 7,52 / 7,39 % | сводка на `PlakatParagraph`, метки кита |
  | Board_Stale | 7,89 % | 7,62 % | полоса — баннер кита |
  | Board_NoMatches | 5,51 % | 4,85 % | метки |
  | Board_AppearanceSheet | 28,90 % | 69,08 % | точечная завеса кита (сетка 4 dp) на весь кадр вместо растра холста, повёрнутого на 45° |
  | Board_Stale_Phone | 19,48 % | 20,07 % | действие баннера — слово «ПОВТОРИТЬ», на холсте ↻ |
  | Settings_TokenMissing | 7,27 % | 8,22 % | баннер кита 40 dp против полосы холста 48 |
  | Repo_Content / _Phone | 10,64 / 14,69 % | 11,59 / 16,01 % | статусы пунктов: знак 20 dp и слово вместо плашки со словом |
  | Repo_Red / _Phone | 10,73 / 14,37 % | 10,30 / 12,52 % | метки упавших проверок |
  Остальные сцены сдвинулись меньше чем на 0,7 п. п. в обе стороны.
- **Голдены** `skin-plakat`: 29 перезаписаны на WSL (пауза mutagen, `viddikRecord` по ssh) и просмотрены.
- **Гейты**: `make check` на маке, `./gradlew check` на WSL — зелёные; `:app:wasmJsBrowserTest` без `CHROME_BIN`
  падает на запуске браузера (окружение бокса, не правка), с Chrome for Testing — зелёный.
