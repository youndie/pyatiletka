---
id: B-29
title: "toon-core 0.1.0.43: обходы toon-ui#30–#37 и #42 заменены API кита"
status: wip
priority: P2
size: M
stage: stage-4-daily-use
epic: feature-skins
blocked_by: []
---

# B-29 — toon-core 0.1.0.43: обходы toon-ui#30–#37 и #42 заменены API кита

Кит закрыл девять находок доски (`docs/kit-findings.md`, строки 1, 9–15 и 17) пунктами B-81…B-88 и выпустил их
к `0.1.0.43`. Renovate держит тот же бамп в #25, красный с 12.11: компилируется, но 12 голденов toon
расходятся — картинки кита изменились. Правило D4: обход живёт, только пока в ките нет нужного.

- **Решение.** Версия `toon = "0.1.0.43"`; каждый обход — на API кита, голдены перезаписаны там, где картинка
  изменилась по делу; каждая из девяти правок проверена на `Board`, `Repo`, `Settings` скина toon на 1280×800 и
  390×844.
- **Отвергнуто.** Принять правку кита по закрытому issue без проверки картинкой: обкатка для того и нужна.
- **Не входит.** Правки кита: где правка находку не закрывает, обход остаётся с issue в ките.

- AC: в `skin-toon` нет `// kit:` со ссылками на закрытые issue, кроме тех, что правка кита не закрыла (у них —
  новый issue); строки 1, 9–15, 17 `docs/kit-findings.md` закрыты с номером версии или открыты с причиной; голдены
  `skin-toon` перезаписаны и просмотрены; оба гейта зелёные; Renovate #25 закрыт со ссылкой на этот PR.
- Anchors: `skin-toon/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/toon/ToonBoard.kt`, `skin-toon/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/toon/ToonRepo.kt`, `skin-toon/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/toon/ToonSettings.kt`, `skin-toon/src/commonMain/kotlin/io/github/youndie/pyatiletka/skin/toon/Stage.kt`, `gradle/libs.versions.toml`
