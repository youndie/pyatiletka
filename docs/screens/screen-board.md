---
id: screen-board
title: Доска (Board)
type: client_screen
platform: [desktop, web]
status: active
entry:
  desktop: "стартовый экран"
  web: "/"
parent_feature: feature-board
calls_api:
  - endpoint-snapshot
source: app/src/commonMain/kotlin/io/github/youndie/pyatiletka/app
design:
  canvas: "Claude Design, проект plakat-ui, страница Board от 12.11.2026; исходники артбордов — design/canvas/Page_Board.dc.html, заметки — design/canvas/canvas.json"
  references: skin-plakat/src/desktopTest/snapshots/design
  states:
    Loading: Board_Loading
    Empty: Board_Empty
    Content: Board_Content
    NoMatches: Board_NoMatches
    Stale: Board_Stale
    Error: Board_Error
    NoToken: Board_NoToken
    AppearanceSheet: Board_AppearanceSheet
---

# Экран: доска

Стартовый экран на обеих платформах. Фича — [feature-board](../features/feature-board.md); здесь же
живут состояния [feature-refresh](../features/feature-refresh.md) (`Loading`, `Stale`, `Error`),
[feature-skins](../features/feature-skins.md) (`AppearanceSheet`) и
[feature-settings](../features/feature-settings.md) (`NoToken`). На вебе читает
[endpoint-snapshot](../api/endpoint-snapshot.md), на десктопе API нет.

Состояния экрана и их получение — в коде ([B-09](../backlog/B-09-app-screen-state-and-navigation.md)); раскладка plakat — [B-11](../backlog/B-11-plakat-board.md) (вид кита принят владельцем: холст — эталон раскладки, остаток паритета разобран по областям), раскладка toon — [B-14](../backlog/B-14-toon-board.md) (голдены `Board_*_Toon`).

## 0a. Якоря кода

| Что | Файл |
|---|---|
| Состояние и навигация | `app/src/commonMain/kotlin/io/github/youndie/pyatiletka/app/BoardModel.kt` — `boardUiState`, `BoardModel`; `app/src/commonMain/kotlin/io/github/youndie/pyatiletka/app/Navigation.kt` |
| Тест состояний | `app/src/commonTest/kotlin/io/github/youndie/pyatiletka/app/ScreenStatesTest.kt` |
| Типы состояний | `core/src/commonMain/kotlin/io/github/youndie/pyatiletka/core/screen/BoardUi.kt` — `BoardScreenState`, `BoardUiState` |
| Раскладка plakat | `skin-plakat/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/plakat/PlakatBoard.kt` (широкая), `skin-plakat/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/plakat/PhoneBoard.kt` (узкая), `skin-plakat/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/plakat/BoardBlocks.kt` |
| Фикстуры и голдены plakat | `skin-plakat/src/desktopTest/kotlin/io/github/youndie/pyatiletka/skin/plakat/BoardFixtures.kt`, `skin-plakat/src/desktopTest/snapshots/` |
| Окно и страница | `app/src/commonMain/kotlin/io/github/youndie/pyatiletka/app/Shell.kt` |
| Раскладка toon | `skin-toon/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/toon/ToonBoard.kt` |
| Голдены toon | `skin-toon/src/desktopTest/snapshots/` |
| Эталоны холста | `skin-plakat/src/desktopTest/snapshots/design/` |

## 0. Вход и видимость

- **Вход:** desktop — стартовый экран; web — `/`.
- **Размеры:** 1280×800; 390×844 (`_Phone`).

- **Между артбордами** (plakat, [B-22](../backlog/B-22-summary-counters-wrap-at-mid-width.md)): от 720
  раскладка широкая, но красная плашка сводки 640 — только от 1200. Ниже — сжатая сводка с плашкой 248,
  как рядом с карточкой, узкие колонки таблицы и шапка без строки владельцев. Число счётчика всегда в
  одну строку и уменьшается, если не помещается. Голдены `Board_Content_Width800` и
  `Board_Content_Width1024` — без эталона холста, с четырёхзначными счётчиками.
- **Уже артборда 390** (plakat, [B-25](../backlog/B-25-plakat-phone-summary-clipped.md)): четыре чипа
  фильтра занимают около 350 dp; на более узком экране последний переносится на вторую строку, а не
  обрезается многоточием. Голден `Board_Content_Width360` — без эталона холста, с четырёхзначными
  счётчиками.

## 1. Состояния экрана

- [ ] **Loading:** шапка с названием, сводка и восемь строк скелетом.
- [ ] **Empty:** у заданных владельцев нет репозиториев: пустое состояние со ссылкой в настройки.
- [ ] **Content:** шапка, сводка (95 %, 1 красный, 12 PR, 7 ждут), фильтр, поиск, 12 строк
  примерных данных.
- [ ] **NoMatches:** сводка на месте, в поиске `zzz`, вместо строк — «ничего не найдено» и сброс.
- [ ] **Stale:** как Content, под шапкой полоса «снимок от 11 ноября, 21:17 — нет связи с GitHub» и
  кнопка повтора.
- [ ] **Error:** снимка нет: причина и кнопка повтора на весь экран.
- [ ] **NoToken:** только desktop, только 1280×800: «нет токена GitHub» и кнопка «Настройки».
- [ ] **AppearanceSheet:** поверх Content выбор скина (Плакат, Мультфильм) и темы; на широком —
  диалог, на узком — нижняя шторка.

Состояние одно на кадр (`BoardScreenState`), остальное едет в `BoardUiState` рядом: открытая
шторка рисуется поверх того, что под ней; поиск без результатов на устаревшем снимке остаётся
`NoMatches`, а полоса устаревания — в `stale`. Порядок выбора — `Empty`, `AppearanceSheet`,
`NoMatches`, `Stale`, `Content` (`boardUiState`). Без токена и без снимка — `NoToken`; без токена,
но со снимком в кэше — `Stale` с причиной «нет токена».

### 1.1 Артборды и эталоны

`_Phone` и `_Dark` — не состояния, а варианты: имя `<Screen>_<State>[_Phone][_Dark]`. В блоке
`design` — широкие светлые артборды; все 17 артбордов страницы Board:

| Состояние | 1280×800 | 390×844 | Тёмный | toon (голден, без артборда) |
|---|---|---|---|---|
| Loading | `Board_Loading` | `Board_Loading_Phone` | — | `Board_Loading_Toon`, `Board_Loading_Phone_Toon` |
| Empty | `Board_Empty` | `Board_Empty_Phone` | — | `Board_Empty_Toon`, `Board_Empty_Phone_Toon` |
| Content | `Board_Content` | `Board_Content_Phone` | `Board_Content_Dark`, `Board_Content_Phone_Dark` | `Board_Content_Toon`, `Board_Content_Phone_Toon` |
| NoMatches | `Board_NoMatches` | `Board_NoMatches_Phone` | — | `Board_NoMatches_Toon`, `Board_NoMatches_Phone_Toon` |
| Stale | `Board_Stale` | `Board_Stale_Phone` | — | `Board_Stale_Toon`, `Board_Stale_Phone_Toon` |
| Error | `Board_Error` | `Board_Error_Phone` | — | `Board_Error_Toon`, `Board_Error_Phone_Toon` |
| NoToken | `Board_NoToken` | — | — | `Board_NoToken_Toon` |
| AppearanceSheet | `Board_AppearanceSheet` | `Board_AppearanceSheet_Phone` | — | `Board_AppearanceSheet_Toon`, `Board_AppearanceSheet_Phone_Toon` |

## 2. Интеграция с API

| Вызов | Контракт | Документ |
| :--- | :--- | :--- |
| `GET snapshot.json` (web) | `Snapshot` | [endpoint-snapshot](../api/endpoint-snapshot.md) |
| сбор в процессе (desktop) | `Snapshot` | [collector](../services/collector.md) |

## 3. Инициализация

| Вызов | Условие | Результат |
| :--- | :--- | :--- |
| чтение кэша снимка | desktop, при запуске | есть снимок — Content/Stale сразу |
| сбор снимка | desktop, при запуске и по таймеру | новый снимок — Content |
| `GET snapshot.json` | web, при открытии | Content |

| Вызов | Случай | Состояние |
| :--- | :--- | :--- |
| сбор / `GET` | удача, репозитории есть | **Content** |
| сбор / `GET` | удача, репозиториев нет | **Empty** |
| сбор / `GET` | неудача, есть прежний снимок | **Stale** с причиной |
| сбор / `GET` | неудача, снимка нет; `404`; неизвестная `schemaVersion` | **Error** |
| сбор | desktop, токена нет | **NoToken** |

## 4. Элементы сверху вниз

### 4.1. Шапка

- Название, кнопка обновления (на узком — потянуть вниз), меню: «Вид» → `AppearanceSheet`,
  «Настройки» → [screen-settings](screen-settings.md) (только desktop).

### 4.2. Сводка

- Выполнение портфеля, красные main, открытые PR, ждут владельца — правила в
  [feature-board](../features/feature-board.md). Главное место для счётчика и плаката («ПЛАН
  ВЫПОЛНЕН НА 95 %»). Не реагирует на фильтр и поиск.

### 4.3. Фильтр и поиск

- Фильтр: «Все», «Красные», «С PR», «Ждут меня»; поиск по имени без учёта регистра.

### 4.4. Строки репозиториев

- `репозиторий — main — PR — план — ждут`. Строка читается на 12 строках подряд и не превращается
  в плакат каждая.
- **По нажатию:** [screen-repo](screen-repo.md).

### 4.5. Что добавил холст

Из заметок страницы Board в `design/canvas/canvas.json` — то, чего не видно на картинке:

- фильтр — `PlakatSegmentedButton` на широком, `PlakatChip` на узком; выбран ровно один;
- ~~на широком прокручивается только список строк: шапка, сводка, фильтр, поиск и заголовок таблицы
  стоят~~ — **отступление от холста, решение владельца 13.11.2026 ([B-23](../backlog/B-23-scroll-whole-board.md)):**
  сводка с фильтром занимали около 300 dp над списком, поэтому на широком, как и на узком, прокручивается
  всё под шапкой — сводка, фильтр, поиск, заголовок таблицы и строки. На месте остаются шапка и полоса
  `Stale`: предупреждение должно быть видно. Так же в скине toon: стоят верхняя панель и снэкбар `Stale`.
  На узком обновление — `PlakatPullToRefreshBox`;
- имя репозитория — в одну строку с многоточием; счётчики и план не обрезаются;
- кнопка ↻ в шапке широкого берёт снимок сейчас; меню ⋮ — `PlakatMenu` («Вид», «Настройки»);
- `NoMatches`: «Сбросить поиск» и крестик в поле очищают запрос, фильтр не сбрасывается;
- `Stale` и `Error`: «Повторить» берёт снимок заново; в `Error` на время запроса на месте кнопки —
  `PlakatCircularProgress`; при успехе полоса `Stale` уходит;
- `Empty` ведёт в настройки на блок «Владельцы», `NoToken` — на блок «Токен GitHub»;
- `AppearanceSheet`: выбор применяется сразу; «Готово» и нажатие на растр закрывают (на узком — ещё
  свайп вниз);
- `Loading`: заголовки колонок известны заранее и не скелетом; фильтр и поиск недоступны до снимка.

## 5. Навигация

- строка ──▶ [screen-repo](screen-repo.md)
- меню «Настройки» ──▶ [screen-settings](screen-settings.md) (desktop)
- `NoToken` «Настройки» ──▶ [screen-settings](screen-settings.md)
- `Empty` ссылка ──▶ [screen-settings](screen-settings.md) (desktop)

## 6. Примерные данные

Значения фикстур, а не живое чтение; только публичные репозитории (проверено 12.11.2026).
Сегодня — 2026-11-12, снимок взят в 09:17. Сводка: 12 репозиториев; выполнение 698 из 728 — 95 %;
красных 1; PR 12; ждут владельца 7. Для `Stale` — те же строки с `takenAt` 11 ноября, 21:17.

| # | Репозиторий | main | PR | План | Ждут |
|---|---|---|---|---|---|
| 1 | youndie/tacku | Red | 1 | 48 из 60 — 80 % | 1 |
| 2 | youndie/kachok | Green | 2 (1 `Failing`) | 177 из 184 — 96 % | 1 |
| 3 | youndie/appframe | Green | 0 | 2 из 7 — 28 % | 3 |
| 4 | youndie/katcher | Green | 0 | 3 из 5 — 60 % | 2 |
| 5 | youndie/sborka | Green | 6 | 34 из 35 — 97 % | 0 |
| 6 | youndie/bochka | Running | 1 | `Flat` — «план одним файлом» | — |
| 7 | youndie/toon-ui | Green | 1 | 66 из 66 — 100 % | 0 |
| 8 | youndie/proba | Green | 1 | `None` — «бэклога нет» | — |
| 9 | youndie/haul | Green | 0 | 179 из 180 — 99 % | 0 |
| 10 | youndie/zavarnik | Green | 0 | 33 из 35 — 94 % | 0 |
| 11 | youndie/kompot | Green | 0 | 83 из 83 — 100 % | 0 |
| 12 | youndie/plakat-ui | Green | 0 | 73 из 73 — 100 % | 0 |

Порядок строк выходит из правил [feature-board](../features/feature-board.md), только если фикстура
задаёт `pushedAt` по убыванию внутри групп в порядке таблицы.
