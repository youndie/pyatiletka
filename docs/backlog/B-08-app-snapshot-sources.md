---
id: B-08
title: "app: источники снимка, кэш, таймер, токен, настройки"
status: open
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
