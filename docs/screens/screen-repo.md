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

Состояния экрана и их получение — в коде ([B-09](../backlog/B-09-app-screen-state-and-navigation.md)); раскладки скинов — *целевые*.

## 0a. Якоря кода

| Что | Файл |
|---|---|
| Состояние и навигация | `app/src/commonMain/kotlin/io/github/youndie/pyatiletka/app/BoardModel.kt` — `BoardModel.card`, `open`, `closeCard` |
| Тест состояний | `app/src/commonTest/kotlin/io/github/youndie/pyatiletka/app/ScreenStatesTest.kt` — `theFourCards` |
| Типы состояний | `core/src/commonMain/kotlin/io/github/youndie/pyatiletka/core/screen/RepoUi.kt` — `RepoScreenState`, `repoUiState` |
| Раскладка plakat | `skin-plakat/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/plakat/` |
| Раскладка toon | `skin-toon/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/toon/` |
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
