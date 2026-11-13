---
id: B-19
title: "Ссылка на страницу с kotlin.website"
status: done
priority: P3
size: S
stage: stage-4-daily-use
epic: feature-skins
blocked_by: [B-17]
---

# B-19 — Ссылка на страницу с kotlin.website

Страница — витрина китов; приходят на неё с kotlin.website.

- **Решение и причина.** Правка вне этого репозитория; здесь пункт держит приёмку.

- AC: ссылка на сайте ведёт на живую страницу.
- Якоря: `README.md` — адрес страницы.

## Iteration 1 (13.11.2026)

Правка сайта подготовлена и ждёт в pull request репозитория сайта (#29, не влит): на страницах
plakat-ui и toon-ui и на их карточках в `/ui`, рядом с «every component», чип «in a real app». Он
открывает `https://youndie.github.io/pyatiletka/` с `?skin=plakat` или `?skin=toon`
([B-16](B-16-live-skin-switch-and-url.md)), поэтому доска встречает посетителя в ките той страницы,
с которой он пришёл. У oldge-ui такого приложения нет, чипа тоже.

| Факт | Где проверено |
|---|---|
| Сайт с правкой собирается | `:site:jsBrowserDevelopmentWebpack :site:jsProcessResources` на маке |
| В браузере правка не смотрена: проверка — на живом сайте после выкатки | — |

## Вопрос владельцу

Влить #29 в репозитории сайта? Влитие и есть публикация (Publish, потом Deploy, около пяти минут),
поэтому по правилу цикла оно ждёт вашего «да». Варианты:

1. **Влить как есть** — цикл вольёт, дождётся Deploy и проверит чип на живых страницах `/plakat-ui`
   и `/toon-ui`; тогда приёмка выполнена.
2. **Поправить подачу** (слово на чипе, место, ссылка на `/ui` вместо страниц китов) — скажите как.
3. **Не ссылаться с сайта** — пункт `dropped`.

## Итог (13.11.2026)

Владелец: «влей как есть». Pull request сайта влит, Publish и Deploy прошли.

| Факт | Где проверено |
|---|---|
| `https://kotlin.website/plakat-ui` ведёт чипом «in a real app» на `https://youndie.github.io/pyatiletka/?skin=plakat`, `/toon-ui` — на `?skin=toon`, `/ui` — на обе; у `/oldge-ui` чипа нет | `curl` экспортированных страниц после Deploy |
| На `/toon-ui` чип стоит в ряду «every component · in a real app · github · all kits», в стиле кита | встроенный браузер |
| Доска по `?skin=toon` и `?skin=plakat` открывается в своём ките | встроенный браузер, проверка [B-23](B-23-scroll-whole-board.md) |
