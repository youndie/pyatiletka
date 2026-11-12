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

Доска — [B-14](../backlog/B-14-toon-board.md): обе раскладки, все состояния, голдены. Карточка и
настройки — *целевые* ([B-15](../backlog/B-15-toon-repo-card-and-settings.md)).

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
| `skin-toon/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/toon/ToonBoard.kt` | `ToonBoard`, `ToonBoardActions` — обе раскладки |
| `skin-toon/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/toon/ToonSkin.kt` | тема кита по теме доски |
| `skin-toon/build.gradle.kts` | `desktop` и `wasmJs`; зависит от `core` и toon-core, больше ни от чего |
| `skin-toon/src/desktopTest/kotlin/io/github/youndie/pyatiletka/skin/toon/BoardFixtures.kt` | фикстуры `Board_*_Toon` |
| `skin-toon/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/toon/Stage.kt` | `Stage` — сцена кита со сдвигом на ¾ px (обход [toon-ui#35](https://github.com/youndie/toon-ui/issues/35)) |
| `skin-toon/src/desktopTest/snapshots/` | голдены — запись (холста нет) |

## 3. Как устроено

- **Первые находки известны заранее.** В toon-ui нет скелета, пустого состояния, счётчика и плаката
  ([исследование §1.3](../research/research-architecture.md)); доска использует все четыре. Каждый
  обход — issue в `youndie/toon-ui` и комментарий `// kit: <ссылка>`.
- **Кириллица.** Ни одна гарнитура toon-ui её не держит; компоненты кита рисуют её резервными
  гарнитурами — проверено в [B-01](../backlog/B-01-skeleton-five-modules.md) на «ПЯТИЛЕТКА»
  ([исследование §1.7](../research/research-architecture.md)). Свой текст через публичный
  `ToonTypeSpec.textStyle` резерва не получает — подтверждено в [B-14](../backlog/B-14-toon-board.md)
  ([toon-ui#31](https://github.com/youndie/toon-ui/issues/31)); поэтому почти весь текст доски отдан
  компонентам кита.
- **Длинный русский текст не помещается в компоненты движка**: бейдж времени карточки, плашка
  заголовочной карточки, подстрока строки списка ([#30](https://github.com/youndie/toon-ui/issues/30),
  [#32](https://github.com/youndie/toon-ui/issues/32), [#33](https://github.com/youndie/toon-ui/issues/33)).
  На узком экране строки сокращены; заголовки «Репозиториев нет» и «Ничего не найдено» обрезаны
  плашкой — обхода нет.
- **`ToonTitleCard`**: пустой `sub` — подзаголовка нет, `null` — демонстрационная строка кита;
  плашка фиксированной ширины, длинный заголовок выходит за край
  ([toon-ui#30](https://github.com/youndie/toon-ui/issues/30), [учёт находок](../kit-findings.md)).

## 4. Зависимости

| Вид | Имя | Зачем |
|---|---|---|
| Модуль | [core](core.md) | типы состояний |
| Библиотека | toon-core `0.1.0.13` из `reposilite.kotlin.website/snapshots` | компоненты и темы |
| Плагин | viddik `0.7.0` | голдены |
- **Сцена — через `Stage`, не через `ToonStage` напрямую.** Кит выбирает текстуру ближайшим текселом
  ровно на границе двух текселов, и под перспективой viddik Intel и AMD рисовали её по-разному:
  голдены падали на раннерах Intel ([B-21](../backlog/B-21-toon-goldens-depend-on-runner-cpu.md),
  [toon-ui#35](https://github.com/youndie/toon-ui/issues/35)). `Stage` сдвигает сцену на ¾ px, и
  голдены одинаковы на WSL и на любом раннере. Новый экран toon берёт `Stage`.

