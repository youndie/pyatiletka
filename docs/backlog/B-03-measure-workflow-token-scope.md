---
id: B-03
title: "Замер: токен workflow читает чужие публичные репозитории"
status: open
priority: P1
size: S
stage: stage-0-risks
epic: feature-refresh
blocked_by: [B-02]
---

# B-03 — Замер: токен workflow читает чужие публичные репозитории

Публичность снимка держится двумя замками: правом токена и фильтром `collector` ([исследование D5](../research/research-architecture.md)). Что `GITHUB_TOKEN` scheduled workflow читает публичные репозитории владельца и не видит приватных — гипотеза.

- **Решение и причина.** Проверить из настоящего scheduled workflow, а не локально: права токена в Actions и на машине разные.
- Запасной путь, если токен видит лишнее или не видит нужного, — отдельный токен только на чтение публичного.
- Не входит: публикация снимка ([B-17](B-17-publish-page-and-hourly-snapshot.md)).

- AC: запрос из scheduled workflow вернул данные; приватных репозиториев в ответе нет.
- Якоря: `.github/workflows/` — workflow замера.
