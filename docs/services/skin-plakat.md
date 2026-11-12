---
id: skin-plakat
title: skin-plakat — экраны на plakat-ui
type: service
module: skin-plakat
tech_stack: [Compose Multiplatform, plakat-core, viddik]
owner: unassigned
depends_on:
  - core
  - plakat-core
publishes: []
---

# skin-plakat

Модуль заведён в [B-01](../backlog/B-01-skeleton-five-modules.md) (`PlakatTitleScreen` — название на
бумаге, `PlakatMasthead`), эталоны холста — в [B-10](../backlog/B-10-design-canvas-and-references.md);
экраны — *целевые* ([B-11](../backlog/B-11-plakat-board.md)…[B-13](../backlog/B-13-plakat-settings.md)).

## 1. Ответственность

Все экраны ([screen-board](../screens/screen-board.md), [screen-repo](../screens/screen-repo.md),
[screen-settings](../screens/screen-settings.md)) во всех состояниях на компонентах plakat-ui, в
двух раскладках (1280×800 и 390×844) и двух темах (`Paper`, `BlackPaper`). Эталоны и паритет с
холстом.

**Не делает:** не знает про toon-ui и [skin-toon](skin-toon.md); не держит состояния — получает его
из [core](core.md) через [app](app.md); не ходит в сеть.

## 2. Контракты

- Вход — типы состояний экранов из `core`.
- Холст — Claude Design, проект plakat-ui, 31 артборд от 12.11.2026; исходники артбордов и заметки
  дизайнера — `design/canvas/` (`canvas.json` — рамки и заметки по страницам); эталонные PNG — по
  одному на артборд, 1x.

## 2a. Якоря кода

| Файл | Что там |
|---|---|
| `skin-plakat/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/plakat/` | `PlakatTitleScreen`; экраны — *целевые* |
| `skin-plakat/build.gradle.kts` | `desktop` и `wasmJs`; зависит от `core` и plakat-core, больше ни от чего |
| `skin-plakat/src/desktopTest/snapshots/design/` | эталонные PNG с холста, имя = артборд, 1280×800 и 390×844 |
| `scripts/design-references.py` | экспорт холста @2x → эталоны 1x, коробочный фильтр ([исследование D9](../research/research-architecture.md)) |
| `design/canvas/` | исходники артбордов, `canvas.json` с заметками дизайнера |
| `skin-plakat/src/desktopTest/` | фикстуры viddik и голдены (*целевой*) |

## 3. Как устроено

- **Паритет — `viddikDesignParity`** против PNG холста; у кита допуски `5.0` % и `16` по каналу,
  нестрогий режим (`youndie/plakat-ui@f2cbd5c5!/plakat-core/build.gradle.kts`). Число паритета
  остаётся в PR, который его измерил; превышение допуска приходит с причиной, а не с ослабленным
  порогом.
- **Обход кита — только с issue** в `youndie/plakat-ui` и комментарием `// kit: <ссылка>` в месте
  обхода ([исследование D4](../research/research-architecture.md)).
- **Имена:** артборд, эталон и фикстура называются одинаково, `<Screen>_<State>[_Phone][_Dark]`.
- **Эталоны — уменьшенный экспорт, а не рендер в 1x** ([исследование D9](../research/research-architecture.md)).
  Новый экспорт холста: `python3 scripts/design-references.py <каталог экспорта>` (нужен Pillow),
  исходники `.dc.html` и `canvas.json` — в `design/canvas/`, PNG экспорта в репозиторий не кладутся.
- **Холст выведен из токенов, а не из компонентов кита**: у дизайнера не было страниц «Ядро»,
  «Компоненты», «Текстуры» ([исследование §1.6](../research/research-architecture.md)). Расхождение
  паритета на компоненте кита сначала сверяется со страницей «Компоненты» plakat-ui.

## 4. Зависимости

| Вид | Имя | Зачем |
|---|---|---|
| Модуль | [core](core.md) | типы состояний |
| Библиотека | plakat-core `0.1.0.15` из `reposilite.kotlin.website/snapshots` | компоненты и токены |
| Плагин | viddik `0.7.0` | голдены и паритет |

## 8. Причуды

- Длинные английские заголовки PR и пунктов рядом с русским интерфейсом — намеренная трудность
  холста; их не сокращают, обрезают многоточием там, где холст так показал.
