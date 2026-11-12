#!/usr/bin/env python3
"""B-04: serve a distribution the way GitHub Pages does, for first-frame.mjs.

    python3 measurements/wasm/serve.py <dist dir> <snapshot.json> <port>

GitHub Pages answers with gzip whenever the browser accepts it, wasm included, and never with brotli
(checked on youndie.github.io/toon-ui, README). This server does the same, at gzip level 6, and adds
`Cache-Control: no-store` so that no run is served from a cache. `snapshot.json` is the file given, so
the page reaches its Content state as the published one will.
"""

import gzip
import http.server
import pathlib
import sys

root = pathlib.Path(sys.argv[1]).resolve()
snapshot = pathlib.Path(sys.argv[2]).read_bytes()
port = int(sys.argv[3])

TYPES = {
    ".html": "text/html; charset=utf-8",
    ".js": "application/javascript; charset=utf-8",
    ".wasm": "application/wasm",
    ".json": "application/json",
    ".ttf": "font/ttf",
    ".otf": "font/otf",
    ".png": "image/png",
    ".webp": "image/webp",
    ".svg": "image/svg+xml",
    ".xml": "application/xml",
}
COMPRESSED = (".png", ".webp", ".jpg")


class Handler(http.server.BaseHTTPRequestHandler):
    def do_GET(self) -> None:  # noqa: N802 - the stdlib's name
        path = self.path.split("?", 1)[0].lstrip("/") or "index.html"
        if path == "snapshot.json":
            body, suffix = snapshot, ".json"
        else:
            f = (root / path).resolve()
            if root not in f.parents or not f.is_file():
                self.send_error(404)
                return
            body, suffix = f.read_bytes(), f.suffix
        headers = {"Content-Type": TYPES.get(suffix, "application/octet-stream"), "Cache-Control": "no-store"}
        if "gzip" in self.headers.get("Accept-Encoding", "") and suffix not in COMPRESSED:
            body = gzip.compress(body, 6)
            headers["Content-Encoding"] = "gzip"
        self.send_response(200)
        for k, v in headers.items():
            self.send_header(k, v)
        self.send_header("Content-Length", str(len(body)))
        self.end_headers()
        self.wfile.write(body)

    def log_message(self, *args) -> None:
        pass


http.server.ThreadingHTTPServer(("127.0.0.1", port), Handler).serve_forever()
