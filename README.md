# pyatiletka

A portfolio board: CI state of the default branch, open pull requests and backlog completion
across a set of GitHub repositories, on one screen.

It is also the first real consumer of two Compose Multiplatform UI kits,
[plakat-ui](https://github.com/youndie/plakat-ui) and [toon-ui](https://github.com/youndie/toon-ui):
every screen is built twice, once per kit, over the same state.

- **Desktop** (JVM) collects the data itself with a local GitHub token.
- **Web** (wasmJs) reads a `snapshot.json` that a scheduled workflow publishes next to the page.

There is no server. The board only reads; it never merges or re-runs anything.

Status: not built yet.

## License

[MIT](LICENSE)
