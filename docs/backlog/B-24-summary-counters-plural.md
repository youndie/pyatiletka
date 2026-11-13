---
id: B-24
title: "Счётчики сводки не согласуются с числом: «0 красный main»"
status: done
priority: P2
size: S
stage: stage-4-daily-use
epic: feature-board
blocked_by: []
---

# B-24 — Счётчики сводки не согласуются с числом: «0 красный main»

QA-сессия 13.11.2026, опубликованная страница. На живом портфеле красных main нет, и сводка пишет
«0 красный main» (plakat широкий, toon), «0 красный» (plakat узкий, сжатая строка toon). Подписи —
константы: `Texts.RED_MAIN`, `Texts.WAITING_ME` и «красный» в `Texts.summaryLineShort` и
`PhoneBoard.PhoneSummary` подставляются после числа без `Texts.plural`. Так же неверно выйдет
«1 ждут меня» — сейчас ждущих 11, поэтому не видно. Голдены и артборды холста держат «1 красный» и
«7 ждут меня», где форма совпадает случайно.

- **Решение и причина.** Подписи счётчиков — через `Texts.plural` от числа: «0 красных main»,
  «1 красный main», «2 красных main»; «1 ждёт меня», «2 ждут меня», «5 ждут меня». «PR открыто» и
  «PR» не склоняются. На плакатной сводке, где подпись стоит под числом отдельной строкой, — то же.
- Не входит: другие тексты экрана.

- AC: тест на `Texts.summaryLine` и `summaryLineShort` для 0, 1, 2, 5, 11, 21; подписи под числами
  plakat — из той же функции; голдены, где меняется текст, перезаписаны и просмотрены.
- Якоря: `core/src/commonMain/kotlin/io/github/youndie/pyatiletka/core/screen/Texts.kt`,
  `skin-plakat/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/plakat/PhoneBoard.kt`,
  `skin-plakat/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/plakat/PlakatBoard.kt` (`Stat`),
  `skin-toon/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/toon/ToonBoard.kt`.

## Итог (13.11.2026)

- Подписи счётчиков — функции от числа в `Texts`: `redMain`/`red`, `pullRequestsOpen`, `waitingMe`/
  `waiting`, `itemsOf`. Константы `RED_MAIN`, `PRS_OPEN`, `WAITING_ME`, `ITEMS_OF` удалены — ими
  больше никто не пользуется. Строки toon (`summaryLine`, `summaryLineShort`) и подписи под числами
  plakat (широкая, сжатая и узкая сводки) берут подписи оттуда.
- Сверх пункта, того же рода: «PR открыто» стало «1 PR открыт», а «из N пунктов» под первым
  счётчиком plakat — «из 21 пункта». Это те же подписи счётчиков сводки, только с другим словом.
- Тест `TextsTest.theSummaryCountersAgreeWithTheirNumbers` — 0, 1, 2, 5, 11, 21 и `itemsOf` для 21,
  728 и 1225. Мутация: `red` и `waiting` без склонения — тест падает на «0 красный main» против «0 красных main».
- Голдены не изменились: у фикстуры 1 красный, 12 PR, 7 ждут и 728 пунктов, а у фикстур промежуточной
  ширины 1288 — формы совпадают. `viddikVerify` зелёный.
- `./gradlew check` прогнан на WSL, браузерные тесты wasm — с `CHROME_BIN`.
