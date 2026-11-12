---
id: B-07
title: "collector: запрос → Snapshot, публичный режим, CLI"
status: done
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

## Итог (12.11.2026)

- `Collector.collect` ходит запросом `portfolio.graphql` страницами по 25, отказ страницы — неудача
  всего снимка; `CollectFailure` различает отвергнутый токен, неизвестного владельца, отказ GitHub и
  утечку в публичном режиме. CLI `collector` пишет файл только после того, как `core` прочитал его.
- 11 тестов на поддельном GitHub (`CollectorTest`), в том числе приёмка: CLI пишет файл, который
  читает `core`; подмешанный приватный репозиторий роняет сбор, файл не пишется.
- Живой прогон с токеном владельца: 49 публичных репозиториев, снимок прочитан обратно
  ([исследование §1.5b](../research/research-architecture.md)). Решено попутно: архивные и форки в
  снимок не входят (D10). Найдено: планов без индекса не 8, а 15.
