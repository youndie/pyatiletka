---
id: B-01
title: "Каркас: пять модулей, конвенции sborka, оба кита и appframe в одной сборке"
status: done
priority: P1
size: M
stage: stage-0-risks
epic: feature-skins
---

# B-01 — Каркас: пять модулей, конвенции sborka, оба кита и appframe в одной сборке

Кода нет. Главный риск продукта — что plakat-ui, toon-ui и appframe не живут в одной сборке на двух таргетах (desktop и wasmJs): тогда рушится всё остальное. Пункт снимает этот риск раньше любой логики.

- **Решение и причина.** Пять модулей `core`, `collector`, `app`, `skin-plakat`, `skin-toon`, версии компилятора и CMP — из той же sborka `0.5.0.135`, что у китов ([исследование §1.2](../research/research-architecture.md)): находка по киту не должна оказаться расхождением рантайма.
- Здесь же решается, где живут типы состояний экранов: принято — в `core`, потому что скины зависят только от `core` ([исследование §3.1, п. 1](../research/research-architecture.md)).
- Здесь же проверяется гипотеза о кириллице в заголовочных шрифтах toon-ui на строке «ПЯТИЛЕТКА» ([исследование, риск 4](../research/research-architecture.md)); находка — issue в toon-ui.
- Не входит: модель, сбор, экраны — только слово на экране в каждом скине.

- AC: окно desktop в рамке appframe и wasm-страница показывают слово «ПЯТИЛЕТКА» в каждом скине; CI зелёный.
- Якоря: `settings.gradle.kts`, `core/build.gradle.kts`, `collector/build.gradle.kts`, `app/build.gradle.kts`, `skin-plakat/build.gradle.kts`, `skin-toon/build.gradle.kts`, `.github/workflows/`.

## Итог (12.11.2026)

- Пять модулей на sborka `0.5.0.135`; plakat-core `0.1.0.15`, toon-core `0.1.0.13` и appframe `0.3.0` —
  в одной сборке на `desktop` и `wasmJs`. `check` (тесты обоих таргетов, ktlint) и оба
  дистрибутива зелёные на WSL; CI — джоба `gradle` на `ubuntu-latest`.
- Слово «ПЯТИЛЕТКА» лежит в `core` (`Product.TITLE`), рисуют его оба скина: plakat — шапкой
  `PlakatMasthead`, toon — `ToonTitleCard`. Страница проверена в браузере глазами; окно appframe
  запущено на маке (`:app:run`), его содержимое держит `BothSkinsTest` — снимок окна сделать не
  удалось, доступ к окну не выдан.
- Гипотеза о кириллице в toon-ui подтверждена: гарнитуры её не держат, компоненты кита рисуют её
  резервными. Первая находка — плашка `ToonTitleCard` уже заголовка,
  [toon-ui#30](https://github.com/youndie/toon-ui/issues/30) ([учёт находок](../kit-findings.md)).
- Типы состояний экранов будут жить в `core`: граф модулей собран так, что скины видят только `core`
  и свой кит ([исследование §3.1, п. 1](../research/research-architecture.md)).
- Попутно: sborka требует `.editorconfig` в корне; скинам для wasm-тестов нужен
  `binaries.executable()` (CMP-4906); старый `runComposeUiTest` устарел и при `-Werror` не
  компилируется — тесты на `androidx.compose.ui.test.v2`.
