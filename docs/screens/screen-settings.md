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
  canvas: "Claude Design, проект plakat-ui, страница Settings — ещё не нарисован (B-10)"
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

Кода ещё нет: все пути — *целевые*, состояния — задуманные.

## 0a. Якоря кода

| Что | Файл |
|---|---|
| Состояние, `settings.json`, источник токена | `app/src/desktopMain/kotlin/io/github/youndie/pyatiletka/app/` |
| Проверка логина | `collector/src/main/kotlin/io/github/youndie/pyatiletka/collector/` |
| Раскладка plakat | `skin-plakat/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/plakat/` |
| Раскладка toon | `skin-toon/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/toon/` |

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

## 5. Навигация

- назад ──▶ [screen-board](screen-board.md)
