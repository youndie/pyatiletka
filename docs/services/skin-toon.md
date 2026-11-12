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

Модуль заведён в [B-01](../backlog/B-01-skeleton-five-modules.md) (`ToonTitleScreen` — название на
`ToonTitleCard`); экраны — *целевые* ([B-14](../backlog/B-14-toon-board.md),
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
| `skin-toon/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/toon/` | `ToonTitleScreen`; экраны — *целевые* |
| `skin-toon/build.gradle.kts` | `desktop` и `wasmJs`; зависит от `core` и toon-core, больше ни от чего |
| `skin-toon/src/desktopTest/` | фикстуры viddik и голдены `_Toon` (*целевой*) |

## 3. Как устроено

- **Первые находки известны заранее.** В toon-ui нет скелета, пустого состояния, счётчика и плаката
  ([исследование §1.3](../research/research-architecture.md)); доска использует все четыре. Каждый
  обход — issue в `youndie/toon-ui` и комментарий `// kit: <ссылка>`.
- **Кириллица.** Ни одна гарнитура toon-ui её не держит; компоненты кита рисуют её резервными
  гарнитурами — проверено в [B-01](../backlog/B-01-skeleton-five-modules.md) на «ПЯТИЛЕТКА»
  ([исследование §1.7](../research/research-architecture.md)). Свой текст через публичный
  `ToonTypeSpec.textStyle` резерва не получает — *гипотеза* до [B-14](../backlog/B-14-toon-board.md).
- **`ToonTitleCard`**: пустой `sub` — подзаголовка нет, `null` — демонстрационная строка кита;
  плашка фиксированной ширины, длинный заголовок выходит за край
  ([toon-ui#30](https://github.com/youndie/toon-ui/issues/30), [учёт находок](../kit-findings.md)).

## 4. Зависимости

| Вид | Имя | Зачем |
|---|---|---|
| Модуль | [core](core.md) | типы состояний |
| Библиотека | toon-core `0.1.0.13` из `reposilite.kotlin.website/snapshots` | компоненты и темы |
| Плагин | viddik `0.7.0` | голдены |
