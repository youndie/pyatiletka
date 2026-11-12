---
id: feature-repo-detail
title: Карточка репозитория
type: feature
status: active
owner: unassigned
involved_services:
  - core
  - app
  - skin-plakat
  - skin-toon
client_entries:
  - screen-repo
api: []
tags: []
---

# Карточка репозитория

## 1. Обзор

Что именно красное, какие PR открыты и в каком они состоянии, какие пункты плана не закрыты.
Каждая строка ведёт на GitHub. Экран — [screen-repo](../screens/screen-repo.md); своего API нет,
данные берутся из того же снимка, что и доска ([feature-board](feature-board.md)).

## 2. Бизнес-правила

* Карточка строится из того же снимка, что доска: своей загрузки у неё нет.
* PR отсортированы: `Failing`, `Ready`, `Running`, `NoChecks`; внутри — старые выше.
* Пункты плана отсортированы: `Question`, `Wip`, `Open`; внутри — по приоритету.
* При `Red` упавшие проверки стоят первыми на карточке.
* Репозиторий без открытых PR и незакрытых пунктов при `Green` показывает состояние `Quiet`.

## 4. Якоря кода

Все пути — *целевые*: кода ещё нет.

| Модуль | Код |
|---|---|
| core | `core/src/commonMain/kotlin/io/github/youndie/pyatiletka/core/` — порядок PR и пунктов |
| app | `app/src/commonMain/kotlin/io/github/youndie/pyatiletka/app/` — состояние карточки |
| skin-plakat | `skin-plakat/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/plakat/` |
| skin-toon | `skin-toon/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/toon/` |

## 5. Сценарии

Все сценарии — *целевые*; данные — примерные данные §5a брифа.

### Scenario: A red main
* **Given:** `youndie/tacku` из примерных данных.
* **When:** открыта карточка.
* **Then:** сверху стоят две упавшие проверки (`server / test`, `client / tck`), ниже PR и пункты.

### Scenario: Nothing to do
* **Given:** `youndie/plakat-ui` — 73 из 73, PR нет, `Green`.
* **Then:** карточка показывает состояние `Quiet`.

### Scenario: No plan in the repository
* **Given:** `youndie/proba` — `Plan.None`.
* **Then:** раздел плана заменён пометкой «бэклога в репозитории нет», PR показаны как обычно.

## 6. Вне объёма

* Действия над PR и проверками — только ссылки на GitHub.
* Закрытые пункты плана: в снимок попадают только незакрытые.

## 7. Причуды

* На `Repo_Red` показаны первые три пункта из 12; сколько пунктов помещается до прокрутки, решает
  холст, а не правило.
