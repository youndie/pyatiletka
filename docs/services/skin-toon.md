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
| `skin-toon/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/toon/Stage.kt` | `Stage` — сцена кита за содержимым; с toon-core `0.1.0.49` без своего сдвига ([toon-ui#71](https://github.com/youndie/toon-ui/issues/71), [B-30](../backlog/B-30-kits-release-candidates.md)) |
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
  «Скин», «Тема» ([B-29](../backlog/B-29-toon-kit-0-1-0-43.md)); `BasicText`
  со стилем кита в скине не остаётся.
- **Длинный текст — забота кита** (с `0.1.0.43`, [B-29](../backlog/B-29-toon-kit-0-1-0-43.md)): бейдж
  времени `ToonCard` растёт под время, `meta` обрезается многоточием; строка `ToonListItem` переносит
  заголовок (`titleMaxLines`) и подстроку (`subMaxLines`) до шеврона и растёт по ним. Доска задаёт
  строки: имя репозитория — одна, подстрока на узком — две; заголовок PR и пункта — без предела.
- **Выбранная строка** — `ToonListItem(selected)`: на широком доска рядом с карточкой отмечает
  открытый репозиторий (`BoardUiState.selected`).
- **«Вид»** — `ToonDialog` на широком и `ToonBottomSheetBox` на узком, радиогруппы в слоте. Выбор
  применяется сразу, отменять нечего: у диалога одна «Готово», `cancel = null`
  ([toon-ui#70](https://github.com/youndie/toon-ui/issues/70), [B-30](../backlog/B-30-kits-release-candidates.md)).
- **`ToonTitleCard`**: пустой `sub` — подзаголовка нет, `null` — демонстрационная строка кита;
  плашка растёт под заголовок, а подстроку шире себя переносит до трёх строк и только потом
  уменьшает, не ниже 12 sp. Предложение пустого экрана — `sub` карточки
  ([toon-ui#72](https://github.com/youndie/toon-ui/issues/72), [B-30](../backlog/B-30-kits-release-candidates.md)).

## 4. Зависимости

| Вид | Имя | Зачем |
|---|---|---|
| Модуль | [core](core.md) | типы состояний |
| Библиотека | toon-core `0.1.0.50` из `reposilite.kotlin.website/snapshots` ([B-30](../backlog/B-30-kits-release-candidates.md)) | компоненты и темы |
| Плагин | viddik `0.8.1.75` | голдены |
- **Сцена — через `Stage`.** Кит выбирал текстуру ближайшим текселом ровно на границе двух текселов, и
  под перспективой viddik Intel и AMD рисовали её по-разному: голдены падали на раннерах Intel
  ([B-21](../backlog/B-21-toon-goldens-depend-on-runner-cpu.md), [toon-ui#35](https://github.com/youndie/toon-ui/issues/35)).
  Доска сдвигала сцену на ¾ px; с toon-core `0.1.0.49` плитку на ⅛ текселя сдвигает сам кит
  ([toon-ui#71](https://github.com/youndie/toon-ui/issues/71)), и сдвиг доски снят: без него
  `:skin-toon:viddikVerify` зелёный на 20 раннерах из 20, из них 5 Intel ([B-30](../backlog/B-30-kits-release-candidates.md)).
  `Stage` остаётся одной точкой, где скин ставит сцену.

