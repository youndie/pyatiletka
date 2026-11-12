#!/usr/bin/env bash
# B-02: run the full portfolio query against the live API and keep what it cost.
#   measurements/graphql/run.sh <owner> <page size> [runs] [query file, default portfolio.graphql]
# Needs `gh` signed in. Appends one line per run to runs.tsv; a page the API refused (a 502 is
# GitHub's time limit on one GraphQL request) is counted, not retried, because a collector that
# retries pays the same seconds again. Writes response-<page size>.json: the last complete run, every
# backlog text replaced by its size and sha256, so the file holds the shape and the counts without a
# copy of other repositories' backlogs.
set -uo pipefail
owner=${1:?owner}; first=${2:?page size}; runs=${3:-5}; qfile=${4:-portfolio.graphql}
dir=$(cd "$(dirname "$0")" && pwd)
query=$(grep -v '^#' "$dir/$qfile")
[ -f "$dir/runs.tsv" ] || printf 'query\tpage_size\trun\tok\tseconds\trequests\trefused\tbytes\tcost\trepos\n' > "$dir/runs.tsv"
now() { python3 -c 'import time; print(time.time())'; }
for i in $(seq "$runs"); do
  after=""; requests=0; refused=0; cost=0; bytes=0; repos=0; ok=1
  : > "$dir/.pages.jsonl"
  start=$(now)
  while :; do
    args=(-f query="$query" -F owner="$owner" -F first="$first")
    [ -n "$after" ] && args+=(-F after="$after")
    requests=$((requests + 1))
    if ! out=$(gh api graphql "${args[@]}" 2>/dev/null); then
      refused=$((refused + 1)); ok=0; break
    fi
    bytes=$((bytes + ${#out}))
    cost=$((cost + $(jq '.data.rateLimit.cost' <<<"$out")))
    repos=$((repos + $(jq '.data.repositoryOwner.repositories.nodes | length' <<<"$out")))
    printf '%s\n' "$out" >> "$dir/.pages.jsonl"
    [ "$(jq '.data.repositoryOwner.repositories.pageInfo.hasNextPage' <<<"$out")" = true ] || break
    after=$(jq -r '.data.repositoryOwner.repositories.pageInfo.endCursor' <<<"$out")
  done
  secs=$(python3 -c "print(round($(now) - $start, 2))")
  printf '%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\n' "${qfile%.graphql}" "$first" "$i" "$ok" "$secs" "$requests" "$refused" "$bytes" "$cost" "$repos" >> "$dir/runs.tsv"
  [ "$ok" = 1 ] && cp "$dir/.pages.jsonl" "$dir/.last-ok.jsonl"
done
if [ -f "$dir/.last-ok.jsonl" ]; then
python3 - "$dir" "$first" "${qfile%.graphql}" <<'PY'
import hashlib, json, sys, pathlib
d = pathlib.Path(sys.argv[1])
pages = [json.loads(l) for l in (d / ".last-ok.jsonl").read_text().splitlines() if l.strip()]
nodes = [n for p in pages for n in p["data"]["repositoryOwner"]["repositories"]["nodes"]]
for n in nodes:
    b = n.get("backlog")
    if b and "text" in b:
        b["sha256"] = hashlib.sha256(b.pop("text").encode()).hexdigest()
out = {"rateLimit": [p["data"]["rateLimit"] for p in pages], "repositories": nodes}
(d / ("response-%s-%s.json" % (sys.argv[3], sys.argv[2]))).write_text(json.dumps(out, ensure_ascii=False, indent=1) + "\n")
PY
fi
rm -f "$dir/.pages.jsonl" "$dir/.last-ok.jsonl"
