---
id: feature-settings
title: Откуда и как собирать (только десктоп)
type: feature
status: active
owner: unassigned
involved_services:
  - collector
  - app
  - skin-plakat
  - skin-toon
client_entries:
  - screen-settings
  - screen-board
api: []
tags: []
---

# Откуда и как собирать (только десктоп)

## 1. Обзор

Чьи репозитории показывать, как часто обновлять и откуда взять токен. Экран —
[screen-settings](../screens/screen-settings.md); без токена доска показывает `NoToken` на
[screen-board](../screens/screen-board.md). На вебе настроек сбора нет — снимок готовый.

## 2. Бизнес-правила

* Токен берётся из `gh auth token` или из переменной `PYATILETKA_GITHUB_TOKEN`; на диск и в логи он
  не пишется, поля для ввода токена нет.
* Владельцы — список логинов пользователей и организаций; добавляемый логин проверяется запросом к
  GitHub.
* Без токена доска показывает `NoToken` со ссылкой в настройки.
* Период обновления — 5, 15 или 60 минут.

## 4. Якоря кода

Все пути — *целевые*: кода ещё нет.

| Модуль | Код |
|---|---|
| collector | `collector/src/main/kotlin/io/github/youndie/pyatiletka/collector/` — проверка логина |
| app | `app/src/desktopMain/kotlin/io/github/youndie/pyatiletka/app/` — источник токена, `settings.json` |
| skin-plakat | `skin-plakat/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/plakat/` |
| skin-toon | `skin-toon/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/toon/` |

## 5. Сценарии

Все сценарии — *целевые*.

### Scenario: No token
* **Given:** `gh` не установлен, переменная `PYATILETKA_GITHUB_TOKEN` пуста.
* **Then:** настройки показывают `TokenMissing` с двумя способами дать токен (`gh auth login`,
  переменная).

### Scenario: The token is rejected
* **Given:** GitHub отвечает `401`.
* **Then:** настройки показывают `TokenRejected`; старый снимок остаётся на доске.
* **Automated:** `CollectorTest.aRejectedTokenIsNamed` — сборщик различает `401`; состояние `TokenRejected` — B-08

### Scenario: No such owner
* **When:** введён логин `youndi`.
* **Then:** поле показывает ошибку `OwnerNotFound`; список владельцев не меняется.
* **Automated:** `CollectorTest.anUnknownOwnerIsNamed` — сборщик различает неизвестный логин; поле и ошибка `OwnerNotFound` — B-08

## 6. Вне объёма

* Ввод токена в приложении.
* Настройки сбора на вебе.

## 7. Причуды

* Список организаций владельца в репозиторий не попадает: его вписывают в настройках на
  машине, в `settings.json`, который не коммитится.
* Читает ли десктоп `GITHUB_TOKEN`, бриф не говорит; принято — нет, это переменная workflow
  ([исследование §3.1, п. 5](../research/research-architecture.md)).
