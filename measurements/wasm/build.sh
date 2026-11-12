#!/usr/bin/env bash
# B-04: build the web page twice and print both distributions as one base64 tar on stdout.
#   ~/.claude/bin/wsl-run measurements/wasm/build.sh > dists.b64     (from the repository root)
#   one-kit  — the tree as it is: Shell draws plakat only, so toon-ui is dead code the wasm
#              optimiser drops;
#   two-kits — the tree with two-kits.patch: Shell draws toon when the skin is toon, and `?skin=toon`
#              opens the page in it, so both kits are in the bundle as the finished page will have them.
# Each build runs in its own copy under /tmp: the mutagen replica deletes build/ (and anything else
# it did not put there) whenever the Mac side syncs.
set -euo pipefail
src=$(pwd)
out=/tmp/pyatiletka-b04
rm -rf "$out" && mkdir -p "$out"
for arm in one-kit two-kits; do
  work="$out/src-$arm"
  rsync -a --exclude build --exclude .gradle --exclude .kotlin --exclude node_modules "$src/" "$work/"
  if [ "$arm" = two-kits ]; then
    git -C "$work" apply "$work/measurements/wasm/two-kits.patch"
  fi
  (cd "$work" && systemd-run --user --scope -q -p MemoryMax=7G \
    ./gradlew :app:wasmJsBrowserDistribution --no-daemon -q >"$out/build-$arm.log" 2>&1)
  cp -r "$work/app/build/dist/wasmJs/productionExecutable" "$out/$arm"
done
tar -C "$out" -cf - one-kit two-kits | base64 -w0
