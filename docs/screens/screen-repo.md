---
id: screen-repo
title: Карточка репозитория (Repo)
type: client_screen
platform: [desktop, web]
status: active
entry:
  desktop: "строка доски"
  web: "/#/<owner>/<name>"
parent_feature: feature-repo-detail
calls_api: []
source: app/src/commonMain/kotlin/io/github/youndie/pyatiletka/app
design:
  canvas: "Claude Design, проект plakat-ui, страница Repo от 12.11.2026; исходники артбордов — design/canvas/Page_Repo.dc.html, заметки — design/canvas/canvas.json"
  references: skin-plakat/src/desktopTest/snapshots/design
  states:
    Content: Repo_Content
    Red: Repo_Red
    Quiet: Repo_Quiet
    NoPlan: Repo_NoPlan
---

# Экран: карточка репозитория

Что красное, какие PR открыты, какие пункты плана не закрыты. Фича —
[feature-repo-detail](../features/feature-repo-detail.md). API нет: данные из того же снимка, что и
[screen-board](screen-board.md).

Состояния экрана и их получение — в коде ([B-09](../backlog/B-09-app-screen-state-and-navigation.md)); раскладка toon — в коде и голденах ([B-15](../backlog/B-15-toon-repo-card-and-settings.md)), раскладка plakat — в коде и голденах ([B-12](../backlog/B-12-plakat-repo-card.md)).

## 0a. Якоря кода

| Что | Файл |
|---|---|
| Состояние и навигация | `app/src/commonMain/kotlin/io/github/youndie/pyatiletka/app/BoardModel.kt` — `BoardModel.card`, `open`, `closeCard` |
| Тест состояний | `app/src/commonTest/kotlin/io/github/youndie/pyatiletka/app/ScreenStatesTest.kt` — `theFourCards` |
| Типы состояний | `core/src/commonMain/kotlin/io/github/youndie/pyatiletka/core/screen/RepoUi.kt` — `RepoScreenState`, `repoUiState` |
| Раскладка plakat | `skin-plakat/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/plakat/PlakatRepo.kt` — `RepoPanel`, `PhoneRepo`; доска рядом — `PlakatBoard.kt` (`card`) |
| Фикстуры plakat | `skin-plakat/src/desktopTest/kotlin/io/github/youndie/pyatiletka/skin/plakat/RepoFixtures.kt` |
| Раскладка toon | `skin-toon/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/toon/ToonRepo.kt` — `ToonRepoScreen`, `ToonRepo` |
| Примерные состояния | `core/src/commonMain/kotlin/io/github/youndie/pyatiletka/core/sample/SampleCardStates.kt` — `SampleRepoStates` |
| Фикстуры toon | `skin-toon/src/desktopTest/kotlin/io/github/youndie/pyatiletka/skin/toon/RepoFixtures.kt` |
| Эталоны холста | `skin-plakat/src/desktopTest/snapshots/design/` |

## 0. Вход и видимость

- **Вход:** desktop — строка доски; web — `/#/<owner>/<name>`.
- **Размеры:** 1280×800 — правая панель шириной 480 рядом с доской (доска видна слева, строка
  выбранного репозитория отмечена); 390×844 (`_Phone`) — отдельный экран.

## 1. Состояния экрана

- [ ] **Content:** `youndie/kachok`: main зелёный, 2 PR, 7 незакрытых пунктов, выполнение 96 %.
- [ ] **Red:** `youndie/tacku`: сверху две упавшие проверки, ниже 1 PR и пункты.
- [ ] **Quiet:** `youndie/plakat-ui`: плакат «план выполнен», 73 из 73, списков нет.
- [ ] **NoPlan:** `youndie/proba`: 1 PR, вместо плана — «бэклога в репозитории нет».

### 1.1 Артборды и эталоны (10 артбордов)

| Состояние | 1280×800 | 390×844 | Тёмный | toon (голден) |
|---|---|---|---|---|
| Content | `Repo_Content` | `Repo_Content_Phone` | `Repo_Content_Dark`, `Repo_Content_Phone_Dark` | `Repo_Content_Toon`, `Repo_Content_Phone_Toon` |
| Red | `Repo_Red` | `Repo_Red_Phone` | — | `Repo_Red_Toon`, `Repo_Red_Phone_Toon` |
| Quiet | `Repo_Quiet` | `Repo_Quiet_Phone` | — | `Repo_Quiet_Toon`, `Repo_Quiet_Phone_Toon` |
| NoPlan | `Repo_NoPlan` | `Repo_NoPlan_Phone` | — | `Repo_NoPlan_Toon`, `Repo_NoPlan_Phone_Toon` |

## 2. Интеграция с API

Нет: карточка строится из снимка, который уже загрузила доска.

## 3. Инициализация

Входной параметр — `owner/name` репозитория (строка доски или адрес на вебе). Запросов нет.

## 4. Элементы сверху вниз

### 4.1. Упавшие проверки

- Только при `Red`, первыми на карточке. **По нажатию:** проверка на GitHub.

### 4.2. Открытые PR

- Порядок: `Failing`, `Ready`, `Running`, `NoChecks`; внутри — старые выше. Заголовок, автор
  (человек / бот), возраст. Длинные английские заголовки не сокращаются.
- **По нажатию:** PR на GitHub.

### 4.3. Пункты плана

- Порядок: `Question`, `Wip`, `Open`; внутри — по приоритету. Номер, метка, приоритет, заголовок.
- `Plan.None` — пометка «бэклога в репозитории нет»; `Quiet` — плакат «план выполнен».
- **По нажатию:** пункт на GitHub.

### 4.4. Что добавил холст

Из заметок страницы Repo в `design/canvas/canvas.json`:

- на широком слева доска в сжатом виде: сводка в одну строку, строка выбранного репозитория
  выделена; ✕ закрывает панель и снимает выделение; на узком ← возвращает к доске;
- панель прокручивается целиком под строкой с названием, доска слева — отдельно;
- заголовки PR и пунктов не обрезаются, а переносятся; название репозитория в шапке узкого — с
  многоточием;
- каждая строка PR, пункта и проверки несёт значок ↗ и открывает GitHub.

### 4.5. Как это сделано в plakat

По холсту, с видом компонентов кита ([B-11](../backlog/B-11-plakat-board.md), [B-12](../backlog/B-12-plakat-repo-card.md)):

- на широком доска остаётся слева на 800: красная плашка «План выполнен на 95 %» шириной 248 и
  четыре счётчика без полосы, поиск занимает остаток строки, колонки таблицы сужены до
  112 / 120 / 208 / 48, полоска плана — до 64; строка открытого репозитория выделена;
- панель 480 за линией 3 dp; разделы — подпись над линией 3 dp, строки — под линией 1 dp, у каждой
  строки ↗. Своего значка ↗ в ките нет ([plakat-ui#21](https://github.com/youndie/plakat-ui/issues/21)),
  поэтому он нарисован по пути с холста;
- показаны **все** незакрытые пункты, в порядке `core` (§4.3). Кнопку «Ещё 9» с холста не взяли:
  дизайнер сам оставил её как вопрос («В брифе этого нет: решить»), и порядок на холсте
  (B-61, B-58, B-52) расходится с порядком `core` (B-61, B-58, B-53).

### 4.6. Как это сделано в toon

Холста у toon нет ([исследование D6](../research/research-architecture.md)); раскладка собрана из
компонентов кита ([B-15](../backlog/B-15-toon-repo-card-and-settings.md)):

- шапка — `ToonTopAppBar` с названием репозитория: на широком ✕ закрывает панель, на узком ← ведёт
  назад; сводка — `ToonCard`: ветка и её состояние, число PR и «сделано из всего», доля плана в
  бейдже;
- проверки, PR и пункты — `ToonListItem` с иконкой состояния; строка кита однострочная и без
  ширины ([toon-ui#36](https://github.com/youndie/toon-ui/issues/36)), поэтому в строке — номер и
  короткие слова, а собственный заголовок PR или пункта переносится абзацем под строкой;
- `Quiet` — `ToonTitleCard` `Empty` «План выполнен» с «73 из 73»; заголовок шире плашки
  ([toon-ui#30](https://github.com/youndie/toon-ui/issues/30)) — обхода нет;
- на широком доска слева — та же `ToonBoard`, но строка открытого репозитория не выделена: у
  строки кита нет выбранного состояния ([toon-ui#37](https://github.com/youndie/toon-ui/issues/37));
  строки доски рядом с панелью идут короткой подстрокой, поле поиска сужается до 248.

## 5. Навигация

- строка PR, пункта или проверки ──▶ GitHub
- закрыть панель (широкий) / назад (узкий) ──▶ [screen-board](screen-board.md)

## 6. Примерные данные

**`Repo_Content`, youndie/kachok.** PR: `#154 fix(engine): hand a stalled block to an idle peer` —
Human, `Failing`, 2 дня; `#152 chore(deps): update dependency gradle to v9.8.3` — Bot, `Ready`, 5
часов. Пункты, в этом порядке:

| Пункт | Метка | Приоритет | Заголовок |
|---|---|---|---|
| B-190 | `[?]` | P3 | v2 and hybrid torrents (BEP 52): SHA-256 piece layers |
| B-184 | `[~]` | P1 | An idle peer takes over a block a slow peer has sat on, before the endgame |
| B-176 | `[~]` | P2 | The UI moves to commonMain |
| B-187 | `[ ]` | P2 | The README says what an unsigned installer looks like on first start, platform by platform |
| B-178 | `[ ]` | P3 | The browser build of the UI is a client of the headless engine |
| B-179 | `[ ]` | P3 | Phase 3: Android and iOS targets on the engine |
| B-180 | `[ ]` | P3 | Phase 3: a headless server with a web face, installable on a box that is always on |

**`Repo_Red`, youndie/tacku.** Упавшие проверки «server / test» и «client / tck»; PR
`#77 chore(deps): update kompot to v0.40.1` — Bot, `Running`, 1 час; пункты — первые три из 12:
B-61 `[?]` P3 «Читать бэклог docs-as-code как доску»; B-58 `[~]` P1 «Прогнать корпус проверок для
клиента»; B-52 `[ ]` P2 «Пригласить человека: сегодня участник заводится только из шелла».

**`Repo_NoPlan`, youndie/proba.** PR `#49 chore(deps): update npm to v12` — Bot, `Ready`, 3 дня.

**`Repo_Quiet`, youndie/plakat-ui.** main зелёный, PR нет, 73 из 73.
