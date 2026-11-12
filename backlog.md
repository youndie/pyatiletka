# Бэклог: «Пятилетка»

> Роль документа — бэклог продукта. **Файл на пункт** в [`docs/backlog/`](docs/backlog/) —
> `B-NN-<slug>.md`. Здесь — индекс (генерируется) и всё, что не пункт: цель, этапы, решения.
>
> Новый пункт: скопировать [`docs/templates/backlog-item.md`](docs/templates/backlog-item.md), взять
> следующий свободный `B-NN`, после правки — `make fix`, затем `make check`.

## Цель

Одна доска отвечает владельцу портфеля, куда смотреть первым: красный main, PR, ждущие вливания,
выполнение плана и пункты, ждущие его ответа. И — на равных — обкатывает plakat-ui и toon-ui до
выпуска на Maven Central: каждый экран собран дважды, всё, чего киту не хватило, становится issue в
ките. Продукт закрыт, когда по каждому киту написан вердикт
([B-20](docs/backlog/B-20-two-week-trial-verdict.md)). Почему устроено так, а не иначе, — в
[исследовании](docs/research/research-architecture.md).

## Этапы

Этап — поле пункта, а не каталог: пункты цитируются по id из документов всех слоёв.

| Id этапа | Этап | Что верно, когда он закрыт |
|---|---|---|
| `stage-0-risks` | Риски | оба кита и appframe живут в одной сборке на двух таргетах; цена запроса, право токена и вес страницы измерены |
| `stage-1-data` | Данные | снимок собирается, разбирается и кэшируется; состояние всех экранов покрыто тестами без UI |
| `stage-2-plakat` | Скин plakat | все состояния экранов в plakat-ui в пределах допуска паритета |
| `stage-3-toon` | Скин toon | те же состояния в toon-ui с эталонами; скин меняется на ходу |
| `stage-4-daily-use` | Ежедневное использование | страница опубликована и обновляется, десктоп стоит у владельца, находки по китам подсчитаны |

## Метки

`[ ]` открыт · `[~]` в работе · `[x]` сделан · `[?]` открытый вопрос · `[-]` отброшен

<!-- BEGIN INDEX -->

## Open (10)

| Task | | Priority | Size | Blocked by |
|---|---|---|---|---|
| [B-11](docs/backlog/B-11-plakat-board.md) `[?]` | plakat: доска, все состояния, оба размера | P1 | L | B-09, B-10 |
| [B-12](docs/backlog/B-12-plakat-repo-card.md) `[ ]` | plakat: карточка репозитория | P1 | M | B-11 |
| [B-13](docs/backlog/B-13-plakat-settings.md) `[ ]` | plakat: настройки | P1 | S | B-11 |
| [B-15](docs/backlog/B-15-toon-repo-card-and-settings.md) `[ ]` | toon: карточка и настройки | P1 | M | B-14 |
| [B-16](docs/backlog/B-16-live-skin-switch-and-url.md) `[ ]` | Смена скина на ходу и по адресу | P1 | S | B-11, B-14 |
| [B-17](docs/backlog/B-17-publish-page-and-hourly-snapshot.md) `[ ]` | Страница: сборка в gh-pages, снимок раз в час | P1 | M | B-03, B-07, B-11 |
| [B-20](docs/backlog/B-20-two-week-trial-verdict.md) `[ ]` | Итог обкатки: две недели ежедневного использования | P1 | S | B-15, B-17, B-18 |
| [B-04](docs/backlog/B-04-measure-wasm-page-weight.md) `[ ]` | Замер: вес и первая отрисовка wasm-страницы с двумя китами | P2 | S | B-01 |
| [B-18](docs/backlog/B-18-desktop-installed-app.md) `[ ]` | Десктоп: установленное приложение на машине владельца | P2 | S | B-12, B-13 |
| [B-19](docs/backlog/B-19-link-from-kotlin-website.md) `[ ]` | Ссылка на страницу с kotlin.website | P3 | S | B-17 |

## Closed (10)

**Риски**

- [B-01](docs/backlog/B-01-skeleton-five-modules.md) `[x]` - Каркас: пять модулей, конвенции sborka, оба кита и appframe в одной сборке
- [B-02](docs/backlog/B-02-measure-full-graphql-query.md) `[x]` - Замер: полный запрос GraphQL на живом портфеле
- [B-03](docs/backlog/B-03-measure-workflow-token-scope.md) `[x]` - Замер: токен workflow читает чужие публичные репозитории
- [B-05](docs/backlog/B-05-kit-findings-and-renovate.md) `[x]` - Учёт находок по китам и Renovate на снапшоты китов

**Данные**

- [B-06](docs/backlog/B-06-core-model-and-rules.md) `[x]` - core: модель, схема snapshot.json, разбор индекса, выполнение и порядок
- [B-07](docs/backlog/B-07-collector-query-and-cli.md) `[x]` - collector: запрос → Snapshot, публичный режим, CLI
- [B-08](docs/backlog/B-08-app-snapshot-sources.md) `[x]` - app: источники снимка, кэш, таймер, токен, настройки
- [B-09](docs/backlog/B-09-app-screen-state-and-navigation.md) `[x]` - app: состояние и навигация экранов Board, Repo, Settings

**Скин plakat**

- [B-10](docs/backlog/B-10-design-canvas-and-references.md) `[x]` - Дизайн: холст по брифу, эталонные PNG в skin-plakat

**Скин toon**

- [B-14](docs/backlog/B-14-toon-board.md) `[x]` - toon: доска, все состояния, оба размера

<!-- END INDEX -->

## Решения, которые не пересматриваются

**Замеры — до кода, который на них стоит.** [B-07](docs/backlog/B-07-collector-query-and-cli.md)
блокирован замером запроса [B-02](docs/backlog/B-02-measure-full-graphql-query.md), публикация
[B-17](docs/backlog/B-17-publish-page-and-hourly-snapshot.md) — замером права токена
[B-03](docs/backlog/B-03-measure-workflow-token-scope.md): если гипотеза не подтвердится, меняется
устройство, а не только число.

**toon не ждёт холста plakat.** [B-14](docs/backlog/B-14-toon-board.md) блокирован только
состоянием экранов [B-09](docs/backlog/B-09-app-screen-state-and-navigation.md): холста у toon нет,
и ждать чужой дизайн незачем.

**Обход кита без issue не вливается.** Находка, обойдённая молча, не попадает в итог
[B-20](docs/backlog/B-20-two-week-trial-verdict.md) — а итог и есть продукт.
