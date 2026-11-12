#!/usr/bin/env python3
"""B-04: what the page weighs on disk, by kind of file, raw and compressed.

    python3 measurements/wasm/weight.py <dist dir> [<dist dir> ...]

Counts what a browser may fetch: the source map and the licence text are left out. gzip is level 9
and brotli quality 11 (the `brotli` command), the best a static host could serve; whether a host
compresses at all is a separate question (README). composeResources holds every font and image of
both kits, of which a first frame fetches only some: what was really transferred is first-frame.mjs's.
"""

import gzip
import pathlib
import subprocess
import sys

SKIP = (".map", ".LICENSE.txt")


def kind(path: pathlib.Path) -> str:
    if path.suffix == ".wasm":
        return "wasm"
    if path.suffix in (".js", ".mjs"):
        return "js"
    if path.suffix in (".ttf", ".otf", ".woff", ".woff2"):
        return "fonts"
    if path.suffix in (".png", ".webp", ".jpg", ".svg"):
        return "images"
    return "other"


def brotli(data: bytes) -> int:
    return len(subprocess.run(["brotli", "-q", "11", "-c"], input=data, capture_output=True, check=True).stdout)


def main() -> None:
    print("dist\tkind\tfiles\traw\tgzip\tbrotli")
    for d in map(pathlib.Path, sys.argv[1:]):
        totals: dict[str, list[int]] = {}
        for f in sorted(p for p in d.rglob("*") if p.is_file() and not p.name.endswith(SKIP)):
            data = f.read_bytes()
            row = totals.setdefault(kind(f), [0, 0, 0, 0])
            row[0] += 1
            row[1] += len(data)
            row[2] += len(gzip.compress(data, 9))
            row[3] += brotli(data)
        all_ = [sum(r[i] for r in totals.values()) for i in range(4)]
        for k, r in sorted(totals.items()) + [("total", all_)]:
            print(f"{d.name}\t{k}\t{r[0]}\t{r[1]}\t{r[2]}\t{r[3]}")


if __name__ == "__main__":
    main()
