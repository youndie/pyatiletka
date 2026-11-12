#!/usr/bin/env bash
# Publishes into the gh-pages branch as one commit with no history (B-17). The page workflow passes
# the built page; the snapshot workflow passes snapshot.json alone. Whatever a call does not pass is
# kept from the branch as it is now, so the page and the snapshot never overwrite each other, as long
# as no two calls run at once, which the workflows' shared `concurrency: gh-pages` group sees to.
#
#   publish-to-pages.sh page <dir>          the page: everything but snapshot.json comes from <dir>
#   publish-to-pages.sh snapshot <file>     the snapshot: snapshot.json comes from <file>
set -euo pipefail

what=$1
from=$2
work=$(mktemp -d)
git_dir=$(git rev-parse --absolute-git-dir)
index=$(mktemp)
rm -f "$index"
# Git over the work directory through a throwaway index: the checkout's own tree and index stay as they are.
pages() { GIT_INDEX_FILE="$index" git --git-dir="$git_dir" --work-tree="$work" -C "$work" "$@"; }

# The branch as it is now, or nothing on the very first run.
if git ls-remote --exit-code --heads origin gh-pages >/dev/null; then
  git fetch -q --depth 1 origin gh-pages
  pages checkout FETCH_HEAD -- .
  rm -f "$index"
fi

case "$what" in
  page)
    keep=""
    if [ -f "$work/snapshot.json" ]; then
      keep=$(mktemp)
      cp "$work/snapshot.json" "$keep"
    fi
    find "$work" -mindepth 1 -delete
    cp -R "$from"/. "$work"/
    if [ -n "$keep" ]; then cp "$keep" "$work/snapshot.json"; fi
    ;;
  snapshot)
    cp "$from" "$work/snapshot.json"
    ;;
  *)
    echo "usage: publish-to-pages.sh page <dir> | snapshot <file>" >&2
    exit 2
    ;;
esac
# Without it GitHub Pages runs Jekyll over the page and drops files it takes for its own.
touch "$work/.nojekyll"

# The commit is built in this checkout, so the push goes with the checkout's own credentials: a tree
# from the work directory, a commit with no parent, a forced push.
pages add -A .
tree=$(pages write-tree)
commit=$(git -c user.name="github-actions[bot]" \
  -c user.email="41898282+github-actions[bot]@users.noreply.github.com" \
  commit-tree "$tree" -m "chore(pages): publish the $what from ${GITHUB_SHA:-a local tree}")
git push -q --force origin "$commit:refs/heads/gh-pages"
echo "published the $what: $(find "$work" -type f | wc -l | tr -d ' ') files"
