---
id: B-01
title: "Каркас: пять модулей, конвенции sborka, оба кита и appframe в одной сборке"
status: open
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
