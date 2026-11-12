---
id: B-16
title: "Смена скина на ходу и по адресу"
status: open
priority: P1
size: S
stage: stage-3-toon
epic: feature-skins
blocked_by: [B-11, B-14]
---

# B-16 — Смена скина на ходу и по адресу

Переключение скина не теряет места ([feature-skins](../features/feature-skins.md)); на вебе скин и тему задаёт адрес.

- **Решение и причина.** Здесь же пишется таблица коротких значений `theme` в адресе → тема кита ([исследование §3.1, п. 4](../research/research-architecture.md)): пример брифа `theme=night` с именами тем не сопоставлен.

- AC: сценарии feature-skins проходят; место на экране не теряется.
- Якоря: `app/src/commonMain/kotlin/io/github/youndie/pyatiletka/app/`, `app/src/wasmJsMain/kotlin/io/github/youndie/pyatiletka/app/`.
