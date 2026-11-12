# pyatiletka

A portfolio board (CI of the default branch, open pull requests, backlog completion) and the first
real consumer of the plakat-ui and toon-ui Compose Multiplatform kits. No server: desktop collects
a snapshot itself, the web page reads `snapshot.json` published by a scheduled workflow.

## How to start a session

1. Read [docs/research/research-architecture.md](docs/research/research-architecture.md): what was
   verified, what was decided and why, which hypotheses are still open and where they get settled.
2. Read [backlog.md](backlog.md) and the item you are taking (`docs/backlog/B-NN-*.md`).
3. Read the layer document the item points at: `docs/features/`, `docs/screens/`, `docs/api/`,
   `docs/services/`. Its business rules and scenarios are the acceptance criteria.
4. Keep the documentation in the same change as the code: anchors, statuses, the item's `status`,
   then `make fix` and `make check`.

## Rules

- Only public repository names go into anything that is published: fixtures, goldens, the page's
  snapshot, documentation. The desktop app sees whatever the owner's token sees; its data never
  becomes a fixture.
- Skins do not know each other: `skin-plakat` depends on plakat-ui only, `skin-toon` on toon-ui only.
  A workaround for a kit is allowed only together with an issue in that kit's repository and a
  `// kit: <link>` comment at the workaround; the finding goes into `docs/kit-findings.md`.
- The board only reads GitHub. The token is never written to disk or logs, and there is no field to
  type it in.
- Code, commits and pull requests in English, Conventional Commits; documentation prose in Russian,
  identifiers verbatim.

## The backlog loop

The owner's decisions of 2026-11-12, for `/loop` over `backlog-item`:

- The loop merges its own pull requests, squash, on a green verdict over the head commit and not
  behind `main` (backlog-item Step 7). Red is a finding, never a reason to loosen a check.
- Kit findings are filed as issues in `youndie/plakat-ui` and `youndie/toon-ui` without asking
  about each one. Nothing else in the kit repositories is changed from here.
- Gradle runs on the WSL box through `~/.claude/bin/wsl-run` (mutagen session `pyatiletka`);
  CI runs on `ubuntu-latest`. Formatters that rewrite files run on the Mac.
- Production-like steps that leave this repository — the owner's machine (B-18), kotlin.website
  (B-19) — are prepared and stopped before the outside change, as a `question`.
