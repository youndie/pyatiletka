---
id: B-30
title: "plakat-core 0.1.0.26 и toon-core 0.1.0.50: последние обходы китов сняты"
status: done
priority: P2
size: M
stage: stage-4-daily-use
epic: feature-skins
blocked_by: []
---

# B-30 — plakat-core 0.1.0.26 и toon-core 0.1.0.50: последние обходы китов сняты

Вердикт [B-20](B-20-two-week-trial-verdict.md) назвал помехой выпуску китов пять открытых строк
`docs/kit-findings.md` (18–22). Киты закрыли все пять: plakat-ui#34 и #35 — в `0.1.0.24` и `0.1.0.25`,
toon-ui#70, #71, #72 — в `0.1.0.48`, `0.1.0.49`, `0.1.0.50`. Правило D4: обход живёт, только пока в ките нет
нужного; закрытый issue — ещё не подтверждение.

- **Решение.** Версии `plakat = "0.1.0.26"`, `toon = "0.1.0.50"` — последние снапшоты; в них же подготовка
  китов к Maven Central. Каждый обход — на API кита; каждая правка проверена картинкой на 1280×800 и 390×844.
- **Отвергнуто.** Закрыть строки по закрытым issue без картинки.
- **Не входит.** Правки китов и их выпуск на Central — по «да» владельца.

- AC: в `skin-plakat` и `skin-toon` нет `// kit:` со ссылками на закрытые issue; строки 18–22
  `docs/kit-findings.md` закрыты с номером версии или открыты с причиной и комментарием в issue кита; голдены
  перезаписаны и просмотрены; оба гейта зелёные; Renovate #55 и #57 закрыты со ссылкой на этот PR.
- Anchors: `skin-plakat/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/plakat/PlakatBoard.kt`, `skin-plakat/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/plakat/BoardBlocks.kt`, `skin-toon/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/toon/ToonBoard.kt`, `skin-toon/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/toon/Stage.kt`, `gradle/libs.versions.toml`

## Находки (26.11.2026)

| Строка | Issue | Что снято | Подтверждено |
|---|---|---|---|
| 18 | [plakat-ui#34](https://github.com/youndie/plakat-ui/issues/34) | своя таблица (`TableHeaderRow`, `TableRow`) → `PlakatTable(density = PlakatDensity.Pointer)`, `row(onClick, selected)` | **да**: строки 36 dp, двенадцать репозиториев в окне 1280×800, строка открытой карточки на бетоне (`Repo_Content`) |
| 19 | [plakat-ui#35](https://github.com/youndie/plakat-ui/issues/35) | свой лист 480 над `PlakatScrim` → `PlakatDialog` со слотом | **да**: лист 480 dp по центру окна (`Board_AppearanceSheet`) |
| 20 | [toon-ui#72](https://github.com/youndie/toon-ui/issues/72) | предложение под карточкой → `sub` у `ToonTitleCard` | **да**: подстрока переносится до трёх строк читаемым кеглем на 1280 и 390 (`Board_Empty_*`, `Board_NoMatches_*`, `Board_Error_*`) |
| 21 | [toon-ui#70](https://github.com/youndie/toon-ui/issues/70) | «Закрыть» и «Готово», обе закрывающие → `ToonDialog(cancel = null)` | **да**: одна «Готово» (`Board_AppearanceSheet_Toon`) |
| 22 | [toon-ui#71](https://github.com/youndie/toon-ui/issues/71) | сдвиг сцены на ¾ px в `Stage` | **да**: голдены, записанные без сдвига, проходят `:skin-toon:viddikVerify` на 20 раннерах из 20 — 5 Intel Xeon (6975P-C, 8573C ×4) и 15 AMD EPYC (7763, 9V45, 9V74); проба `cpu-probe`, прогон 45717673798, две попытки |

- **Таблица — одна позиция ленты.** `PlakatTable` раскладывает строки сама и не ленивая, поэтому в
  `LazyColumn` доски она — один элемент под сводкой; `BoardScrollTest` прокручивает к нему. Для портфеля в
  несколько десятков репозиториев этого хватает.
- **Колонки по холсту.** Промежуток между колонками у кита 8 dp, на холсте 16: каждая колонка, кроме
  последней (она прижата вправо), шире на 8 dp и начинается там же, где на холсте. Это выбор ширин доской,
  не обход кита.
- **Шаг строки — 37 dp, а не 36.** Кит кладёт волосяную линию под строку 36 dp, холст рисует её внутри
  строки; за двенадцать строк набегает 12 dp. Двенадцать строк в окне 1280×800 остаются, issue закрыт
  по сути; расхождение отмечено комментарием в plakat-ui#34. Обхода нет.
- **Паритет с холстом** (`viddikDesignParity`, ±16 на канал; мак, числа на телефонных сценах совпадают с
  WSL из B-28), B-28 → B-30: `Board_Content` 7,52 → 11,26 %, `Board_Stale` 7,62 → 10,85 %,
  `Repo_Content` 11,59 → 14,40 %, `Repo_Red` 10,30 → 13,02 %, `Board_AppearanceSheet` 69,08 → 70,61 %. Рост —
  шаг строки и посадка текста в ячейке кита (по центру строки) плюс лист диалога кита с красной плашкой
  заголовка вместо рамки холста. Узкие сцены не изменились: на телефоне таблицы и диалога нет.
- **Сцена toon.** Плитка кита сдвинута на ⅛ текселя; все голдены toon сдвинулись на ≤ 8 по каналу, один раз.
- **Голдены** записаны шагом `viddikRecord` в CI на `ubuntu-latest` (WSL-машина недоступна с мака), 39 в
  первом круге и 12 plakat после выравнивания колонок; просмотрены.
