---
id: B-08
title: "app: источники снимка, кэш, таймер, токен, настройки"
status: done
priority: P1
size: M
stage: stage-1-data
epic: feature-refresh
blocked_by: [B-06, B-07]
---

# B-08 — app: источники снимка, кэш, таймер, токен, настройки

Доска всегда показывает последний удачный снимок и говорит, насколько он стар ([feature-refresh](../features/feature-refresh.md)); токен не хранится ([feature-settings](../features/feature-settings.md)).

- **Решение и причина.** Десктоп — `collector` в процессе, кэш снимка и `settings.json` в каталогах ОС; веб — `snapshot.json` относительно адреса страницы ([endpoint-snapshot](../api/endpoint-snapshot.md)).
- Не входит: экраны.

- AC: сценарии feature-refresh и feature-settings проходят на поддельном GitHub.
- Якоря: `app/src/desktopMain/kotlin/io/github/youndie/pyatiletka/app/`, `app/src/wasmJsMain/kotlin/io/github/youndie/pyatiletka/app/`.

## Итог (12.11.2026)

- `SnapshotStore` (общий код) держит последний удачный снимок через неудачи, считает устаревание по
  правилам feature-refresh и обновляет по таймеру; источники — сборщик в процессе (десктоп) и
  `snapshot.json` (веб); кэш — файл в каталоге кэшей ОС. Токен — `TokenFinder`, заново на каждую
  попытку; настройки — `SettingsModel` и `settings.json` без секретов.
- Тесты: `SnapshotStoreTest` и `PageSnapshotSourceTest` на desktop и wasmJs, `DesktopSourcesTest` и
  `SettingsModelTest` на поддельном GitHub (Ktor `MockEngine`); все сценарии feature-refresh и
  feature-settings проходят на уровне данных. Мутант «неудача выбрасывает прежний снимок» убит.
- В `collector` добавлены `ownerExists` и вид отказа `Network`.
- Не сделано здесь: подключение к окну и странице и состояния экранов — [B-09](B-09-app-screen-state-and-navigation.md);
  `window.fetch` проверится в браузере вместе с ними — до тех пор он покрыт только общим разбором ответа.
