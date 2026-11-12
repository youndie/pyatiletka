---
id: skin-toon
title: skin-toon — экраны на toon-ui
type: service
module: skin-toon
tech_stack: [Compose Multiplatform, toon-core, viddik]
owner: unassigned
depends_on:
  - core
  - toon-core
publishes: []
---

# skin-toon

Модуля ещё нет, пути — *целевые* ([B-14](../backlog/B-14-toon-board.md),
[B-15](../backlog/B-15-toon-repo-card-and-settings.md)).

## 1. Ответственность

Те же экраны и состояния, что у [skin-plakat](skin-plakat.md), на компонентах toon-ui; темы
`InkGouacheLight`, `InkGouacheNight`, `BrightCel`. Голдены `<Screen>_<State>[_Phone]_Toon`.

**Не делает:** не знает про plakat-ui; не держит состояния; холста у него нет — раскладка
собирается в коде ([исследование D6](../research/research-architecture.md)).

## 2. Контракты

- Вход — типы состояний экранов из [core](core.md).

## 2a. Якоря кода

| Файл | Что там |
|---|---|
| `skin-toon/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/toon/` | экраны (*целевой*) |
| `skin-toon/src/desktopTest/` | фикстуры viddik и голдены `_Toon` (*целевой*) |

## 3. Как устроено

- **Первые находки известны заранее.** В toon-ui нет скелета, пустого состояния, счётчика и плаката
  ([исследование §1.3](../research/research-architecture.md)); доска использует все четыре. Каждый
  обход — issue в `youndie/toon-ui` и комментарий `// kit: <ссылка>`.
- **Кириллица в заголовках** — *гипотеза*: заголовочные шрифты toon-ui её не держат, текст уйдёт на
  шрифты-спутники; проверка в [B-01](../backlog/B-01-skeleton-five-modules.md) на строке
  «ПЯТИЛЕТКА».

## 4. Зависимости

| Вид | Имя | Зачем |
|---|---|---|
| Модуль | [core](core.md) | типы состояний |
| Библиотека | toon-core `0.1.0.13` из `reposilite.kotlin.website/snapshots` | компоненты и темы |
| Плагин | viddik `0.7.0` | голдены |
