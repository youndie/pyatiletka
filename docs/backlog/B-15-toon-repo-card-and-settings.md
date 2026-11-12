---
id: B-15
title: "toon: карточка и настройки"
status: done
priority: P1
size: M
stage: stage-3-toon
epic: feature-repo-detail
blocked_by: [B-14]
---

# B-15 — toon: карточка и настройки

Те же состояния карточки и настроек, что у plakat, в toon-ui.

- **Решение и причина.** Один пункт на два экрана: в toon нет паритета с холстом, остаётся раскладка и голдены.

- AC: эталоны `Repo_*_Toon`, `Settings_*_Toon` записаны.
- Якоря: `skin-toon/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/toon/`.

## Итог

- Карточка — `ToonRepoScreen`: на широком доска и панель 480 справа, на узком карточка одна;
  настройки — `ToonSettings`. Всё из компонентов кита, свои — только заголовки разделов, абзацы
  заголовков PR и пунктов и плашка команды.
- Примерные состояния — в `core` (`SampleRepoStates`, `SampleSettingsStates`), чтобы plakat в
  [B-12](B-12-plakat-repo-card.md) и [B-13](B-13-plakat-settings.md) рисовал те же; что настройки
  по умолчанию совпадают с моделью, держит `SettingsStatesTest.theFourStates`.
- 12 голденов `Repo_*_Toon` и `Settings_*_Toon` записаны на раннере CI
  ([toon-ui#35](https://github.com/youndie/toon-ui/issues/35)).
- Находки: [toon-ui#36](https://github.com/youndie/toon-ui/issues/36) (заголовок строки не
  переносится — обход), [toon-ui#37](https://github.com/youndie/toon-ui/issues/37) (нет выбранной
  строки — без обхода); «План выполнен» на `Repo_Quiet_*_Toon` обрезан плашкой
  ([toon-ui#30](https://github.com/youndie/toon-ui/issues/30)).
