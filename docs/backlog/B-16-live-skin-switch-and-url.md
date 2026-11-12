---
id: B-16
title: "Смена скина на ходу и по адресу"
status: done
priority: P1
size: S
stage: stage-3-toon
epic: feature-skins
blocked_by: [B-11, B-14]
---

# B-16 — Смена скина на ходу и по адресу

Переключение скина не теряет места ([feature-skins](../features/feature-skins.md)); на вебе скин и тему задаёт адрес.

- **Решение и причина.** Здесь же пишется таблица коротких значений `theme` в адресе → тема кита ([исследование §3.1, п. 4](../research/research-architecture.md)): пример брифа `theme=night` с именами тем не сопоставлен.

- AC: сценарии feature-skins проходят; место на экране не теряется.
- Якоря: `app/src/commonMain/kotlin/io/github/youndie/pyatiletka/app/`, `app/src/wasmJsMain/kotlin/io/github/youndie/pyatiletka/app/`.

## Итог (12.11.2026)

- `Shell` рисует доску и карточку в ките, который назван видом. На десктопе в нём же рисуются
  настройки (`DesktopSettings`). Место на экране — открытая карточка, фильтр, запрос — хранится в
  `BoardModel`, а не в скине, поэтому при смене кита не теряется: `ShellTest.switchingSkinsKeepsThePlace`.
  Мутация: с `Shell` из `main` (toon не рисуется) тест красный.
- Адрес: `appearanceOfQuery` по ключам тем, которые уже были в `Theme`. Страница строит доску сразу с
  этим видом, поэтому первый кадр уже в выбранном ките. Таблица значений записана в причудах
  feature-skins, исследование §3.1 п. 4 закрыт.
- Выбор сохраняется: на десктопе — в `settings.json` (`skin`, `theme`, поля там уже были), на
  вебе — в `localStorage`. Адрес главнее сохранённого.
- Вес страницы теперь как у плеча `two-kits` из [B-04](B-04-measure-wasm-page-weight.md): +116 КиБ
  к одному киту.

