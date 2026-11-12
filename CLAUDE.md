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
