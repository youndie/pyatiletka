---
id: skin-toon
title: skin-toon — экраны на toon-ui
type: service
module: skin-toon
tech_stack: [Compose Multiplatform, toon-core, viddik]
owner: unassigned
depends_on:
  - core
  - toon-core
publishes: []
---

# skin-toon

Доска — [B-14](../backlog/B-14-toon-board.md), карточка и настройки —
[B-15](../backlog/B-15-toon-repo-card-and-settings.md): обе раскладки, все состояния, голдены.

## 1. Ответственность

Те же экраны и состояния, что у [skin-plakat](skin-plakat.md), на компонентах toon-ui; темы
`InkGouacheLight`, `InkGouacheNight`, `BrightCel`. Голдены `<Screen>_<State>[_Phone]_Toon`.

**Не делает:** не знает про plakat-ui; не держит состояния; холста у него нет — раскладка
собирается в коде ([исследование D6](../research/research-architecture.md)).

## 2. Контракты

- Вход — типы состояний экранов из [core](core.md).

## 2a. Якоря кода

| Файл | Что там |
|---|---|
| `skin-toon/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/toon/ToonBoard.kt` | `ToonBoard`, `ToonBoardActions` — обе раскладки |
| `skin-toon/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/toon/ToonRepo.kt` | `ToonRepoScreen` (доска и панель или карточка одна), `ToonRepo`, `ToonRepoActions` |
| `skin-toon/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/toon/ToonSettings.kt` | `ToonSettings`, `ToonSettingsActions` |
| `skin-toon/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/toon/ToonSkin.kt` | тема кита по теме доски |
| `skin-toon/build.gradle.kts` | `desktop` и `wasmJs`; зависит от `core` и toon-core, больше ни от чего |
| `skin-toon/src/desktopTest/kotlin/io/github/youndie/pyatiletka/skin/toon/BoardFixtures.kt` | фикстуры `Board_*_Toon` |
| `skin-toon/src/desktopTest/kotlin/io/github/youndie/pyatiletka/skin/toon/RepoFixtures.kt`, `SettingsFixtures.kt` | фикстуры `Repo_*_Toon`, `Settings_*_Toon` |
| `skin-toon/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/toon/Stage.kt` | `Stage` — сцена кита со сдвигом на ¾ px (обход [toon-ui#71](https://github.com/youndie/toon-ui/issues/71), продолжение #35) |
| `skin-toon/src/desktopTest/snapshots/` | голдены — запись (холста нет) |

## 3. Как устроено

- **Первые находки известны заранее.** В toon-ui нет скелета, пустого состояния, счётчика и плаката
  ([исследование §1.3](../research/research-architecture.md)); доска использует все четыре. Каждый
  обход — issue в `youndie/toon-ui` и комментарий `// kit: <ссылка>`.
- **Кириллица.** Ни одна гарнитура toon-ui её не держит; компоненты кита рисуют её резервными
  гарнитурами — проверено в [B-01](../backlog/B-01-skeleton-five-modules.md) на «ПЯТИЛЕТКА»
  ([исследование §1.7](../research/research-architecture.md)). Свой текст через публичный
  `ToonTypeSpec.textStyle` резерва не получает ([toon-ui#31](https://github.com/youndie/toon-ui/issues/31)).
  С toon-core `0.1.0.43` свой русский текст идёт через `ToonText` кита — заголовки разделов, «Вид»,
  «Скин», «Тема», предложение пустого экрана ([B-29](../backlog/B-29-toon-kit-0-1-0-43.md)); `BasicText`
  со стилем кита в скине не остаётся.
- **Длинный текст — забота кита** (с `0.1.0.43`, [B-29](../backlog/B-29-toon-kit-0-1-0-43.md)): бейдж
  времени `ToonCard` растёт под время, `meta` обрезается многоточием; строка `ToonListItem` переносит
  заголовок (`titleMaxLines`) и подстроку (`subMaxLines`) до шеврона и растёт по ним. Доска задаёт
  строки: имя репозитория — одна, подстрока на узком — две; заголовок PR и пункта — без предела.
- **Выбранная строка** — `ToonListItem(selected)`: на широком доска рядом с карточкой отмечает
  открытый репозиторий (`BoardUiState.selected`).
- **«Вид»** — `ToonDialog` на широком и `ToonBottomSheetBox` на узком, радиогруппы в слоте. Кнопка
  отмены у диалога обязательна, поэтому «Закрыть» и «Готово» обе закрывают
  ([toon-ui#70](https://github.com/youndie/toon-ui/issues/70)).
- **`ToonTitleCard`**: пустой `sub` — подзаголовка нет, `null` — демонстрационная строка кита;
  плашка растёт под заголовок, но подстроку шире себя уменьшает без предела, поэтому доска передаёт
  `sub = ""` и ставит предложение под карточкой ([toon-ui#72](https://github.com/youndie/toon-ui/issues/72),
  [учёт находок](../kit-findings.md)).

## 4. Зависимости

| Вид | Имя | Зачем |
|---|---|---|
| Модуль | [core](core.md) | типы состояний |
| Библиотека | toon-core `0.1.0.43` из `reposilite.kotlin.website/snapshots` | компоненты и темы |
| Плагин | viddik `0.7.0` | голдены |
- **Сцена — через `Stage`, не через `ToonStage` напрямую.** Кит выбирает текстуру ближайшим текселом
  ровно на границе двух текселов, и под перспективой viddik Intel и AMD рисовали её по-разному:
  голдены падали на раннерах Intel ([B-21](../backlog/B-21-toon-goldens-depend-on-runner-cpu.md),
  [toon-ui#35](https://github.com/youndie/toon-ui/issues/35)). `Stage` сдвигает сцену на ¾ px, и
  голдены одинаковы на WSL и на любом раннере. Новый экран toon берёт `Stage`. Кит закрыл #35
  выключателем `ToonTheme(stageTexture = false)`: он убирает текстуру из голденов, а не делает её
  выборку одинаковой, — сдвиг остаётся до [toon-ui#71](https://github.com/youndie/toon-ui/issues/71)
  ([B-29](../backlog/B-29-toon-kit-0-1-0-43.md)).

