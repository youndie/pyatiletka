#!/usr/bin/env bash
# B-04: first frame of the page, every arm and profile, N cold runs each, appended to runs.tsv.
#   measurements/wasm/run.sh <dists dir from build.sh> <snapshot.json> [runs, default 5] [chrome]
# Arms: one-kit (the tree as it is), two-kits (both kits in the bundle, the page in plakat),
# two-kits-toon (the same bundle opened with ?skin=toon). Runs are interleaved by arm within a round,
# so a slow minute of the machine falls on every arm alike.
set -euo pipefail
dists=${1:?dists}; snapshot=${2:?snapshot.json}; runs=${3:-5}
chrome=${4:-/Applications/Google Chrome.app/Contents/MacOS/Google Chrome}
dir=$(cd "$(dirname "$0")" && pwd)
[ -f "$dir/runs.tsv" ] || printf 'arm\tprofile\trun\tboard_ms\tctx_ms\tfirst_ms\tready_ms\tkib\tkit_files\n' > "$dir/runs.tsv"
python3 "$dir/serve.py" "$dists/one-kit" "$snapshot" 8751 & a=$!
python3 "$dir/serve.py" "$dists/two-kits" "$snapshot" 8752 & b=$!
trap 'kill $a $b' EXIT
sleep 1
for profile in local slow4g; do
  for i in $(seq "$runs"); do
    node "$dir/first-frame.mjs" "$chrome" one-kit http://127.0.0.1:8751/ "$profile" 1 | sed "s/\t1\t/\t$i\t/" >> "$dir/runs.tsv"
    node "$dir/first-frame.mjs" "$chrome" two-kits http://127.0.0.1:8752/ "$profile" 1 | sed "s/\t1\t/\t$i\t/" >> "$dir/runs.tsv"
    node "$dir/first-frame.mjs" "$chrome" two-kits-toon "http://127.0.0.1:8752/?skin=toon" "$profile" 1 | sed "s/\t1\t/\t$i\t/" >> "$dir/runs.tsv"
  done
done
