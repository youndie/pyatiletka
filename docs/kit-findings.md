# Находки по китам

Всё, чего plakat-ui или toon-ui не хватило, когда доска их использовала. Правило
([исследование D4](research/research-architecture.md)): обход в коде доски — только вместе с
issue в репозитории кита и комментарием `// kit: <ссылка>` в месте обхода. Находка без обхода —
тоже строка здесь: её считает итог обкатки ([B-20](backlog/B-20-two-week-trial-verdict.md)).

| # | Кит | Находка | Issue | Обход в доске | Пункт | Статус |
|---|---|---|---|---|---|---|
| 1 | toon-ui | `ToonTitleCard`: плашка фиксированной ширины (280 у `Splash`, 260 у `Empty`), заголовок и подстрока шире неё вылезают за края — «ПЯТИЛЕТКА» на 0.1.0.13, а на узких экранах доски «Репозиториев нет» и «Ничего не найдено» обрезаны | [toon-ui#30](https://github.com/youndie/toon-ui/issues/30) | нет — видно на `Board_Empty_Phone_Toon`, `Board_NoMatches_Phone_Toon` | [B-01](backlog/B-01-skeleton-five-modules.md), [B-14](backlog/B-14-toon-board.md) | открыта |
| 2 | plakat-ui | Нет метки статуса (слово со знаком: клин-галочка, штриховка, плашка) для main, PR и пунктов | [plakat-ui#19](https://github.com/youndie/plakat-ui/issues/19) | `skin-plakat/…/Marks.kt` | [B-11](backlog/B-11-plakat-board.md) | открыта |
| 3 | plakat-ui | Нет таблицы: строки с колонками под заголовком | [plakat-ui#20](https://github.com/youndie/plakat-ui/issues/20) | `skin-plakat/…/PlakatBoard.kt` — колонки | [B-11](backlog/B-11-plakat-board.md) | открыта |
| 4 | plakat-ui | Нет стенсила «⋮», и потребитель не может сделать свой (`PlakatStencil` — `internal`) | [plakat-ui#21](https://github.com/youndie/plakat-ui/issues/21) | `skin-plakat/…/Marks.kt` — `MoreGlyph` | [B-11](backlog/B-11-plakat-board.md) | открыта |
| 5 | plakat-ui | Текст потребителя стилями кита стоит на 4–6 px ниже, чем CSS: размещение по CSS (`CssParagraph`) — `internal` | [plakat-ui#22](https://github.com/youndie/plakat-ui/issues/22) | нет — остаток паритета | [B-11](backlog/B-11-plakat-board.md) | открыта |
| 6 | plakat-ui | У `PlakatDialog` нет слота содержимого | [plakat-ui#23](https://github.com/youndie/plakat-ui/issues/23) | `skin-plakat/…/BoardBlocks.kt` — `AppearanceDialog` | [B-11](backlog/B-11-plakat-board.md) | открыта |
| 7 | plakat-ui | Нет растра-подложки для своих оверлеев | [plakat-ui#24](https://github.com/youndie/plakat-ui/issues/24) | `skin-plakat/…/BoardBlocks.kt` — `halftone` | [B-11](backlog/B-11-plakat-board.md) | открыта |
| 8 | plakat-ui | Нет полосы-предупреждения внутри экрана | [plakat-ui#25](https://github.com/youndie/plakat-ui/issues/25) | `skin-plakat/…/BoardBlocks.kt` — `StaleBar` | [B-11](backlog/B-11-plakat-board.md) | открыта |
| 9 | toon-ui | Свой русский текст стилями кита (`ToonTypeSpec.textStyle`) идёт системным шрифтом: резервные гарнитуры кириллицы — `internal` | [toon-ui#31](https://github.com/youndie/toon-ui/issues/31) | нет — заголовки шторки вида системным шрифтом | [B-14](backlog/B-14-toon-board.md) | открыта |
| 10 | toon-ui | `ToonCard`: бейдж времени фиксированной ширины, строка `meta` не обрезается | [toon-ui#32](https://github.com/youndie/toon-ui/issues/32) | `skin-toon/…/ToonBoard.kt` — только час, короткие счётчики на узком | [B-14](backlog/B-14-toon-board.md) | открыта |
| 11 | toon-ui | `ToonListItem`: подстрока не обрезается и уходит за край | [toon-ui#33](https://github.com/youndie/toon-ui/issues/33) | `skin-toon/…/ToonBoard.kt` — короткая подстрока на узком | [B-14](backlog/B-14-toon-board.md) | открыта |
| 12 | toon-ui | `ToonDialog` и `ToonBottomSheet` без слота содержимого; английские значения по умолчанию | [toon-ui#34](https://github.com/youndie/toon-ui/issues/34) | `skin-toon/…/ToonBoard.kt` — `AppearancePanel` | [B-14](backlog/B-14-toon-board.md) | открыта |

Статусы: `открыта` — issue открыт; `закрыта` — исправлено в ките, обход снят; `отклонена` — кит
решил иначе, и это записано в issue.
