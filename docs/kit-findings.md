# Находки по китам

Всё, чего plakat-ui или toon-ui не хватило, когда доска их использовала. Правило
([исследование D4](research/research-architecture.md)): обход в коде доски — только вместе с
issue в репозитории кита и комментарием `// kit: <ссылка>` в месте обхода. Находка без обхода —
тоже строка здесь: её считает итог обкатки ([B-20](backlog/B-20-two-week-trial-verdict.md)).

| # | Кит | Находка | Issue | Обход в доске | Пункт | Статус |
|---|---|---|---|---|---|---|
| 1 | toon-ui | `ToonTitleCard`: плашка фиксированной ширины (280 у `Splash`, 260 у `Empty`), заголовок шире неё вылезает за края — «ПЯТИЛЕТКА» на 0.1.0.13 | [toon-ui#30](https://github.com/youndie/toon-ui/issues/30) | нет | [B-01](backlog/B-01-skeleton-five-modules.md) | открыта |
| 2 | plakat-ui | Нет метки статуса (слово со знаком: клин-галочка, штриховка, плашка) для main, PR и пунктов | [plakat-ui#19](https://github.com/youndie/plakat-ui/issues/19) | `skin-plakat/…/Marks.kt` | [B-11](backlog/B-11-plakat-board.md) | открыта |
| 3 | plakat-ui | Нет таблицы: строки с колонками под заголовком | [plakat-ui#20](https://github.com/youndie/plakat-ui/issues/20) | `skin-plakat/…/PlakatBoard.kt` — колонки | [B-11](backlog/B-11-plakat-board.md) | открыта |
| 4 | plakat-ui | Нет стенсила «⋮», и потребитель не может сделать свой (`PlakatStencil` — `internal`) | [plakat-ui#21](https://github.com/youndie/plakat-ui/issues/21) | `skin-plakat/…/Marks.kt` — `MoreGlyph` | [B-11](backlog/B-11-plakat-board.md) | открыта |
| 5 | plakat-ui | Текст потребителя стилями кита стоит на 4–6 px ниже, чем CSS: размещение по CSS (`CssParagraph`) — `internal` | [plakat-ui#22](https://github.com/youndie/plakat-ui/issues/22) | нет — остаток паритета | [B-11](backlog/B-11-plakat-board.md) | открыта |
| 6 | plakat-ui | У `PlakatDialog` нет слота содержимого | [plakat-ui#23](https://github.com/youndie/plakat-ui/issues/23) | `skin-plakat/…/BoardBlocks.kt` — `AppearanceDialog` | [B-11](backlog/B-11-plakat-board.md) | открыта |
| 7 | plakat-ui | Нет растра-подложки для своих оверлеев | [plakat-ui#24](https://github.com/youndie/plakat-ui/issues/24) | `skin-plakat/…/BoardBlocks.kt` — `halftone` | [B-11](backlog/B-11-plakat-board.md) | открыта |
| 8 | plakat-ui | Нет полосы-предупреждения внутри экрана | [plakat-ui#25](https://github.com/youndie/plakat-ui/issues/25) | `skin-plakat/…/BoardBlocks.kt` — `StaleBar` | [B-11](backlog/B-11-plakat-board.md) | открыта |

Статусы: `открыта` — issue открыт; `закрыта` — исправлено в ките, обход снят; `отклонена` — кит
решил иначе, и это записано в issue.
