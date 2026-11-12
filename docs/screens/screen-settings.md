---
id: screen-settings
title: Настройки (Settings)
type: client_screen
platform: [desktop]
status: active
entry:
  desktop: "меню шапки → «Настройки»"
parent_feature: feature-settings
calls_api: []
source: app/src/desktopMain/kotlin/io/github/youndie/pyatiletka/app
design:
  canvas: "Claude Design, проект plakat-ui, страница Settings от 12.11.2026; исходники артбордов — design/canvas/Page_Settings.dc.html, заметки — design/canvas/canvas.json"
  references: skin-plakat/src/desktopTest/snapshots/design
  states:
    Content: Settings_Content
    TokenMissing: Settings_TokenMissing
    TokenRejected: Settings_TokenRejected
    OwnerNotFound: Settings_OwnerNotFound
---

# Экран: настройки

Только десктоп. Фича — [feature-settings](../features/feature-settings.md). API нет; проверка
логина владельца идёт через [collector](../services/collector.md).

Состояния экрана и их получение — в коде ([B-09](../backlog/B-09-app-screen-state-and-navigation.md)); раскладка toon — в коде и голденах ([B-15](../backlog/B-15-toon-repo-card-and-settings.md)), раскладка plakat — в коде и голденах ([B-13](../backlog/B-13-plakat-settings.md)).

## 0a. Якоря кода

| Что | Файл |
|---|---|
| Состояние, `settings.json`, источник токена | `app/src/desktopMain/kotlin/io/github/youndie/pyatiletka/app/desktop/SettingsModel.kt` — `SettingsState.ui` |
| Типы состояний | `core/src/commonMain/kotlin/io/github/youndie/pyatiletka/core/screen/SettingsUi.kt` — `SettingsScreenState`, `SettingsUiState` |
| Тест состояний | `app/src/desktopTest/kotlin/io/github/youndie/pyatiletka/app/desktop/SettingsModelTest.kt` — `SettingsStatesTest` |
| Проверка логина | `collector/src/main/kotlin/io/github/youndie/pyatiletka/collector/` |
| Раскладка plakat | `skin-plakat/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/plakat/PlakatSettings.kt` — `PlakatSettings`, `SettingsActions` |
| В окне десктопа | `app/src/desktopMain/kotlin/io/github/youndie/pyatiletka/app/desktop/DesktopSettings.kt` — поверх `SettingsModel`; переход — `Navigator` в `app/src/desktopMain/kotlin/io/github/youndie/pyatiletka/app/Main.kt` |
| Фикстуры plakat | `skin-plakat/src/desktopTest/kotlin/io/github/youndie/pyatiletka/skin/plakat/SettingsFixtures.kt` |
| Раскладка toon | `skin-toon/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/toon/ToonSettings.kt` — `ToonSettings`, `ToonSettingsActions` |
| Примерные состояния | `core/src/commonMain/kotlin/io/github/youndie/pyatiletka/core/sample/SampleCardStates.kt` — `SampleSettingsStates` |
| Фикстуры toon | `skin-toon/src/desktopTest/kotlin/io/github/youndie/pyatiletka/skin/toon/SettingsFixtures.kt` |

## 0. Вход и видимость

- **Вход:** меню шапки доски → «Настройки»; кнопка «Настройки» в `NoToken`.
- **Размеры:** только 1280×800. Тёмных вариантов нет.

## 1. Состояния экрана

- [ ] **Content:** токен: «найден через gh»; владельцы: `youndie`; период: 15 минут.
- [ ] **TokenMissing:** токена нет: два способа его дать (`gh auth login`, переменная
  `PYATILETKA_GITHUB_TOKEN`), остальное недоступно.
- [ ] **TokenRejected:** «GitHub отверг токен (401)», остальное как в Content.
- [ ] **OwnerNotFound:** в поле владельца `youndi` и ошибка под полем.

Эталоны toon: `Settings_<State>_Toon` для каждого из четырёх.

## 2. Интеграция с API

Нет своего API. Добавляемый логин проверяется запросом к GitHub через `collector`.

## 3. Инициализация

Читает `settings.json` и пробует источник токена.

## 4. Элементы сверху вниз

### 4.1. Источник токена

- Выбор из двух: `gh auth token` или `PYATILETKA_GITHUB_TOKEN`. Поля для ввода самого токена нет.

### 4.2. Владельцы

- Поле добавления и список с удалением. Неизвестный логин — `OwnerNotFound`, список не меняется.

### 4.3. Период

- 5, 15 или 60 минут.

### 4.4. Что добавил холст

Из заметок страницы Settings в `design/canvas/canvas.json`:

- `TokenMissing`: выбранный способ показывает команду или имя переменной; «Проверить снова» ищет
  токен заново; владельцы и период недоступны до токена;
- `TokenRejected`: остальное доступно; «Проверить снова» повторяет запрос к GitHub;
- владелец добавляется полем по Enter или «Добавить», убирается ✕ в строке; ошибка
  `OwnerNotFound` появляется после «Добавить» и уходит при правке поля;
- команда и имя переменной набраны Golos Text 600 на плашке: моноширинного шрифта в ките нет.

### 4.5. Как это сделано в plakat

По холсту, с видом компонентов кита ([B-11](../backlog/B-11-plakat-board.md), [B-13](../backlog/B-13-plakat-settings.md)):

- раздел — название и пояснение слева (320), управление справа (560), линия 1 dp под разделом;
- способ получить токен — `PlakatRadioButton`, команда или переменная — на бетонной плашке; «Проверить
  снова» — `PlakatButton` в `TokenMissing` и `PlakatOutlinedButton` в `TokenRejected`; полоса
  «Внимание» в `TokenMissing` нарисована сама ([plakat-ui#25](https://github.com/youndie/plakat-ui/issues/25));
- владелец — строка с ✕ (`PlakatIconButton`), поле — `PlakatTextField` с ошибкой кита под ним,
  «Добавить» — `PlakatOutlinedButton`; период — `PlakatSegmentedButton`; в `TokenMissing` всё это
  недоступно;
- **без числа репозиториев у владельца.** На холсте у строки «12 репозиториев», а в
  `SettingsUiState` такого числа нет. Чтобы оно появилось, нужен счётчик по владельцу в состоянии, и
  это за рамками пункта;
- свои слова кита (префикс ошибки поля, подписи загрузки) идут по-русски через `PlakatStrings` в
  `PlakatSkin` — по умолчанию у кита английские.

### 4.6. Как это сделано в toon

Из компонентов кита ([B-15](../backlog/B-15-toon-repo-card-and-settings.md)): состояние токена —
`ToonListItem` с галочкой или предупреждением, способ — `ToonRadioGroup`, «Проверить снова» —
`ToonButton` `Outlined` (в `TokenMissing` и `TokenRejected`), владельцы — `ToonTextField` с ошибкой
под полем, `ToonButton` «Добавить» и `ToonChip` `Input` с ✕ на каждого, период — `ToonSegmented`.
В `TokenMissing` поле, кнопка, чипы и период недоступны. Команда или переменная — на плашке
цветов кита стилем `Title`: моноширинного шрифта нет и в toon. Заголовки разделов идут мимо
кириллического резерва кита ([toon-ui#31](https://github.com/youndie/toon-ui/issues/31)).

## 5. Навигация

- назад ──▶ [screen-board](screen-board.md)
