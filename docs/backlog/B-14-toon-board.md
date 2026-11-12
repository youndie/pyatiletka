---
id: B-14
title: "toon: доска, все состояния, оба размера"
status: done
priority: P1
size: L
stage: stage-3-toon
epic: feature-board
blocked_by: [B-09]
---

# B-14 — toon: доска, все состояния, оба размера

Скин toon собирается в коде без холста ([skin-toon](../services/skin-toon.md)). Скелета, пустого состояния, счётчика и плаката в toon-ui нет ([исследование §1.3](../research/research-architecture.md)) — первые находки известны заранее.

- **Решение и причина.** Не ждать холста plakat: блокирует только состояние экранов ([B-09](B-09-app-screen-state-and-navigation.md)).

- AC: эталоны `Board_*_Toon` записаны; на каждый обход есть issue в toon-ui.
- Якоря: `skin-toon/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/toon/`, `skin-toon/src/desktopTest/`.

## Итог (12.11.2026)

- Доска в toon-ui — обе раскладки и все состояния из компонентов кита (исследование §1.9); 15
  голденов `Board_*_Toon` записаны на Linux, `check` зелёный.
- Находки по toon-ui, на каждый обход — issue и `// kit:`: [#31](https://github.com/youndie/toon-ui/issues/31)
  свой текст без кириллицы (гипотеза подтверждена), [#32](https://github.com/youndie/toon-ui/issues/32)
  `ToonCard`, [#33](https://github.com/youndie/toon-ui/issues/33) `ToonListItem`,
  [#34](https://github.com/youndie/toon-ui/issues/34) слоты диалога и шторки; [#30](https://github.com/youndie/toon-ui/issues/30)
  дополнен подстрокой. Все — в `docs/kit-findings.md`.
- Остаётся видимым без обхода: заголовки `ToonTitleCard` на узких «Пусто» и «Ничего не найдено»
  обрезаны плашкой (#30).
- Голдены toon записаны на раннере CI: текстура сцены рисуется на WSL и на `ubuntu-latest` с разницей
  в 1–4 ступени ([toon-ui#35](https://github.com/youndie/toon-ui/issues/35)); CI при провале проверки
  записывает голдены сам и выкладывает их артефактом.
- Окно и страница пока рисуют доску плакатом: переключение скинов — [B-16](B-16-live-skin-switch-and-url.md).
