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
  не пишется, поля для ввода токена нет. Сначала спрашивается источник, выбранный в настройках, потом
  второй — машина, где есть хотя бы один, работает (`TokenFinder`).
* Владельцы — список логинов пользователей и организаций; добавляемый логин проверяется запросом к
  GitHub.
* Без токена доска показывает `NoToken` со ссылкой в настройки.
* Период обновления — 5, 15 или 60 минут.

## 4. Якоря кода

Данные и правила — в коде ([B-08](../backlog/B-08-app-snapshot-sources.md)); экраны — *целевые*.

| Модуль | Код |
|---|---|
| collector | `collector/src/main/kotlin/io/github/youndie/pyatiletka/collector/Collector.kt` — `ownerExists` |
| app | `app/src/desktopMain/kotlin/io/github/youndie/pyatiletka/app/desktop/SettingsModel.kt` — правила настроек |
| app | `app/src/desktopMain/kotlin/io/github/youndie/pyatiletka/app/desktop/Token.kt` — `TokenFinder` |
| app | `app/src/desktopMain/kotlin/io/github/youndie/pyatiletka/app/desktop/Files.kt` — `settings.json`, каталоги ОС |
| skin-plakat | `skin-plakat/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/plakat/` |
| skin-toon | `skin-toon/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/toon/` |

## 5. Сценарии

Сценарии проверены на уровне данных (строки `Automated`); на экране — *целевые*, до экранов.

### Scenario: No token
* **Given:** `gh` не установлен, переменная `PYATILETKA_GITHUB_TOKEN` пуста.
* **Then:** настройки показывают `TokenMissing` с двумя способами дать токен (`gh auth login`,
  переменная).
* **Automated:** `SettingsModelTest.noTokenAnywhereIsTokenMissing`, `DesktopSourcesTest.withoutATokenNothingIsAsked` — модель; экран `TokenMissing` — B-13

### Scenario: The token is rejected
* **Given:** GitHub отвечает `401`.
* **Then:** настройки показывают `TokenRejected`; старый снимок остаётся на доске.
* **Automated:** `CollectorTest.aRejectedTokenIsNamed`, `DesktopSourcesTest.aRejectedTokenIsNamed`, `SettingsModelTest.aRejectedTokenWhileCheckingALoginIsTokenRejected` — модель; экран — B-13

### Scenario: No such owner
* **When:** введён логин `youndi`.
* **Then:** поле показывает ошибку `OwnerNotFound`; список владельцев не меняется.
* **Automated:** `SettingsModelTest.anUnknownLoginLeavesTheListAlone`, `CollectorTest.anOwnerIsCheckedBeforeItJoinsTheList` — модель; поле и ошибка на экране — B-13

## 6. Вне объёма

* Ввод токена в приложении.
* Настройки сбора на вебе.

## 7. Причуды

* Список организаций владельца в репозиторий не попадает: его вписывают в настройках на
  машине, в `settings.json`, который не коммитится.
* Читает ли десктоп `GITHUB_TOKEN`, бриф не говорит; принято — нет, это переменная workflow
  ([исследование §3.1, п. 5](../research/research-architecture.md)).
