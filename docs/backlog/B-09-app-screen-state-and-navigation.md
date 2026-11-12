---
id: B-09
title: "app: состояние и навигация экранов Board, Repo, Settings"
status: done
priority: P1
size: M
stage: stage-1-data
epic: feature-board
blocked_by: [B-08]
---

# B-09 — app: состояние и навигация экранов Board, Repo, Settings

Скины только рисуют; всё состояние экранов и навигация — в `app` поверх типов из `core`. Пока это не покрыто тестами без UI, каждый скин будет изобретать его заново.

- **Решение и причина.** Каждое состояние §1 экранов [screen-board](../screens/screen-board.md), [screen-repo](../screens/screen-repo.md), [screen-settings](../screens/screen-settings.md) получается из примерных данных в тесте — эти же данные потом станут фикстурами эталонов.

- AC: каждое состояние экранов достижимо в тесте из примерных данных.
- Якоря: `app/src/commonMain/kotlin/io/github/youndie/pyatiletka/app/`, `app/src/commonTest/`.

## Итог (12.11.2026)

- Типы состояний трёх экранов — в `core/screen` (`BoardScreenState`, `RepoScreenState`,
  `SettingsScreenState` с данными для скинов); получение — в `app`: `boardUiState` и `BoardModel`
  (снимок + фильтр, поиск, шторка вида, открытая карточка), `repoUiState`, `SettingsState.ui`;
  навигация — `Route` и `Navigator`, карточка не маршрут.
- Каждое состояние §1 трёх экранов достижимо в тесте из примерных данных: `ScreenStatesTest` (доска и
  карточка, desktop и wasmJs) и `SettingsStatesTest`. Мутант «нет токена — это просто ошибка» убит.
- Страница подключена: читает `snapshot.json` и пишет смену экрана доски в консоль. Проверено в
  браузере на дистрибутиве: с живым снимком — `Content`, 49 строк, 95 %; без файла — `Error`,
  `NotPublished`. Это закрывает непроверенный в B-08 `window.fetch`.
- Не сделано здесь: окно десктопа не подключено к `DesktopGraph` — сбор без экранов только тратил бы
  лимит; подключение — с экранами скина ([B-11](B-11-plakat-board.md)).
