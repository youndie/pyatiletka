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
| `stage-2-plakat` | Скин plakat | все состояния экранов в plakat-ui; паритет измерен, остаток выше допуска разобран по областям (вид кита принят, [B-11](docs/backlog/B-11-plakat-board.md)) |
| `stage-3-toon` | Скин toon | те же состояния в toon-ui с эталонами; скин меняется на ходу |
| `stage-4-daily-use` | Ежедневное использование | страница опубликована и обновляется, десктоп стоит у владельца, находки по китам подсчитаны |

## Метки

`[ ]` открыт · `[~]` в работе · `[x]` сделан · `[?]` открытый вопрос · `[-]` отброшен

<!-- BEGIN INDEX -->

## Open (4)

| Task | | Priority | Size | Blocked by |
|---|---|---|---|---|
| [B-20](docs/backlog/B-20-two-week-trial-verdict.md) `[ ]` | Итог обкатки: две недели ежедневного использования | P1 | S | B-15, B-17, B-18 |
| [B-25](docs/backlog/B-25-plakat-phone-summary-clipped.md) `[~]` | plakat, узкий: подпись «План выполнен на» срезана сверху, фильтр «Ждут меня» обрезан на 375 | P2 | S | - |
| [B-26](docs/backlog/B-26-toon-narrow-meta-under-badge.md) `[ ]` | toon, узкий: на живых числах строка сводки уходит под бейдж времени | P2 | S | - |
| [B-27](docs/backlog/B-27-escape-and-back-close-overlays.md) `[?]` | Esc и «Назад» не закрывают карточку и диалог вида | P3 | M | - |

## Closed (23)

**Риски**

- [B-01](docs/backlog/B-01-skeleton-five-modules.md) `[x]` - Каркас: пять модулей, конвенции sborka, оба кита и appframe в одной сборке
- [B-02](docs/backlog/B-02-measure-full-graphql-query.md) `[x]` - Замер: полный запрос GraphQL на живом портфеле
- [B-03](docs/backlog/B-03-measure-workflow-token-scope.md) `[x]` - Замер: токен workflow читает чужие публичные репозитории
- [B-04](docs/backlog/B-04-measure-wasm-page-weight.md) `[x]` - Замер: вес и первая отрисовка wasm-страницы с двумя китами
- [B-05](docs/backlog/B-05-kit-findings-and-renovate.md) `[x]` - Учёт находок по китам и Renovate на снапшоты китов

**Данные**

- [B-06](docs/backlog/B-06-core-model-and-rules.md) `[x]` - core: модель, схема snapshot.json, разбор индекса, выполнение и порядок
- [B-07](docs/backlog/B-07-collector-query-and-cli.md) `[x]` - collector: запрос → Snapshot, публичный режим, CLI
- [B-08](docs/backlog/B-08-app-snapshot-sources.md) `[x]` - app: источники снимка, кэш, таймер, токен, настройки
- [B-09](docs/backlog/B-09-app-screen-state-and-navigation.md) `[x]` - app: состояние и навигация экранов Board, Repo, Settings

**Скин plakat**

- [B-10](docs/backlog/B-10-design-canvas-and-references.md) `[x]` - Дизайн: холст по брифу, эталонные PNG в skin-plakat
- [B-11](docs/backlog/B-11-plakat-board.md) `[x]` - plakat: доска, все состояния, оба размера
- [B-12](docs/backlog/B-12-plakat-repo-card.md) `[x]` - plakat: карточка репозитория
- [B-13](docs/backlog/B-13-plakat-settings.md) `[x]` - plakat: настройки
- [B-22](docs/backlog/B-22-summary-counters-wrap-at-mid-width.md) `[x]` - plakat: счётчики сводки переносятся по цифрам на ширине около 800

**Скин toon**

- [B-14](docs/backlog/B-14-toon-board.md) `[x]` - toon: доска, все состояния, оба размера
- [B-15](docs/backlog/B-15-toon-repo-card-and-settings.md) `[x]` - toon: карточка и настройки
- [B-16](docs/backlog/B-16-live-skin-switch-and-url.md) `[x]` - Смена скина на ходу и по адресу
- [B-21](docs/backlog/B-21-toon-goldens-depend-on-runner-cpu.md) `[x]` - Голдены toon зависят от процессора раннера: текстура сцены выбирается на границе текселов

**Ежедневное использование**

- [B-17](docs/backlog/B-17-publish-page-and-hourly-snapshot.md) `[x]` - Страница: сборка в gh-pages, снимок раз в час
- [B-18](docs/backlog/B-18-desktop-installed-app.md) `[x]` - Десктоп: установленное приложение на машине владельца
- [B-19](docs/backlog/B-19-link-from-kotlin-website.md) `[x]` - Ссылка на страницу с kotlin.website
- [B-23](docs/backlog/B-23-scroll-whole-board.md) `[x]` - Широкая доска: прокручивается всё под шапкой, а не только список
- [B-24](docs/backlog/B-24-summary-counters-plural.md) `[x]` - Счётчики сводки не согласуются с числом: «0 красный main»

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
