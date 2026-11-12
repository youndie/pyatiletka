#!/usr/bin/env bash
# B-03: what the token a workflow runs with can see of one owner's repositories.
#   GH_TOKEN=... measurements/token/probe.sh <owner>
# Prints counts only, never a repository name: if the token did see a private repository, its name
# must not land in a public Actions log. Two questions:
#   1. Without any privacy filter, how many repositories does the token list, by visibility?
#   2. The collector's real query (measurements/graphql/portfolio.graphql, pages of 25): does it read
#      other repositories' backlogs, and what does it cost against this token's own limit?
set -euo pipefail
owner=${1:?owner}
root=$(cd "$(dirname "$0")/../.." && pwd)

visibility_query='query($owner: String!, $after: String) {
  rateLimit { limit cost remaining }
  repositoryOwner(login: $owner) {
    repositories(first: 100, after: $after, ownerAffiliations: OWNER) {
      totalCount
      pageInfo { hasNextPage endCursor }
      nodes { visibility }
    }
  }
}'
after=""; public=0; other=0; total=0; limit=0
while :; do
  args=(-f query="$visibility_query" -F owner="$owner")
  [ -n "$after" ] && args+=(-F after="$after")
  out=$(gh api graphql "${args[@]}")
  total=$(jq '.data.repositoryOwner.repositories.totalCount' <<<"$out")
  limit=$(jq '.data.rateLimit.limit' <<<"$out")
  public=$((public + $(jq '[.data.repositoryOwner.repositories.nodes[] | select(.visibility == "PUBLIC")] | length' <<<"$out")))
  other=$((other + $(jq '[.data.repositoryOwner.repositories.nodes[] | select(.visibility != "PUBLIC")] | length' <<<"$out")))
  [ "$(jq '.data.repositoryOwner.repositories.pageInfo.hasNextPage' <<<"$out")" = true ] || break
  after=$(jq -r '.data.repositoryOwner.repositories.pageInfo.endCursor' <<<"$out")
done
echo "listed_total=$total listed_public=$public listed_not_public=$other rate_limit=$limit"

query=$(grep -v '^#' "$root/measurements/graphql/portfolio.graphql")
after=""; repos=0; backlogs=0; texts=0; cost=0; pages=0
start=$(date +%s)
while :; do
  args=(-f query="$query" -F owner="$owner" -F first=25)
  [ -n "$after" ] && args+=(-F after="$after")
  out=$(gh api graphql "${args[@]}")
  pages=$((pages + 1))
  cost=$((cost + $(jq '.data.rateLimit.cost' <<<"$out")))
  repos=$((repos + $(jq '.data.repositoryOwner.repositories.nodes | length' <<<"$out")))
  backlogs=$((backlogs + $(jq '[.data.repositoryOwner.repositories.nodes[] | select(.backlog != null)] | length' <<<"$out")))
  texts=$((texts + $(jq '[.data.repositoryOwner.repositories.nodes[] | select(.backlog.text != null and (.backlog.text | length) > 0)] | length' <<<"$out")))
  [ "$(jq '.data.repositoryOwner.repositories.pageInfo.hasNextPage' <<<"$out")" = true ] || break
  after=$(jq -r '.data.repositoryOwner.repositories.pageInfo.endCursor' <<<"$out")
done
echo "portfolio_repos=$repos backlog_blobs=$backlogs backlog_texts=$texts pages=$pages cost=$cost seconds=$(( $(date +%s) - start ))"
