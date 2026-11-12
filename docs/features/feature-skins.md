---
id: feature-skins
title: Два скина на одном состоянии
type: feature
status: active
owner: unassigned
involved_services:
  - app
  - skin-plakat
  - skin-toon
client_entries:
  - screen-board
api: []
tags: []
---

# Два скина на одном состоянии

## 1. Обзор

Каждый экран существует в plakat-ui и в toon-ui. Переключение на ходу не теряет место:
выбранный репозиторий, фильтр и поиск остаются. Выбор — в состоянии `AppearanceSheet` на
[screen-board](../screens/screen-board.md). Ради этой фичи продукт и существует: всё, чего киту не
хватило, становится issue в репозитории кита ([исследование D3, D4](../research/research-architecture.md)).

## 2. Бизнес-правила

* Скины не знают друг о друге: `skin-plakat` зависит только от plakat-ui, `skin-toon` — только от
  toon-ui; общего слоя «компонентов поверх китов» нет.
* У каждого состояния каждого экрана есть эталон в обоих скинах.
* Темы: plakat — `Paper`, `BlackPaper`; toon — `InkGouacheLight`, `InkGouacheNight`, `BrightCel`.
* Выбор хранится локально; на вебе его задаёт и адрес: `?skin=toon&theme=night`.
* Обходное решение из-за кита разрешено только вместе с issue в репозитории кита и комментарием
  `// kit: <ссылка>` в месте обхода.

## 4. Якоря кода

Все пути — *целевые*: кода ещё нет.

| Модуль | Код |
|---|---|
| app | `app/src/commonMain/kotlin/io/github/youndie/pyatiletka/app/` — выбор скина и темы |
| app | `app/src/wasmJsMain/kotlin/io/github/youndie/pyatiletka/app/` — разбор адреса |
| skin-plakat | `skin-plakat/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/plakat/` |
| skin-toon | `skin-toon/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/toon/` |
| учёт находок | `docs/kit-findings.md` (*целевой*, [B-05](../backlog/B-05-kit-findings-and-renovate.md)) |

## 5. Сценарии

Все сценарии — *целевые*.

### Scenario: Switching skins on the fly
* **Given:** открыта карточка `youndie/kachok` в plakat.
* **When:** выбран скин toon.
* **Then:** открыта та же карточка; фильтр и поиск на доске прежние.

### Scenario: The address picks the skin
* **Given:** веб-страница открыта с `?skin=toon`.
* **Then:** первый кадр уже в toon.

## 6. Вне объёма

* Английская локализация — строки лежат в ресурсах, перевод отдельным пунктом позже.
* Третий кит.

## 7. Причуды

* Короткие значения `theme` в адресе (`night` в примере брифа) не сопоставлены с именами тем
  китов; таблица — в [B-16](../backlog/B-16-live-skin-switch-and-url.md)
  ([исследование §3.1, п. 4](../research/research-architecture.md)).
