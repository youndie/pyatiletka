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

Доска — [B-11](../backlog/B-11-plakat-board.md): обе раскладки, все состояния, голдены; эталоны холста —
[B-10](../backlog/B-10-design-canvas-and-references.md). Карточка — [B-12](../backlog/B-12-plakat-repo-card.md), настройки —
[B-13](../backlog/B-13-plakat-settings.md).

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
| `skin-plakat/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/plakat/PlakatBoard.kt` | `PlakatBoard`, `BoardActions`; широкая раскладка |
| `skin-plakat/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/plakat/PhoneBoard.kt` | узкая раскладка |
| `skin-plakat/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/plakat/PlakatRepo.kt` | карточка: `RepoPanel` рядом с доской, `PhoneRepo` отдельным экраном, `RepoActions`; ↗ своим глифом (`// kit:`) |
| `skin-plakat/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/plakat/Marks.kt` | обходы кита: метки статуса, «⋮» (`// kit:`) |
| `skin-plakat/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/plakat/PlakatSettings.kt` | настройки: `PlakatSettings`, `SettingsActions`; полоса «Внимание» своя (`// kit:`) |
| `skin-plakat/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/plakat/PlakatSkin.kt` | тема кита по теме доски; `RussianStrings` — слова кита по-русски |
| `skin-plakat/build.gradle.kts` | `desktop` и `wasmJs`; зависит от `core` и plakat-core, больше ни от чего |
| `skin-plakat/src/desktopTest/snapshots/design/` | эталонные PNG с холста, имя = артборд, 1280×800 и 390×844 |
| `scripts/design-references.py` | экспорт холста @2x → эталоны 1x, коробочный фильтр ([исследование D9](../research/research-architecture.md)) |
| `design/canvas/` | исходники артбордов, `canvas.json` с заметками дизайнера |
| `skin-plakat/src/desktopTest/` | фикстуры viddik (`BoardFixtures.kt`, `RepoFixtures.kt`, `SettingsFixtures.kt`) и голдены |

## 3. Как устроено

- **Паритет — `viddikDesignParity`** против PNG холста; у кита допуски `5.0` % и `16` по каналу,
  нестрогий режим (`youndie/plakat-ui@f2cbd5c5!/plakat-core/build.gradle.kts`). Число паритета
  остаётся в PR, который его измерил; превышение допуска приходит с причиной, а не с ослабленным
  порогом.
- **Обход кита — только с issue** в `youndie/plakat-ui` и комментарием `// kit: <ссылка>` в месте
  обхода ([исследование D4](../research/research-architecture.md)).
- **Имена:** артборд, эталон и фикстура называются одинаково, `<Screen>_<State>[_Phone][_Dark]`.
- **Компоненты кита — даже там, где холст рисует иначе.** Поле поиска, сегменты, прогресс, чипы,
  скелет, пустое состояние, шторка — кита; паритет с холстом там не сходится, и это вопрос владельцу
  ([B-11](../backlog/B-11-plakat-board.md), исследование §1.8), а не повод рисовать «под холст».
- **Голдены пишутся на Linux** (там же, где их проверяет CI), с паузой синхронизации: реплика mutagen
  стирает файлы, которых нет на маке. `mutagen sync pause pyatiletka`, `viddikRecord` по ssh, забрать
  `src/desktopTest/snapshots/`, `mutagen sync resume`.
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
| Библиотека | plakat-core `0.1.0.23` из `reposilite.kotlin.website/snapshots` ([B-28](../backlog/B-28-plakat-kit-0-1-0-23.md)) | компоненты и токены |
| Плагин | viddik `0.7.0` | голдены и паритет |

## 8. Причуды

- Длинные английские заголовки PR и пунктов рядом с русским интерфейсом — намеренная трудность
  холста; их не сокращают, обрезают многоточием там, где холст так показал.
