---
id: B-07
title: "collector: запрос → Snapshot, публичный режим, CLI"
status: wip
priority: P1
size: M
stage: stage-1-data
epic: feature-refresh
blocked_by: [B-02, B-06]
---

# B-07 — collector: запрос → Snapshot, публичный режим, CLI

Сборщик — библиотека для десктопа и CLI для workflow ([collector](../services/collector.md)); устройство запроса зависит от замера [B-02](B-02-measure-full-graphql-query.md).

- **Решение и причина.** `--public-only` падает на не-`Public`, а не только отбрасывает ([исследование D5](../research/research-architecture.md)).
- Тесты — на поддельном GitHub, без сети.

- AC: `collect --public-only` пишет файл, который читает `core`; подмешанный приватный репозиторий роняет сбор.
- Якоря: `collector/src/main/kotlin/io/github/youndie/pyatiletka/collector/`, `collector/src/test/`.
