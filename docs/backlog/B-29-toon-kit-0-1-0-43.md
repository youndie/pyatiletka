---
id: B-29
title: "toon-core 0.1.0.43: обходы toon-ui#30–#37 и #42 заменены API кита"
status: done
priority: P2
size: M
stage: stage-4-daily-use
epic: feature-skins
blocked_by: []
---

# B-29 — toon-core 0.1.0.43: обходы toon-ui#30–#37 и #42 заменены API кита

Кит закрыл девять находок доски (`docs/kit-findings.md`, строки 1, 9–15 и 17) пунктами B-81…B-88 и выпустил их
к `0.1.0.43`. Renovate держит тот же бамп в #25, красный с 12.11: компилируется, но 12 голденов toon
расходятся — картинки кита изменились. Правило D4: обход живёт, только пока в ките нет нужного.

- **Решение.** Версия `toon = "0.1.0.43"`; каждый обход — на API кита, голдены перезаписаны там, где картинка
  изменилась по делу; каждая из девяти правок проверена на `Board`, `Repo`, `Settings` скина toon на 1280×800 и
  390×844.
- **Отвергнуто.** Принять правку кита по закрытому issue без проверки картинкой: обкатка для того и нужна.
- **Не входит.** Правки кита: где правка находку не закрывает, обход остаётся с issue в ките.

- AC: в `skin-toon` нет `// kit:` со ссылками на закрытые issue, кроме тех, что правка кита не закрыла (у них —
  новый issue); строки 1, 9–15, 17 `docs/kit-findings.md` закрыты с номером версии или открыты с причиной; голдены
  `skin-toon` перезаписаны и просмотрены; оба гейта зелёные; Renovate #25 закрыт со ссылкой на этот PR.
- Anchors: `skin-toon/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/toon/ToonBoard.kt`, `skin-toon/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/toon/ToonRepo.kt`, `skin-toon/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/toon/ToonSettings.kt`, `skin-toon/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/toon/Stage.kt`, `gradle/libs.versions.toml`

## Находки (25.11.2026)

Что из девяти правок кита подтвердилось на доске:

| Находка | Issue | Чем доска пользуется теперь | Итог |
|---|---|---|---|
| 1 | [toon-ui#30](https://github.com/youndie/toon-ui/issues/30) | плашка `ToonTitleCard` растёт под «Репозиториев нет», «Ничего не найдено», «Снимка нет» | **наполовину**: заголовок — да; подстрока шире плашки уменьшается без предела (предложение — около 7 sp) и не переносится — [toon-ui#72](https://github.com/youndie/toon-ui/issues/72), строка 20, обход: предложение под карточкой через `ToonText` |
| 9 | [toon-ui#31](https://github.com/youndie/toon-ui/issues/31) | `ToonText` — «Вид», «Скин», «Тема», заголовки разделов карточки и настроек, команда токена | да: «Вид» и «Токен» в `Settings_*` (2231 px) были системным шрифтом, стали гарнитурами кита; на wasm — на живой странице |
| 10 | [toon-ui#32](https://github.com/youndie/toon-ui/issues/32) | бейдж времени держит «12 ноября, 09:17» на широком; `meta` обрезается многоточием | да |
| 11 | [toon-ui#33](https://github.com/youndie/toon-ui/issues/33) | на узком подстрока строки та же, что на широком, в две строки (`subMaxLines = 2`), до шеврона | да |
| 12 | [toon-ui#34](https://github.com/youndie/toon-ui/issues/34) | «Вид» — `ToonDialog` со слотом на широком, `ToonBottomSheetBox` на узком; своя поверхность и затемнение сняты | да; по ходу — у диалога нет формы с одной кнопкой: [toon-ui#70](https://github.com/youndie/toon-ui/issues/70), строка 21, «Закрыть» и «Готово» обе закрывают |
| 13 | [toon-ui#35](https://github.com/youndie/toon-ui/issues/35) | — | **нет**: кит добавил `ToonTheme(stageTexture = false)`, а `drawToonStage` между `.13` и `.43` не изменился — выборка на границе текселов осталась; тест кита идёт без перспективы и на AMD, где расхождения и не бывает. Сдвиг `Stage` на ¾ px остаётся: [toon-ui#71](https://github.com/youndie/toon-ui/issues/71), строка 22 |
| 14 | [toon-ui#36](https://github.com/youndie/toon-ui/issues/36) | заголовок PR и пункта — заголовок строки (`titleMaxLines = Int.MAX_VALUE`), строка растёт; абзац `Entry` под строкой снят | да |
| 15 | [toon-ui#37](https://github.com/youndie/toon-ui/issues/37) | `ToonListItem(selected)` по `BoardUiState.selected` | да |
| 17 | [toon-ui#42](https://github.com/youndie/toon-ui/issues/42) | без правки доски: сегменты по ширине подписей | да, «Ждут меня» внутри рамки на 360 и 390 |

- **Имя репозитория в строке доски — одна строка** (`titleMaxLines = 1`): по умолчанию кит теперь даёт две, а
  screen-board держит имя в одну.
- **Три счётчика на узкой сводке остались** — уже не обход: `meta` у `ToonCard` одна строка, и полная строка
  счётчиков на 360 обрезалась бы на «ждут». На узком бейдж — час, на широком — день и час.
- **«ждут N» — с неразрывным пробелом**: подстрока в две строки иначе оставляла число одно на второй.
- **Голдены**: 26 голденов toon перезаписаны и просмотрены. Записаны раннером `ubuntu-latest` в шаге
  `viddikRecord` (WSL-бокс был недоступен; шаг для этого и есть, B-21); голдены plakat тем же шагом записаны
  байт в байт как в ветке.
- **Живая страница** (`:app:wasmJsBrowserDistribution`, собрана на маке, опубликованный `snapshot.json`):
  `?skin=toon` на 1280×800 и 390×844 — доска, «Вид» (диалог, Esc закрывает; шторка, «Готово» закрывает, тема
  применяется сразу), карточка с отмеченной строкой и перенесёнными заголовками. `Settings` — только десктоп,
  проверены голденами 1280×800.
